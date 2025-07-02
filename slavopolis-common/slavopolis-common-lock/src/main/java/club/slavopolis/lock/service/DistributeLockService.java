package club.slavopolis.lock.service;

import club.slavopolis.lock.annotation.DistributeLock;
import club.slavopolis.lock.exception.DistributeLockException;
import club.slavopolis.lock.exception.LockNotHeldException;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁编程式服务接口
 * <p>
 * 提供手动控制分布式锁生命周期的API，与{@link DistributeLock}注解形成互补。
 * 支持锁的获取、释放、状态查询等操作，适用于复杂业务场景。
 * </p>
 * <p>
 * 主要使用场景：
 * <ul>
 * <li>需要在方法执行过程中动态控制锁</li>
 * <li>跨方法间共享同一个锁实例</li>
 * <li>根据业务条件提前释放锁</li>
 * <li>查询锁状态进行业务决策</li>
 * <li>非阻塞的锁获取尝试</li>
 * </ul>
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * @see DistributeLock 分布式锁注解
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface DistributeLockService {

    /**
     * 尝试获取锁（非阻塞）
     * <p>
     * 立即返回获取结果，不会等待。适用于需要快速响应的场景。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @return 如果成功获取锁返回true，否则返回false
     * @throws DistributeLockException 当参数无效时抛出
     */
    boolean tryLock(String scene, String key);

    /**
     * 尝试获取锁（限时等待）
     * <p>
     * 在指定时间内等待获取锁，超时后返回失败。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @param waitTime 等待时间
     * @param timeUnit 时间单位
     * @return 如果在等待时间内成功获取锁返回true，否则返回false
     * @throws DistributeLockException 当参数无效时抛出
     * @throws InterruptedException 当等待被中断时抛出
     */
    boolean tryLock(String scene, String key, long waitTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 尝试获取锁（限时等待+过期时间）
     * <p>
     * 在指定时间内等待获取锁，获取成功后锁将在指定过期时间后自动释放。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @param waitTime 等待时间
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @return 如果在等待时间内成功获取锁返回true，否则返回false
     * @throws DistributeLockException 当参数无效时抛出
     * @throws InterruptedException 当等待被中断时抛出
     */
    boolean tryLock(String scene, String key, long waitTime, long expireTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 获取锁（阻塞等待）
     * <p>
     * 一直等待直到获取锁成功，使用Redisson的自动续期机制。
     * 适用于必须获取锁才能继续执行的场景。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @throws DistributeLockException 当参数无效时抛出
     * @throws InterruptedException 当等待被中断时抛出
     */
    void lock(String scene, String key) throws InterruptedException;

    /**
     * 获取锁（阻塞等待+过期时间）
     * <p>
     * 一直等待直到获取锁成功，获取成功后锁将在指定过期时间后自动释放。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @throws DistributeLockException 当参数无效时抛出
     * @throws InterruptedException 当等待被中断时抛出
     */
    void lock(String scene, String key, long expireTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 释放锁
     * <p>
     * 只能释放当前线程持有的锁。如果锁未被当前线程持有，将抛出异常。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @throws LockNotHeldException 当锁未被当前线程持有时抛出
     * @throws DistributeLockException 当参数无效时抛出
     */
    void unlock(String scene, String key);

    /**
     * 检查锁是否被当前线程持有
     * <p>
     * 用于判断当前线程是否持有指定的锁。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @return 如果锁被当前线程持有返回true，否则返回false
     * @throws DistributeLockException 当参数无效时抛出
     */
    boolean isHeldByCurrentThread(String scene, String key);

    /**
     * 检查锁是否被任意线程持有
     * <p>
     * 用于判断指定的锁是否正在被使用，不关心是哪个线程持有。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @return 如果锁被任意线程持有返回true，否则返回false
     * @throws DistributeLockException 当参数无效时抛出
     */
    boolean isLocked(String scene, String key);

    /**
     * 强制释放锁（危险操作）
     * <p>
     * 强制释放指定的锁，不检查是否被当前线程持有。
     * 这是一个危险操作，可能导致业务逻辑错误，请谨慎使用。
     * 通常只在异常恢复或管理工具中使用。
     * </p>
     *
     * @param scene 业务场景名称
     * @param key 锁的业务key
     * @throws DistributeLockException 当参数无效时抛出
     */
    void forceUnlock(String scene, String key);
} 