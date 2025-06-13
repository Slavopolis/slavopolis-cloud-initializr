package club.slavopolis.cache.config.autoconfigure;

import club.slavopolis.cache.config.RedisAutoConfiguration;
import club.slavopolis.cache.config.properties.CacheProperties;
import club.slavopolis.cache.service.CacheService;
import club.slavopolis.cache.service.impl.CacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 缓存模块自动配置类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({RedisTemplate.class, CacheService.class})
@ConditionalOnProperty(prefix = "slavopolis.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CacheProperties.class)
@Import(RedisAutoConfiguration.class)
public class CacheAutoConfiguration {

    /**
     * 注册 CacheService Bean
     *
     * @param redisTemplate Redis模板
     * @param cacheProperties 缓存配置属性
     * @return CacheService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheService cacheService(RedisTemplate<String, Object> redisTemplate, CacheProperties cacheProperties) {
        log.info("注册 CacheService Bean，配置: {}", cacheProperties.getKeyPrefix());
        return new CacheServiceImpl(redisTemplate, cacheProperties);
    }
} 