package club.slavopolis.cache.script;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 限流算法Lua脚本集合，提供各种限流算法的高性能原子操作脚本
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.script
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimitScripts {

    /**
     * 滑动窗口限流脚本
     * <p>
     * 参数说明：
     * <ul>
     * <li>KEYS[1] - 限流键</li>
     * <li>ARGV[1] - 窗口大小（秒）</li>
     * <li>ARGV[2] - 最大请求数</li>
     * <li>ARGV[3] - 当前时间戳（毫秒）</li>
     * <li>ARGV[4] - 请求数量</li>
     * </ul>
     * <p>
     * 返回值：{允许标志(0/1), 剩余配额, 窗口重置时间}
     */
    public static final String SLIDING_WINDOW_SCRIPT = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local requests = tonumber(ARGV[4])
        
        -- 清理过期数据（当前时间-窗口大小之前的数据）
        local clearTime = now - (window * 1000)
        redis.call('ZREMRANGEBYSCORE', key, 0, clearTime)
        
        -- 获取当前窗口内的请求数
        local currentCount = redis.call('ZCARD', key)
        
        -- 检查是否超出限制
        if currentCount + requests > limit then
            -- 计算重置时间（最早请求的时间 + 窗口大小）
            local earliest = redis.call('ZRANGE', key, 0, 0, 'WITHSCORES')
            local resetTime = now
            if #earliest > 0 then
                resetTime = earliest[2] + (window * 1000)
            end
            return {0, limit - currentCount, resetTime}
        end
        
        -- 添加当前请求到窗口
        for i = 1, requests do
            redis.call('ZADD', key, now + i - 1, now .. '-' .. i)
        end
        
        -- 设置键的过期时间
        redis.call('EXPIRE', key, window + 1)
        
        -- 返回成功结果
        return {1, limit - currentCount - requests, now + (window * 1000)}
        """;

    /**
     * 令牌桶限流脚本
     * <p>
     * 参数说明：
     * <ul>
     * <li>KEYS[1] - 令牌桶键</li>
     * <li>ARGV[1] - 桶容量</li>
     * <li>ARGV[2] - 补充速率（令牌/秒）</li>
     * <li>ARGV[3] - 当前时间戳（毫秒）</li>
     * <li>ARGV[4] - 请求令牌数</li>
     * </ul>
     * <p>
     * 返回值：{允许标志(0/1), 剩余令牌数, 下次补充时间}
     */
    public static final String TOKEN_BUCKET_SCRIPT = """
        local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local refillRate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local tokens = tonumber(ARGV[4])
        
        -- 获取桶信息
        local bucket = redis.call('HMGET', key, 'tokens', 'lastRefill')
        local currentTokens = tonumber(bucket[1]) or capacity
        local lastRefill = tonumber(bucket[2]) or now
        
        -- 计算需要补充的令牌数
        local timePassed = math.max(0, now - lastRefill)
        local tokensToAdd = math.floor((timePassed / 1000) * refillRate)
        currentTokens = math.min(capacity, currentTokens + tokensToAdd)
        
        -- 检查是否有足够的令牌
        if currentTokens < tokens then
            -- 更新桶状态
            redis.call('HMSET', key, 'tokens', currentTokens, 'lastRefill', now)
            redis.call('EXPIRE', key, 3600)
            
            -- 计算下次有足够令牌的时间
            local tokensNeeded = tokens - currentTokens
            local waitTime = math.ceil(tokensNeeded / refillRate * 1000)
            return {0, currentTokens, now + waitTime}
        end
        
        -- 消费令牌
        currentTokens = currentTokens - tokens
        
        -- 更新桶状态
        redis.call('HMSET', key, 'tokens', currentTokens, 'lastRefill', now)
        redis.call('EXPIRE', key, 3600)
        
        return {1, currentTokens, now}
        """;

    /**
     * 固定窗口限流脚本
     * <p>
     * 参数说明：
     * <ul>
     * <li>KEYS[1] - 限流键</li>
     * <li>ARGV[1] - 窗口大小（秒）</li>
     * <li>ARGV[2] - 最大请求数</li>
     * <li>ARGV[3] - 当前时间戳（毫秒）</li>
     * <li>ARGV[4] - 请求数量</li>
     * </ul>
     * <p>
     * 返回值：{允许标志(0/1), 剩余配额, 窗口重置时间}
     */
    public static final String FIXED_WINDOW_SCRIPT = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local requests = tonumber(ARGV[4])
        
        -- 计算当前窗口的开始时间
        local windowStart = math.floor(now / (window * 1000)) * (window * 1000)
        local windowKey = key .. ':' .. windowStart
        
        -- 获取当前窗口的计数
        local currentCount = tonumber(redis.call('GET', windowKey)) or 0
        
        -- 检查是否超出限制
        if currentCount + requests > limit then
            local resetTime = windowStart + (window * 1000)
            return {0, limit - currentCount, resetTime}
        end
        
        -- 增加计数
        local newCount = redis.call('INCRBY', windowKey, requests)
        
        -- 设置过期时间
        if newCount == requests then
            redis.call('EXPIRE', windowKey, window + 1)
        end
        
        local resetTime = windowStart + (window * 1000)
        return {1, limit - newCount, resetTime}
        """;

    /**
     * 漏桶限流脚本
     * <p>
     * 参数说明：
     * <ul>
     * <li>KEYS[1] - 漏桶键</li>
     * <li>ARGV[1] - 桶容量</li>
     * <li>ARGV[2] - 漏水速率（请求/秒）</li>
     * <li>ARGV[3] - 当前时间戳（毫秒）</li>
     * <li>ARGV[4] - 请求数量</li>
     * </ul>
     * <p>
     * 返回值：{允许标志(0/1), 桶中剩余容量, 预计处理时间}
     */
    public static final String LEAKY_BUCKET_SCRIPT = """
        local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local leakRate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local requests = tonumber(ARGV[4])
        
        -- 获取桶信息
        local bucket = redis.call('HMGET', key, 'volume', 'lastLeak')
        local currentVolume = tonumber(bucket[1]) or 0
        local lastLeak = tonumber(bucket[2]) or now
        
        -- 计算漏出的水量
        local timePassed = math.max(0, now - lastLeak)
        local leaked = math.floor((timePassed / 1000) * leakRate)
        currentVolume = math.max(0, currentVolume - leaked)
        
        -- 检查桶是否能容纳新请求
        if currentVolume + requests > capacity then
            -- 更新桶状态
            redis.call('HMSET', key, 'volume', currentVolume, 'lastLeak', now)
            redis.call('EXPIRE', key, 3600)
            
            -- 计算处理时间
            local overflow = currentVolume + requests - capacity
            local waitTime = math.ceil(overflow / leakRate * 1000)
            return {0, capacity - currentVolume, now + waitTime}
        end
        
        -- 添加请求到桶中
        currentVolume = currentVolume + requests
        
        -- 更新桶状态
        redis.call('HMSET', key, 'volume', currentVolume, 'lastLeak', now)
        redis.call('EXPIRE', key, 3600)
        
        -- 计算预计处理时间
        local processTime = math.ceil(currentVolume / leakRate * 1000)
        return {1, capacity - currentVolume, now + processTime}
        """;

    /**
     * 分布式滑动窗口限流脚本，支持多实例环境下的公平限流
     * <p>
     * 参数说明：
     * <ul>
     * <li>KEYS[1] - 全局限流键</li>
     * <li>KEYS[2] - 实例限流键</li>
     * <li>ARGV[1] - 窗口大小（秒）</li>
     * <li>ARGV[2] - 全局最大请求数</li>
     * <li>ARGV[3] - 当前时间戳（毫秒）</li>
     * <li>ARGV[4] - 请求数量</li>
     * <li>ARGV[5] - 实例ID</li>
     * </ul>
     * <p>
     * 返回值：{允许标志(0/1), 全局剩余配额, 实例剩余配额, 重置时间}
     */
    public static final String DISTRIBUTED_SLIDING_WINDOW_SCRIPT = """
        local globalKey = KEYS[1]
        local instanceKey = KEYS[2]
        local window = tonumber(ARGV[1])
        local globalLimit = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        local requests = tonumber(ARGV[4])
        local instanceId = ARGV[5]
        
        -- 清理过期数据
        local clearTime = now - (window * 1000)
        redis.call('ZREMRANGEBYSCORE', globalKey, 0, clearTime)
        redis.call('ZREMRANGEBYSCORE', instanceKey, 0, clearTime)
        
        -- 获取全局和实例当前请求数
        local globalCount = redis.call('ZCARD', globalKey)
        local instanceCount = redis.call('ZCARD', instanceKey)
        
        -- 计算实例限制（全局限制的1/3，但至少为1）
        local instanceLimit = math.max(1, math.floor(globalLimit / 3))
        
        -- 检查全局限制
        if globalCount + requests > globalLimit then
            return {0, globalLimit - globalCount, instanceLimit - instanceCount, now + (window * 1000)}
        end
        
        -- 检查实例限制
        if instanceCount + requests > instanceLimit then
            return {0, globalLimit - globalCount, instanceLimit - instanceCount, now + (window * 1000)}
        end
        
        -- 添加请求记录
        for i = 1, requests do
            local requestId = instanceId .. '-' .. now .. '-' .. i
            redis.call('ZADD', globalKey, now + i - 1, requestId)
            redis.call('ZADD', instanceKey, now + i - 1, requestId)
        end
        
        -- 设置过期时间
        redis.call('EXPIRE', globalKey, window + 1)
        redis.call('EXPIRE', instanceKey, window + 1)
        
        return {1, globalLimit - globalCount - requests, instanceLimit - instanceCount - requests, now + (window * 1000)}
        """;
} 