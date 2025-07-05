package club.slavopolis.biz.file.dto;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文件上传DTO
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
public class FileUploadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 文件描述
     */
    @Size(max = 500, message = "文件描述不能超过500字符")
    private String description;
} 