package club.slavopolis.biz.file.dto;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * 分片上传DTO
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
public class ChunkUploadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 上传ID
     */
    @NotBlank(message = "上传ID不能为空")
    private String uploadId;

    /**
     * 分片索引（从0开始）
     */
    @NotNull(message = "分片索引不能为空")
    @PositiveOrZero(message = "分片索引必须大于等于0")
    private Integer chunkIndex;

    /**
     * 分片哈希值
     */
    private String chunkHash;
} 