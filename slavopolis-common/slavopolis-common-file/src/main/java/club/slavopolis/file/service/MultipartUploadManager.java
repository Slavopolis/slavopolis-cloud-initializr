package club.slavopolis.file.service;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.domain.FileMetadata;
import club.slavopolis.file.domain.FileUploadSession;
import club.slavopolis.file.domain.request.ChunkUploadRequest;
import club.slavopolis.file.domain.request.FileUploadRequest;
import club.slavopolis.file.domain.result.ChunkUploadResult;
import club.slavopolis.file.domain.result.FileUploadResult;
import club.slavopolis.file.enums.AccessPermission;
import club.slavopolis.file.enums.FileStatus;
import club.slavopolis.file.enums.UploadMethod;
import club.slavopolis.file.repository.FileInfoRepository;
import club.slavopolis.file.repository.FileUploadSessionRepository;
import club.slavopolis.file.exception.FileOperationException;
import club.slavopolis.file.util.FileUtils;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;
import club.slavopolis.persistence.jdbc.transaction.TransactionStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分片上传管理器
 * <p>
 * 提供分片上传的管理功能，包括初始化、上传分片、合并文件等
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MultipartUploadManager {

    private final Map<StorageType, FileStorageStrategy> storageStrategies;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DataSourceTransactionManager transactionManager;
    private final TransactionDefinition defaultTransactionDefinition;
    private final CurrentSystemProperties systemProperties;
    private final FileUploadSessionRepository fileUploadSessionRepository;
    private final FileInfoRepository fileInfoRepository;
    private final Tika tika = new Tika();

    /**
     * 上传会话缓存
     */
    private final Map<String, UploadSession> sessionCache = new ConcurrentHashMap<>();

    /**
     * 默认会话过期时间（小时）
     */
    private static final int DEFAULT_SESSION_EXPIRY_HOURS = 24;

    /**
     * 初始化分片上传
     *
     * @param request 文件上传请求
     * @return 上传会话ID
     */
    public String initializeMultipartUpload(FileUploadRequest request) {
        log.debug("初始化分片上传: {}", request.getOriginalName());
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 验证请求
            validateMultipartUploadRequest(request);
            
            // 2. 计算分片信息
            long chunkSize = getChunkSize();
            int totalChunks = (int) Math.ceil((double) request.getFileSize() / chunkSize);
            
            // 3. 生成上传会话ID
            String uploadId = UUID.randomUUID().toString().replace("-", "");
            
            // 4. 保存上传会话信息到数据库
            FileUploadSession uploadSession = new FileUploadSession();
            uploadSession.setUploadId(uploadId);
            uploadSession.setOriginalName(request.getOriginalName());
            uploadSession.setTotalSize(request.getFileSize());
            uploadSession.setChunkSize(chunkSize);
            uploadSession.setTotalChunks(totalChunks);
            uploadSession.setUploadedChunks(0);
            uploadSession.setStorageType(getStorageType());
            uploadSession.setStatus("UPLOADING");
            uploadSession.setTenantId(request.getTenantId());
            uploadSession.setCreatedBy(request.getCreatedBy());
            uploadSession.setAccessPermission(request.getAccessPermission());
            
            fileUploadSessionRepository.save(namedJdbc, uploadSession);
            
            // 5. 创建内存会话
            UploadSession session = new UploadSession();
            session.setUploadId(uploadId);
            session.setOriginalName(request.getOriginalName());
            session.setTotalSize(request.getFileSize());
            session.setChunkSize(chunkSize);
            session.setTotalChunks(totalChunks);
            session.setStorageType(getStorageType());
            session.setTenantId(request.getTenantId());
            session.setCreatedBy(request.getCreatedBy());
            session.setAccessPermission(request.getAccessPermission());
            
            sessionCache.put(uploadId, session);
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("分片上传初始化成功: uploadId={}, totalChunks={}", uploadId, totalChunks);
            return uploadId;
            
        } catch (Exception e) {
            log.error("初始化分片上传失败: {}", request.getOriginalName(), e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "初始化分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 上传分片
     *
     * @param request 分片上传请求
     * @return 分片上传结果
     */
    public ChunkUploadResult uploadChunk(ChunkUploadRequest request) {
        log.debug("上传分片: uploadId={}, chunkIndex={}", request.getUploadId(), request.getChunkIndex());
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 获取上传会话
            UploadSession session = getUploadSession(request.getUploadId());
            
            // 2. 验证分片请求
            validateChunkUploadRequest(request, session);
            
            // 3. 检查分片是否已存在
            if (session.getUploadedChunks().containsKey(request.getChunkIndex())) {
                log.debug("分片已存在，跳过上传: uploadId={}, chunkIndex={}", 
                    request.getUploadId(), request.getChunkIndex());
                namedJdbc.commitTransaction(transactionStatus);
                return createChunkUploadResult(request, true, "分片已存在");
            }
            
            // 4. 计算分片哈希
            String chunkHash = DigestUtils.md5DigestAsHex(request.getChunkStream());
            
            // 5. 存储分片信息
            ChunkInfo chunkInfo = new ChunkInfo();
            chunkInfo.setChunkIndex(request.getChunkIndex());
            chunkInfo.setChunkSize(request.getChunkSize());
            chunkInfo.setChunkHash(chunkHash);
            chunkInfo.setChunkData(request.getChunkStream().readAllBytes());
            chunkInfo.setUploadTime(LocalDateTime.now());
            
            session.getUploadedChunks().put(request.getChunkIndex(), chunkInfo);
            
            // 6. 更新数据库
            updateUploadSessionProgress(namedJdbc, session);
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("分片上传成功: uploadId={}, chunkIndex={}, progress={}/{}", 
                request.getUploadId(), request.getChunkIndex(), 
                session.getUploadedChunks().size(), session.getTotalChunks());
                
            return createChunkUploadResult(request, true, "上传成功");
            
        } catch (Exception e) {
            log.error("分片上传失败: uploadId={}, chunkIndex={}", 
                request.getUploadId(), request.getChunkIndex(), e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 完成分片上传
     *
     * @param uploadId 上传会话ID
     * @return 文件上传结果
     */
    public FileUploadResult completeUpload(String uploadId) {
        log.debug("完成分片上传: uploadId={}", uploadId);
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 获取上传会话
            UploadSession session = getUploadSession(uploadId);
            
            // 2. 验证所有分片是否上传完成
            if (session.getUploadedChunks().size() != session.getTotalChunks()) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    String.format("分片上传未完成: %d/%d", 
                        session.getUploadedChunks().size(), session.getTotalChunks())
                );
            }
            
            // 3. 合并分片数据
            byte[] fileData = mergeChunks(session);
            
            // 4. 计算文件哈希
            String fileHash = DigestUtils.md5DigestAsHex(fileData);
            
            // 5. 创建文件元数据
            FileMetadata metadata = createFileMetadata(session, fileHash, fileData.length);
            
            // 6. 生成存储键值
            String storageKey = generateStorageKey(session.getOriginalName(), fileHash);
            
            // 7. 上传到存储层
            FileStorageStrategy strategy = getStorageStrategy();
            try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
                strategy.store(storageKey, inputStream, metadata);
            }
            
            // 8. 保存文件信息到数据库
            FileInfo fileInfo = saveFileInfo(namedJdbc, session, metadata, storageKey);
            
            // 9. 更新上传会话状态
            markUploadSessionCompleted(namedJdbc, uploadId);
            
            // 10. 清理会话缓存
            sessionCache.remove(uploadId);
            
            // 11. 构建上传结果
            FileUploadResult result = new FileUploadResult();
            result.setFileInfo(fileInfo);
            result.setUploadMethod(UploadMethod.MULTIPART);
            result.setFileId(fileInfo.getFileId());
            result.setUploadCompleteTime(LocalDateTime.now());
            result.setSuccess(true);
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("分片上传完成: uploadId={}, fileId={}", uploadId, fileInfo.getFileId());
            return result;
            
        } catch (Exception e) {
            log.error("完成分片上传失败: uploadId={}", uploadId, e);
            // 标记上传会话为失败状态
            markUploadSessionFailed(namedJdbc, uploadId, e.getMessage());
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "完成分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 取消分片上传
     *
     * @param uploadId 上传会话ID
     */
    public void abortUpload(String uploadId) {
        log.debug("取消分片上传: uploadId={}", uploadId);
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 标记上传会话为取消状态
            boolean cancelled = fileUploadSessionRepository.markCancelled(namedJdbc, uploadId);
            if (!cancelled) {
                log.warn("标记上传会话为取消状态失败: {}", uploadId);
            }
            
            // 2. 清理会话缓存
            sessionCache.remove(uploadId);
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("分片上传已取消: uploadId={}", uploadId);
            
        } catch (Exception e) {
            log.error("取消分片上传失败: uploadId={}", uploadId, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "取消分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 获取上传会话
     */
    private UploadSession getUploadSession(String uploadId) {
        // 先从缓存查找
        UploadSession session = sessionCache.get(uploadId);
        if (session != null) {
            return session;
        }
        
        // 从数据库加载
        session = loadUploadSessionFromDatabase(uploadId);
        if (session == null) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "上传会话不存在: " + uploadId
            );
        }
        
        // 加入缓存
        sessionCache.put(uploadId, session);
        return session;
    }

    /**
     * 从数据库加载上传会话
     */
    private UploadSession loadUploadSessionFromDatabase(String uploadId) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            FileUploadSession sessionData = fileUploadSessionRepository.findById(namedJdbc, uploadId);
            if (sessionData == null) {
                return null;
            }
            
            // 转换为内部UploadSession对象
            UploadSession session = new UploadSession();
            session.setUploadId(sessionData.getUploadId());
            session.setOriginalName(sessionData.getOriginalName());
            session.setTotalSize(sessionData.getTotalSize());
            session.setChunkSize(sessionData.getChunkSize());
            session.setTotalChunks(sessionData.getTotalChunks());
            session.setStorageType(sessionData.getStorageType());
            session.setTenantId(sessionData.getTenantId());
            session.setCreatedBy(sessionData.getCreatedBy());
            session.setAccessPermission(sessionData.getAccessPermission());
            
            return session;
            
        } catch (Exception e) {
            log.error("加载上传会话失败: uploadId={}", uploadId, e);
            return null;
        }
    }

    /**
     * 验证分片上传请求
     */
    private void validateMultipartUploadRequest(FileUploadRequest request) {
        long minSize = systemProperties.getFile().getChunk().getMinChunkSize();
        if (request.getFileSize() < minSize) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "文件太小，不需要分片上传"
            );
        }
        
        long maxSize = systemProperties.getFile().getSecurity().getMaxFileSize();
        if (request.getFileSize() > maxSize) {
            throw new FileOperationException(
                FileConstants.FILE_TOO_LARGE, 
                "文件大小超出限制"
            );
        }
    }

    /**
     * 验证分片请求
     */
    private void validateChunkUploadRequest(ChunkUploadRequest request, UploadSession session) {
        if (request.getChunkIndex() < 0 || request.getChunkIndex() >= session.getTotalChunks()) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "无效的分片索引: " + request.getChunkIndex()
            );
        }
        
        if (request.getChunkStream() == null || request.getChunkSize() == 0) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "分片数据不能为空"
            );
        }
        
        // 验证分片大小
        long expectedSize = session.getChunkSize();
        if (request.getChunkIndex() == session.getTotalChunks() - 1) {
            // 最后一个分片可能较小
            expectedSize = session.getTotalSize() % session.getChunkSize();
            if (expectedSize == 0) {
                expectedSize = session.getChunkSize();
            }
        }
        
        if (request.getChunkSize() != expectedSize) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                String.format("分片大小不匹配: expected=%d, actual=%d", 
                    expectedSize, request.getChunkSize())
            );
        }
    }

    /**
     * 合并分片数据
     */
    private byte[] mergeChunks(UploadSession session) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (int i = 0; i < session.getTotalChunks(); i++) {
                ChunkInfo chunkInfo = session.getUploadedChunks().get(i);
                if (chunkInfo == null) {
                    throw new FileOperationException(
                        FileConstants.UPLOAD_FAILED, 
                        "缺少分片: " + i
                    );
                }
                outputStream.write(chunkInfo.getChunkData());
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "合并分片失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 创建文件元数据
     */
    private FileMetadata createFileMetadata(UploadSession session, String fileHash, int fileSize) {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName(session.getOriginalName());
        metadata.setFileSize((long) fileSize);
        metadata.setFileHash(fileHash);
        metadata.setExtension(FileUtils.getFileExtension(session.getOriginalName()));
        
        // 检测MIME类型
        try {
            String contentType = tika.detect(session.getOriginalName());
            metadata.setContentType(contentType);
        } catch (Exception e) {
            log.warn("检测文件MIME类型失败: {}", session.getOriginalName(), e);
            metadata.setContentType("application/octet-stream");
        }
        
        metadata.setCreateTime(LocalDateTime.now());
        metadata.setLastUpdateTime(LocalDateTime.now());
        
        return metadata;
    }

    /**
     * 生成存储键值
     */
    private String generateStorageKey(String originalName, String fileHash) {
        String extension = FileUtils.getFileExtension(originalName);
        String fileName = UUID.randomUUID().toString();
        return StringUtils.hasText(extension) ? fileName + "." + extension : fileName;
    }

    /**
     * 保存文件信息到数据库
     */
    private FileInfo saveFileInfo(EnhancedJdbcTemplate namedJdbc, UploadSession session, 
                                  FileMetadata metadata, String storageKey) {
        // 构建FileInfo对象
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(session.getOriginalName());
        fileInfo.setFileSize(metadata.getFileSize());
        fileInfo.setContentType(metadata.getContentType());
        fileInfo.setFileHash(metadata.getFileHash());
        fileInfo.setExtension(metadata.getExtension());
        fileInfo.setStorageType(session.getStorageType());
        fileInfo.setStorageKey(storageKey);
        fileInfo.setStatus(FileStatus.ACTIVE);
        fileInfo.setAccessPermission(session.getAccessPermission());
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setTenantId(session.getTenantId());
        fileInfo.setDownloadCount(0);
        fileInfo.setCreatedBy(session.getCreatedBy());
        
        // 使用Repository保存
        return fileInfoRepository.save(namedJdbc, fileInfo);
    }

    /**
     * 获取文件信息
     */
    private FileInfo getFileInfo(String fileId) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return fileInfoRepository.findById(namedJdbc, fileId);
        } catch (Exception e) {
            log.error("查询文件信息失败: {}", fileId, e);
            return null;
        }
    }

    /**
     * 更新上传会话进度
     */
    private void updateUploadSessionProgress(EnhancedJdbcTemplate namedJdbc, UploadSession session) {
        boolean updated = fileUploadSessionRepository.updateProgress(namedJdbc, session.getUploadId(), session.getUploadedChunks().size());
        if (!updated) {
            log.warn("更新上传会话进度失败: uploadId={}", session.getUploadId());
        }
    }

    /**
     * 标记上传会话完成
     */
    private void markUploadSessionCompleted(EnhancedJdbcTemplate namedJdbc, String uploadId) {
        boolean marked = fileUploadSessionRepository.markCompleted(namedJdbc, uploadId);
        if (!marked) {
            log.warn("标记上传会话完成失败: uploadId={}", uploadId);
        }
    }

    /**
     * 标记上传会话失败
     */
    private void markUploadSessionFailed(EnhancedJdbcTemplate namedJdbc, String uploadId, String errorMessage) {
        boolean marked = fileUploadSessionRepository.markFailed(namedJdbc, uploadId, errorMessage);
        if (!marked) {
            log.warn("标记上传会话失败状态失败: uploadId={}", uploadId);
        }
    }

    /**
     * 创建分片上传结果
     */
    private ChunkUploadResult createChunkUploadResult(ChunkUploadRequest request, boolean success, String message) {
        ChunkUploadResult result = new ChunkUploadResult();
        result.setUploadId(request.getUploadId());
        result.setChunkIndex(request.getChunkIndex());
        result.setSuccess(success);
        result.setUploadTime(LocalDateTime.now());
        
        // 设置消息到相应的字段
        if (!success && message != null) {
            result.setErrorMessage(message);
        }
        
        return result;
    }

    /**
     * 获取分片大小
     */
    private long getChunkSize() {
        return systemProperties.getFile().getChunk().getDefaultChunkSize();
    }

    /**
     * 获取存储类型
     */
    private StorageType getStorageType() {
        return systemProperties.getFile().getStorageType();
    }

    /**
     * 获取存储策略
     */
    private FileStorageStrategy getStorageStrategy() {
        StorageType storageType = getStorageType();
        FileStorageStrategy strategy = storageStrategies.get(storageType);
        if (strategy == null) {
            throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE, 
                "不支持的存储类型: " + storageType
            );
        }
        return strategy;
    }

    /**
     * 上传会话信息
     */
    @Data
    private static class UploadSession {
        private String uploadId;
        private String originalName;
        private long totalSize;
        private long chunkSize;
        private int totalChunks;
        private StorageType storageType;
        private String tenantId;
        private String createdBy;
        private AccessPermission accessPermission;
        private final Map<Integer, ChunkInfo> uploadedChunks = new ConcurrentHashMap<>();
    }

    /**
     * 分片信息
     */
    @Data
    private static class ChunkInfo {
        private int chunkIndex;
        private int chunkSize;
        private String chunkHash;
        private byte[] chunkData;
        private LocalDateTime uploadTime;
    }
} 