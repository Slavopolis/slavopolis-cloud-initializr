package club.slavopolis.infrastructure.cache.redis.util;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.cache.redis.exception.CacheException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * 缓存键工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheKeyUtil {

    /**
     * Redis key最大长度限制
     */
    private static final int MAX_KEY_LENGTH = 250;

    /**
     * 构建缓存键
     *
     * @param prefix 前缀
     * @param parts 键组成部分
     * @return 完整的缓存键
     */
    public static String buildKey(String prefix, Object... parts) {
        StringBuilder keyBuilder = new StringBuilder();
        
        if (StringUtils.hasText(prefix)) {
            keyBuilder.append(prefix);
        }
        
        if (parts != null) {
            for (Object part : parts) {
                if (part != null) {
                    if (!keyBuilder.isEmpty()) {
                        keyBuilder.append(CommonConstants.CACHE_KEY_SEPARATOR);
                    }
                    keyBuilder.append(normalizeKeyPart(part));
                }
            }
        }
        
        String key = keyBuilder.toString();
        
        // 如果键长度超过限制，进行哈希压缩
        if (key.length() > MAX_KEY_LENGTH) {
            return hashKey(key);
        }
        
        return key;
    }

    /**
     * 构建模糊匹配模式
     *
     * @param prefix 前缀
     * @param parts 键组成部分
     * @return 模糊匹配模式
     */
    public static String buildPattern(String prefix, Object... parts) {
        String key = buildKey(prefix, parts);
        return key + CommonConstants.ASTERISK;
    }

    /**
     * 批量构建缓存键
     *
     * @param prefix 前缀
     * @param identifiers 标识符集合
     * @return 缓存键集合
     */
    public static Collection<String> buildKeys(String prefix, Collection<?> identifiers) {
        return identifiers.stream()
                .map(id -> buildKey(prefix, id))
                .toList();
    }

    /**
     * 从键中提取标识符
     *
     * @param key 完整键
     * @param prefix 前缀
     * @return 标识符
     */
    public static String extractIdentifier(String key, String prefix) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(prefix)) {
            return key;
        }
        
        String prefixWithSeparator = prefix + CommonConstants.CACHE_KEY_SEPARATOR;
        if (key.startsWith(prefixWithSeparator)) {
            return key.substring(prefixWithSeparator.length());
        }
        
        return key;
    }

    /**
     * 验证键是否有效
     *
     * @param key 缓存键
     * @return 是否有效
     */
    public static boolean isValidKey(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        
        // 检查长度
        if (key.length() > MAX_KEY_LENGTH) {
            return false;
        }
        
        // 检查是否包含非法字符
        return !key.contains(CommonConstants.SPACE) &&
                !key.contains(CommonConstants.NEW_LINE) &&
                !key.contains(CommonConstants.CARRIAGE_RETURN) &&
                !key.contains(CommonConstants.TAB);
    }

    /**
     * 规范化键的组成部分
     *
     * @param part 键的组成部分
     * @return 规范化后的字符串
     */
    private static String normalizeKeyPart(Object part) {
        if (part == null) {
            return "null";
        }
        
        String str = part.toString();
        
        // 替换非法字符
        str = str.replaceAll("\\s+", CommonConstants.UNDERSCORE)
                .replaceAll("[\\r\\n\\t]", CommonConstants.UNDERSCORE);
        
        return str;
    }

    /**
     * 对键进行哈希压缩
     *
     * @param key 原始键
     * @return 哈希后的键
     */
    private static String hashKey(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance(CommonConstants.MD5_ALGORITHM);
            byte[] hashBytes = md.digest(key.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            // 保留原键的前缀部分，增加可读性
            String prefix = key.length() > 50 ? key.substring(0, 50) : key;
            return prefix + "_hash_" + sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new CacheException("Failed to hash cache key", e);
        }
    }

    /**
     * 构建集合元素键
     *
     * @param prefix 前缀
     * @param collectionName 集合名称
     * @param element 元素标识
     * @return 集合元素键
     */
    public static String buildCollectionElementKey(String prefix, String collectionName, Object element) {
        return buildKey(prefix, collectionName, "element", element);
    }

    /**
     * 构建分页键
     *
     * @param prefix 前缀
     * @param baseName 基础名称
     * @param page 页码
     * @param size 每页大小
     * @return 分页键
     */
    public static String buildPageKey(String prefix, String baseName, int page, int size) {
        return buildKey(prefix, baseName, "page", page, "size", size);
    }

    /**
     * 构建统计键
     *
     * @param prefix 前缀
     * @param metricName 指标名称
     * @param dimensions 维度参数
     * @return 统计键
     */
    public static String buildMetricKey(String prefix, String metricName, Object... dimensions) {
        return buildKey(prefix, "metrics", metricName, dimensions);
    }
} 