package club.slavopolis.file.storage;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.constant.MetadataConstant;
import club.slavopolis.file.domain.FileMetadata;
import club.slavopolis.file.exception.FileOperationException;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.FileCopyUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * MinIO存储策略实现
 * <p>
 * 基于MinIO的文件存储实现
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
@ConditionalOnProperty(name = "biz.file.storage.type", havingValue = "MINIO")
public class MinioFileStorageStrategy implements FileStorageStrategy {

    private final CurrentSystemProperties systemProperties;

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
        return StorageType.MINIO;
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
        log.debug("开始上传文件到MinIO: {}", key);
        
        try {
            MinioClient minioClient = createMinioClient();
            
            // 准备用户元数据
            Map<String, String> userMetadata = new HashMap<>();
            if (StringUtils.hasText(metadata.getOriginalName())) {
                userMetadata.put(MetadataConstant.ORIGINAL_NAME, metadata.getOriginalName());
            }
            if (StringUtils.hasText(metadata.getExtension())) {
                userMetadata.put(MetadataConstant.EXTENSION, metadata.getExtension());
            }
            if (StringUtils.hasText(metadata.getFileHash())) {
                userMetadata.put(MetadataConstant.FILE_HASH, metadata.getFileHash());
            }
            
            // 执行上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .stream(inputStream, metadata.getFileSize(), -1)
                .contentType(metadata.getContentType())
                .userMetadata(userMetadata)
                .build();
            
            minioClient.putObject(putObjectArgs);
            
            log.debug("文件上传成功: {}", key);
            return key;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "MinIO文件上传失败: " + e.getMessage(), 
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
        log.debug("开始从MinIO下载文件: {}", key);
        
        try {
            MinioClient minioClient = createMinioClient();
            
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .build();
            
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            
            log.debug("文件下载成功: {}", key);
            return inputStream;
            
        } catch (Exception e) {
            log.error("文件下载失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.DOWNLOAD_FAILED, 
                "MinIO文件下载失败: " + e.getMessage(), 
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
    public boolean delete(String key) {
        log.debug("开始删除MinIO文件: {}", key);
        
        try {
            MinioClient minioClient = createMinioClient();
            
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .build();
            
            minioClient.removeObject(removeObjectArgs);
            
            log.debug("文件删除成功: {}", key);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", key, e);
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
            MinioClient minioClient = createMinioClient();
            
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .build();
            
            minioClient.statObject(statObjectArgs);
            return true;
            
        } catch (Exception e) {
            log.debug("文件不存在或检查失败: {}", key);
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
        log.debug("初始化MinIO分片上传: {}", key);
        
        try {
            String uploadId = generateUploadId();
            
            // 创建分片上传会话
            MultipartUploadSession session = new MultipartUploadSession(uploadId, key, metadata);
            uploadSessions.put(uploadId, session);
            
            // 创建临时文件用于合并分片
            session.tempFilePath = Files.createTempFile("minio_upload_" + uploadId, ".tmp");
            
            log.debug("分片上传会话创建成功: {}", uploadId);
            return uploadId;
            
        } catch (Exception e) {
            log.error("初始化分片上传失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "初始化MinIO分片上传失败: " + e.getMessage(), 
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
        
        try {
            MultipartUploadSession session = getAndValidateSession(uploadId);
            
            // 将分片写入临时文件
            try (FileOutputStream fos = new FileOutputStream(session.tempFilePath.toFile(), true)) {
                FileCopyUtils.copy(inputStream, fos);
            }
            
            // 记录分片使用索引作为标识
            String chunkId = String.valueOf(chunkIndex);
            session.chunkIds.add(chunkId);
            
            log.debug("分片写入临时文件成功: {} - {}", uploadId, chunkIndex);
            return chunkId;
            
        } catch (Exception e) {
            log.error("分片上传失败: {} - {}", uploadId, chunkIndex, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "MinIO分片上传失败: " + e.getMessage(), 
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
        log.debug("完成MinIO分片上传: {}", uploadId);
        
        try {
            MultipartUploadSession session = getAndValidateSession(uploadId);
            
            // 验证所有分片都已上传
            if (session.chunkIds.size() != chunkIds.size()) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "分片数量不匹配: 期望=" + chunkIds.size() + ", 实际=" + session.chunkIds.size()
                );
            }
            
            MinioClient minioClient = createMinioClient();
            
            // 使用临时文件一次性上传到MinIO
            try (FileInputStream fis = new FileInputStream(session.tempFilePath.toFile())) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(session.key)
                    .stream(fis, session.tempFilePath.toFile().length(), -1)
                    .contentType(session.metadata.getContentType())
                    .build();
                
                minioClient.putObject(putObjectArgs);
            }
            
            // 清理临时文件和会话
            Files.deleteIfExists(session.tempFilePath);
            uploadSessions.remove(uploadId);
            
            log.debug("分片上传完成: {}", session.key);
            return session.key;
            
        } catch (Exception e) {
            log.error("完成分片上传失败: {}", uploadId, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "完成MinIO分片上传失败: " + e.getMessage(), 
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
    public boolean abortMultipartUpload(String uploadId) {
        log.debug("取消MinIO分片上传: {}", uploadId);
        
        try {
            MultipartUploadSession session = uploadSessions.get(uploadId);
            if (session != null) {
                // 清理临时文件
                if (session.tempFilePath != null) {
                    Files.deleteIfExists(session.tempFilePath);
                }
                
                // 清理会话
                uploadSessions.remove(uploadId);
                
                log.debug("分片上传取消成功: {}", uploadId);
            }
            
            return true;
            
        } catch (Exception e) {
            log.warn("取消分片上传失败: {}", uploadId, e);
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
            MinioClient minioClient = createMinioClient();
            
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .build();
            
            StatObjectResponse response = minioClient.statObject(statObjectArgs);
            
            FileMetadata metadata = new FileMetadata();
            metadata.setFileSize(response.size());
            metadata.setContentType(response.contentType());
            metadata.setCreateTime(response.lastModified().toLocalDateTime());
            metadata.setLastUpdateTime(response.lastModified().toLocalDateTime());
            
            // 提取用户元数据
            Map<String, String> userMetadata = response.userMetadata();
            if (userMetadata != null) {
                metadata.setOriginalName(userMetadata.get(MetadataConstant.ORIGINAL_NAME));
                metadata.setExtension(userMetadata.get(MetadataConstant.EXTENSION));
                metadata.setFileHash(userMetadata.get(MetadataConstant.FILE_HASH));
            }
            
            return metadata;
            
        } catch (Exception e) {
            log.error("获取文件元数据失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "获取MinIO文件元数据失败: " + e.getMessage(), 
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
        try {
            // MinIO不支持直接更新元数据，需要复制对象来更新
            MinioClient minioClient = createMinioClient();
            
            // 准备新的用户元数据
            Map<String, String> userMetadata = new HashMap<>();
            if (StringUtils.hasText(metadata.getOriginalName())) {
                userMetadata.put(MetadataConstant.ORIGINAL_NAME, metadata.getOriginalName());
            }
            if (StringUtils.hasText(metadata.getExtension())) {
                userMetadata.put(MetadataConstant.EXTENSION, metadata.getExtension());
            }
            if (StringUtils.hasText(metadata.getFileHash())) {
                userMetadata.put(MetadataConstant.FILE_HASH, metadata.getFileHash());
            }
            
            // 使用复制来更新元数据
            CopySource copySource = CopySource.builder()
                .bucket(getBucketName())
                .object(key)
                .build();
            
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .source(copySource)
                .headers(userMetadata)
                .metadataDirective(io.minio.Directive.REPLACE)
                .build();
            
            minioClient.copyObject(copyArgs);
            return true;
            
        } catch (Exception e) {
            log.error("更新文件元数据失败: {}", key, e);
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
        log.debug("生成MinIO预签名URL: {}, 方法: {}, 过期时间: {}", key, method, expiry);
        
        try {
            MinioClient minioClient = createMinioClient();
            
            // 转换HTTP方法
            Method minioMethod = convertHttpMethod(method);
            
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(minioMethod)
                .bucket(getBucketName())
                .object(key)
                .expiry((int) expiry.toSeconds())
                .build();
            
            String url = minioClient.getPresignedObjectUrl(args);
            
            log.debug("预签名URL生成成功: {}", key);
            return url;
            
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "生成MinIO预签名URL失败: " + e.getMessage(), 
                e
            );
        }
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
        log.debug("复制MinIO文件: {} -> {}", sourceKey, targetKey);
        
        try {
            MinioClient minioClient = createMinioClient();
            
            // 检查源文件是否存在
            if (!exists(sourceKey)) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "源文件不存在: " + sourceKey
                );
            }
            
            CopySource copySource = CopySource.builder()
                .bucket(getBucketName())
                .object(sourceKey)
                .build();
            
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                .bucket(getBucketName())
                .object(targetKey)
                .source(copySource)
                .build();
            
            minioClient.copyObject(copyArgs);
            
            log.debug("文件复制成功: {} -> {}", sourceKey, targetKey);
            return true;
            
        } catch (Exception e) {
            log.error("复制文件失败: {} -> {}", sourceKey, targetKey, e);
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
        log.debug("移动MinIO文件: {} -> {}", sourceKey, targetKey);
        
        try {
            // 先复制文件
            if (!copyFile(sourceKey, targetKey)) {
                return false;
            }
            
            // 再删除源文件
            return delete(sourceKey);
            
        } catch (Exception e) {
            log.error("移动文件失败: {} -> {}", sourceKey, targetKey, e);
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
            MinioClient minioClient = createMinioClient();
            
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket(getBucketName())
                .object(key)
                .build();
            
            StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs);
            return statObjectResponse.size();
            
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "获取MinIO文件大小失败: " + e.getMessage(), 
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
     * 创建MinIO客户端
     */
    private MinioClient createMinioClient() {
        CurrentSystemProperties.MinioConfig minioConfig = systemProperties.getFile().getMinio();
        
        if (!StringUtils.hasText(minioConfig.getEndpoint())) {
            throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE, 
                "MinIO endpoint未配置"
            );
        }
        
        MinioClient.Builder builder = MinioClient.builder()
            .endpoint(minioConfig.getEndpoint())
            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey());
        
        // 如果配置了区域，则设置区域
        if (StringUtils.hasText(minioConfig.getRegion())) {
            builder.region(minioConfig.getRegion());
        }
        
        return builder.build();
    }

    /**
     * 获取存储桶名称
     */
    private String getBucketName() {
        String bucketName = systemProperties.getFile().getMinio().getBucketName();
        
        if (!StringUtils.hasText(bucketName)) {
            throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE, 
                "MinIO bucket name未配置"
            );
        }
        
        return bucketName;
    }

    /**
     * 转换HTTP方法
     */
    private Method convertHttpMethod(HttpMethod method) {
        if (method == HttpMethod.GET) {
            return Method.GET;
        } else if (method == HttpMethod.PUT) {
            return Method.PUT;
        } else if (method == HttpMethod.POST) {
            return Method.POST;
        } else if (method == HttpMethod.DELETE) {
            return Method.DELETE;
        } else {
            throw new IllegalArgumentException("不支持的HTTP方法: " + method);
        }
    }

    /**
     * 生成上传ID
     */
    private String generateUploadId() {
        return "minio_upload_" + System.currentTimeMillis() + "_" + Thread.currentThread().threadId();
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
         * 文件键值
         */
        final String key;

        /**
         * 文件元数据
         */
        final FileMetadata metadata;

        /**
         * 分片ID列表
         */
        final List<String> chunkIds = new ArrayList<>();

        /**
         * 临时文件路径（用于合并分片）
         */
        Path tempFilePath;

        MultipartUploadSession(String uploadId, String key, FileMetadata metadata) {
            this.uploadId = uploadId;
            this.key = key;
            this.metadata = metadata;
        }
    }
} 