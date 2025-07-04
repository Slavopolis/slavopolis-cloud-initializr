package club.slavopolis.file.domain;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.file.enums.AccessPermission;
import club.slavopolis.file.enums.FileStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文件信息领域模型
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
public class FileInfo {

    /**
     * 文件唯一标识
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    private String contentType;

    /**
     * 文件哈希值（SHA256）
     */
    private String fileHash;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 存储类型
     */
    private StorageType storageType;

    /**
     * 存储路径或键值
     */
    private String storageKey;

    /**
     * 文件状态
     */
    private FileStatus status;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

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
    private AccessPermission accessPermission;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 扩展信息
     */
    private Map<String, Object> extensionInfo;
} 