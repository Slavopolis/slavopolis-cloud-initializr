package club.slavopolis.file.domain.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分片上传结果
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
public class ChunkUploadResult {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 上传会话ID
     */
    private String uploadId;

    /**
     * 分片索引
     */
    private Integer chunkIndex;

    /**
     * 分片标识/ETag
     */
    private String chunkId;

    /**
     * 分片大小
     */
    private Long chunkSize;

    /**
     * 上传进度（已上传分片数/总分片数）
     */
    private UploadProgress progress;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 上传耗时（毫秒）
     */
    private Long uploadDuration;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 是否需要重试
     */
    private Boolean needRetry = false;

    /**
     * 重试次数
     */
    private Integer retryCount = 0;

    /**
     * 上传进度信息
     */
    @Data
    @Accessors(chain = true)
    public static class UploadProgress {
        /**
         * 已上传分片数
         */
        private Integer uploadedChunks;

        /**
         * 总分片数
         */
        private Integer totalChunks;

        /**
         * 已上传字节数
         */
        private Long uploadedBytes;

        /**
         * 总字节数
         */
        private Long totalBytes;

        /**
         * 上传百分比（0-100）
         */
        private Double percentage;

        /**
         * 计算百分比
         */
        public void calculatePercentage() {
            if (totalChunks != null && totalChunks > 0 && uploadedChunks != null) {
                this.percentage = (double) uploadedChunks / totalChunks * 100;
            }
        }
    }

    /**
     * 创建成功结果
     *
     * @param uploadId   上传会话ID
     * @param chunkIndex 分片索引
     * @param chunkId    分片标识
     * @return 分片上传结果
     */
    public static ChunkUploadResult success(String uploadId, Integer chunkIndex, String chunkId) {
        return new ChunkUploadResult()
                .setSuccess(true)
                .setUploadId(uploadId)
                .setChunkIndex(chunkIndex)
                .setChunkId(chunkId)
                .setUploadTime(LocalDateTime.now());
    }

    /**
     * 创建失败结果
     *
     * @param uploadId     上传会话ID
     * @param chunkIndex   分片索引
     * @param errorCode    错误代码
     * @param errorMessage 错误信息
     * @return 分片上传结果
     */
    public static ChunkUploadResult failure(String uploadId, Integer chunkIndex, String errorCode, String errorMessage) {
        return new ChunkUploadResult()
                .setSuccess(false)
                .setUploadId(uploadId)
                .setChunkIndex(chunkIndex)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage);
    }

    /**
     * 创建需要重试的结果
     *
     * @param uploadId     上传会话ID
     * @param chunkIndex   分片索引
     * @param errorCode    错误代码
     * @param errorMessage 错误信息
     * @param retryCount   重试次数
     * @return 分片上传结果
     */
    public static ChunkUploadResult needRetry(String uploadId, Integer chunkIndex, String errorCode, String errorMessage, Integer retryCount) {
        return new ChunkUploadResult()
                .setSuccess(false)
                .setUploadId(uploadId)
                .setChunkIndex(chunkIndex)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage)
                .setNeedRetry(true)
                .setRetryCount(retryCount);
    }
} 