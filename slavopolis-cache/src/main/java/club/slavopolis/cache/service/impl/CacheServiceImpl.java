package club.slavopolis.cache.service.impl;

import club.slavopolis.cache.config.properties.CacheProperties;
import club.slavopolis.cache.exception.CacheException;
import club.slavopolis.cache.service.CacheService;
import club.slavopolis.cache.util.CacheKeyUtil;
import club.slavopolis.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 缓存服务实现类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    /**
     * 空值占位符，用于防止缓存穿透
     */
    private static final String NULL_VALUE_PLACEHOLDER = "___NULL___";

    public CacheServiceImpl(@Autowired RedisTemplate<String, Object> redisTemplate, @Autowired CacheProperties cacheProperties) {
        this.redisTemplate = redisTemplate;
        this.cacheProperties = cacheProperties;
        log.info("CacheService 初始化完成，键前缀: {}", cacheProperties.getKeyPrefix());
    }

    // ================================
    // 通用操作实现
    // ================================

    @Override
    public boolean hasKey(String key) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.hasKey(finalKey);
        } catch (Exception e) {
            log.error("检查键是否存在失败，key: {}", key, e);
            throw new CacheException("检查键是否存在失败", e);
        }
    }

    @Override
    public boolean delete(String key) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.delete(finalKey);
        } catch (Exception e) {
            log.error("删除缓存失败，key: {}", key, e);
            throw new CacheException("删除缓存失败", e);
        }
    }

    @Override
    public long delete(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0;
        }

        try {
            Collection<String> finalKeys = keys.stream()
                    .map(this::buildKey)
                    .toList();

            return redisTemplate.delete(finalKeys);
        } catch (Exception e) {
            log.error("批量删除缓存失败，keys: {}", keys, e);
            throw new CacheException("批量删除缓存失败", e);
        }
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.expire(finalKey, timeout, unit);
        } catch (Exception e) {
            log.error("设置过期时间失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            throw new CacheException("设置过期时间失败", e);
        }
    }

    @Override
    public boolean expire(String key, Duration duration) {
        return expire(key, duration.toSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.getExpire(finalKey, unit);
        } catch (Exception e) {
            log.error("获取过期时间失败，key: {}", key, e);
            throw new CacheException("获取过期时间失败", e);
        }
    }

    @Override
    public boolean persist(String key) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.persist(finalKey);
        } catch (Exception e) {
            log.error("移除过期时间失败，key: {}", key, e);
            throw new CacheException("移除过期时间失败", e);
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        try {
            String finalPattern = buildKey(pattern);
            Set<String> keys = redisTemplate.keys(finalPattern);

            // 移除键前缀，返回业务键
            String prefix = cacheProperties.getKeyPrefix() + CommonConstants.CACHE_KEY_SEPARATOR;
            return keys.stream()
                    .map(key -> key.startsWith(prefix) ? key.substring(prefix.length()) : key)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("查找键失败，pattern: {}", pattern, e);
            throw new CacheException("查找键失败", e);
        }
    }

    @Override
    public String randomKey() {
        try {
            String key = redisTemplate.randomKey();
            String prefix = cacheProperties.getKeyPrefix() + CommonConstants.CACHE_KEY_SEPARATOR;
            return key.startsWith(prefix) ? key.substring(prefix.length()) : key;
        } catch (Exception e) {
            log.error("获取随机键失败", e);
            throw new CacheException("获取随机键失败", e);
        }
    }

    @Override
    public void rename(String oldKey, String newKey) {
        try {
            String finalOldKey = buildKey(oldKey);
            String finalNewKey = buildKey(newKey);
            redisTemplate.rename(finalOldKey, finalNewKey);
        } catch (Exception e) {
            log.error("重命名键失败，oldKey: {}, newKey: {}", oldKey, newKey, e);
            throw new CacheException("重命名键失败", e);
        }
    }

    @Override
    public String type(String key) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.type(finalKey).getClass().getSimpleName();
        } catch (Exception e) {
            log.error("获取键类型失败，key: {}", key, e);
            throw new CacheException("获取键类型失败", e);
        }
    }

    // ================================
    // 字符串操作实现
    // ================================

    @Override
    public void set(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            redisTemplate.opsForValue().set(finalKey, value);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}", key, e);
            throw new CacheException("设置缓存失败", e);
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String finalKey = buildKey(key);
            redisTemplate.opsForValue().set(finalKey, value, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            throw new CacheException("设置缓存失败", e);
        }
    }

    @Override
    public void set(String key, Object value, Duration duration) {
        set(key, value, duration.toSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(finalKey, value));
        } catch (Exception e) {
            log.error("条件设置缓存失败，key: {}", key, e);
            throw new CacheException("条件设置缓存失败", e);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String finalKey = buildKey(key);
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(finalKey, value, timeout, unit));
        } catch (Exception e) {
            log.error("条件设置缓存失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            throw new CacheException("条件设置缓存失败", e);
        }
    }

    @Override
    public boolean setIfPresent(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(finalKey, value));
        } catch (Exception e) {
            log.error("条件设置缓存失败，key: {}", key, e);
            throw new CacheException("条件设置缓存失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForValue().get(finalKey);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("获取缓存失败，key: {}", key, e);
            throw new CacheException("获取缓存失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = get(key);
            if (value == null) {
                return null;
            }
            
            if (type.isInstance(value)) {
                return (T) value;
            }
            
            // 如果类型不匹配，尝试转换（这里可以根据需要扩展转换逻辑）
            log.warn("缓存值类型不匹配，key: {}, expected: {}, actual: {}", 
                     key, type.getName(), value.getClass().getName());
            return null;
        } catch (Exception e) {
            log.error("获取缓存失败，key: {}, type: {}", key, type.getName(), e);
            throw new CacheException("获取缓存失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAndSet(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            Object oldValue = redisTemplate.opsForValue().getAndSet(finalKey, value);
            return processNullValue(oldValue);
        } catch (Exception e) {
            log.error("获取并设置缓存失败，key: {}", key, e);
            throw new CacheException("获取并设置缓存失败", e);
        }
    }

    @Override
    public void multiSet(Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        try {
            Map<String, Object> finalMap = map.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> buildKey(entry.getKey()),
                            Map.Entry::getValue
                    ));
            
            redisTemplate.opsForValue().multiSet(finalMap);
        } catch (Exception e) {
            log.error("批量设置缓存失败", e);
            throw new CacheException("批量设置缓存失败", e);
        }
    }

    @Override
    public List<Object> multiGet(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new ArrayList<>();
        }

        try {
            Collection<String> finalKeys = keys.stream()
                    .map(this::buildKey)
                    .toList();
            
            List<Object> values = redisTemplate.opsForValue().multiGet(finalKeys);
            return values != null ? values.stream()
                    .map(this::processNullValue)
                    .toList() : new ArrayList<>();
        } catch (Exception e) {
            log.error("批量获取缓存失败，keys: {}", keys, e);
            throw new CacheException("批量获取缓存失败", e);
        }
    }

    @Override
    public long increment(String key) {
        return increment(key, 1L);
    }

    @Override
    public long increment(String key, long delta) {
        try {
            String finalKey = buildKey(key);
            Long result = redisTemplate.opsForValue().increment(finalKey, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("数值自增失败，key: {}, delta: {}", key, delta, e);
            throw new CacheException("数值自增失败", e);
        }
    }

    @Override
    public double increment(String key, double delta) {
        try {
            String finalKey = buildKey(key);
            Double result = redisTemplate.opsForValue().increment(finalKey, delta);
            return result != null ? result : 0.0;
        } catch (Exception e) {
            log.error("浮点数自增失败，key: {}, delta: {}", key, delta, e);
            throw new CacheException("浮点数自增失败", e);
        }
    }

    @Override
    public long decrement(String key) {
        return decrement(key, 1L);
    }

    @Override
    public long decrement(String key, long delta) {
        return increment(key, -delta);
    }

    // ================================
    // 哈希操作实现
    // ================================

    @Override
    public void hSet(String key, String field, Object value) {
        try {
            String finalKey = buildKey(key);
            redisTemplate.opsForHash().put(finalKey, field, value);
        } catch (Exception e) {
            log.error("设置哈希字段失败，key: {}, field: {}", key, field, e);
            throw new CacheException("设置哈希字段失败", e);
        }
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        try {
            String finalKey = buildKey(key);
            redisTemplate.opsForHash().putAll(finalKey, map);
        } catch (Exception e) {
            log.error("批量设置哈希字段失败，key: {}", key, e);
            throw new CacheException("批量设置哈希字段失败", e);
        }
    }

    @Override
    public boolean hSetIfAbsent(String key, String field, Object value) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForHash().putIfAbsent(finalKey, field, value);
        } catch (Exception e) {
            log.error("条件设置哈希字段失败，key: {}, field: {}", key, field, e);
            throw new CacheException("条件设置哈希字段失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String field) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForHash().get(finalKey, field);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("获取哈希字段失败，key: {}, field: {}", key, field, e);
            throw new CacheException("获取哈希字段失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String field, Class<T> type) {
        try {
            Object value = hGet(key, field);
            if (value == null) {
                return null;
            }
            
            if (type.isInstance(value)) {
                return (T) value;
            }
            
            log.warn("哈希字段值类型不匹配，key: {}, field: {}, expected: {}, actual: {}", 
                     key, field, type.getName(), value.getClass().getName());
            return null;
        } catch (Exception e) {
            log.error("获取哈希字段失败，key: {}, field: {}, type: {}", key, field, type.getName(), e);
            throw new CacheException("获取哈希字段失败", e);
        }
    }

    @Override
    public List<Object> hMultiGet(String key, Collection<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return new ArrayList<>();
        }

        try {
            String finalKey = buildKey(key);
            Collection<Object> objectFields = new ArrayList<>(fields);
            List<Object> values = redisTemplate.opsForHash().multiGet(finalKey, objectFields);
            return values.stream()
                    .map(this::processNullValue)
                    .toList();
        } catch (Exception e) {
            log.error("批量获取哈希字段失败，key: {}, fields: {}", key, fields, e);
            throw new CacheException("批量获取哈希字段失败", e);
        }
    }

    @Override
    public Map<String, Object> hGetAll(String key) {
        try {
            String finalKey = buildKey(key);
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(finalKey);
            
            if (CollectionUtils.isEmpty(entries)) {
                return new HashMap<>();
            }
            
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String fieldKey = entry.getKey().toString();
                Object fieldValue = processNullValue(entry.getValue());
                result.put(fieldKey, fieldValue);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取哈希所有字段失败，key: {}", key, e);
            throw new CacheException("获取哈希所有字段失败", e);
        }
    }

    @Override
    public boolean hExists(String key, String field) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForHash().hasKey(finalKey, field);
        } catch (Exception e) {
            log.error("检查哈希字段是否存在失败，key: {}, field: {}", key, field, e);
            throw new CacheException("检查哈希字段是否存在失败", e);
        }
    }

    @Override
    public long hDelete(String key, String... fields) {
        if (fields == null || fields.length == 0) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForHash().delete(finalKey, (Object[]) fields);
        } catch (Exception e) {
            log.error("删除哈希字段失败，key: {}, fields: {}", key, Arrays.toString(fields), e);
            throw new CacheException("删除哈希字段失败", e);
        }
    }

    @Override
    public long hSize(String key) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForHash().size(finalKey);
        } catch (Exception e) {
            log.error("获取哈希大小失败，key: {}", key, e);
            throw new CacheException("获取哈希大小失败", e);
        }
    }

    @Override
    public Set<String> hKeys(String key) {
        try {
            String finalKey = buildKey(key);
            Set<Object> keys = redisTemplate.opsForHash().keys(finalKey);
            
            if (CollectionUtils.isEmpty(keys)) {
                return new HashSet<>();
            }
            
            return keys.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取哈希字段名失败，key: {}", key, e);
            throw new CacheException("获取哈希字段名失败", e);
        }
    }

    @Override
    public List<Object> hValues(String key) {
        try {
            String finalKey = buildKey(key);
            List<Object> values = redisTemplate.opsForHash().values(finalKey);
            
            if (CollectionUtils.isEmpty(values)) {
                return new ArrayList<>();
            }
            
            return values.stream()
                    .map(this::processNullValue)
                    .toList();
        } catch (Exception e) {
            log.error("获取哈希字段值失败，key: {}", key, e);
            throw new CacheException("获取哈希字段值失败", e);
        }
    }

    @Override
    public long hIncrement(String key, String field, long delta) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForHash().increment(finalKey, field, delta);
        } catch (Exception e) {
            log.error("哈希字段自增失败，key: {}, field: {}, delta: {}", key, field, delta, e);
            throw new CacheException("哈希字段自增失败", e);
        }
    }

    @Override
    public double hIncrement(String key, String field, double delta) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForHash().increment(finalKey, field, delta);
        } catch (Exception e) {
            log.error("哈希字段浮点数自增失败，key: {}, field: {}, delta: {}", key, field, delta, e);
            throw new CacheException("哈希字段浮点数自增失败", e);
        }
    }

    // ================================
    // 工具方法
    // ================================

    /**
     * 构建完整的缓存键
     *
     * @param key 业务键
     * @return 完整的缓存键
     */
    private String buildKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("缓存键不能为空");
        }
        
        return CacheKeyUtil.buildKey(cacheProperties.getKeyPrefix(), key);
    }

    /**
     * 处理空值占位符
     *
     * @param value 原始值
     * @return 处理后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T processNullValue(Object value) {
        if (NULL_VALUE_PLACEHOLDER.equals(value)) {
            return null;
        }
        return (T) value;
    }



    // ================================
    // 列表操作实现
    // ================================

    @Override
    public long lLeftPush(String key, Object... values) {
        if (values == null || values.length == 0) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            Long result = redisTemplate.opsForList().leftPushAll(finalKey, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("左侧推入列表失败，key: {}", key, e);
            throw new CacheException("左侧推入列表失败", e);
        }
    }

    @Override
    public long lRightPush(String key, Object... values) {
        if (values == null || values.length == 0) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            Long result = redisTemplate.opsForList().rightPushAll(finalKey, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("右侧推入列表失败，key: {}", key, e);
            throw new CacheException("右侧推入列表失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T lLeftPop(String key) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForList().leftPop(finalKey);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("左侧弹出列表失败，key: {}", key, e);
            throw new CacheException("左侧弹出列表失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T lRightPop(String key) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForList().rightPop(finalKey);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("右侧弹出列表失败，key: {}", key, e);
            throw new CacheException("右侧弹出列表失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T lBlockingLeftPop(String key, long timeout, TimeUnit unit) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForList().leftPop(finalKey, timeout, unit);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("阻塞左侧弹出列表失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            throw new CacheException("阻塞左侧弹出列表失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T lBlockingRightPop(String key, long timeout, TimeUnit unit) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForList().rightPop(finalKey, timeout, unit);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("阻塞右侧弹出列表失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            throw new CacheException("阻塞右侧弹出列表失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> lRange(String key, long start, long end) {
        try {
            String finalKey = buildKey(key);
            List<Object> values = redisTemplate.opsForList().range(finalKey, start, end);
            
            if (CollectionUtils.isEmpty(values)) {
                return new ArrayList<>();
            }
            
            return values.stream()
                    .map(value -> (T) processNullValue(value))
                    .toList();
        } catch (Exception e) {
            log.error("获取列表范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            throw new CacheException("获取列表范围失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T lIndex(String key, long index) {
        try {
            String finalKey = buildKey(key);
            Object value = redisTemplate.opsForList().index(finalKey, index);
            return processNullValue(value);
        } catch (Exception e) {
            log.error("获取列表索引失败，key: {}, index: {}", key, index, e);
            throw new CacheException("获取列表索引失败", e);
        }
    }

    @Override
    public void lSet(String key, long index, Object value) {
        try {
            String finalKey = buildKey(key);
            redisTemplate.opsForList().set(finalKey, index, value);
        } catch (Exception e) {
            log.error("设置列表索引失败，key: {}, index: {}", key, index, e);
            throw new CacheException("设置列表索引失败", e);
        }
    }

    @Override
    public long lSize(String key) {
        try {
            String finalKey = buildKey(key);
            Long size = redisTemplate.opsForList().size(finalKey);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取列表长度失败，key: {}", key, e);
            throw new CacheException("获取列表长度失败", e);
        }
    }

    @Override
    public long lRemove(String key, long count, Object value) {
        try {
            String finalKey = buildKey(key);
            Long removedCount = redisTemplate.opsForList().remove(finalKey, count, value);
            return removedCount != null ? removedCount : 0;
        } catch (Exception e) {
            log.error("移除列表元素失败，key: {}, count: {}", key, count, e);
            throw new CacheException("移除列表元素失败", e);
        }
    }

    @Override
    public void lTrim(String key, long start, long end) {
        try {
            String finalKey = buildKey(key);
            redisTemplate.opsForList().trim(finalKey, start, end);
        } catch (Exception e) {
            log.error("裁剪列表失败，key: {}, start: {}, end: {}", key, start, end, e);
            throw new CacheException("裁剪列表失败", e);
        }
    }

    // ================================
    // 集合操作实现
    // ================================

    @Override
    public long sAdd(String key, Object... values) {
        if (values == null || values.length == 0) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            Long result = redisTemplate.opsForSet().add(finalKey, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("向集合添加元素失败，key: {}", key, e);
            throw new CacheException("向集合添加元素失败", e);
        }
    }

    @Override
    public long sRemove(String key, Object... values) {
        if (values == null || values.length == 0) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            Long result = redisTemplate.opsForSet().remove(finalKey, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("从集合移除元素失败，key: {}", key, e);
            throw new CacheException("从集合移除元素失败", e);
        }
    }

    @Override
    public boolean sIsMember(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(finalKey, value));
        } catch (Exception e) {
            log.error("检查集合成员失败，key: {}", key, e);
            throw new CacheException("检查集合成员失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> sMembers(String key) {
        try {
            String finalKey = buildKey(key);
            Set<Object> members = redisTemplate.opsForSet().members(finalKey);
            
            if (CollectionUtils.isEmpty(members)) {
                return new HashSet<>();
            }
            
            return members.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取集合成员失败，key: {}", key, e);
            throw new CacheException("获取集合成员失败", e);
        }
    }

    @Override
    public long sSize(String key) {
        try {
            String finalKey = buildKey(key);
            Long size = redisTemplate.opsForSet().size(finalKey);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取集合大小失败，key: {}", key, e);
            throw new CacheException("获取集合大小失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T sRandomMember(String key) {
        try {
            String finalKey = buildKey(key);
            Object member = redisTemplate.opsForSet().randomMember(finalKey);
            return processNullValue(member);
        } catch (Exception e) {
            log.error("获取随机集合成员失败，key: {}", key, e);
            throw new CacheException("获取随机集合成员失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> sRandomMembers(String key, long count) {
        try {
            String finalKey = buildKey(key);
            List<Object> members = redisTemplate.opsForSet().randomMembers(finalKey, count);
            
            if (CollectionUtils.isEmpty(members)) {
                return new ArrayList<>();
            }
            
            return members.stream()
                    .map(member -> (T) processNullValue(member))
                    .toList();
        } catch (Exception e) {
            log.error("获取随机集合成员失败，key: {}, count: {}", key, count, e);
            throw new CacheException("获取随机集合成员失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T sPop(String key) {
        try {
            String finalKey = buildKey(key);
            Object member = redisTemplate.opsForSet().pop(finalKey);
            return processNullValue(member);
        } catch (Exception e) {
            log.error("弹出集合成员失败，key: {}", key, e);
            throw new CacheException("弹出集合成员失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> sIntersect(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new HashSet<>();
        }

        try {
            Collection<String> finalKeys = keys.stream()
                    .map(this::buildKey)
                    .toList();
            
            Set<Object> result = redisTemplate.opsForSet().intersect(finalKeys);
            
            if (CollectionUtils.isEmpty(result)) {
                return new HashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取集合交集失败，keys: {}", keys, e);
            throw new CacheException("获取集合交集失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> sUnion(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new HashSet<>();
        }

        try {
            Collection<String> finalKeys = keys.stream()
                    .map(this::buildKey)
                    .toList();
            
            Set<Object> result = redisTemplate.opsForSet().union(finalKeys);
            
            if (CollectionUtils.isEmpty(result)) {
                return new HashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取集合并集失败，keys: {}", keys, e);
            throw new CacheException("获取集合并集失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> sDifference(String key, Collection<String> otherKeys) {
        try {
            String finalKey = buildKey(key);
            Collection<String> finalOtherKeys = otherKeys.stream()
                    .map(this::buildKey)
                    .toList();
            
            Set<Object> result = redisTemplate.opsForSet().difference(finalKey, finalOtherKeys);
            
            if (CollectionUtils.isEmpty(result)) {
                return new HashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取集合差集失败，key: {}, otherKeys: {}", key, otherKeys, e);
            throw new CacheException("获取集合差集失败", e);
        }
    }

    // ================================
    // 有序集合操作实现
    // ================================

    @Override
    public boolean zAdd(String key, Object value, double score) {
        try {
            String finalKey = buildKey(key);
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(finalKey, value, score));
        } catch (Exception e) {
            log.error("向有序集合添加元素失败，key: {}, score: {}", key, score, e);
            throw new CacheException("向有序集合添加元素失败", e);
        }
    }

    @Override
    public long zAdd(String key, Map<Object, Double> scoreValueMap) {
        if (CollectionUtils.isEmpty(scoreValueMap)) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> tuples = 
                    scoreValueMap.entrySet().stream()
                            .map(entry -> new org.springframework.data.redis.core.DefaultTypedTuple<>(
                                    entry.getKey(), entry.getValue()))
                            .collect(Collectors.toSet());
            
            Long result = redisTemplate.opsForZSet().add(finalKey, tuples);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("批量向有序集合添加元素失败，key: {}", key, e);
            throw new CacheException("批量向有序集合添加元素失败", e);
        }
    }

    @Override
    public long zRemove(String key, Object... values) {
        if (values == null || values.length == 0) {
            return 0;
        }

        try {
            String finalKey = buildKey(key);
            Long result = redisTemplate.opsForZSet().remove(finalKey, values);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("从有序集合移除元素失败，key: {}", key, e);
            throw new CacheException("从有序集合移除元素失败", e);
        }
    }

    @Override
    public Double zScore(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForZSet().score(finalKey, value);
        } catch (Exception e) {
            log.error("获取有序集合元素分数失败，key: {}", key, e);
            throw new CacheException("获取有序集合元素分数失败", e);
        }
    }

    @Override
    public Long zRank(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForZSet().rank(finalKey, value);
        } catch (Exception e) {
            log.error("获取有序集合元素排名失败，key: {}", key, e);
            throw new CacheException("获取有序集合元素排名失败", e);
        }
    }

    @Override
    public Long zReverseRank(String key, Object value) {
        try {
            String finalKey = buildKey(key);
            return redisTemplate.opsForZSet().reverseRank(finalKey, value);
        } catch (Exception e) {
            log.error("获取有序集合元素逆序排名失败，key: {}", key, e);
            throw new CacheException("获取有序集合元素逆序排名失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRange(String key, long start, long end) {
        try {
            String finalKey = buildKey(key);
            Set<Object> result = redisTemplate.opsForZSet().range(finalKey, start, end);
            
            if (CollectionUtils.isEmpty(result)) {
                return new LinkedHashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("获取有序集合范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            throw new CacheException("获取有序集合范围失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> zReverseRange(String key, long start, long end) {
        try {
            String finalKey = buildKey(key);
            Set<Object> result = redisTemplate.opsForZSet().reverseRange(finalKey, start, end);
            
            if (CollectionUtils.isEmpty(result)) {
                return new LinkedHashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("获取有序集合逆序范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            throw new CacheException("获取有序集合逆序范围失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRangeByScore(String key, double min, double max) {
        try {
            String finalKey = buildKey(key);
            Set<Object> result = redisTemplate.opsForZSet().rangeByScore(finalKey, min, max);
            
            if (CollectionUtils.isEmpty(result)) {
                return new LinkedHashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("根据分数获取有序集合失败，key: {}, min: {}, max: {}", key, min, max, e);
            throw new CacheException("根据分数获取有序集合失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRangeByScore(String key, double min, double max, long offset, long count) {
        try {
            String finalKey = buildKey(key);
            Set<Object> result = redisTemplate.opsForZSet().rangeByScore(finalKey, min, max, offset, count);
            
            if (CollectionUtils.isEmpty(result)) {
                return new LinkedHashSet<>();
            }
            
            return result.stream()
                    .map(member -> (T) processNullValue(member))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("根据分数获取有序集合失败，key: {}, min: {}, max: {}, offset: {}, count: {}", 
                      key, min, max, offset, count, e);
            throw new CacheException("根据分数获取有序集合失败", e);
        }
    }

    @Override
    public long zSize(String key) {
        try {
            String finalKey = buildKey(key);
            Long size = redisTemplate.opsForZSet().size(finalKey);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("获取有序集合大小失败，key: {}", key, e);
            throw new CacheException("获取有序集合大小失败", e);
        }
    }

    @Override
    public long zCount(String key, double min, double max) {
        try {
            String finalKey = buildKey(key);
            Long count = redisTemplate.opsForZSet().count(finalKey, min, max);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("统计有序集合元素数量失败，key: {}, min: {}, max: {}", key, min, max, e);
            throw new CacheException("统计有序集合元素数量失败", e);
        }
    }

    @Override
    public double zIncrementScore(String key, Object value, double delta) {
        try {
            String finalKey = buildKey(key);
            Double result = redisTemplate.opsForZSet().incrementScore(finalKey, value, delta);
            return result != null ? result : 0.0;
        } catch (Exception e) {
            log.error("增加有序集合元素分数失败，key: {}, delta: {}", key, delta, e);
            throw new CacheException("增加有序集合元素分数失败", e);
        }
    }

    // ================================
    // 缓存穿透保护实现
    // ================================

    @Override
    public <T> T getOrLoad(String key, Function<String, T> loader, Duration duration) {
        try {
            // 先尝试从缓存获取
            T cachedValue = get(key);
            if (cachedValue != null) {
                return cachedValue;
            }
            
            // 缓存不存在，执行加载函数
            T loadedValue = loader.apply(key);
            
            if (loadedValue != null) {
                // 加载到数据，设置缓存
                set(key, loadedValue, duration);
                return loadedValue;
            } else {
                // 加载为空，设置空值缓存防止缓存穿透
                if (cacheProperties.isAllowNullValues()) {
                    Duration nullDuration = cacheProperties.getNullValueExpiration();
                    set(key, NULL_VALUE_PLACEHOLDER, nullDuration);
                }
                return null;
            }
        } catch (Exception e) {
            log.error("获取或加载缓存失败，key: {}", key, e);
            // 发生异常时，尝试直接调用loader，避免缓存故障影响业务
            try {
                return loader.apply(key);
            } catch (Exception loaderException) {
                log.error("加载函数执行失败，key: {}", key, loaderException);
                throw new CacheException("获取或加载缓存失败", e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, Class<T> type, Function<String, T> loader, Duration duration) {
        try {
            // 先尝试从缓存获取
            T cachedValue = get(key, type);
            if (cachedValue != null) {
                return cachedValue;
            }
            
            // 缓存不存在，执行加载函数
            T loadedValue = loader.apply(key);
            
            if (loadedValue != null) {
                // 加载到数据，设置缓存
                set(key, loadedValue, duration);
                return loadedValue;
            } else {
                // 加载为空，设置空值缓存防止缓存穿透
                if (cacheProperties.isAllowNullValues()) {
                    Duration nullDuration = cacheProperties.getNullValueExpiration();
                    set(key, NULL_VALUE_PLACEHOLDER, nullDuration);
                }
                return null;
            }
        } catch (Exception e) {
            log.error("获取或加载缓存失败，key: {}", key, e);
            // 发生异常时，尝试直接调用loader，避免缓存故障影响业务
            try {
                return loader.apply(key);
            } catch (Exception loaderException) {
                log.error("加载函数执行失败，key: {}", key, loaderException);
                throw new CacheException("获取或加载缓存失败", e);
            }
        }
    }

    @Override
    public <T> Map<String, T> batchGetOrLoad(Collection<String> keys, Function<Collection<String>, Map<String, T>> loader,
                                             Duration duration) {
        if (CollectionUtils.isEmpty(keys)) {
            return new HashMap<>();
        }

        try {
            Map<String, T> result = new HashMap<>();
            Set<String> missedKeys = identifyMissedKeys(keys, result);
            
            if (!missedKeys.isEmpty()) {
                loadAndCacheMissedData(missedKeys, loader, duration, result);
            }

            return result;
        } catch (Exception e) {
            log.error("批量获取或加载缓存失败，keys: {}", keys, e);
            throw new CacheException("批量获取或加载缓存失败", e);
        }
    }

    /**
     * 识别缓存中缺失的键
     */
    private <T> Set<String> identifyMissedKeys(Collection<String> keys, Map<String, T> result) {
        List<Object> cachedValues = multiGet(keys);
        Set<String> missedKeys = new HashSet<>();

        int index = 0;
        for (String key : keys) {
            Object cachedValue = cachedValues.get(index++);
            if (cachedValue != null) {
                @SuppressWarnings("unchecked")
                T typedValue = (T) cachedValue;
                result.put(key, typedValue);
            } else {
                missedKeys.add(key);
            }
        }

        return missedKeys;
    }

    /**
     * 加载并缓存缺失的数据
     */
    private <T> void loadAndCacheMissedData(Set<String> missedKeys, Function<Collection<String>, Map<String, T>> loader,
                                            Duration duration, Map<String, T> result) {
        Map<String, T> loadedData = loader.apply(missedKeys);
        
        if (!CollectionUtils.isEmpty(loadedData)) {
            processCacheForLoadedData(loadedData, duration, result);
        }

        handleRemainingMissedKeys(missedKeys, loadedData, result);
    }

    /**
     * 处理已加载数据的缓存
     */
    private <T> void processCacheForLoadedData(Map<String, T> loadedData, Duration duration, Map<String, T> result) {
        Map<String, Object> cacheData = new HashMap<>();
        
        for (Map.Entry<String, T> entry : loadedData.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
            if (entry.getValue() != null) {
                cacheData.put(entry.getKey(), entry.getValue());
            } else if (cacheProperties.isAllowNullValues()) {
                cacheData.put(entry.getKey(), NULL_VALUE_PLACEHOLDER);
            }
        }

        if (!cacheData.isEmpty()) {
            multiSet(cacheData);
            cacheData.keySet().forEach(key -> expire(key, duration));
        }
    }

    /**
     * 处理仍然缺失的键
     */
    private <T> void handleRemainingMissedKeys(Set<String> missedKeys, Map<String, T> loadedData, Map<String, T> result) {
        for (String missedKey : missedKeys) {
            if (loadedData == null || !loadedData.containsKey(missedKey)) {
                if (cacheProperties.isAllowNullValues()) {
                    set(missedKey, NULL_VALUE_PLACEHOLDER, cacheProperties.getNullValueExpiration());
                }
                result.put(missedKey, null);
            }
        }
    }

    @Override
    public <T> T refresh(String key, Function<String, T> loader, Duration duration) {
        try {
            // 删除现有缓存
            delete(key);
            
            // 重新加载
            return getOrLoad(key, loader, duration);
        } catch (Exception e) {
            log.error("刷新缓存失败，key: {}", key, e);
            throw new CacheException("刷新缓存失败", e);
        }
    }
} 