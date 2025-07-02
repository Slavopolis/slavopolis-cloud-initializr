package club.slavopolis.lock.manager;

import club.slavopolis.lock.constant.DistributeLockConstant;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 分布式锁管理器
 * <p>
 * 负责管理分布式锁实例的生命周期，包括锁的创建、缓存和清理。
 * 通过缓存机制避免重复创建锁实例，提高性能并防止内存泄漏。
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public class LockManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockManager.class);

    /**
     * 锁实例缓存
     * <p>
     * key: 完整的锁key (scene:lockKey)
     * value: Redis锁实例
     * </p>
     */
    private final ConcurrentMap<String, RLock> lockCache;

    /**
     * 构造锁管理器
     */
    public LockManager() {
        this.lockCache = new ConcurrentHashMap<>(
            DistributeLockConstant.LOCK_CACHE_INITIAL_CAPACITY,
            DistributeLockConstant.LOCK_CACHE_LOAD_FACTOR
        );
        LOGGER.info("分布式锁管理器初始化完成");
    }

    /**
     * 获取或创建锁实例
     * <p>
     * 如果缓存中存在则返回缓存实例，否则创建新实例并缓存
     * </p>
     *
     * @param redissonClient Redisson客户端
     * @param lockKey 完整的锁key
     * @return Redis锁实例
     */
    public RLock getLock(RedissonClient redissonClient, String lockKey) {
        if (!StringUtils.hasText(lockKey)) {
            throw new IllegalArgumentException("锁key不能为空");
        }

        return lockCache.computeIfAbsent(lockKey, key -> {
            LOGGER.debug("创建新的锁实例, key: {}", key);
            return redissonClient.getLock(key);
        });
    }

    /**
     * 移除锁实例
     * <p>
     * 从缓存中移除指定的锁实例，通常在锁不再需要时调用
     * </p>
     *
     * @param lockKey 完整的锁key
     * @return 被移除的锁实例，如果不存在则返回null
     */
    public RLock removeLock(String lockKey) {
        if (!StringUtils.hasText(lockKey)) {
            return null;
        }

        RLock removedLock = lockCache.remove(lockKey);
        if (removedLock != null) {
            LOGGER.debug("移除锁实例, key: {}", lockKey);
        }
        return removedLock;
    }

    /**
     * 检查锁是否存在于缓存中
     *
     * @param lockKey 完整的锁key
     * @return 如果存在则返回true，否则返回false
     */
    public boolean containsLock(String lockKey) {
        return StringUtils.hasText(lockKey) && lockCache.containsKey(lockKey);
    }

    /**
     * 获取当前缓存的锁数量
     *
     * @return 缓存中的锁实例数量
     */
    public int getCacheSize() {
        return lockCache.size();
    }

    /**
     * 清空所有锁缓存
     * <p>
     * 谨慎使用，通常只在应用关闭或重置时调用
     * </p>
     */
    public void clearAll() {
        int size = lockCache.size();
        lockCache.clear();
        LOGGER.info("清空锁缓存，共清理 {} 个锁实例", size);
    }

    /**
     * 清理未持有的锁实例
     * <p>
     * 遍历缓存，移除当前线程未持有的锁实例，释放内存
     * </p>
     *
     * @return 清理的锁实例数量
     */
    public int cleanupUnheldLocks() {
        int originalSize = lockCache.size();
        
        lockCache.entrySet().removeIf(entry -> {
            RLock lock = entry.getValue();
            if (!lock.isHeldByCurrentThread()) {
                LOGGER.debug("清理未持有的锁实例, key: {}", entry.getKey());
                return true;
            }
            return false;
        });

        int cleanedCount = originalSize - lockCache.size();
        
        if (cleanedCount > 0) {
            LOGGER.info("清理未持有的锁实例，共清理 {} 个", cleanedCount);
        }

        return cleanedCount;
    }
} 