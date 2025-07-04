package club.slavopolis.base.enums;

/**
 * 存储类型枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public enum StorageType {

    /**
     * 阿里云OSS
     */
    OSS,

    /**
     * MinIO对象存储
     */
    MINIO,

    /**
     * 数据库存储
     */
    DATABASE,

    /**
     * 本地文件系统
     */
    LOCAL
}
