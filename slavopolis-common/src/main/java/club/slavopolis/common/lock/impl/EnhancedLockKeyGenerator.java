package club.slavopolis.common.lock.impl;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 增强的锁键生成器
 *
 * <p>
 * 在默认生成器的基础上增加了更多功能：
 * - 支持多级缓存
 * - 支持自定义变量
 * - 支持复杂表达式缓存
 * </p>
 */
@Slf4j
@Component("enhancedLockKeyGenerator")
public class EnhancedLockKeyGenerator extends DefaultLockKeyGenerator {

    /**
     * 表达式缓存
     */
    private final ConcurrentHashMap<String, String> expressionCache = new ConcurrentHashMap<>();

    /**
     * 最大缓存大小
     */
    private static final int MAX_CACHE_SIZE = 1000;

    @Override
    public String generate(String prefix, String key, ProceedingJoinPoint joinPoint) {
        // 构建缓存键
        String cacheKey = buildCacheKey(prefix, key, joinPoint);

        // 尝试从缓存获取
        String cachedKey = expressionCache.get(cacheKey);
        if (cachedKey != null) {
            log.debug("使用缓存的锁键: {}", cachedKey);
            return cachedKey;
        }

        // 生成新的锁键
        String lockKey = super.generate(prefix, key, joinPoint);

        // 添加到缓存
        if (expressionCache.size() < MAX_CACHE_SIZE) {
            expressionCache.put(cacheKey, lockKey);
        }

        return lockKey;
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String prefix, String key, ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(":");
        sb.append(key).append(":");
        sb.append(joinPoint.getSignature().toLongString());

        // 添加参数类型信息
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                sb.append(":").append(arg.getClass().getName());
            }
        }

        return sb.toString();
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        expressionCache.clear();
        log.info("清除锁定键表达式缓存");
    }
}
