package club.slavopolis.file.service;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileService;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.domain.FileMetadata;
import club.slavopolis.file.domain.request.ChunkUploadRequest;
import club.slavopolis.file.domain.request.FileListRequest;
import club.slavopolis.file.domain.request.FileUploadRequest;
import club.slavopolis.file.domain.result.ChunkUploadResult;
import club.slavopolis.file.domain.result.FileUploadResult;
import club.slavopolis.file.repository.FileInfoRepository;
import club.slavopolis.file.enums.FileStatus;
import club.slavopolis.file.enums.UploadMethod;
import club.slavopolis.file.exception.FileOperationException;
import club.slavopolis.file.util.FileUtils;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;
import club.slavopolis.persistence.jdbc.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件服务统一实现: 提供文件上传、下载、删除等核心功能的统一实现
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j  
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Map<StorageType, FileStorageStrategy> storageStrategies;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DataSourceTransactionManager transactionManager;
    private final TransactionDefinition defaultTransactionDefinition;
    private final CurrentSystemProperties systemProperties;
    private final MultipartUploadManager multipartUploadManager;
    private final FileInfoRepository fileInfoRepository;
    private final Tika tika = new Tika();

    // ================================ 基础操作 ================================

    @Override
    public FileUploadResult upload(FileUploadRequest request) {
        log.debug("开始上传文件: {}", request.getOriginalName());
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 验证文件
            validateFileUpload(request);
            
            // 2. 计算文件哈希值
            String fileHash = calculateFileHash(request.getInputStream());
            
            // 3. 检查是否可以秒传
            FileInfo existingFile = findFileByHash(fileHash);
            if (existingFile != null && isInstantUploadEnabled()) {
                namedJdbc.commitTransaction(transactionStatus);
                return createInstantUploadResult(existingFile);
            }
            
            // 4. 创建文件元数据
            FileMetadata metadata = createFileMetadata(request, fileHash);
            
            // 5. 生成存储键值
            String storageKey = generateStorageKey(request.getOriginalName());
            
            // 6. 上传到存储层
            FileStorageStrategy strategy = getStorageStrategy();
            strategy.store(storageKey, request.getInputStream(), metadata);
            
            // 7. 保存文件信息到数据库
            FileInfo fileInfo = saveFileInfo(namedJdbc, request, metadata, storageKey);
            
            // 8. 构建上传结果
            FileUploadResult result = new FileUploadResult();
            result.setFileInfo(fileInfo);
            result.setUploadMethod(UploadMethod.NORMAL);
            result.setFileId(fileInfo.getFileId());
            result.setUploadCompleteTime(LocalDateTime.now());
            result.setSuccess(true);
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("文件上传成功: {}", fileInfo.getFileId());
            return result;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", request.getOriginalName(), e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "文件上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    @Override
    public InputStream download(String fileId) {
        log.debug("开始下载文件: {}", fileId);
        
        try {
            // 1. 查询文件信息
            FileInfo fileInfo = getFileInfo(fileId);
            if (fileInfo == null) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "文件不存在: " + fileId
                );
            }
            
            // 2. 检查文件状态和权限
            validateFileAccess(fileInfo);
            
            // 3. 从存储层下载文件
            FileStorageStrategy strategy = getStorageStrategy(fileInfo.getStorageType());
            InputStream inputStream = strategy.retrieve(fileInfo.getStorageKey());
            
            // 4. 更新访问记录
            updateFileAccessInfo(fileInfo);
            
            log.debug("文件下载成功: {}", fileId);
            return inputStream;
            
        } catch (FileOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            throw new FileOperationException(
                FileConstants.DOWNLOAD_FAILED, 
                "文件下载失败: " + e.getMessage(), 
                e
            );
        }
    }

    @Override
    public boolean delete(String fileId) {
        log.debug("开始删除文件: {}", fileId);
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 查询文件信息
            FileInfo fileInfo = getFileInfo(fileId);
            if (fileInfo == null) {
                log.debug("文件不存在，跳过删除: {}", fileId);
                namedJdbc.commitTransaction(transactionStatus);
                return true;
            }
            
            // 2. 检查删除权限
            validateFileDelete(fileInfo);
            
            // 3. 从存储层删除文件
            FileStorageStrategy strategy = getStorageStrategy(fileInfo.getStorageType());
            strategy.delete(fileInfo.getStorageKey());
            
            // 4. 更新数据库记录（逻辑删除）
            markFileAsDeleted(namedJdbc, fileId);
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("文件删除成功: {}", fileId);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileId, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    // ================================ 分片操作 ================================

    @Override
    public String initializeMultipartUpload(FileUploadRequest request) {
        log.debug("初始化分片上传: {}", request.getOriginalName());
        
        try {
            // 1. 验证文件
            validateFileUpload(request);
            
            // 2. 创建文件元数据
            String fileHash = "temp_" + UUID.randomUUID();
            FileMetadata metadata = createFileMetadata(request, fileHash);
            
            // 3. 生成存储键值
            String storageKey = generateStorageKey(request.getOriginalName());
            
            // 4. 初始化分片上传
            FileStorageStrategy strategy = getStorageStrategy();
            String uploadId = strategy.initializeMultipartUpload(storageKey, metadata);
            
            log.debug("分片上传初始化成功: {}", uploadId);
            return uploadId;
            
        } catch (Exception e) {
            log.error("初始化分片上传失败: {}", request.getOriginalName(), e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "初始化分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    @Override
    public ChunkUploadResult uploadChunk(ChunkUploadRequest request) {
        return multipartUploadManager.uploadChunk(request);
    }

    @Override
    public FileUploadResult completeMultipartUpload(String uploadId) {
        return multipartUploadManager.completeUpload(uploadId);
    }

    @Override
    public boolean abortMultipartUpload(String uploadId) {
        try {
            multipartUploadManager.abortUpload(uploadId);
            return true;
        } catch (Exception e) {
            log.error("取消分片上传失败: {}", uploadId, e);
            return false;
        }
    }

    // ================================ 元数据操作 ================================

    @Override
    public FileInfo getFileInfo(String fileId) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return fileInfoRepository.findById(namedJdbc, fileId);
        } catch (Exception e) {
            log.error("查询文件信息失败: {}", fileId, e);
            return null;
        }
    }

    @Override
    public List<FileInfo> listFiles(FileListRequest request) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return fileInfoRepository.findByRequest(namedJdbc, request);
        } catch (Exception e) {
            log.error("查询文件列表失败", e);
            throw new FileOperationException(
                FileConstants.DOWNLOAD_FAILED, 
                "查询文件列表失败: " + e.getMessage(), 
                e
            );
        }
    }

    // ================================ 高级功能 ================================

    @Override
    public String generateDownloadUrl(String fileId, Duration expiry) {
        log.debug("生成下载链接: {}", fileId);
        
        try {
            FileInfo fileInfo = getFileInfo(fileId);
            if (fileInfo == null) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "文件不存在: " + fileId
                );
            }
            
            FileStorageStrategy strategy = getStorageStrategy(fileInfo.getStorageType());
            return strategy.generatePresignedUrl(fileInfo.getStorageKey(), expiry, HttpMethod.GET);
            
        } catch (Exception e) {
            log.error("生成下载链接失败: {}", fileId, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "生成下载链接失败: " + e.getMessage(), 
                e
            );
        }
    }

    @Override
    public String checkFileExists(String fileHash) {
        try {
            FileInfo existingFile = findFileByHash(fileHash);
            return existingFile != null ? existingFile.getFileId() : null;
        } catch (Exception e) {
            log.error("检查文件存在性失败: {}", fileHash, e);
            return null;
        }
    }

    @Override
    public String copyFile(String sourceFileId, String targetPath) {
        log.debug("复制文件: {} -> {}", sourceFileId, targetPath);
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 查询源文件信息
            FileInfo sourceFile = getFileInfo(sourceFileId);
            if (sourceFile == null) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "源文件不存在: " + sourceFileId
                );
            }
            
            // 2. 生成新的文件ID和存储键
            String newFileId = UUID.randomUUID().toString().replace("-", "");
            String newStorageKey = generateStorageKey(sourceFile.getOriginalName());
            
            // 3. 在存储层复制文件
            FileStorageStrategy strategy = getStorageStrategy(sourceFile.getStorageType());
            boolean copied = strategy.copyFile(sourceFile.getStorageKey(), newStorageKey);
            
            if (!copied) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "存储层文件复制失败"
                );
            }
            
            // 4. 复制文件信息到数据库
            boolean fileInfoCopied = fileInfoRepository.copyFileInfo(namedJdbc, sourceFileId, newFileId, newStorageKey, "SYSTEM");
            if (!fileInfoCopied) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "复制文件信息失败"
                );
            }
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("文件复制成功: {} -> {}", sourceFileId, newFileId);
            return newFileId;
            
        } catch (Exception e) {
            log.error("复制文件失败: {} -> {}", sourceFileId, targetPath, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "复制文件失败: " + e.getMessage(), 
                e
            );
        }
    }

    @Override
    public boolean moveFile(String fileId, String targetPath) {
        log.debug("移动文件: {} -> {}", fileId, targetPath);
        
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 1. 查询文件信息
            FileInfo fileInfo = getFileInfo(fileId);
            if (fileInfo == null) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "文件不存在: " + fileId
                );
            }
            
            // 2. 生成新的存储键
            String newStorageKey = generateStorageKey(fileInfo.getOriginalName());
            
            // 3. 在存储层移动文件
            FileStorageStrategy strategy = getStorageStrategy(fileInfo.getStorageType());
            boolean moved = strategy.moveFile(fileInfo.getStorageKey(), newStorageKey);
            
            if (!moved) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "存储层文件移动失败"
                );
            }
            
            // 4. 更新数据库中的存储键
            boolean updated = fileInfoRepository.updateStorageKey(namedJdbc, fileId, newStorageKey);
            if (!updated) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "更新文件存储键失败"
                );
            }
            
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("文件移动成功: {} -> {}", fileId, targetPath);
            return true;
            
        } catch (Exception e) {
            log.error("移动文件失败: {} -> {}", fileId, targetPath, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    // ================================ 私有方法 ================================

    /**
     * 验证文件上传请求
     */
    private void validateFileUpload(FileUploadRequest request) {
        if (request.getInputStream() == null) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "文件内容不能为空"
            );
        }
        
        if (!StringUtils.hasText(request.getOriginalName())) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "文件名不能为空"
            );
        }
        
        // 检查文件大小
        if (request.getFileSize() > systemProperties.getFile().getSecurity().getMaxFileSize()) {
            throw new FileOperationException(
                FileConstants.FILE_TOO_LARGE, 
                "文件大小超出限制"
            );
        }
        
        // 检查文件类型
        String extension = FileUtils.getFileExtension(request.getOriginalName());
        if (!isAllowedFileType(extension)) {
            throw new FileOperationException(
                FileConstants.FILE_TYPE_NOT_ALLOWED, 
                "不允许的文件类型: " + extension
            );
        }
    }

    /**
     * 计算文件哈希值
     */
    private String calculateFileHash(InputStream inputStream) {
        try {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (Exception e) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "计算文件哈希值失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 根据哈希值查找文件
     */
    private FileInfo findFileByHash(String fileHash) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return fileInfoRepository.findByHash(namedJdbc, fileHash);
        } catch (Exception e) {
            log.error("根据哈希值查找文件失败: {}", fileHash, e);
            return null;
        }
    }

    /**
     * 创建秒传结果
     */
    private FileUploadResult createInstantUploadResult(FileInfo existingFile) {
        FileUploadResult result = new FileUploadResult();
        result.setFileInfo(existingFile);
        result.setUploadMethod(UploadMethod.INSTANT);
        result.setFileId(existingFile.getFileId());
        result.setUploadCompleteTime(LocalDateTime.now());
        result.setSuccess(true);
        result.setIsInstantUpload(true);
        
        log.debug("文件秒传成功: {}", existingFile.getFileId());
        return result;
    }

    /**
     * 创建文件元数据
     */
    private FileMetadata createFileMetadata(FileUploadRequest request, String fileHash) {
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName(request.getOriginalName());
        metadata.setFileSize(request.getFileSize());
        metadata.setFileHash(fileHash);
        metadata.setExtension(FileUtils.getFileExtension(request.getOriginalName()));
        
        // 检测MIME类型
        try {
            String contentType = tika.detect(request.getInputStream(), request.getOriginalName());
            metadata.setContentType(contentType);
        } catch (Exception e) {
            log.warn("检测文件MIME类型失败: {}", request.getOriginalName(), e);
            metadata.setContentType("application/octet-stream");
        }
        
        metadata.setCreateTime(LocalDateTime.now());
        metadata.setLastUpdateTime(LocalDateTime.now());
        
        return metadata;
    }

    /**
     * 生成存储键值
     */
    private String generateStorageKey(String originalName) {
        String extension = FileUtils.getFileExtension(originalName);
        String fileName = UUID.randomUUID().toString();
        return StringUtils.hasText(extension) ? fileName + "." + extension : fileName;
    }

    /**
     * 保存文件信息到数据库
     */
    private FileInfo saveFileInfo(EnhancedJdbcTemplate namedJdbc, FileUploadRequest request, 
                                  FileMetadata metadata, String storageKey) {
        // 构建FileInfo对象
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(request.getOriginalName());
        fileInfo.setFileSize(request.getFileSize());
        fileInfo.setContentType(metadata.getContentType());
        fileInfo.setFileHash(metadata.getFileHash());
        fileInfo.setExtension(metadata.getExtension());
        fileInfo.setStorageType(getStorageStrategy().getStorageType());
        fileInfo.setStorageKey(storageKey);
        fileInfo.setStatus(FileStatus.ACTIVE);
        fileInfo.setAccessPermission(request.getAccessPermission());
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setTenantId(request.getTenantId());
        fileInfo.setDownloadCount(0);
        fileInfo.setCreatedBy(request.getCreatedBy());
        
        // 使用Repository保存
        return fileInfoRepository.save(namedJdbc, fileInfo);
    }

    /**
     * 获取存储策略
     */
    private FileStorageStrategy getStorageStrategy() {
        StorageType storageType = systemProperties.getFile().getStorageType();
        return getStorageStrategy(storageType);
    }

    /**
     * 根据存储类型获取存储策略
     */
    private FileStorageStrategy getStorageStrategy(StorageType storageType) {
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
     * 验证文件访问权限
     */
    private void validateFileAccess(FileInfo fileInfo) {
        if (fileInfo.getStatus() != FileStatus.ACTIVE) {
            throw new FileOperationException(
                FileConstants.PERMISSION_DENIED, 
                "文件状态不允许访问: " + fileInfo.getStatus()
            );
        }
    }

    /**
     * 验证文件删除权限
     */
    private void validateFileDelete(FileInfo fileInfo) {
        if (fileInfo.getStatus() == FileStatus.DELETED) {
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "文件已被删除"
            );
        }
    }

    /**
     * 更新文件访问信息
     */
    private void updateFileAccessInfo(FileInfo fileInfo) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            fileInfoRepository.updateAccessInfo(namedJdbc, fileInfo.getFileId(), LocalDateTime.now());
        } catch (Exception e) {
            log.warn("更新文件访问信息失败: {}", fileInfo.getFileId(), e);
        }
    }

    /**
     * 标记文件为已删除
     */
    private void markFileAsDeleted(EnhancedJdbcTemplate namedJdbc, String fileId) {
        fileInfoRepository.markAsDeleted(namedJdbc, fileId);
    }

    /**
     * 检查是否允许的文件类型
     */
    private boolean isAllowedFileType(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        String lowerExtension = extension.toLowerCase();
        
        // 检查危险文件类型
        List<String> blockedTypes = systemProperties.getFile().getSecurity().getBlockedFileTypes();
        for (String dangerous : blockedTypes) {
            if (dangerous.equals(lowerExtension)) {
                return false;
            }
        }
        
        // 检查允许的文件类型
        List<String> allowedTypes = systemProperties.getFile().getSecurity().getAllowedFileTypes();
        return allowedTypes.contains(lowerExtension);
    }

    /**
     * 检查是否启用秒传功能
     */
    private boolean isInstantUploadEnabled() {
        return systemProperties.getFile().getStorage().isEnableDeduplication();
    }
} 