package club.slavopolis.cache.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存服务接口
 * 提供完整的Redis操作能力，包括字符串、哈希、列表、集合、有序集合等数据结构操作
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
public interface CacheService {

    // ================================
    // 通用操作
    // ================================

    /**
     * 判断键是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    boolean hasKey(String key);

    /**
     * 删除缓存键
     *
     * @param key 缓存键
     * @return 是否删除成功
     */
    boolean delete(String key);

    /**
     * 批量删除缓存键
     *
     * @param keys 缓存键集合
     * @return 删除的键数量
     */
    long delete(Collection<String> keys);

    /**
     * 设置键的过期时间
     *
     * @param key 缓存键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 设置键的过期时间
     *
     * @param key 缓存键
     * @param duration 过期时间
     * @return 是否设置成功
     */
    boolean expire(String key, Duration duration);

    /**
     * 获取键的过期时间
     *
     * @param key 缓存键
     * @param unit 时间单位
     * @return 过期时间，-1表示永不过期，-2表示键不存在
     */
    long getExpire(String key, TimeUnit unit);

    /**
     * 移除键的过期时间，使其永不过期
     *
     * @param key 缓存键
     * @return 是否操作成功
     */
    boolean persist(String key);

    /**
     * 根据模式查找键
     *
     * @param pattern 匹配模式
     * @return 匹配的键集合
     */
    Set<String> keys(String pattern);

    /**
     * 随机获取一个键
     *
     * @return 随机键
     */
    String randomKey();

    /**
     * 重命名键
     *
     * @param oldKey 旧键名
     * @param newKey 新键名
     */
    void rename(String oldKey, String newKey);

    /**
     * 获取键的数据类型
     *
     * @param key 缓存键
     * @return 数据类型
     */
    String type(String key);

    // ================================
    // 字符串操作
    // ================================

    /**
     * 设置字符串值
     *
     * @param key 缓存键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 设置字符串值并指定过期时间
     *
     * @param key 缓存键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 设置字符串值并指定过期时间
     *
     * @param key 缓存键
     * @param value 值
     * @param duration 过期时间
     */
    void set(String key, Object value, Duration duration);

    /**
     * 仅当键不存在时设置值
     *
     * @param key 缓存键
     * @param value 值
     * @return 是否设置成功
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * 仅当键不存在时设置值并指定过期时间
     *
     * @param key 缓存键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 仅当键存在时设置值
     *
     * @param key 缓存键
     * @param value 值
     * @return 是否设置成功
     */
    boolean setIfPresent(String key, Object value);

    /**
     * 获取字符串值
     *
     * @param key 缓存键
     * @return 值
     */
    <T> T get(String key);

    /**
     * 获取字符串值并指定类型
     *
     * @param key 缓存键
     * @param type 值类型
     * @return 值
     */
    <T> T get(String key, Class<T> type);

    /**
     * 获取并设置新值
     *
     * @param key 缓存键
     * @param value 新值
     * @return 旧值
     */
    <T> T getAndSet(String key, Object value);

    /**
     * 批量设置多个键值对
     *
     * @param map 键值对映射
     */
    void multiSet(Map<String, Object> map);

    /**
     * 批量获取多个键的值
     *
     * @param keys 键集合
     * @return 值列表
     */
    List<Object> multiGet(Collection<String> keys);

    /**
     * 数值自增
     *
     * @param key 缓存键
     * @return 自增后的值
     */
    long increment(String key);

    /**
     * 数值自增指定步长
     *
     * @param key 缓存键
     * @param delta 步长
     * @return 自增后的值
     */
    long increment(String key, long delta);

    /**
     * 浮点数自增
     *
     * @param key 缓存键
     * @param delta 步长
     * @return 自增后的值
     */
    double increment(String key, double delta);

    /**
     * 数值自减
     *
     * @param key 缓存键
     * @return 自减后的值
     */
    long decrement(String key);

    /**
     * 数值自减指定步长
     *
     * @param key 缓存键
     * @param delta 步长
     * @return 自减后的值
     */
    long decrement(String key, long delta);

    // ================================
    // 哈希操作
    // ================================

    /**
     * 设置哈希字段值
     *
     * @param key 缓存键
     * @param field 字段名
     * @param value 值
     */
    void hSet(String key, String field, Object value);

    /**
     * 批量设置哈希字段值
     *
     * @param key 缓存键
     * @param map 字段值映射
     */
    void hSetAll(String key, Map<String, Object> map);

    /**
     * 仅当哈希字段不存在时设置值
     *
     * @param key 缓存键
     * @param field 字段名
     * @param value 值
     * @return 是否设置成功
     */
    boolean hSetIfAbsent(String key, String field, Object value);

    /**
     * 获取哈希字段值
     *
     * @param key 缓存键
     * @param field 字段名
     * @return 字段值
     */
    <T> T hGet(String key, String field);

    /**
     * 获取哈希字段值并指定类型
     *
     * @param key 缓存键
     * @param field 字段名
     * @param type 值类型
     * @return 字段值
     */
    <T> T hGet(String key, String field, Class<T> type);

    /**
     * 批量获取哈希字段值
     *
     * @param key 缓存键
     * @param fields 字段名集合
     * @return 字段值列表
     */
    List<Object> hMultiGet(String key, Collection<String> fields);

    /**
     * 获取哈希所有字段值
     *
     * @param key 缓存键
     * @return 字段值映射
     */
    Map<String, Object> hGetAll(String key);

    /**
     * 判断哈希字段是否存在
     *
     * @param key 缓存键
     * @param field 字段名
     * @return 是否存在
     */
    boolean hExists(String key, String field);

    /**
     * 删除哈希字段
     *
     * @param key 缓存键
     * @param fields 字段名
     * @return 删除的字段数量
     */
    long hDelete(String key, String... fields);

    /**
     * 获取哈希字段数量
     *
     * @param key 缓存键
     * @return 字段数量
     */
    long hSize(String key);

    /**
     * 获取哈希所有字段名
     *
     * @param key 缓存键
     * @return 字段名集合
     */
    Set<String> hKeys(String key);

    /**
     * 获取哈希所有字段值
     *
     * @param key 缓存键
     * @return 字段值列表
     */
    List<Object> hValues(String key);

    /**
     * 哈希字段数值自增
     *
     * @param key 缓存键
     * @param field 字段名
     * @param delta 步长
     * @return 自增后的值
     */
    long hIncrement(String key, String field, long delta);

    /**
     * 哈希字段浮点数自增
     *
     * @param key 缓存键
     * @param field 字段名
     * @param delta 步长
     * @return 自增后的值
     */
    double hIncrement(String key, String field, double delta);

    // ================================
    // 列表操作
    // ================================

    /**
     * 从列表左侧推入元素
     *
     * @param key 缓存键
     * @param values 元素值
     * @return 列表长度
     */
    long lLeftPush(String key, Object... values);

    /**
     * 从列表右侧推入元素
     *
     * @param key 缓存键
     * @param values 元素值
     * @return 列表长度
     */
    long lRightPush(String key, Object... values);

    /**
     * 从列表左侧弹出元素
     *
     * @param key 缓存键
     * @return 弹出的元素
     */
    <T> T lLeftPop(String key);

    /**
     * 从列表右侧弹出元素
     *
     * @param key 缓存键
     * @return 弹出的元素
     */
    <T> T lRightPop(String key);

    /**
     * 阻塞式从列表左侧弹出元素
     *
     * @param key 缓存键
     * @param timeout 阻塞超时时间
     * @param unit 时间单位
     * @return 弹出的元素
     */
    <T> T lBlockingLeftPop(String key, long timeout, TimeUnit unit);

    /**
     * 阻塞式从列表右侧弹出元素
     *
     * @param key 缓存键
     * @param timeout 阻塞超时时间
     * @param unit 时间单位
     * @return 弹出的元素
     */
    <T> T lBlockingRightPop(String key, long timeout, TimeUnit unit);

    /**
     * 获取列表指定范围的元素
     *
     * @param key 缓存键
     * @param start 开始位置
     * @param end 结束位置
     * @return 元素列表
     */
    <T> List<T> lRange(String key, long start, long end);

    /**
     * 获取列表指定位置的元素
     *
     * @param key 缓存键
     * @param index 位置索引
     * @return 元素值
     */
    <T> T lIndex(String key, long index);

    /**
     * 设置列表指定位置的元素值
     *
     * @param key 缓存键
     * @param index 位置索引
     * @param value 元素值
     */
    void lSet(String key, long index, Object value);

    /**
     * 获取列表长度
     *
     * @param key 缓存键
     * @return 列表长度
     */
    long lSize(String key);

    /**
     * 从列表中移除元素
     *
     * @param key 缓存键
     * @param count 移除数量（0=全部，正数=从头开始，负数=从尾开始）
     * @param value 要移除的元素值
     * @return 实际移除的数量
     */
    long lRemove(String key, long count, Object value);

    /**
     * 裁剪列表，保留指定范围的元素
     *
     * @param key 缓存键
     * @param start 开始位置
     * @param end 结束位置
     */
    void lTrim(String key, long start, long end);

    // ================================
    // 集合操作
    // ================================

    /**
     * 向集合添加元素
     *
     * @param key 缓存键
     * @param values 元素值
     * @return 实际添加的元素数量
     */
    long sAdd(String key, Object... values);

    /**
     * 从集合移除元素
     *
     * @param key 缓存键
     * @param values 元素值
     * @return 实际移除的元素数量
     */
    long sRemove(String key, Object... values);

    /**
     * 判断元素是否在集合中
     *
     * @param key 缓存键
     * @param value 元素值
     * @return 是否存在
     */
    boolean sIsMember(String key, Object value);

    /**
     * 获取集合所有元素
     *
     * @param key 缓存键
     * @return 元素集合
     */
    <T> Set<T> sMembers(String key);

    /**
     * 获取集合大小
     *
     * @param key 缓存键
     * @return 集合大小
     */
    long sSize(String key);

    /**
     * 随机获取集合中的元素
     *
     * @param key 缓存键
     * @return 随机元素
     */
    <T> T sRandomMember(String key);

    /**
     * 随机获取集合中的多个元素
     *
     * @param key 缓存键
     * @param count 数量
     * @return 随机元素列表
     */
    <T> List<T> sRandomMembers(String key, long count);

    /**
     * 随机弹出集合中的元素
     *
     * @param key 缓存键
     * @return 弹出的元素
     */
    <T> T sPop(String key);

    /**
     * 获取多个集合的交集
     *
     * @param keys 集合键
     * @return 交集元素
     */
    <T> Set<T> sIntersect(Collection<String> keys);

    /**
     * 获取多个集合的并集
     *
     * @param keys 集合键
     * @return 并集元素
     */
    <T> Set<T> sUnion(Collection<String> keys);

    /**
     * 获取集合的差集
     *
     * @param key 主集合键
     * @param otherKeys 其他集合键
     * @return 差集元素
     */
    <T> Set<T> sDifference(String key, Collection<String> otherKeys);

    // ================================
    // 有序集合操作
    // ================================

    /**
     * 向有序集合添加元素
     *
     * @param key 缓存键
     * @param value 元素值
     * @param score 分数
     * @return 是否添加成功
     */
    boolean zAdd(String key, Object value, double score);

    /**
     * 批量向有序集合添加元素
     *
     * @param key 缓存键
     * @param scoreValueMap 分数值映射
     * @return 实际添加的元素数量
     */
    long zAdd(String key, Map<Object, Double> scoreValueMap);

    /**
     * 从有序集合移除元素
     *
     * @param key 缓存键
     * @param values 元素值
     * @return 实际移除的元素数量
     */
    long zRemove(String key, Object... values);

    /**
     * 获取元素的分数
     *
     * @param key 缓存键
     * @param value 元素值
     * @return 分数
     */
    Double zScore(String key, Object value);

    /**
     * 获取元素的排名（从小到大）
     *
     * @param key 缓存键
     * @param value 元素值
     * @return 排名
     */
    Long zRank(String key, Object value);

    /**
     * 获取元素的逆序排名（从大到小）
     *
     * @param key 缓存键
     * @param value 元素值
     * @return 逆序排名
     */
    Long zReverseRank(String key, Object value);

    /**
     * 获取指定范围的元素（按分数从小到大）
     *
     * @param key 缓存键
     * @param start 开始位置
     * @param end 结束位置
     * @return 元素集合
     */
    <T> Set<T> zRange(String key, long start, long end);

    /**
     * 获取指定范围的元素（按分数从大到小）
     *
     * @param key 缓存键
     * @param start 开始位置
     * @param end 结束位置
     * @return 元素集合
     */
    <T> Set<T> zReverseRange(String key, long start, long end);

    /**
     * 根据分数范围获取元素
     *
     * @param key 缓存键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    <T> Set<T> zRangeByScore(String key, double min, double max);

    /**
     * 根据分数范围获取元素（限制数量）
     *
     * @param key 缓存键
     * @param min 最小分数
     * @param max 最大分数
     * @param offset 偏移量
     * @param count 数量
     * @return 元素集合
     */
    <T> Set<T> zRangeByScore(String key, double min, double max, long offset, long count);

    /**
     * 获取有序集合大小
     *
     * @param key 缓存键
     * @return 集合大小
     */
    long zSize(String key);

    /**
     * 根据分数范围统计元素数量
     *
     * @param key 缓存键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素数量
     */
    long zCount(String key, double min, double max);

    /**
     * 增加元素的分数
     *
     * @param key 缓存键
     * @param value 元素值
     * @param delta 分数增量
     * @return 增加后的分数
     */
    double zIncrementScore(String key, Object value, double delta);

    // ================================
    // 缓存穿透保护
    // ================================

    /**
     * 获取缓存值，如果不存在则执行加载函数并缓存结果
     *
     * @param key 缓存键
     * @param loader 数据加载函数
     * @param duration 缓存时间
     * @return 缓存值
     */
    <T> T getOrLoad(String key, Function<String, T> loader, Duration duration);

    /**
     * 获取缓存值，如果不存在则执行加载函数并缓存结果
     *
     * @param key 缓存键
     * @param type 值类型
     * @param loader 数据加载函数
     * @param duration 缓存时间
     * @return 缓存值
     */
    <T> T getOrLoad(String key, Class<T> type, Function<String, T> loader, Duration duration);

    // ================================
    // 批量操作
    // ================================

    /**
     * 批量获取缓存值，不存在的键会执行加载函数
     *
     * @param keys 缓存键集合
     * @param loader 数据加载函数（入参为不存在的键集合）
     * @param duration 缓存时间
     * @return 键值映射
     */
    <T> Map<String, T> batchGetOrLoad(Collection<String> keys, Function<Collection<String>, Map<String, T>> loader, Duration duration);

    /**
     * 刷新缓存（删除后重新加载）
     *
     * @param key 缓存键
     * @param loader 数据加载函数
     * @param duration 缓存时间
     * @return 新的缓存值
     */
    <T> T refresh(String key, Function<String, T> loader, Duration duration);
} 