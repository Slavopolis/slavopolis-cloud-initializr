package club.slavopolis.file.storage;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.constant.MetadataConstant;
import club.slavopolis.file.domain.FileMetadata;
import club.slavopolis.file.exception.FileOperationException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;



import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OSS存储策略实现
 * <p>
 * 基于阿里云OSS的文件存储实现
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
@ConditionalOnProperty(name = "biz.file.storage.type", havingValue = "OSS")
public class OssFileStorageStrategy implements FileStorageStrategy {

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
        return StorageType.OSS;
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
        log.debug("开始上传文件到OSS: {}", key);
        
        try {
            OSS ossClient = createOssClient();
            
            // 设置对象元数据
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(metadata.getFileSize());
            objectMetadata.setContentType(metadata.getContentType());
            objectMetadata.setContentMD5(metadata.getFileHash());
            
            // 设置自定义元数据
            if (StringUtils.hasText(metadata.getOriginalName())) {
                objectMetadata.addUserMetadata(MetadataConstant.ORIGINAL_NAME, metadata.getOriginalName());
            }
            if (StringUtils.hasText(metadata.getExtension())) {
                objectMetadata.addUserMetadata(MetadataConstant.EXTENSION, metadata.getExtension());
            }
            
            // 执行上传
            PutObjectRequest putObjectRequest = new PutObjectRequest(getBucketName(), key, inputStream, objectMetadata);
            ossClient.putObject(putObjectRequest);
            
            log.debug("文件上传成功: {}", key);
            return key;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "OSS文件上传失败: " + e.getMessage(), 
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
        log.debug("开始从OSS下载文件: {}", key);
        
        try {
            OSS ossClient = createOssClient();
            GetObjectRequest getObjectRequest = new GetObjectRequest(getBucketName(), key);
            OSSObject ossObject = ossClient.getObject(getObjectRequest);
            
            log.debug("文件下载成功: {}", key);
            return ossObject.getObjectContent();
            
        } catch (Exception e) {
            log.error("文件下载失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.DOWNLOAD_FAILED, 
                "OSS文件下载失败: " + e.getMessage(), 
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
        log.debug("开始删除OSS文件: {}", key);
        
        try {
            OSS ossClient = createOssClient();
            ossClient.deleteObject(getBucketName(), key);
            
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
            OSS ossClient = createOssClient();
            return ossClient.doesObjectExist(getBucketName(), key);
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
        log.debug("初始化OSS分片上传: {}", key);
        
        try {
            OSS ossClient = createOssClient();
            
            // 设置对象元数据
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(metadata.getContentType());
            
            // 设置自定义元数据
            if (StringUtils.hasText(metadata.getOriginalName())) {
                objectMetadata.addUserMetadata(MetadataConstant.ORIGINAL_NAME, metadata.getOriginalName());
            }
            if (StringUtils.hasText(metadata.getExtension())) {
                objectMetadata.addUserMetadata(MetadataConstant.EXTENSION, metadata.getExtension());
            }
            if (StringUtils.hasText(metadata.getFileHash())) {
                objectMetadata.addUserMetadata(MetadataConstant.FILE_HASH, metadata.getFileHash());
            }
            
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(getBucketName(), key, objectMetadata);
            InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
            String uploadId = result.getUploadId();
            
            // 缓存会话信息
            MultipartUploadSession session = new MultipartUploadSession(uploadId, key, metadata);
            uploadSessions.put(uploadId, session);
            
            log.debug("分片上传会话创建成功: {}", uploadId);
            return uploadId;
            
        } catch (Exception e) {
            log.error("初始化分片上传失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "初始化OSS分片上传失败: " + e.getMessage(), 
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
            
            OSS ossClient = createOssClient();
            
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(getBucketName());
            uploadPartRequest.setKey(session.key);
            uploadPartRequest.setUploadId(uploadId);
            // OSS part number 从1开始
            uploadPartRequest.setPartNumber(chunkIndex + 1);
            uploadPartRequest.setInputStream(inputStream);
            
            UploadPartResult result = ossClient.uploadPart(uploadPartRequest);
            PartETag partETag = result.getPartETag();
            
            // 记录分片信息
            String chunkId = chunkIndex + 1 + "_" + partETag.getETag();
            session.chunkIds.add(chunkId);
            session.partETags.add(partETag);
            
            log.debug("分片上传成功: {} - {}", uploadId, chunkIndex);
            return chunkId;
            
        } catch (Exception e) {
            log.error("分片上传失败: {} - {}", uploadId, chunkIndex, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "OSS分片上传失败: " + e.getMessage(), 
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
        log.debug("完成OSS分片上传: {}", uploadId);
        
        try {
            MultipartUploadSession session = getAndValidateSession(uploadId);
            
            // 验证所有分片都已上传
            if (session.chunkIds.size() != chunkIds.size()) {
                throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED, 
                    "分片数量不匹配: 期望=" + chunkIds.size() + ", 实际=" + session.chunkIds.size()
                );
            }
            
            OSS ossClient = createOssClient();
            CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
                getBucketName(), session.key, uploadId, session.partETags);
            ossClient.completeMultipartUpload(request);
            
            // 清理会话
            uploadSessions.remove(uploadId);
            
            log.debug("分片上传完成: {}", session.key);
            return session.key;
            
        } catch (Exception e) {
            log.error("完成分片上传失败: {}", uploadId, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "完成OSS分片上传失败: " + e.getMessage(), 
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
        log.debug("取消OSS分片上传: {}", uploadId);
        
        try {
            MultipartUploadSession session = uploadSessions.get(uploadId);
            if (session != null) {
                OSS ossClient = createOssClient();
                AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(
                    getBucketName(), session.key, uploadId);
                ossClient.abortMultipartUpload(request);
                
                uploadSessions.remove(uploadId);
                log.debug("分片上传取消成功: {}", uploadId);
            }
            return true;
            
        } catch (Exception e) {
            log.error("取消分片上传失败: {}", uploadId, e);
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
            OSS ossClient = createOssClient();
            ObjectMetadata objectMetadata = ossClient.getObjectMetadata(getBucketName(), key);
            
            FileMetadata metadata = new FileMetadata();
            metadata.setFileSize(objectMetadata.getContentLength());
            metadata.setContentType(objectMetadata.getContentType());
            metadata.setFileHash(objectMetadata.getContentMD5());
            
            if (objectMetadata.getLastModified() != null) {
                metadata.setCreateTime(objectMetadata.getLastModified().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                metadata.setLastUpdateTime(metadata.getCreateTime());
            }
            
            // 提取用户元数据
            Map<String, String> userMetadata = objectMetadata.getUserMetadata();
            if (userMetadata != null) {
                metadata.setOriginalName(userMetadata.get(MetadataConstant.ORIGINAL_NAME));
                metadata.setExtension(userMetadata.get(MetadataConstant.EXTENSION));
                String fileHash = userMetadata.get(MetadataConstant.FILE_HASH);
                if (StringUtils.hasText(fileHash)) {
                    metadata.setFileHash(fileHash);
                }
            }
            
            return metadata;
            
        } catch (Exception e) {
            log.error("获取文件元数据失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "获取OSS文件元数据失败: " + e.getMessage(), 
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
            // OSS不支持直接更新元数据，需要复制对象来更新
            OSS ossClient = createOssClient();
            
            // 准备新的元数据
            ObjectMetadata newMetadata = new ObjectMetadata();
            newMetadata.setContentType(metadata.getContentType());
            
            if (StringUtils.hasText(metadata.getOriginalName())) {
                newMetadata.addUserMetadata(MetadataConstant.ORIGINAL_NAME, metadata.getOriginalName());
            }
            if (StringUtils.hasText(metadata.getExtension())) {
                newMetadata.addUserMetadata(MetadataConstant.EXTENSION, metadata.getExtension());
            }
            if (StringUtils.hasText(metadata.getFileHash())) {
                newMetadata.addUserMetadata(MetadataConstant.FILE_HASH, metadata.getFileHash());
            }
            
            // 使用复制来更新元数据
            CopyObjectRequest copyRequest = new CopyObjectRequest(getBucketName(), key, getBucketName(), key);
            copyRequest.setNewObjectMetadata(newMetadata);
            ossClient.copyObject(copyRequest);

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
        log.debug("生成OSS预签名URL: {}, 方法: {}, 过期时间: {}", key, method, expiry);
        
        try {
            OSS ossClient = createOssClient();
            
            // 转换HTTP方法
            com.aliyun.oss.HttpMethod ossMethod = convertHttpMethod(method);
            
            // 计算过期时间
            Date expirationDate = Date.from(Instant.now().plus(expiry));
            
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(getBucketName(), key, ossMethod);
            request.setExpiration(expirationDate);
            URL url = ossClient.generatePresignedUrl(request);
            
            log.debug("预签名URL生成成功: {}", key);
            return url.toString();
            
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.UPLOAD_FAILED, 
                "生成OSS预签名URL失败: " + e.getMessage(), 
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
        log.debug("复制OSS文件: {} -> {}", sourceKey, targetKey);
        
        try {
            OSS ossClient = createOssClient();
            
            // 检查源文件是否存在
            if (!exists(sourceKey)) {
                throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND, 
                    "源文件不存在: " + sourceKey
                );
            }
            
            CopyObjectRequest copyRequest = new CopyObjectRequest(getBucketName(), sourceKey, getBucketName(), targetKey);
            ossClient.copyObject(copyRequest);
            
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
        log.debug("移动OSS文件: {} -> {}", sourceKey, targetKey);
        
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
            OSS ossClient = createOssClient();
            ObjectMetadata objectMetadata = ossClient.getObjectMetadata(getBucketName(), key);
            return objectMetadata.getContentLength();
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", key, e);
            throw new FileOperationException(
                FileConstants.FILE_NOT_FOUND, 
                "获取OSS文件大小失败: " + e.getMessage(), 
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
     * 创建OSS客户端
     */
    private OSS createOssClient() {
        CurrentSystemProperties.OssConfig ossConfig = systemProperties.getFile().getOss();
        
        if (!StringUtils.hasText(ossConfig.getEndpoint())) {
            throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE, 
                "OSS endpoint未配置"
            );
        }
        
        return new OSSClientBuilder().build(
            ossConfig.getEndpoint(), 
            ossConfig.getAccessKeyId(), 
            ossConfig.getAccessKeySecret()
        );
    }

    /**
     * 获取存储桶名称
     */
    private String getBucketName() {
        String bucketName = systemProperties.getFile().getOss().getBucketName();
        
        if (!StringUtils.hasText(bucketName)) {
            throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE, 
                "OSS bucket name未配置"
            );
        }
        
        return bucketName;
    }

    /**
     * 转换HTTP方法
     */
    private com.aliyun.oss.HttpMethod convertHttpMethod(HttpMethod method) {
        if (method == HttpMethod.GET) {
            return com.aliyun.oss.HttpMethod.GET;
        } else if (method == HttpMethod.PUT) {
            return com.aliyun.oss.HttpMethod.PUT;
        } else if (method == HttpMethod.POST) {
            return com.aliyun.oss.HttpMethod.POST;
        } else if (method == HttpMethod.DELETE) {
            return com.aliyun.oss.HttpMethod.DELETE;
        } else {
            throw new IllegalArgumentException("不支持的HTTP方法: " + method);
        }
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
         * 存储键值
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
         * 分片ETag列表
         */
        final List<PartETag> partETags = new ArrayList<>();

        MultipartUploadSession(String uploadId, String key, FileMetadata metadata) {
            this.uploadId = uploadId;
            this.key = key;
            this.metadata = metadata;
        }
    }
} 