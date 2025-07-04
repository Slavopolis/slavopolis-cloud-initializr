package club.slavopolis.limiter.core;

/**
 * 限流服务
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface RateLimiter {

    /**
     * 尝试获取令牌
     *
     * @param key           令牌桶的key
     * @param limit         令牌桶的容量
     * @param windowSize    令牌桶的窗口大小
     * @return 是否获取成功
     */
    Boolean tryAcquire(String key, long limit, long windowSize);
}
