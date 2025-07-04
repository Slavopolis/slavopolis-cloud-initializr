package club.slavopolis.file.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文件元数据
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
public class FileMetadata {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 文件哈希值
     */
    private String fileHash;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 是否启用压缩
     */
    private Boolean enableCompression;

    /**
     * 压缩级别
     */
    private Integer compressionLevel;

    /**
     * 缓存控制
     */
    private String cacheControl;

    /**
     * 内容编码
     */
    private String contentEncoding;

    /**
     * 内容语言
     */
    private String contentLanguage;

    /**
     * 自定义元数据
     */
    private Map<String, String> customMetadata;

    /**
     * 标签
     */
    private Map<String, String> tags;

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
        private final FileMetadata metadata = new FileMetadata();

        public Builder fileName(String fileName) {
            metadata.setFileName(fileName);
            return this;
        }

        public Builder fileSize(Long fileSize) {
            metadata.setFileSize(fileSize);
            return this;
        }

        public Builder contentType(String contentType) {
            metadata.setContentType(contentType);
            return this;
        }

        public Builder fileHash(String fileHash) {
            metadata.setFileHash(fileHash);
            return this;
        }

        public Builder extension(String extension) {
            metadata.setExtension(extension);
            return this;
        }

        public Builder originalName(String originalName) {
            metadata.setOriginalName(originalName);
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            metadata.setCreateTime(createTime);
            return this;
        }

        public Builder lastUpdateTime(LocalDateTime lastUpdateTime) {
            metadata.setLastUpdateTime(lastUpdateTime);
            return this;
        }

        public Builder createdBy(String createdBy) {
            metadata.setCreatedBy(createdBy);
            return this;
        }

        public Builder tenantId(String tenantId) {
            metadata.setTenantId(tenantId);
            return this;
        }

        public Builder enableCompression(Boolean enableCompression) {
            metadata.setEnableCompression(enableCompression);
            return this;
        }

        public Builder compressionLevel(Integer compressionLevel) {
            metadata.setCompressionLevel(compressionLevel);
            return this;
        }

        public Builder cacheControl(String cacheControl) {
            metadata.setCacheControl(cacheControl);
            return this;
        }

        public Builder contentEncoding(String contentEncoding) {
            metadata.setContentEncoding(contentEncoding);
            return this;
        }

        public Builder contentLanguage(String contentLanguage) {
            metadata.setContentLanguage(contentLanguage);
            return this;
        }

        public Builder customMetadata(Map<String, String> customMetadata) {
            metadata.setCustomMetadata(customMetadata);
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            metadata.setTags(tags);
            return this;
        }

        public FileMetadata build() {
            return metadata;
        }
    }
} 