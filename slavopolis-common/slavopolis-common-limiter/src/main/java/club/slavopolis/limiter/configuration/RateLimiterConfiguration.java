package club.slavopolis.limiter.configuration;

import club.slavopolis.limiter.core.SlidingWindowRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 限流配置类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@SpringBootConfiguration
public class RateLimiterConfiguration {

    @Bean
    public SlidingWindowRateLimiter slidingWindowRateLimiter(RedissonClient redissonClient) {
        return new SlidingWindowRateLimiter(redissonClient);
    }
}
