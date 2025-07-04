package club.slavopolis.file.storage;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.domain.FileMetadata;
import club.slavopolis.file.exception.FileOperationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地文件存储策略实现
 * <p>
 * 基于本地磁盘的文件存储实现
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
@ConditionalOnProperty(name = "biz.file.storage.type", havingValue = "LOCAL")
public class LocalFileStorageStrategy implements FileStorageStrategy {

    private final CurrentSystemProperties systemProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        return StorageType.LOCAL;
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
        log.debug("开始上传文件到本地存储: {}", key);

        try {
            // 构建完整的文件路径
            Path filePath = buildFilePath(key);

            // 确保目录存在, 如果不存在则创建
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.debug("创建目录: {}", parentDir);
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                FileCopyUtils.copy(inputStream, outputStream);
            }

            // 保存元数据
            saveMetadata(key, metadata);

            log.debug("文件上传成功: {}", filePath);
            return key;
        } catch (Exception e) {
            log.error("本地文件上传失败: {}", key, e);
            throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED,
                    "本地文件上传失败: " + e.getMessage(),
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
        log.debug("开始从本地存储下载文件: {}", key);

        try {
            Path filePath = buildFilePath(key);
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                throw new FileOperationException(
                        FileConstants.FILE_NOT_FOUND,
                        "文件不存在: " + key
                );
            }

            InputStream inputStream = new FileInputStream(file);
            log.debug("文件下载成功: {}", filePath);
            return inputStream;
        } catch (FileOperationException e) {
            log.error("本地文件下载失败: {}", key, e);
            throw e;
        } catch (Exception e) {
            log.error("本地文件下载失败: {}", key, e);
            throw new FileOperationException(
                    FileConstants.DOWNLOAD_FAILED,
                    "本地文件下载失败: " + e.getMessage(),
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
        log.debug("开始删除本地文件: {}", key);

        try {
            Path filePath = buildFilePath(key);
            Path metadataPath = buildMetadataPath(key);

            boolean fileDeleted = true;
            boolean metadataDeleted = true;

            // 删除文件
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                fileDeleted = safeDeleteFile(filePath, "文件");
            }

            // 删除元数据
            if (Files.exists(metadataPath) && Files.isRegularFile(metadataPath)) {
                metadataDeleted = safeDeleteFile(metadataPath, "元数据");
            }

            return fileDeleted && metadataDeleted;

        } catch (Exception e) {
            log.error("本地文件删除失败: {}", key, e);
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
            Path filePath = buildFilePath(key);
            File file = filePath.toFile();
            return file.exists() && file.isFile();
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
        log.debug("初始化本地分片上传: {}", key);

        String uploadId = generateUploadId();
        MultipartUploadSession session = new MultipartUploadSession(uploadId, key, metadata);
        uploadSessions.put(uploadId, session);

        // 创建临时目录
        Path tempDir = buildTempDir(uploadId);
        try {
            Files.createDirectories(tempDir);
            log.debug("分片上传临时目录创建成功: {}", tempDir);
        } catch (IOException e) {
            log.error("创建分片上传临时目录失败: {}", tempDir, e);
            throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED,
                    "创建分片上传临时目录失败: " + e.getMessage(),
                    e
            );
        }

        log.debug("分片上传会话创建成功: {}", uploadId);
        return uploadId;
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

            // 构建分片文件路径
            Path chunkPath = buildTempDir(uploadId).resolve("chunk_" + chunkIndex);

            // 写入分片文件
            try (FileOutputStream outputStream = new FileOutputStream(chunkPath.toFile())) {
                FileCopyUtils.copy(inputStream, outputStream);
            }

            String chunkId = uploadId + "_" + chunkIndex;
            session.chunkIds.add(chunkId);

            log.debug("分片上传成功: {} - {}", uploadId, chunkIndex);
            return chunkId;

        } catch (Exception e) {
            log.error("分片上传失败: {} - {}", uploadId, chunkIndex, e);
            throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED,
                    "本地分片上传失败: " + e.getMessage(),
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
        log.debug("完成本地分片上传: {}", uploadId);

        try {
            MultipartUploadSession session = getAndValidateSession(uploadId);

            // 验证所有分片都已上传
            if (session.chunkIds.size() != chunkIds.size()) {
                throw new FileOperationException(
                        FileConstants.UPLOAD_FAILED,
                        "分片数量不匹配: 期望=" + chunkIds.size() + ", 实际=" + session.chunkIds.size()
                );
            }

            // 构建最终文件路径
            Path finalFilePath = buildFilePath(session.key);
            Path parentDir = finalFilePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // 合并分片文件
            Path tempDir = buildTempDir(uploadId);
            try (FileOutputStream finalOutputStream = new FileOutputStream(finalFilePath.toFile())) {
                for (int i = 0; i < chunkIds.size(); i++) {
                    Path chunkPath = tempDir.resolve("chunk_" + i);
                    try (FileInputStream chunkInputStream = new FileInputStream(chunkPath.toFile())) {
                        FileCopyUtils.copy(chunkInputStream, finalOutputStream);
                    }
                }
            }

            // 保存元数据
            saveMetadata(session.key, session.metadata);

            // 清理临时文件和会话
            cleanupTempDir(tempDir);
            uploadSessions.remove(uploadId);

            log.debug("分片上传完成: {}", session.key);
            return session.key;

        } catch (Exception e) {
            log.error("完成分片上传失败: {}", uploadId, e);
            throw new FileOperationException(
                    FileConstants.UPLOAD_FAILED,
                    "完成本地分片上传失败: " + e.getMessage(),
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
        log.debug("取消本地分片上传: {}", uploadId);

        try {
            MultipartUploadSession session = uploadSessions.get(uploadId);
            if (session != null) {
                // 清理临时文件
                Path tempDir = buildTempDir(uploadId);
                cleanupTempDir(tempDir);

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
            Path metadataPath = buildMetadataPath(key);
            File metadataFile = metadataPath.toFile();

            if (!metadataFile.exists()) {
                throw new FileOperationException(
                        FileConstants.FILE_NOT_FOUND,
                        "文件元数据不存在: " + key
                );
            }

            return objectMapper.readValue(metadataFile, FileMetadata.class);

        } catch (Exception e) {
            log.error("获取文件元数据失败: {}", key, e);
            throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND,
                    "获取本地文件元数据失败: " + e.getMessage(),
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
            Path metadataPath = buildMetadataPath(key);
            File metadataFile = metadataPath.toFile();

            if (!metadataFile.exists()) {
                return false;
            }

            // 更新时间戳
            metadata.setLastUpdateTime(LocalDateTime.now());

            objectMapper.writeValue(metadataFile, metadata);
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
     * @param key    存储键值
     * @param expiry 过期时间
     * @param method HTTP方法（GET/PUT等）
     * @return 预签名URL
     */
    @Override
    public String generatePresignedUrl(String key, Duration expiry, HttpMethod method) {
        log.warn("本地存储模式不支持预签名URL: {}", key);
        throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE,
                "本地存储模式不支持预签名URL功能"
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
        log.debug("复制本地文件: {} -> {}", sourceKey, targetKey);

        try {
            FileOperationPaths paths = prepareFileOperation(sourceKey, targetKey);

            // 复制文件
            Files.copy(paths.sourcePath, paths.targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 复制元数据（如果存在）
            if (Files.exists(paths.sourceMetadataPath)) {
                Files.copy(paths.sourceMetadataPath, paths.targetMetadataPath, StandardCopyOption.REPLACE_EXISTING);
            }

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
        log.debug("移动本地文件: {} -> {}", sourceKey, targetKey);

        try {
            FileOperationPaths paths = prepareFileOperation(sourceKey, targetKey);

            // 移动文件
            Files.move(paths.sourcePath, paths.targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 移动元数据（如果存在）
            if (Files.exists(paths.sourceMetadataPath)) {
                Files.move(paths.sourceMetadataPath, paths.targetMetadataPath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.debug("文件移动成功: {} -> {}", sourceKey, targetKey);
            return true;

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
            Path filePath = buildFilePath(key);
            File file = filePath.toFile();

            if (!file.exists() || !file.isFile()) {
                throw new FileOperationException(
                        FileConstants.FILE_NOT_FOUND,
                        "文件不存在: " + key
                );
            }

            return file.length();

        } catch (FileOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", key, e);
            throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND,
                    "获取本地文件大小失败: " + e.getMessage(),
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
     * 构建文件的完整路径
     *
     * @param key 业务键
     * @return 文件完整路径
     */
    private Path buildFilePath(String key) {
        CurrentSystemProperties.StorageConfig storageConfig = systemProperties.getFile().getStorage();
        String rootPath = storageConfig.getRootPath();

        if (!StringUtils.hasText(rootPath)) {
            throw new FileOperationException(
                    FileConstants.STORAGE_SERVICE_UNAVAILABLE,
                    "本地存储基础路径未配置"
            );
        }

        return Paths.get(rootPath, "data", key);
    }

    /**
     * 构建元数据文件路径
     *
     * @param key 业务键
     * @return 元数据文件路径
     */
    private Path buildMetadataPath(String key) {
        CurrentSystemProperties.StorageConfig storageConfig = systemProperties.getFile().getStorage();
        String rootPath = storageConfig.getRootPath();

        return Paths.get(rootPath, "metadata", key + ".meta");
    }

    /**
     * 构建临时目录路径
     *
     * @param uploadId 上传ID
     * @return 临时目录路径
     */
    private Path buildTempDir(String uploadId) {
        CurrentSystemProperties.StorageConfig storageConfig = systemProperties.getFile().getStorage();
        String tempPath = storageConfig.getTempPath();

        return Paths.get(tempPath, "temp", uploadId);
    }

    /**
     * 保存文件元数据
     *
     * @param key      业务键
     * @param metadata 文件元数据
     */
    private void saveMetadata(String key, FileMetadata metadata) {
        try {
            Path metadataPath = buildMetadataPath(key);
            Path parentDir = metadataPath.getParent();

            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            metadata.setCreateTime(LocalDateTime.now());
            metadata.setLastUpdateTime(LocalDateTime.now());

            objectMapper.writeValue(metadataPath.toFile(), metadata);
        } catch (Exception e) {
            log.warn("保存文件元数据失败: {}", key, e);
        }
    }

    /**
     * 清理临时目录
     *
     * @param tempDir 临时目录路径
     */
    private void cleanupTempDir(Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                try (var paths = Files.walk(tempDir)) {
                    paths
                            // 先删除文件，再删除目录
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    log.warn("删除临时文件失败: {}", path, e);
                                }
                            });
                }
            }
        } catch (Exception e) {
            log.warn("清理临时目录失败: {}", tempDir, e);
        }
    }

    /**
     * 生成上传ID
     */
    private String generateUploadId() {
        return "local_upload_" + System.currentTimeMillis() + "_" + Thread.currentThread().threadId();
    }

    /**
     * 安全删除文件的通用方法
     *
     * @param path     文件路径
     * @param fileType 文件类型描述（用于日志）
     * @return 是否删除成功
     */
    private boolean safeDeleteFile(Path path, String fileType) {
        try {
            Files.delete(path);
            log.debug("{}删除成功: {}", fileType, path);
            return true;
        } catch (IOException e) {
            log.warn("{}删除失败: {}", fileType, path, e);
            return false;
        }
    }

    /**
     * 准备文件操作的路径并验证
     *
     * @param sourceKey 源文件键值
     * @param targetKey 目标文件键值
     * @return 文件操作路径对象
     * @throws IOException            如果目录创建失败
     * @throws FileOperationException 如果源文件不存在
     */
    private FileOperationPaths prepareFileOperation(String sourceKey, String targetKey) throws IOException {
        Path sourcePath = buildFilePath(sourceKey);
        Path targetPath = buildFilePath(targetKey);
        Path sourceMetadataPath = buildMetadataPath(sourceKey);
        Path targetMetadataPath = buildMetadataPath(targetKey);

        // 检查源文件是否存在
        if (!exists(sourceKey)) {
            throw new FileOperationException(
                    FileConstants.FILE_NOT_FOUND,
                    "源文件不存在: " + sourceKey
            );
        }

        // 确保目标目录存在
        Path targetParentDir = targetPath.getParent();
        if (targetParentDir != null && !Files.exists(targetParentDir)) {
            Files.createDirectories(targetParentDir);
        }

        Path targetMetadataParentDir = targetMetadataPath.getParent();
        if (targetMetadataParentDir != null && !Files.exists(targetMetadataParentDir)) {
            Files.createDirectories(targetMetadataParentDir);
        }

        return new FileOperationPaths(sourcePath, targetPath, sourceMetadataPath, targetMetadataPath);
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
         * 文件键
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

    /**
     * 文件操作路径封装类
     *
     * @param sourcePath         源文件路径
     * @param targetPath         目标文件路径
     * @param sourceMetadataPath 源元数据路径
     * @param targetMetadataPath 目标元数据路径
     */
    private record FileOperationPaths(Path sourcePath, Path targetPath, Path sourceMetadataPath,
                                      Path targetMetadataPath) {

    }
} 