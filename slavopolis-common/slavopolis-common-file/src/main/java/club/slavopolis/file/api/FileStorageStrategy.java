package club.slavopolis.file.api;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.file.domain.FileMetadata;
import org.springframework.http.HttpMethod;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

/**
 * 文件存储策略接口
 * <p>
 * 定义不同存储类型（OSS、MinIO、Database、Local）的统一接口
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface FileStorageStrategy {

    /**
     * 获取存储类型
     *
     * @return 存储类型
     */
    StorageType getStorageType();

    // ================================ 基础存储操作 ================================

    /**
     * 存储文件
     *
     * @param key         存储键值
     * @param inputStream 文件输入流
     * @param metadata    文件元数据
     * @return 存储路径或标识
     */
    String store(String key, InputStream inputStream, FileMetadata metadata);

    /**
     * 获取文件
     *
     * @param key 存储键值
     * @return 文件输入流
     */
    InputStream retrieve(String key);

    /**
     * 删除文件
     *
     * @param key 存储键值
     * @return 是否删除成功
     */
    boolean delete(String key);

    /**
     * 检查文件是否存在
     *
     * @param key 存储键值
     * @return 是否存在
     */
    boolean exists(String key);

    // ================================ 分片操作支持 ================================

    /**
     * 初始化分片上传
     *
     * @param key      存储键值
     * @param metadata 文件元数据
     * @return 上传会话ID
     */
    String initializeMultipartUpload(String key, FileMetadata metadata);

    /**
     * 上传分片
     *
     * @param uploadId    上传会话ID
     * @param chunkIndex  分片索引
     * @param inputStream 分片输入流
     * @return 分片标识
     */
    String uploadChunk(String uploadId, int chunkIndex, InputStream inputStream);

    /**
     * 完成分片上传
     *
     * @param uploadId 上传会话ID
     * @param chunkIds 分片标识列表
     * @return 文件存储路径
     */
    String completeMultipartUpload(String uploadId, List<String> chunkIds);

    /**
     * 取消分片上传
     *
     * @param uploadId 上传会话ID
     * @return 是否取消成功
     */
    boolean abortMultipartUpload(String uploadId);

    // ================================ 元数据操作 ================================

    /**
     * 获取文件元数据
     *
     * @param key 存储键值
     * @return 文件元数据
     */
    FileMetadata getMetadata(String key);

    /**
     * 更新文件元数据
     *
     * @param key      存储键值
     * @param metadata 新的元数据
     * @return 是否更新成功
     */
    boolean updateMetadata(String key, FileMetadata metadata);

    // ================================ 高级功能 ================================

    /**
     * 生成预签名URL
     *
     * @param key      存储键值
     * @param expiry   过期时间
     * @param method   HTTP方法（GET/PUT等）
     * @return 预签名URL
     */
    String generatePresignedUrl(String key, Duration expiry, HttpMethod method);

    /**
     * 复制文件
     *
     * @param sourceKey 源文件键值
     * @param targetKey 目标文件键值
     * @return 是否复制成功
     */
    boolean copyFile(String sourceKey, String targetKey);

    /**
     * 移动文件
     *
     * @param sourceKey 源文件键值
     * @param targetKey 目标文件键值
     * @return 是否移动成功
     */
    boolean moveFile(String sourceKey, String targetKey);

    /**
     * 获取文件大小
     *
     * @param key 存储键值
     * @return 文件大小（字节）
     */
    long getFileSize(String key);
} 