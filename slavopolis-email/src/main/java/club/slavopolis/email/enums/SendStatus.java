package club.slavopolis.email.enums;

/**
 * 邮件发送状态枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.enums
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public enum SendStatus {
    /**
     * 发送中
     */
    SENDING,
    
    /**
     * 发送成功
     */
    SUCCESS,
    
    /**
     * 发送失败
     */
    FAILED,
    
    /**
     * 已取消
     */
    CANCELLED
}
