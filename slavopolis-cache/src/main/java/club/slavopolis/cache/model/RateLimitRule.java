package club.slavopolis.cache.model;

import club.slavopolis.cache.enums.RateLimitAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * 限流规则模型
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.model
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitRule {

    /**
     * 规则名称
     */
    private String name;

    /**
     * 限流算法类型
     */
    private RateLimitAlgorithm algorithm;

    /**
     * 时间窗口大小
     */
    private Duration windowSize;

    /**
     * 最大请求数/桶容量
     */
    private long maxRequests;

    /**
     * 补充速率（令牌桶/漏桶）
     */
    private long refillRate;

    /**
     * 优先级（数字越小优先级越高）
     */
    private int priority;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 适用的维度（如：user、ip、api等）
     */
    private String dimension;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 创建滑动窗口规则
     */
    public static RateLimitRule slidingWindow(String name, Duration windowSize, long maxRequests) {
        return RateLimitRule.builder()
                .name(name)
                .algorithm(RateLimitAlgorithm.SLIDING_WINDOW)
                .windowSize(windowSize)
                .maxRequests(maxRequests)
                .enabled(true)
                .priority(1)
                .build();
    }

    /**
     * 创建令牌桶规则
     */
    public static RateLimitRule tokenBucket(String name, long capacity, long refillRate) {
        return RateLimitRule.builder()
                .name(name)
                .algorithm(RateLimitAlgorithm.TOKEN_BUCKET)
                .maxRequests(capacity)
                .refillRate(refillRate)
                .enabled(true)
                .priority(1)
                .build();
    }

    /**
     * 创建固定窗口规则
     */
    public static RateLimitRule fixedWindow(String name, Duration windowSize, long maxRequests) {
        return RateLimitRule.builder()
                .name(name)
                .algorithm(RateLimitAlgorithm.FIXED_WINDOW)
                .windowSize(windowSize)
                .maxRequests(maxRequests)
                .enabled(true)
                .priority(1)
                .build();
    }

    /**
     * 创建漏桶规则
     */
    public static RateLimitRule leakyBucket(String name, long capacity, long leakRate) {
        return RateLimitRule.builder()
                .name(name)
                .algorithm(RateLimitAlgorithm.LEAKY_BUCKET)
                .maxRequests(capacity)
                .refillRate(leakRate)
                .enabled(true)
                .priority(1)
                .build();
    }
} 