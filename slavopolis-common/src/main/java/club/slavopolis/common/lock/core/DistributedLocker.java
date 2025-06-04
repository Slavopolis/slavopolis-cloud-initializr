package club.slavopolis.common.lock.core;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁核心接口
 *
 * <p>
 * 定义了分布式锁的标准操作接口，所有分布式锁实现都需要遵循此接口规范。
 * 该接口支持基本的锁操作、超时控制、自动释放等特性。
 * </p>
 */
public interface DistributedLocker {

    /**
     * 尝试获取锁
     *
     * @param lockKey 锁键
     * @param waitTime 等待时间
     * @param leaseTime 持有时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 获取锁（阻塞直到获取成功）
     *
     * @param lockKey 锁键
     * @param leaseTime 持有时间
     * @param unit 时间单位
     */
    void lock(String lockKey, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param lockKey 锁键
     */
    void unlock(String lockKey);

    /**
     * 判断是否持有锁
     *
     * @param lockKey 锁键
     * @return 是否持有锁
     */
    boolean isLocked(String lockKey);


    /**
     * 判断锁是否被任何线程持有
     *
     * @param lockKey 锁键
     * @return 锁是否被持有
     */
    boolean isExists(String lockKey);

    /**
     * 在锁保护下执行业务逻辑
     *
     * @param lockKey 锁键
     * @param waitTime 等待时间
     * @param leaseTime 持有时间
     * @param unit 时间单位
     * @param supplier 业务逻辑
     * @param <T> 返回值类型
     * @return 业务执行结果
     */
    <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier);

    /**
     * 强制解锁（谨慎使用）
     *
     * @param lockKey 锁键
     */
    void forceUnlock(String lockKey);

    /**
     * 获取锁的剩余有效时间
     *
     * @param lockKey 锁键
     * @return 剩余时间（毫秒），-1表示未设置过期时间，-2表示锁不存在
     */
    long getRemainTime(String lockKey);

    /**
     * 续期
     *
     * @param lockKey 锁键
     * @param leaseTime 新的持有时间
     * @param unit 时间单位
     * @return 是否续期成功
     */
    boolean renewLock(String lockKey, long leaseTime, TimeUnit unit);
}
