package club.slavopolis.lock.util;

import club.slavopolis.cache.constant.CacheConstant;
import club.slavopolis.lock.exception.DistributeLockException;
import org.springframework.util.StringUtils;

/**
 * 分布式锁key工具类
 * <p>
 * 提供锁key的构建、验证等共享工具方法，确保锁key格式的一致性。
 * 被注解式和编程式分布式锁实现共同使用。
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public final class LockKeyUtil {

    /**
     * 私有构造器，防止实例化
     */
    private LockKeyUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 构建完整的锁key
     * <p>
     * 格式: scene + ":" + key，用于实现业务场景隔离
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @return 完整的锁key
     * @throws DistributeLockException 当scene或key为空时抛出
     */
    public static String buildLockKey(String scene, String key) {
        validateScene(scene);
        validateKey(key);
        return scene + CacheConstant.CACHE_KEY_SEPARATOR + key;
    }

    /**
     * 验证场景名称参数
     * <p>
     * 场景名称不能为空，用于业务隔离
     * </p>
     *
     * @param scene 业务场景名称
     * @throws DistributeLockException 当scene为空时抛出
     */
    public static void validateScene(String scene) {
        if (!StringUtils.hasText(scene)) {
            throw new DistributeLockException("业务场景名称(scene)不能为空");
        }
    }

    /**
     * 验证锁key参数
     * <p>
     * 锁key不能为空，是锁的唯一标识
     * </p>
     *
     * @param key 锁key
     * @throws DistributeLockException 当key为空时抛出
     */
    public static void validateKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new DistributeLockException("锁key不能为空");
        }
    }
} 