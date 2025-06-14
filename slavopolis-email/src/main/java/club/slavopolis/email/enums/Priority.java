package club.slavopolis.email.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件优先级枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.enums
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
@AllArgsConstructor
public enum Priority {
    /**
     * 低优先级
     */
    LOW(5),
    
    /**
     * 普通优先级
     */
    NORMAL(3),
    
    /**
     * 高优先级
     */
    HIGH(1);

    /**
     * 优先级数值（数值越小优先级越高）
     */
    private final int value;
}
