package club.slavopolis.file.domain.request;

import club.slavopolis.file.enums.AccessPermission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.Map;

/**
 * 文件上传请求
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
@Accessors(chain = true)
public class FileUploadRequest {

    /**
     * 文件名
     */
    private String originalName;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 目标路径（可选）
     */
    private String targetPath;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 访问权限
     */
    private AccessPermission accessPermission = AccessPermission.PRIVATE;

    /**
     * 是否启用分片上传
     */
    private Boolean enableMultipart = false;

    /**
     * 分片大小（字节）
     */
    private Long chunkSize;

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overwrite = false;

    /**
     * 自定义元数据
     */
    private Map<String, String> customMetadata;

    /**
     * 标签
     */
    private Map<String, String> tags;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 是否启用去重（秒传）
     */
    private Boolean enableDeduplication = true;

    /**
     * 预先计算的文件哈希值（用于秒传）
     */
    private String preComputedHash;

    /**
     * 回调URL（上传完成后回调）
     */
    private String callbackUrl;

    /**
     * 自定义存储类型（覆盖全局配置）
     */
    private String customStorageType;

    /**
     * 创建构建器
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 构建器
     */
    public static class Builder {
        private final FileUploadRequest request = new FileUploadRequest();

        public Builder fileName(String fileName) {
            request.setOriginalName(fileName);
            return this;
        }

        public Builder inputStream(InputStream inputStream) {
            request.setInputStream(inputStream);
            return this;
        }

        public Builder fileSize(Long fileSize) {
            request.setFileSize(fileSize);
            return this;
        }

        public Builder contentType(String contentType) {
            request.setContentType(contentType);
            return this;
        }

        public Builder targetPath(String targetPath) {
            request.setTargetPath(targetPath);
            return this;
        }

        public Builder createdBy(String createdBy) {
            request.setCreatedBy(createdBy);
            return this;
        }

        public Builder tenantId(String tenantId) {
            request.setTenantId(tenantId);
            return this;
        }

        public Builder accessPermission(AccessPermission accessPermission) {
            request.setAccessPermission(accessPermission);
            return this;
        }

        public Builder enableMultipart(Boolean enableMultipart) {
            request.setEnableMultipart(enableMultipart);
            return this;
        }

        public Builder chunkSize(Long chunkSize) {
            request.setChunkSize(chunkSize);
            return this;
        }

        public Builder overwrite(Boolean overwrite) {
            request.setOverwrite(overwrite);
            return this;
        }

        public Builder customMetadata(Map<String, String> customMetadata) {
            request.setCustomMetadata(customMetadata);
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            request.setTags(tags);
            return this;
        }

        public Builder description(String description) {
            request.setDescription(description);
            return this;
        }

        public Builder enableDeduplication(Boolean enableDeduplication) {
            request.setEnableDeduplication(enableDeduplication);
            return this;
        }

        public Builder preComputedHash(String preComputedHash) {
            request.setPreComputedHash(preComputedHash);
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            request.setCallbackUrl(callbackUrl);
            return this;
        }

        public Builder customStorageType(String customStorageType) {
            request.setCustomStorageType(customStorageType);
            return this;
        }

        public FileUploadRequest build() {
            return request;
        }
    }
} 