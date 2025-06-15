package club.slavopolis.infrastructure.cache.lock.annotation;

import club.slavopolis.infrastructure.cache.lock.enums.LockType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式写锁注解
 *
 * <p>
 * 读写锁中的写锁，独占锁，同一时间只允许一个线程持有。
 * 是 @DistributedLock(type = LockType.WRITE) 的简化版本。
 * </p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DistributedLock(type = LockType.WRITE, key = "#root.methodName")
public @interface DistributedWriteLock {

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
