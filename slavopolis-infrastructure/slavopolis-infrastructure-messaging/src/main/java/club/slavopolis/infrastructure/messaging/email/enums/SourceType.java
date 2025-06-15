package club.slavopolis.infrastructure.messaging.email.enums;

/**
 * 资源来源枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.enums
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public enum SourceType {

    /**
     * 字节数组
     */
    BYTES,
    /**
     * 输入流
     */
    INPUT_STREAM,
    /**
     * 文件路径
     */
    FILE_PATH,
    /**
     * URL
     */
    URL
}
