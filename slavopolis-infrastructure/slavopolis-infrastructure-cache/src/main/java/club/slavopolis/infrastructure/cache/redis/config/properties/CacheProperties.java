package club.slavopolis.infrastructure.cache.redis.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存配置属性
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
@Data
@ConfigurationProperties(prefix = "slavopolis.cache")
public class CacheProperties {

    /**
     * 是否启用Redis缓存，默认启用
     */
    private boolean enabled = true;

    /**
     * 全局缓存键前缀
     */
    private String keyPrefix = "slavopolis";

    /**
     * 默认缓存过期时间，默认1小时
     */
    private Duration defaultExpiration = Duration.ofHours(1);

    /**
     * 是否启用空值缓存，防止缓存穿透
     */
    private boolean allowNullValues = true;

    /**
     * 空值缓存时间，默认5分钟
     */
    private Duration nullValueExpiration = Duration.ofMinutes(5);

    /**
     * 是否启用缓存统计
     */
    private boolean enableStatistics = false;

    /**
     * 批量操作默认大小
     */
    private int batchSize = 1000;

    /**
     * 序列化配置
     */
    @NestedConfigurationProperty
    private SerializationConfig serialization = new SerializationConfig();

    /**
     * 缓存名称配置映射
     */
    private Map<String, CacheConfig> caches = new HashMap<>();

    /**
     * 序列化配置
     */
    @Data
    public static class SerializationConfig {
        
        /**
         * 键序列化类型：STRING, JSON
         */
        private SerializationType keyType = SerializationType.STRING;
        
        /**
         * 值序列化类型：STRING, JSON, JDK, FASTJSON2
         */
        private SerializationType valueType = SerializationType.JSON;
        
        /**
         * 哈希键序列化类型
         */
        private SerializationType hashKeyType = SerializationType.STRING;
        
        /**
         * 哈希值序列化类型
         */
        private SerializationType hashValueType = SerializationType.JSON;
    }

    /**
     * 单个缓存名称配置
     */
    @Data
    public static class CacheConfig {
        
        /**
         * 缓存过期时间
         */
        private Duration expiration;
        
        /**
         * 是否允许空值
         */
        private Boolean allowNullValues;
        
        /**
         * 空值过期时间
         */
        private Duration nullValueExpiration;
        
        /**
         * 缓存键前缀
         */
        private String keyPrefix;
    }

    /**
     * 序列化类型枚举
     */
    public enum SerializationType {
        STRING, JSON, JDK, FASTJSON2
    }
} 