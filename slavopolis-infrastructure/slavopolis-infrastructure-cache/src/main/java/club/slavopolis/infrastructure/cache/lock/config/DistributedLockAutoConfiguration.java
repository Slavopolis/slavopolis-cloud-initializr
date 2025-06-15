package club.slavopolis.infrastructure.cache.lock.config;

import club.slavopolis.infrastructure.cache.lock.aspect.DistributedLockAspect;
import club.slavopolis.infrastructure.cache.lock.config.properties.DistributedLockProperties;
import club.slavopolis.infrastructure.cache.lock.core.DistributedLocker;
import club.slavopolis.infrastructure.cache.lock.core.LockKeyGenerator;
import club.slavopolis.infrastructure.cache.lock.impl.DefaultLockKeyGenerator;
import club.slavopolis.infrastructure.cache.lock.impl.RedisDistributedLocker;
import club.slavopolis.infrastructure.cache.lock.listener.LockEventListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁自动配置类
 *
 * <p>
 * 自动配置分布式锁相关的Bean，包括：
 * - Redisson 客户端
 * - 分布式锁实现
 * - 锁键生成器
 * - 切面处理器
 * - 事件监听器
 * </p>
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "distributed.lock", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DistributedLockProperties.class)
@Import({DistributedLockAspect.class, LockEventListener.class})
public class DistributedLockAutoConfiguration {

    /**
     * 配置 Redisson 客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(DistributedLockProperties properties) {
        log.info("Initializing Redisson client for distributed lock");

        Config config = new Config();
        DistributedLockProperties.RedisProperties redis = properties.getRedis();

        // 根据模式配置
        switch (redis.getMode()) {
            case SINGLE ->
                    config.useSingleServer()
                            .setAddress(redis.getAddress())
                            .setPassword(redis.getPassword())
                            .setDatabase(redis.getDatabase())
                            .setConnectionPoolSize(redis.getConnectionPoolSize())
                            .setConnectionMinimumIdleSize(redis.getConnectionMinimumIdleSize())
                            .setConnectTimeout(redis.getConnectTimeout())
                            .setTimeout(redis.getTimeout())
                            .setRetryAttempts(redis.getRetryAttempts())
                            .setRetryInterval(redis.getRetryInterval());
            case CLUSTER ->
                    config.useClusterServers()
                            .addNodeAddress(redis.getNodeAddresses().toArray(new String[0]))
                            .setPassword(redis.getPassword())
                            .setMasterConnectionPoolSize(redis.getConnectionPoolSize())
                            .setSlaveConnectionPoolSize(redis.getConnectionPoolSize())
                            .setConnectTimeout(redis.getConnectTimeout())
                            .setTimeout(redis.getTimeout())
                            .setRetryAttempts(redis.getRetryAttempts())
                            .setRetryInterval(redis.getRetryInterval());
            case SENTINEL ->
                    config.useSentinelServers()
                            .setMasterName(redis.getMasterName())
                            .addSentinelAddress(redis.getSentinelAddresses().toArray(new String[0]))
                            .setPassword(redis.getPassword())
                            .setDatabase(redis.getDatabase())
                            .setMasterConnectionPoolSize(redis.getConnectionPoolSize())
                            .setSlaveConnectionPoolSize(redis.getConnectionPoolSize())
                            .setConnectTimeout(redis.getConnectTimeout())
                            .setTimeout(redis.getTimeout())
                            .setRetryAttempts(redis.getRetryAttempts())
                            .setRetryInterval(redis.getRetryInterval());
        }

        // 设置编解码器
        config.setCodec(new org.redisson.codec.JsonJacksonCodec());

        // 设置线程池
        config.setThreads(properties.getThreads());
        config.setNettyThreads(properties.getNettyThreads());

        return Redisson.create(config);
    }

    /**
     * 配置分布式锁实现
     */
    @Bean
    @ConditionalOnMissingBean
    public DistributedLocker distributedLocker(RedissonClient redissonClient) {
        log.info("Initializing Redis distributed locker");
        return new RedisDistributedLocker(redissonClient);
    }

    /**
     * 配置锁键生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public LockKeyGenerator lockKeyGenerator() {
        log.info("Initializing default lock key generator");
        return new DefaultLockKeyGenerator();
    }

    /**
     * 配置分布式锁切面
     */
    @Bean
    @ConditionalOnMissingBean
    public DistributedLockAspect distributedLockAspect(
            DistributedLocker distributedLocker,
            LockKeyGenerator lockKeyGenerator,
            ApplicationEventPublisher eventPublisher) {
        log.info("Initializing distributed lock aspect");
        return new DistributedLockAspect(distributedLocker, lockKeyGenerator, eventPublisher);
    }
}
