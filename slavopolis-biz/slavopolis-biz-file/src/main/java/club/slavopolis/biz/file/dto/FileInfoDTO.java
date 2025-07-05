package club.slavopolis.biz.file.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 文件信息DTO
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
public class FileInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 存储策略
     */
    private String storageStrategy;

    /**
     * 存储键
     */
    private String storageKey;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 描述
     */
    private String description;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;
} 