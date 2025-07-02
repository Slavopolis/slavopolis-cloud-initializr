package club.slavopolis.lock.service.impl;

import club.slavopolis.lock.constant.DistributeLockConstant;
import club.slavopolis.lock.exception.DistributeLockException;
import club.slavopolis.lock.exception.LockNotHeldException;
import club.slavopolis.lock.manager.LockManager;
import club.slavopolis.lock.service.DistributeLockService;
import club.slavopolis.lock.util.LockKeyUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的分布式锁服务实现
 * <p>
 * 实现{@link DistributeLockService}接口，提供完整的编程式分布式锁功能。
 * 使用Redis作为锁存储，支持锁的自动续期、过期时间设置等高级特性。
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * @see DistributeLockService 分布式锁服务接口
 * @see RedissonClient Redisson客户端
 * @see LockManager 锁管理器
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Service
@RequiredArgsConstructor
public class RedissonDistributeLockServiceImpl implements DistributeLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonDistributeLockServiceImpl.class);

    private final RedissonClient redissonClient;
    private final LockManager lockManager;

    @Override
    public boolean tryLock(String scene, String key) {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        try {
            boolean result = rLock.tryLock(DistributeLockConstant.DEFAULT_TRY_WAIT_TIME, TimeUnit.MILLISECONDS);
            if (result) {
                LOGGER.debug("成功获取锁（非阻塞）, key: {}", lockKey);
            } else {
                LOGGER.debug("获取锁失败（非阻塞）, key: {}", lockKey);
            }
            return result;
        } catch (InterruptedException e) {
            // 恢复中断状态，遵循Java并发最佳实践
            Thread.currentThread().interrupt();

            String contextInfo = String.format("获取锁被中断（非阻塞），key: %s, 线程: %s", 
                lockKey, Thread.currentThread().getName());
            LOGGER.error(contextInfo, e);

            throw new DistributeLockException(contextInfo, e);
        }
    }

    @Override
    public boolean tryLock(String scene, String key, long waitTime, TimeUnit timeUnit) throws InterruptedException {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        boolean result = rLock.tryLock(waitTime, timeUnit);
        if (result) {
            LOGGER.debug("成功获取锁（限时等待）, key: {}, waitTime: {} {}", lockKey, waitTime, timeUnit);
        } else {
            LOGGER.debug("获取锁失败（限时等待）, key: {}, waitTime: {} {}", lockKey, waitTime, timeUnit);
        }
        return result;
    }

    @Override
    public boolean tryLock(String scene, String key, long waitTime, long expireTime, TimeUnit timeUnit) throws InterruptedException {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        boolean result = rLock.tryLock(waitTime, expireTime, timeUnit);
        if (result) {
            LOGGER.debug("成功获取锁（限时等待+过期时间）, key: {}, waitTime: {} {}, expireTime: {} {}", 
                lockKey, waitTime, timeUnit, expireTime, timeUnit);
        } else {
            LOGGER.debug("获取锁失败（限时等待+过期时间）, key: {}, waitTime: {} {}, expireTime: {} {}", 
                lockKey, waitTime, timeUnit, expireTime, timeUnit);
        }
        return result;
    }

    @Override
    public void lock(String scene, String key) throws InterruptedException {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        LOGGER.debug("开始获取锁（阻塞等待）, key: {}", lockKey);
        rLock.lock();
        LOGGER.debug("成功获取锁（阻塞等待）, key: {}", lockKey);
    }

    @Override
    public void lock(String scene, String key, long expireTime, TimeUnit timeUnit) throws InterruptedException {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        LOGGER.debug("开始获取锁（阻塞等待+过期时间）, key: {}, expireTime: {} {}", lockKey, expireTime, timeUnit);
        rLock.lock(expireTime, timeUnit);
        LOGGER.debug("成功获取锁（阻塞等待+过期时间）, key: {}, expireTime: {} {}", lockKey, expireTime, timeUnit);
    }

    @Override
    public void unlock(String scene, String key) {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        // 检查锁是否被当前线程持有
        if (!rLock.isHeldByCurrentThread()) {
            String errorMsg = DistributeLockConstant.ERROR_LOCK_NOT_HELD_BY_CURRENT_THREAD + 
                ", key: " + lockKey + ", 当前线程: " + Thread.currentThread().getName();
            LOGGER.warn(errorMsg);
            throw new LockNotHeldException(errorMsg);
        }

        try {
            rLock.unlock();
            LOGGER.debug("成功释放锁, key: {}", lockKey);
        } catch (IllegalMonitorStateException e) {
            // Redisson 在锁状态异常时抛出此异常，通常是锁已被其他线程释放或过期
            String errorMsg = String.format("释放锁失败，锁状态异常, key: %s, 线程: %s, 异常信息: %s",
                lockKey, Thread.currentThread().getName(), e.getMessage());
            LOGGER.error("IllegalMonitorStateException异常详情: {}", errorMsg, e);

            throw new LockNotHeldException(errorMsg, e);
        }
    }

    @Override
    public boolean isHeldByCurrentThread(String scene, String key) {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);
        
        boolean result = rLock.isHeldByCurrentThread();
        LOGGER.debug("检查锁是否被当前线程持有, key: {}, result: {}", lockKey, result);
        return result;
    }

    @Override
    public boolean isLocked(String scene, String key) {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);
        
        boolean result = rLock.isLocked();
        LOGGER.debug("检查锁是否被任意线程持有, key: {}, result: {}", lockKey, result);
        return result;
    }

    @Override
    public void forceUnlock(String scene, String key) {
        String lockKey = LockKeyUtil.buildLockKey(scene, key);
        RLock rLock = lockManager.getLock(redissonClient, lockKey);

        // 记录危险操作警告
        LOGGER.warn("{}, key: {}, 当前线程: {}", 
            DistributeLockConstant.WARNING_FORCE_UNLOCK, lockKey, Thread.currentThread().getName());

        try {
            boolean result = rLock.forceUnlock();
            if (result) {
                LOGGER.warn("强制释放锁成功, key: {}", lockKey);
            } else {
                LOGGER.warn("强制释放锁失败（锁可能已不存在）, key: {}", lockKey);
            }
        } catch (Exception e) {
            String errorMsg = String.format("强制释放锁异常, key: %s, 线程: %s, 异常类型: %s, 异常信息: %s",
                lockKey, Thread.currentThread().getName(), e.getClass().getSimpleName(), e.getMessage());
            LOGGER.error("强制释放锁异常详情: {}", errorMsg, e);

            throw new DistributeLockException(errorMsg, e);
        }
    }
} 