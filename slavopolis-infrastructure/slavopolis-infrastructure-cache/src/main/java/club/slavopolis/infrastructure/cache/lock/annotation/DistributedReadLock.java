package club.slavopolis.infrastructure.cache.lock.annotation;

import club.slavopolis.infrastructure.cache.lock.enums.LockType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式读锁注解
 *
 * <p>
 * 读写锁中的读锁，允许多个线程同时获取读锁。
 * 是 @DistributedLock(type = LockType.READ) 的简化版本。
 * </p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DistributedLock(type = LockType.READ, key = "#root.methodName")
public @interface DistributedReadLock {

    /**
     * 锁的唯一标识键
     */
    String key();

    /**
     * 等待时间
     */
    long waitTime() default 3L;

    /**
     * 持有时间
     */
    long leaseTime() default 30L;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
