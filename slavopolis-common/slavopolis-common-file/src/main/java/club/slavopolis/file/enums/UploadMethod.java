package club.slavopolis.file.enums;

import lombok.Getter;

/**
 * 上传方式枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
public enum UploadMethod {
    /**
     * 普通上传
     */
    NORMAL("普通上传"),
    
    /**
     * 分片上传
     */
    MULTIPART("分片上传"),
    
    /**
     * 秒传
     */
    INSTANT("秒传");

    private final String description;

    UploadMethod(String description) {
        this.description = description;
    }

}