package club.slavopolis.file.enums;

import lombok.Getter;

/**
 * 文件状态枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
public enum FileStatus {
    /**
     * 上传中
     */
    UPLOADING("上传中"),
    
    /**
     * 活跃状态
     */
    ACTIVE("活跃"),
    
    /**
     * 已删除
     */
    DELETED("已删除"),
    
    /**
     * 处理中
     */
    PROCESSING("处理中"),
    
    /**
     * 处理失败
     */
    FAILED("失败");

    private final String description;

    FileStatus(String description) {
        this.description = description;
    }

}