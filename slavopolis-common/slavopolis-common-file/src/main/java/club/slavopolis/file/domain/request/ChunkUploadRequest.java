package club.slavopolis.file.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;

/**
 * 分片上传请求
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
public class ChunkUploadRequest {

    /**
     * 上传会话ID
     */
    private String uploadId;

    /**
     * 分片索引（从0开始）
     */
    private Integer chunkIndex;

    /**
     * 分片输入流
     */
    private InputStream chunkStream;

    /**
     * 分片大小
     */
    private Integer chunkSize;

    /**
     * 分片哈希值（可选，用于校验）
     */
    private String chunkHash;

    /**
     * 是否是最后一个分片
     */
    private Boolean isLastChunk = false;

    /**
     * 重试次数
     */
    private Integer retryCount = 0;

    /**
     * 创建者
     */
    private String createdBy;

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
        private final ChunkUploadRequest request = new ChunkUploadRequest();

        public Builder uploadId(String uploadId) {
            request.setUploadId(uploadId);
            return this;
        }

        public Builder chunkIndex(Integer chunkIndex) {
            request.setChunkIndex(chunkIndex);
            return this;
        }

        public Builder chunkStream(InputStream chunkStream) {
            request.setChunkStream(chunkStream);
            return this;
        }

        public Builder chunkSize(Integer chunkSize) {
            request.setChunkSize(chunkSize);
            return this;
        }

        public Builder chunkHash(String chunkHash) {
            request.setChunkHash(chunkHash);
            return this;
        }

        public Builder isLastChunk(Boolean isLastChunk) {
            request.setIsLastChunk(isLastChunk);
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            request.setRetryCount(retryCount);
            return this;
        }

        public Builder createdBy(String createdBy) {
            request.setCreatedBy(createdBy);
            return this;
        }

        public ChunkUploadRequest build() {
            return request;
        }
    }
} 