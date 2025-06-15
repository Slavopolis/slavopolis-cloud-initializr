package club.slavopolis.infrastructure.cache.redis.model;

import club.slavopolis.infrastructure.cache.redis.enums.RateLimitAlgorithm;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 限流结果模型
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
public class RateLimitResult {

    /**
     * 是否允许通过
     */
    private boolean allowed;

    /**
     * 限流标识符
     */
    private String key;

    /**
     * 本次请求数量
     */
    private long requestCount;

    /**
     * 剩余配额
     */
    private long remainingQuota;

    /**
     * 配额重置时间
     */
    private LocalDateTime resetTime;

    /**
     * 重试建议时间（毫秒）
     */
    private long retryAfterMs;

    /**
     * 限流算法类型
     */
    private RateLimitAlgorithm algorithm;

    /**
     * 限流原因（被限流时）
     */
    private String reason;

    /**
     * 额外信息
     */
    private Object metadata;

    /**
     * 限流检查耗时（纳秒）
     */
    private long processingTimeNanos;

    /**
     * 创建允许通过的结果
     */
    public static RateLimitResult allowed(String key, long remainingQuota, RateLimitAlgorithm algorithm) {
        return RateLimitResult.builder()
                .allowed(true)
                .key(key)
                .remainingQuota(remainingQuota)
                .algorithm(algorithm)
                .build();
    }

    /**
     * 创建被限流的结果
     */
    public static RateLimitResult rejected(String key, String reason, long retryAfterMs, RateLimitAlgorithm algorithm) {
        return RateLimitResult.builder()
                .allowed(false)
                .key(key)
                .reason(reason)
                .retryAfterMs(retryAfterMs)
                .algorithm(algorithm)
                .remainingQuota(0)
                .build();
    }

    /**
     * 创建被限流的结果（带重置时间）
     */
    public static RateLimitResult rejected(String key, String reason, LocalDateTime resetTime, RateLimitAlgorithm algorithm) {
        return RateLimitResult.builder()
                .allowed(false)
                .key(key)
                .reason(reason)
                .resetTime(resetTime)
                .algorithm(algorithm)
                .remainingQuota(0)
                .build();
    }
} 