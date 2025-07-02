package club.slavopolis.lock.config;

import club.slavopolis.lock.aspect.DistributeLockAspect;
import club.slavopolis.lock.manager.LockManager;
import club.slavopolis.lock.service.DistributeLockService;
import club.slavopolis.lock.service.impl.RedissonDistributeLockServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 分布式锁配置类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@SpringBootConfiguration
public class DistributeLockConfiguration {

    /**
     * 配置锁管理器Bean
     * <p>
     * 创建分布式锁管理器实例，负责锁实例的缓存和生命周期管理。
     * </p>
     *
     * @return 锁管理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public LockManager lockManager() {
        return new LockManager();
    }

    /**
     * 配置分布式锁服务Bean
     * <p>
     * 创建编程式分布式锁服务实例，提供手动控制锁生命周期的API。
     * 依赖RedissonClient和LockManager实现完整的锁功能。
     * </p>
     *
     * @param redissonClient Redis分布式锁客户端
     * @param lockManager 锁管理器
     * @return 分布式锁服务实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DistributeLockService distributeLockService(RedissonClient redissonClient, LockManager lockManager) {
        return new RedissonDistributeLockServiceImpl(redissonClient, lockManager);
    }

    /**
     * 配置分布式锁切面Bean
     * <p>
     * 创建分布式锁AOP切面实例，依赖RedissonClient实现锁功能。
     * 使用@ConditionalOnMissingBean确保只有在容器中没有该类型Bean时才创建。
     * </p>
     *
     * @param redissonClient Redis分布式锁客户端
     * @return 分布式锁切面实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DistributeLockAspect distributeLockAspect(RedissonClient redissonClient) {
        return new DistributeLockAspect(redissonClient);
    }
}
