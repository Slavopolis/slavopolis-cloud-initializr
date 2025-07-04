package club.slavopolis.file.domain;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.file.enums.AccessPermission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文件上传会话领域模型
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
public class FileUploadSession {

    /**
     * 上传会话ID
     */
    private String uploadId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 分片大小
     */
    private Long chunkSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 已上传分片数
     */
    private Integer uploadedChunks;

    /**
     * 存储类型
     */
    private StorageType storageType;

    /**
     * 上传状态
     */
    private String status;

    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 访问权限
     */
    private AccessPermission accessPermission;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 删除标识
     */
    private Boolean deleteFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
} 