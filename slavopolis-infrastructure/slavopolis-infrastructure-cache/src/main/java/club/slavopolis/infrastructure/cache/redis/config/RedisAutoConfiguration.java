package club.slavopolis.infrastructure.cache.redis.config;

import club.slavopolis.infrastructure.cache.redis.config.properties.CacheProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;

/**
 * Redis 自动配置类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(prefix = "slavopolis.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CacheProperties.class)
public class RedisAutoConfiguration {

    private final CacheProperties cacheProperties;

    public RedisAutoConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
        log.info("Redis 缓存模块初始化，配置前缀: {}", cacheProperties.getKeyPrefix());
    }

    /**
     * 创建主要的 RedisTemplate，使用 JSON 序列化
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 配置序列化器
        RedisSerializer<String> stringSerializer = createStringSerializer();
        RedisSerializer<Object> jsonSerializer = createJsonSerializer();

        // 设置key序列化方式
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 设置value序列化方式
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // 设置默认序列化器
        template.setDefaultSerializer(jsonSerializer);
        
        // 启用事务支持
        template.setEnableTransactionSupport(true);
        
        template.afterPropertiesSet();
        
        log.info("RedisTemplate 配置完成，使用 JSON 序列化");
        return template;
    }

    /**
     * 创建 StringRedisTemplate
     *
     * @param connectionFactory Redis连接工厂
     * @return StringRedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);
        log.info("StringRedisTemplate 配置完成");
        return template;
    }

    /**
     * 创建用于JDK序列化的RedisTemplate
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean("jdkRedisTemplate")
    @ConditionalOnMissingBean(name = "jdkRedisTemplate")
    public RedisTemplate<String, Object> jdkRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<String> stringSerializer = createStringSerializer();
        RedisSerializer<Object> jdkSerializer = new JdkSerializationRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jdkSerializer);
        template.setHashValueSerializer(jdkSerializer);
        template.setDefaultSerializer(jdkSerializer);
        template.setEnableTransactionSupport(true);
        
        template.afterPropertiesSet();
        
        log.info("JDK RedisTemplate 配置完成");
        return template;
    }

    /**
     * 创建字符串序列化器
     *
     * @return StringRedisSerializer
     */
    private RedisSerializer<String> createStringSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 创建JSON序列化器
     *
     * @return Jackson2JsonRedisSerializer
     */
    private RedisSerializer<Object> createJsonSerializer() {
        // 创建ObjectMapper
        ObjectMapper objectMapper = createObjectMapper();
        
        // 创建JSON序列化器
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 创建和配置ObjectMapper
     *
     * @return ObjectMapper
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 设置可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // 启用默认类型，用于序列化时包含类型信息
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        // 注册Java时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期写为时间戳
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 忽略未知属性
        objectMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        return objectMapper;
    }

    /**
     * 创建GenericJackson2JsonRedisSerializer（另一种JSON序列化选择）
     *
     * @return GenericJackson2JsonRedisSerializer
     */
    @Bean("genericJsonRedisSerializer")
    @ConditionalOnMissingBean(name = "genericJsonRedisSerializer")
    public RedisSerializer<Object> genericJsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(createObjectMapper());
    }

    /**
     * 创建用于通用JSON序列化的RedisTemplate
     *
     * @param connectionFactory Redis连接工厂
     * @param genericJsonRedisSerializer 通用JSON序列化器
     * @return RedisTemplate
     */
    @Bean("genericJsonRedisTemplate")
    @ConditionalOnMissingBean(name = "genericJsonRedisTemplate")
    public RedisTemplate<String, Object> genericJsonRedisTemplate(
            RedisConnectionFactory connectionFactory,
            RedisSerializer<Object> genericJsonRedisSerializer) {
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<String> stringSerializer = createStringSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(genericJsonRedisSerializer);
        template.setHashValueSerializer(genericJsonRedisSerializer);
        template.setDefaultSerializer(genericJsonRedisSerializer);
        template.setEnableTransactionSupport(true);
        
        template.afterPropertiesSet();
        
        log.info("Generic JSON RedisTemplate 配置完成");
        return template;
    }
} 