package club.slavopolis.infrastructure.cache.redis.service.impl;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.cache.redis.config.properties.CacheProperties;
import club.slavopolis.infrastructure.cache.redis.enums.RateLimitAlgorithm;
import club.slavopolis.infrastructure.cache.redis.exception.CacheException;
import club.slavopolis.infrastructure.cache.redis.model.RateLimitResult;
import club.slavopolis.infrastructure.cache.redis.model.RateLimitRule;
import club.slavopolis.infrastructure.cache.redis.script.RateLimitScripts;
import club.slavopolis.infrastructure.cache.redis.service.LuaScriptService;
import club.slavopolis.infrastructure.cache.redis.service.RateLimitService;
import club.slavopolis.infrastructure.cache.redis.util.CacheKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 限流服务实现
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.service.impl
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final LuaScriptService luaScriptService;
    private final CacheProperties cacheProperties;
    
    /**
     * 限流键前缀
     */
    private static final String RATE_LIMIT_PREFIX = "rate_limit";
    
    /**
     * 实例ID键名常量
     */
    private static final String INSTANCE_ID_KEY = "instanceId";
    
    /**
     * 系统启动时间
     */
    private static final long SYSTEM_START_TIME = System.currentTimeMillis();
    
    /**
     * 实例ID，用于分布式限流
     */
    private final String instanceId = UUID.randomUUID().toString().substring(0, 8);

    public RateLimitServiceImpl(@Autowired LuaScriptService luaScriptService, @Autowired CacheProperties cacheProperties) {
        this.luaScriptService = luaScriptService;
        this.cacheProperties = cacheProperties;
        log.info("RateLimitService 初始化完成，实例ID: {}", instanceId);
    }

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
    @Override
    public RateLimitResult slidingWindowLimit(String key, Duration windowSize, long maxRequests, long requestCount) {
        long startTime = System.nanoTime();
        try {
            String rateLimitKey = buildRateLimitKey(key, "sliding");
            long now = System.currentTimeMillis();
            
            List<String> keys = List.of(rateLimitKey);
            Object[] args = {windowSize.getSeconds(), maxRequests, now, requestCount};
            
            @SuppressWarnings("unchecked")
            List<Long> result = luaScriptService.execute(RateLimitScripts.SLIDING_WINDOW_SCRIPT, List.class, keys, args);
            
            return buildResult(key, result, RateLimitAlgorithm.SLIDING_WINDOW, requestCount, startTime);
        } catch (Exception e) {
            log.error("滑动窗口限流执行失败, key: {}, windowSize: {}, maxRequests: {}, requestCount: {}", 
                     key, windowSize, maxRequests, requestCount, e);
            throw new CacheException("滑动窗口限流执行失败", e);
        }
    }

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
    @Override
    public RateLimitResult tokenBucketLimit(String key, long capacity, long refillRate, long requestTokens) {
        long startTime = System.nanoTime();
        try {
            String rateLimitKey = buildRateLimitKey(key, "token_bucket");
            long now = System.currentTimeMillis();
            
            List<String> keys = List.of(rateLimitKey);
            Object[] args = {capacity, refillRate, now, requestTokens};
            
            @SuppressWarnings("unchecked")
            List<Long> result = luaScriptService.execute(RateLimitScripts.TOKEN_BUCKET_SCRIPT, List.class, keys, args);
            
            return buildResult(key, result, RateLimitAlgorithm.TOKEN_BUCKET, requestTokens, startTime);
        } catch (Exception e) {
            log.error("令牌桶限流执行失败, key: {}, capacity: {}, refillRate: {}, requestTokens: {}", 
                     key, capacity, refillRate, requestTokens, e);
            throw new CacheException("令牌桶限流执行失败", e);
        }
    }

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
    @Override
    public RateLimitResult fixedWindowLimit(String key, Duration windowSize, long maxRequests, long requestCount) {
        long startTime = System.nanoTime();
        try {
            String rateLimitKey = buildRateLimitKey(key, "fixed");
            long now = System.currentTimeMillis();
            
            List<String> keys = List.of(rateLimitKey);
            Object[] args = {windowSize.getSeconds(), maxRequests, now, requestCount};
            
            @SuppressWarnings("unchecked")
            List<Long> result = luaScriptService.execute(RateLimitScripts.FIXED_WINDOW_SCRIPT, List.class, keys, args);
            
            return buildResult(key, result, RateLimitAlgorithm.FIXED_WINDOW, requestCount, startTime);
        } catch (Exception e) {
            log.error("固定窗口限流执行失败, key: {}, windowSize: {}, maxRequests: {}, requestCount: {}", 
                     key, windowSize, maxRequests, requestCount, e);
            throw new CacheException("固定窗口限流执行失败", e);
        }
    }

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
    @Override
    public RateLimitResult leakyBucketLimit(String key, long capacity, long leakRate, long requestCount) {
        long startTime = System.nanoTime();
        try {
            String rateLimitKey = buildRateLimitKey(key, "leaky_bucket");
            long now = System.currentTimeMillis();
            
            List<String> keys = List.of(rateLimitKey);
            Object[] args = {capacity, leakRate, now, requestCount};
            
            @SuppressWarnings("unchecked")
            List<Long> result = luaScriptService.execute(RateLimitScripts.LEAKY_BUCKET_SCRIPT, List.class, keys, args);
            
            return buildResult(key, result, RateLimitAlgorithm.LEAKY_BUCKET, requestCount, startTime);
        } catch (Exception e) {
            log.error("漏桶限流执行失败, key: {}, capacity: {}, leakRate: {}, requestCount: {}", 
                     key, capacity, leakRate, requestCount, e);
            throw new CacheException("漏桶限流执行失败", e);
        }
    }

    /**
     * 基于规则的限流检查
     * 支持多维度、多规则组合限流
     *
     * @param key 限流标识符
     * @param rules 限流规则列表
     * @param requestCount 本次请求数量
     * @return 限流结果
     */
    @Override
    public RateLimitResult ruleBasedLimit(String key, List<RateLimitRule> rules, long requestCount) {
        if (CollectionUtils.isEmpty(rules)) {
            return RateLimitResult.allowed(key, Long.MAX_VALUE, RateLimitAlgorithm.RULE_BASED);
        }

        // 按优先级排序规则
        List<RateLimitRule> sortedRules = rules.stream()
                .filter(RateLimitRule::isEnabled)
                .sorted(Comparator.comparingInt(RateLimitRule::getPriority))
                .toList();

        // 依次检查每个规则
        for (RateLimitRule rule : sortedRules) {
            RateLimitResult result = executeRule(key, rule, requestCount);
            if (!result.isAllowed()) {
                result.setAlgorithm(RateLimitAlgorithm.RULE_BASED);
                result.setReason("违反限流规则: " + rule.getName());
                return result;
            }
        }

        return RateLimitResult.allowed(key, Long.MAX_VALUE, RateLimitAlgorithm.RULE_BASED);
    }

    /**
     * 复合限流检查
     * 同时应用多种限流算法，所有算法都通过才允许访问
     *
     * @param key 限流标识符
     * @param limitChecks 限流检查映射（算法名 -> 检查函数）
     * @return 限流结果
     */
    @Override
    public RateLimitResult compositeLimit(String key, Map<String, RateLimitResult> limitChecks) {
        long startTime = System.nanoTime();
        
        // 检查所有限流结果
        for (Map.Entry<String, RateLimitResult> entry : limitChecks.entrySet()) {
            RateLimitResult result = entry.getValue();
            if (!result.isAllowed()) {
                return RateLimitResult.builder()
                        .allowed(false)
                        .key(key)
                        .algorithm(RateLimitAlgorithm.COMPOSITE)
                        .reason("复合限流失败 - " + entry.getKey() + CommonConstants.CACHE_KEY_SEPARATOR + result.getReason())
                        .retryAfterMs(result.getRetryAfterMs())
                        .resetTime(result.getResetTime())
                        .processingTimeNanos(System.nanoTime() - startTime)
                        .build();
            }
        }

        // 所有检查都通过
        long minQuota = limitChecks.values().stream()
                .mapToLong(RateLimitResult::getRemainingQuota)
                .min()
                .orElse(0);

        return RateLimitResult.builder()
                .allowed(true)
                .key(key)
                .remainingQuota(minQuota)
                .algorithm(RateLimitAlgorithm.COMPOSITE)
                .processingTimeNanos(System.nanoTime() - startTime)
                .build();
    }

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
    @Override
    public RateLimitResult distributedSlidingWindowLimit(String key, Duration windowSize, long maxRequests, long requestCount, String instanceId) {
        long startTime = System.nanoTime();
        try {
            String globalKey = buildRateLimitKey(key, "global_sliding");
            String instanceKey = buildRateLimitKey(key + CommonConstants.CACHE_KEY_SEPARATOR + instanceId, "instance_sliding");
            long now = System.currentTimeMillis();
            
            List<String> keys = List.of(globalKey, instanceKey);
            Object[] args = {windowSize.getSeconds(), maxRequests, now, requestCount, instanceId};
            
            @SuppressWarnings("unchecked")
            List<Long> result = luaScriptService.execute(RateLimitScripts.DISTRIBUTED_SLIDING_WINDOW_SCRIPT, List.class, keys, args);
            
            return buildDistributedResult(key, result, requestCount, startTime);
        } catch (Exception e) {
            log.error("分布式滑动窗口限流执行失败, key: {}, windowSize: {}, maxRequests: {}, requestCount: {}, instanceId: {}", 
                     key, windowSize, maxRequests, requestCount, instanceId, e);
            throw new CacheException("分布式滑动窗口限流执行失败", e);
        }
    }

    /**
     * 获取限流状态信息
     *
     * @param key 限流标识符
     * @return 限流状态信息
     */
    @Override
    public Map<String, Object> getLimitStatus(String key) {
        // 简化实现，实际应该查询具体的限流状态
        Map<String, Object> status = new HashMap<>();
        status.put("key", key);
        status.put("timestamp", System.currentTimeMillis());
        status.put(INSTANCE_ID_KEY, instanceId);
        return status;
    }

    /**
     * 重置限流状态
     *
     * @param key 限流标识符
     * @return 是否重置成功
     */
    @Override
    public boolean resetLimit(String key) {
        try {
            // 构建限流键模式，匹配所有相关键
            String keyPattern = buildRateLimitKey(key, CommonConstants.ASTERISK);
            
            // 执行删除脚本
            String deleteScript = """
                local keys = redis.call('keys', ARGV[1])
                local count = 0
                for i = 1, #keys do
                    redis.call('del', keys[i])
                    count = count + 1
                end
                return count
                """;
            
            Long deletedCount = luaScriptService.execute(deleteScript, Long.class, Collections.emptyList(), keyPattern);
            
            log.info("重置限流状态成功, key: {}, 删除键数量: {}", key, deletedCount);
            return deletedCount != null && deletedCount > 0;
            
        } catch (Exception e) {
            log.error("重置限流状态失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 获取剩余配额
     *
     * @param key 限流标识符
     * @param windowSize 窗口大小
     * @param maxRequests 最大请求数
     * @return 剩余配额
     */
    @Override
    public long getRemainingQuota(String key, Duration windowSize, long maxRequests) {
        try {
            RateLimitResult result = slidingWindowLimit(key, windowSize, maxRequests, 0);
            return result.getRemainingQuota();
        } catch (Exception e) {
            log.error("获取剩余配额失败, key: {}", key, e);
            return 0;
        }
    }

    /**
     * 限流预热
     * 系统启动时逐渐提升限流阈值
     *
     * @param key 限流标识符
     * @param warmupDuration 预热时长
     * @param coldFactor 冷启动因子
     * @return 预热后的限流阈值
     */
    @Override
    public long warmUpLimit(String key, Duration warmupDuration, double coldFactor) {
        try {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - SYSTEM_START_TIME;
            long warmupTimeMs = warmupDuration.toMillis();
            
            // 如果已经完成预热，返回正常阈值
            if (elapsedTime >= warmupTimeMs) {
                log.debug("系统预热完成, key: {}, 返回正常阈值", key);
                return 1000; // 正常阈值
            }
            
            // 计算预热进度 (0.0 - 1.0)
            double warmupProgress = (double) elapsedTime / warmupTimeMs;
            
            // 使用指数预热曲线，更平滑的阈值提升
            // 冷启动时阈值很低，随着时间推移按指数增长
            double coldThreshold = 1000 / coldFactor;
            double normalThreshold = 1000;
            
            // 指数预热公式：coldThreshold + (normalThreshold - coldThreshold) * progress^2
            long currentThreshold = Math.round(coldThreshold + 
                (normalThreshold - coldThreshold) * Math.pow(warmupProgress, 2));
            
            log.debug("系统预热中, key: {}, 预热进度: {}%, 当前阈值: {}",
                     key, warmupProgress * 100, currentThreshold);
            
            return Math.max(currentThreshold, Math.round(coldThreshold));
            
        } catch (Exception e) {
            log.error("限流预热计算失败, key: {}, 使用默认阈值", key, e);
            // 发生异常时返回保守阈值
            return Math.round(1000 / coldFactor);
        }
    }

    /**
     * 获取当前限流指标
     * 用于监控和告警
     *
     * @param key 限流标识符
     * @return 限流指标
     */
    @Override
    public Map<String, Object> getLimitMetrics(String key) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("key", key);
        metrics.put(INSTANCE_ID_KEY, instanceId);
        metrics.put("timestamp", System.currentTimeMillis());
        metrics.put("status", "active");
        return metrics;
    }

    /**
     * 构建限流键
     *
     * @param key 限流标识符
     * @param algorithm 限流算法
     * @return 限流键
     */
    private String buildRateLimitKey(String key, String algorithm) {
        return CacheKeyUtil.buildKey(cacheProperties.getKeyPrefix(), RATE_LIMIT_PREFIX, algorithm, key);
    }

    /**
     * 执行具体的限流规则
     *
     * @param key 限流标识符
     * @param rule 限流规则
     * @param requestCount 本次请求数量
     * @return 限流结果
     */
    private RateLimitResult executeRule(String key, RateLimitRule rule, long requestCount) {
        String ruleKey = key + CommonConstants.CACHE_KEY_SEPARATOR + rule.getName();

        return switch (rule.getAlgorithm()) {
            case SLIDING_WINDOW -> slidingWindowLimit(ruleKey, rule.getWindowSize(), rule.getMaxRequests(), requestCount);
            case TOKEN_BUCKET -> tokenBucketLimit(ruleKey, rule.getMaxRequests(), rule.getRefillRate(), requestCount);
            case FIXED_WINDOW -> fixedWindowLimit(ruleKey, rule.getWindowSize(), rule.getMaxRequests(), requestCount);
            case LEAKY_BUCKET -> leakyBucketLimit(ruleKey, rule.getMaxRequests(), rule.getRefillRate(), requestCount);
            default -> throw new IllegalArgumentException("不支持的限流算法: " + rule.getAlgorithm());
        };
    }

    /**
     * 构建限流结果
     *
     * @param key 限流标识符
     * @param scriptResult 限流脚本返回结果
     * @param algorithm 限流算法
     * @param requestCount 本次请求数量
     * @param startTime 开始时间
     * @return 限流结果
     */
    private RateLimitResult buildResult(String key, List<Long> scriptResult, RateLimitAlgorithm algorithm,
                                       long requestCount, long startTime) {
        if (scriptResult == null || scriptResult.size() < 3) {
            throw new CacheException("限流脚本返回结果格式错误");
        }

        boolean allowed = scriptResult.get(0) == 1;
        long remainingQuota = scriptResult.get(1);
        long resetTimeMs = scriptResult.get(2);
        
        LocalDateTime resetTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(resetTimeMs), ZoneId.systemDefault());

        RateLimitResult.RateLimitResultBuilder builder = RateLimitResult.builder()
                .allowed(allowed)
                .key(key)
                .requestCount(requestCount)
                .remainingQuota(remainingQuota)
                .resetTime(resetTime)
                .algorithm(algorithm)
                .processingTimeNanos(System.nanoTime() - startTime);

        if (!allowed) {
            builder.reason(algorithm.getDescription() + "限流").retryAfterMs(resetTimeMs - System.currentTimeMillis());
        }

        return builder.build();
    }

    /**
     * 构建分布式限流结果
     *
     * @param key 限流标识符
     * @param scriptResult 限流脚本返回结果
     * @param requestCount 本次请求数量
     * @param startTime 开始时间
     * @return 限流结果
     */
    private RateLimitResult buildDistributedResult(String key, List<Long> scriptResult, long requestCount, long startTime) {
        if (scriptResult == null || scriptResult.size() < 4) {
            throw new CacheException("分布式限流脚本返回结果格式错误");
        }

        boolean allowed = scriptResult.get(0) == 1;
        long globalQuota = scriptResult.get(1);
        long instanceQuota = scriptResult.get(2);
        long resetTimeMs = scriptResult.get(3);
        
        LocalDateTime resetTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(resetTimeMs), ZoneId.systemDefault());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("globalQuota", globalQuota);
        metadata.put("instanceQuota", instanceQuota);
        metadata.put(INSTANCE_ID_KEY, instanceId);

        RateLimitResult.RateLimitResultBuilder builder = RateLimitResult.builder()
                .allowed(allowed)
                .key(key)
                .requestCount(requestCount)
                .remainingQuota(Math.min(globalQuota, instanceQuota))
                .resetTime(resetTime)
                .algorithm(RateLimitAlgorithm.SLIDING_WINDOW)
                .metadata(metadata)
                .processingTimeNanos(System.nanoTime() - startTime);

        if (!allowed) {
            builder.reason("分布式滑动窗口限流").retryAfterMs(resetTimeMs - System.currentTimeMillis());
        }

        return builder.build();
    }
} 