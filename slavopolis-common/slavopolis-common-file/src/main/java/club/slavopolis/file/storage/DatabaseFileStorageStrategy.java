package club.slavopolis.file.storage;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.domain.FileMetadata;
import club.slavopolis.file.exception.FileOperationException;
import club.slavopolis.file.repository.FileContentRepository;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;
import club.slavopolis.persistence.jdbc.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库存储策略实现
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
@ConditionalOnProperty(name = "biz.file.storage.type", havingValue = "DATABASE")
public class DatabaseFileStorageStrategy implements FileStorageStrategy {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DataSourceTransactionManager transactionManager;
    private final TransactionDefinition defaultTransactionDefinition;
    private final CurrentSystemProperties systemProperties;
    private final FileContentRepository fileContentRepository;

    /**
     * 默认分片大小（1MB）
     */
    private static final int DEFAULT_CHUNK_SIZE = 1024 * 1024;

    /**
     * 分片上传会话缓存
     */
    private final Map<String, MultipartUploadSession> uploadSessions = new ConcurrentHashMap<>();

    /**
     * 获取存储类型
     *
     * @return 存储类型
     */
    @Override
    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }

    // ================================ 基础存储操作 ================================

    /**
     * 存储文件
     *
     * @param key         存储键值
     * @param inputStream 文件输入流
     * @param metadata    文件元数据
     * @return 存储路径或标识
     */
    @Override
    public String store(String key, InputStream inputStream, FileMetadata metadata) {
        log.debug("开始上传文件到数据库: {}", key);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();

        try {
            // 删除已存在的文件内容
            deleteFileContent(namedJdbc, key);
            
            // 分片读取并存储文件内容
            byte[] buffer = new byte[DEFAULT_CHUNK_SIZE];
            int chunkIndex = 0;
            int bytesRead;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] chunkData = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunkData, 0, bytesRead);
                
                // 计算分片哈希
                String chunkHash = DigestUtils.md5DigestAsHex(chunkData);
                md5.update(chunkData);
                
                // 插入分片数据
                boolean saved = fileContentRepository.saveChunk(namedJdbc, key, chunkIndex, chunkData, chunkHash);
                if (!saved) {
                    throw new FileOperationException(
                        FileConstants.UPLOAD_FAILED, 
                        "保存分片失败: chunk=" + chunkIndex
                    );
                }
                chunkIndex++;
            }
            
            log.debug("文件上传成功，共{}个分片: {}", chunkIndex, key);

            namedJdbc.commitTransaction(transactionStatus);
            return key;
            
        } catch (Exception e) {
            log.error("数据库文件上传失败: {}", key, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "数据库文件上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 获取文件
     *
     * @param key 存储键值
     * @return 文件输入流
     */
    @Override
    public InputStream retrieve(String key) {
        log.debug("开始从数据库下载文件: {}", key);
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
        
        try {
            // 查询所有分片，按分片索引排序
            List<byte[]> chunks = fileContentRepository.findAllChunks(namedJdbc, key);
            if (chunks.isEmpty()) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "文件不存在: " + key
                );
            }
            
            // 合并所有分片
            int totalSize = chunks.stream().mapToInt(chunk -> chunk.length).sum();
            byte[] fileData = new byte[totalSize];
            int offset = 0;
            
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, fileData, offset, chunk.length);
                offset += chunk.length;
            }
            
            log.debug("文件下载成功，共{}个分片，{}字节: {}", chunks.size(), totalSize, key);
            return new ByteArrayInputStream(fileData);
            
        } catch (FileOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库文件下载失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.DOWNLOAD_FAILED, 
                "数据库文件下载失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 删除文件
     *
     * @param key 存储键值
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean delete(String key) {
        log.debug("开始删除数据库文件: {}", key);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            deleteFileContent(namedJdbc, key);
            log.debug("文件删除成功: {}", key);
            namedJdbc.commitTransaction(transactionStatus);
            return true;
            
        } catch (Exception e) {
            log.error("数据库文件删除失败: {}", key, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param key 存储键值
     * @return 是否存在
     */
    @Override
    public boolean exists(String key) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return fileContentRepository.existsByFileId(namedJdbc, key);
        } catch (Exception e) {
            log.error("检查文件存在性失败: {}", key, e);
            return false;
        }
    }

    // ================================ 分片操作支持 ================================

    /**
     * 初始化分片上传
     *
     * @param key      存储键值
     * @param metadata 文件元数据
     * @return 上传会话ID
     */
    @Override
    public String initializeMultipartUpload(String key, FileMetadata metadata) {
        log.debug("初始化数据库分片上传: {}", key);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();

        try {
            String uploadId = generateUploadId();
            MultipartUploadSession session = new MultipartUploadSession(uploadId, key, metadata);
            uploadSessions.put(uploadId, session);

            // 先删除已存在的文件内容
            deleteFileContent(namedJdbc, key);

            log.debug("分片上传会话创建成功: {}", uploadId);
            namedJdbc.commitTransaction(transactionStatus);
            return uploadId;
        } catch (Exception e) {
            log.error("初始化数据库分片上传失败: {}", key, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED,
                "初始化数据库分片上传失败: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * 上传分片
     *
     * @param uploadId    上传会话ID
     * @param chunkIndex  分片索引
     * @param inputStream 分片输入流
     * @return 分片标识
     */
    @Override
    public String uploadChunk(String uploadId, int chunkIndex, InputStream inputStream) {
        log.debug("上传分片: {} - {}", uploadId, chunkIndex);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            MultipartUploadSession session = getAndValidateSession(uploadId);
            
            // 读取分片数据
            byte[] chunkData = inputStream.readAllBytes();
            String chunkHash = DigestUtils.md5DigestAsHex(chunkData);
            
            // 插入分片数据
            boolean saved = fileContentRepository.saveChunk(namedJdbc, session.key, chunkIndex, chunkData, chunkHash);
            if (!saved) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "保存分片失败: chunk=" + chunkIndex
                );
            }
            
            String chunkId = session.key + "_" + chunkIndex;
            session.chunkIds.add(chunkId);
            
            log.debug("分片上传成功: {} - {}", uploadId, chunkIndex);
            namedJdbc.commitTransaction(transactionStatus);
            return chunkId;
            
        } catch (Exception e) {
            log.error("分片上传失败: {} - {}", uploadId, chunkIndex, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "数据库分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 完成分片上传
     *
     * @param uploadId 上传会话ID
     * @param chunkIds 分片标识列表
     * @return 文件存储路径
     */
    @Override
    public String completeMultipartUpload(String uploadId, List<String> chunkIds) {
        log.debug("完成数据库分片上传: {}", uploadId);
        
        try {
            MultipartUploadSession session = getAndValidateSession(uploadId);
            
            // 验证所有分片都已上传
            if (session.chunkIds.size() != chunkIds.size()) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "分片数量不匹配: 期望=" + chunkIds.size() + ", 实际=" + session.chunkIds.size()
                );
            }
            
            // 清理会话
            uploadSessions.remove(uploadId);
            
            log.debug("分片上传完成: {}", session.key);
            return session.key;
            
        } catch (Exception e) {
            log.error("完成分片上传失败: {}", uploadId, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "完成数据库分片上传失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 取消分片上传
     *
     * @param uploadId 上传会话ID
     * @return 是否取消成功
     */
    @Override
    @Transactional
    public boolean abortMultipartUpload(String uploadId) {
        log.debug("取消数据库分片上传: {}", uploadId);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            MultipartUploadSession session = uploadSessions.get(uploadId);
            if (session != null) {
                // 删除已上传的分片
                deleteFileContent(namedJdbc, session.key);
                uploadSessions.remove(uploadId);
                log.debug("分片上传取消成功: {}", uploadId);
            }

            namedJdbc.commitTransaction(transactionStatus);
            return true;
            
        } catch (Exception e) {
            log.error("取消分片上传失败: {}", uploadId, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    // ================================ 元数据操作 ================================

    /**
     * 获取文件元数据
     *
     * @param key 存储键值
     * @return 文件元数据
     */
    @Override
    public FileMetadata getMetadata(String key) {
        try {
            String sql = """
                SELECT fi.original_name, fi.extension, fi.content_type,
                       fi.size, fi.hash, fi.created_time
                FROM file_info fi
                WHERE fi.file_id = :fileId AND fi.delete_flag = 0
                """;

            Map<String, Object> params = new HashMap<>();
            params.put("fileId", key);

            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return namedJdbc.queryForObject(sql, params, FileMetadata.class, systemProperties.isDebug());
            
        } catch (Exception e) {
            log.error("获取文件元数据失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "获取数据库文件元数据失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 更新文件元数据
     *
     * @param key      存储键值
     * @param metadata 新的元数据
     * @return 是否更新成功
     */
    @Override
    public boolean updateMetadata(String key, FileMetadata metadata) {
        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();

        try {
            String sql = """
                UPDATE file_info
                SET content_type = :contentType, file_hash = :fileHash, last_update_time = NOW()
                WHERE file_id = :fileId AND delete_flag = 0
                """;

            Map<String, Object> params = new HashMap<>();
            params.put("contentType", metadata.getContentType());
            params.put("fileHash", metadata.getFileHash());
            params.put("fileId", key);
                
            int updated = namedJdbc.update(sql, params, systemProperties.isDebug());
            namedJdbc.commitTransaction(transactionStatus);
            return updated > 0;
            
        } catch (Exception e) {
            log.error("更新文件元数据失败: {}", key, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    // ================================ 高级功能 ================================

    /**
     * 生成预签名URL
     *
     * @param key      存储键值
     * @param expiry   过期时间
     * @param method   HTTP方法（GET/PUT等）
     * @return 预签名URL
     */
    @Override
    public String generatePresignedUrl(String key, Duration expiry, HttpMethod method) {
        log.warn("数据库存储模式不支持预签名URL: {}", key);
        throw new FileOperationException(
            FileConstants.STORAGE_SERVICE_UNAVAILABLE, 
            "数据库存储模式不支持预签名URL功能"
        );
    }

    /**
     * 复制文件
     *
     * @param sourceKey 源文件键值
     * @param targetKey 目标文件键值
     * @return 是否复制成功
     */
    @Override
    public boolean copyFile(String sourceKey, String targetKey) {
        log.debug("复制数据库文件: {} -> {}", sourceKey, targetKey);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 检查源文件是否存在
            if (!exists(sourceKey)) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "源文件不存在: " + sourceKey
                );
            }
            
            // 复制文件内容
            boolean copied = fileContentRepository.copyContent(namedJdbc, sourceKey, targetKey, "SYSTEM");
            if (!copied) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "复制文件内容失败"
                );
            }
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("文件复制成功: {} -> {}", sourceKey, targetKey);
            return true;
            
        } catch (Exception e) {
            log.error("复制文件失败: {} -> {}", sourceKey, targetKey, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    /**
     * 移动文件
     *
     * @param sourceKey 源文件键值
     * @param targetKey 目标文件键值
     * @return 是否移动成功
     */
    @Override
    public boolean moveFile(String sourceKey, String targetKey) {
        log.debug("移动数据库文件: {} -> {}", sourceKey, targetKey);

        EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition
        );
        TransactionStatus transactionStatus = namedJdbc.getTransactionStatus();
        
        try {
            // 检查源文件是否存在
            if (!exists(sourceKey)) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "源文件不存在: " + sourceKey
                );
            }
            
            // 更新文件ID（相当于重命名）
            boolean moved = fileContentRepository.moveContent(namedJdbc, sourceKey, targetKey);
            if (!moved) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "移动文件内容失败"
                );
            }
            namedJdbc.commitTransaction(transactionStatus);
            log.debug("文件移动成功: {} -> {}", sourceKey, targetKey);
            return true;
            
        } catch (Exception e) {
            log.error("移动文件失败: {} -> {}", sourceKey, targetKey, e);
            namedJdbc.rollbackTransaction(transactionStatus);
            return false;
        }
    }

    /**
     * 获取文件大小
     *
     * @param key 存储键值
     * @return 文件大小（字节）
     */
    @Override
    public long getFileSize(String key) {
        try {
            EnhancedJdbcTemplate namedJdbc = new EnhancedJdbcTemplate(namedParameterJdbcTemplate);
            return fileContentRepository.getTotalSize(namedJdbc, key);
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "获取数据库文件大小失败: " + e.getMessage(), 
                e
            );
        }
    }

    // ================================ 私有方法 ================================

    /**
     * 获取并验证分片上传会话
     *
     * @param uploadId 上传会话ID
     * @return 分片上传会话
     * @throws FileOperationException 如果会话不存在
     */
    private MultipartUploadSession getAndValidateSession(String uploadId) {
        MultipartUploadSession session = uploadSessions.get(uploadId);
        if (session == null) {
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "分片上传会话不存在: " + uploadId
            );
        }
        return session;
    }

    /**
     * 删除文件内容（逻辑删除）
     *
     * @param namedJdbc NamedParameterJdbcTemplate
     * @param fileId 文件ID
     */
    private void deleteFileContent(EnhancedJdbcTemplate namedJdbc, String fileId) {
        boolean deleted = fileContentRepository.deleteByFileId(namedJdbc, fileId);
        if (!deleted) {
            log.warn("删除文件内容失败: fileId={}", fileId);
        }
    }

    /**
     * 生成上传ID
     */
    private String generateUploadId() {
        return "db_upload_" + System.currentTimeMillis() + "_" + Thread.currentThread().threadId();
    }

    /**
     * 分片上传会话
     */
    private static class MultipartUploadSession {

        /**
         * 上传ID
         */
        final String uploadId;

        /**
         * 文件名
         */
        final String key;

        /**
         * 文件元数据
         */
        final FileMetadata metadata;

        /**
         * 分片ID列表
         */
        final List<String> chunkIds = new java.util.ArrayList<>();

        MultipartUploadSession(String uploadId, String key, FileMetadata metadata) {
            this.uploadId = uploadId;
            this.key = key;
            this.metadata = metadata;
        }
    }
} 