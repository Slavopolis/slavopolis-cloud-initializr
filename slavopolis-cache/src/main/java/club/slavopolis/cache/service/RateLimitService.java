package club.slavopolis.cache.service;

import club.slavopolis.cache.model.RateLimitResult;
import club.slavopolis.cache.model.RateLimitRule;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 限流服务接口，提供多种限流算法的实现，支持多维度限流控制
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.service
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface RateLimitService {

    // ================================
    // 滑动窗口限流
    // ================================

    /**
     * 滑动窗口限流检查
     * 最适合邮件发送等场景，平滑限流
     *
     * @param key 限流标识符
     * @param windowSize 窗口大小
     * @param maxRequests 窗口内最大请求数
     * @param requestCount 本次请求数量（默认为1）
     * @return 限流结果
     */
    RateLimitResult slidingWindowLimit(String key, Duration windowSize, long maxRequests, long requestCount);

    /**
     * 滑动窗口限流检查（默认请求数为1）
     */
    default RateLimitResult slidingWindowLimit(String key, Duration windowSize, long maxRequests) {
        return slidingWindowLimit(key, windowSize, maxRequests, 1);
    }

    // ================================
    // 令牌桶限流
    // ================================

    /**
     * 令牌桶限流检查
     * 适合突发流量控制，允许短时间内的流量突发
     *
     * @param key 限流标识符
     * @param capacity 桶容量
     * @param refillRate 令牌补充速率（令牌/秒）
     * @param requestTokens 请求的令牌数量
     * @return 限流结果
     */
    RateLimitResult tokenBucketLimit(String key, long capacity, long refillRate, long requestTokens);

    /**
     * 令牌桶限流检查（默认请求1个令牌）
     */
    default RateLimitResult tokenBucketLimit(String key, long capacity, long refillRate) {
        return tokenBucketLimit(key, capacity, refillRate, 1);
    }

    // ================================
    // 固定窗口限流
    // ================================

    /**
     * 固定窗口限流检查
     * 简单的计数器限流，适合简单场景
     *
     * @param key 限流标识符
     * @param windowSize 窗口大小
     * @param maxRequests 窗口内最大请求数
     * @param requestCount 本次请求数量
     * @return 限流结果
     */
    RateLimitResult fixedWindowLimit(String key, Duration windowSize, long maxRequests, long requestCount);

    /**
     * 固定窗口限流检查（默认请求数为1）
     */
    default RateLimitResult fixedWindowLimit(String key, Duration windowSize, long maxRequests) {
        return fixedWindowLimit(key, windowSize, maxRequests, 1);
    }

    // ================================
    // 漏桶限流
    // ================================

    /**
     * 漏桶限流检查
     * 严格按照固定速率处理请求，超出的请求被丢弃
     *
     * @param key 限流标识符
     * @param capacity 桶容量
     * @param leakRate 漏水速率（请求/秒）
     * @param requestCount 本次请求数量
     * @return 限流结果
     */
    RateLimitResult leakyBucketLimit(String key, long capacity, long leakRate, long requestCount);

    /**
     * 漏桶限流检查（默认请求数为1）
     */
    default RateLimitResult leakyBucketLimit(String key, long capacity, long leakRate) {
        return leakyBucketLimit(key, capacity, leakRate, 1);
    }

    // ================================
    // 规则化限流
    // ================================

    /**
     * 基于规则的限流检查
     * 支持多维度、多规则组合限流
     *
     * @param key 限流标识符
     * @param rules 限流规则列表
     * @param requestCount 本次请求数量
     * @return 限流结果
     */
    RateLimitResult ruleBasedLimit(String key, List<RateLimitRule> rules, long requestCount);

    /**
     * 基于规则的限流检查（默认请求数为1）
     */
    default RateLimitResult ruleBasedLimit(String key, List<RateLimitRule> rules) {
        return ruleBasedLimit(key, rules, 1);
    }

    // ================================
    // 复合限流
    // ================================

    /**
     * 复合限流检查
     * 同时应用多种限流算法，所有算法都通过才允许访问
     *
     * @param key 限流标识符
     * @param limitChecks 限流检查映射（算法名 -> 检查函数）
     * @return 限流结果
     */
    RateLimitResult compositeLimit(String key, Map<String, RateLimitResult> limitChecks);

    // ================================
    // 分布式限流
    // ================================

    /**
     * 分布式滑动窗口限流
     * 适用于多实例部署场景
     *
     * @param key 限流标识符
     * @param windowSize 窗口大小
     * @param maxRequests 窗口内最大请求数
     * @param requestCount 本次请求数量
     * @param instanceId 实例标识
     * @return 限流结果
     */
    RateLimitResult distributedSlidingWindowLimit(String key, Duration windowSize, long maxRequests, 
                                                   long requestCount, String instanceId);

    // ================================
    // 限流状态查询
    // ================================

    /**
     * 获取限流状态信息
     *
     * @param key 限流标识符
     * @return 限流状态信息
     */
    Map<String, Object> getLimitStatus(String key);

    /**
     * 重置限流状态
     *
     * @param key 限流标识符
     * @return 是否重置成功
     */
    boolean resetLimit(String key);

    /**
     * 获取剩余配额
     *
     * @param key 限流标识符
     * @param windowSize 窗口大小
     * @param maxRequests 最大请求数
     * @return 剩余配额
     */
    long getRemainingQuota(String key, Duration windowSize, long maxRequests);

    // ================================
    // 预热和降级
    // ================================

    /**
     * 限流预热
     * 系统启动时逐渐提升限流阈值
     *
     * @param key 限流标识符
     * @param warmupDuration 预热时长
     * @param coldFactor 冷启动因子
     * @return 预热后的限流阈值
     */
    long warmUpLimit(String key, Duration warmupDuration, double coldFactor);

    /**
     * 获取当前限流指标
     * 用于监控和告警
     *
     * @param key 限流标识符
     * @return 限流指标
     */
    Map<String, Object> getLimitMetrics(String key);
} 