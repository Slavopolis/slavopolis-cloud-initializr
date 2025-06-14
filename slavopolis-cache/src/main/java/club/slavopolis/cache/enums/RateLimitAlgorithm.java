package club.slavopolis.cache.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 限流算法枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.enums
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
@AllArgsConstructor
public enum RateLimitAlgorithm {

    /**
     * 滑动窗口
     */
    SLIDING_WINDOW("滑动窗口"),

    /**
     * 令牌桶
     */
    TOKEN_BUCKET("令牌桶"),

    /**
     * 固定窗口
     */
    FIXED_WINDOW("固定窗口"),

    /**
     * 漏桶
     */
    LEAKY_BUCKET("漏桶"),

    /**
     * 复合限流
     */
    COMPOSITE("复合限流"),

    /**
     * 规则限流
     */
    RULE_BASED("规则限流");

    private final String description;
}
