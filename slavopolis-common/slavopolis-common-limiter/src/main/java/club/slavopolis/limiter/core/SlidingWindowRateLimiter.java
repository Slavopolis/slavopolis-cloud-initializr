package club.slavopolis.limiter.core;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * 滑动窗口限流服务实现
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@RequiredArgsConstructor
public class SlidingWindowRateLimiter implements RateLimiter {

    private final RedissonClient redissonClient;

    private static final String LIMIT_KEY_PREFIX = "slavopolis:limit:";

    /**
     * 尝试获取令牌
     *
     * @param key        令牌桶的key
     * @param limit      令牌桶的容量
     * @param windowSize 令牌桶的窗口大小
     * @return 是否获取成功
     */
    @Override
    public Boolean tryAcquire(String key, long limit, long windowSize) {
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(LIMIT_KEY_PREFIX + key);
        if (!rRateLimiter.isExists()) {
            rRateLimiter.trySetRate(RateType.OVERALL, limit, windowSize, RateIntervalUnit.SECONDS);
        }
        return rRateLimiter.tryAcquire();
    }
}
