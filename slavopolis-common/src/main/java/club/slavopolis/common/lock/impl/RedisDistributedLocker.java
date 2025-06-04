package club.slavopolis.common.lock.impl;

import club.slavopolis.common.lock.core.DistributedLocker;
import club.slavopolis.common.lock.exception.LockAcquisitionException;
import club.slavopolis.common.lock.exception.LockReleaseException;
import club.slavopolis.common.lock.exception.LockServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.redisnode.RedisNodes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 基于 Redisson 的分布式锁实现
 *
 * <p>
 * 该类使用 Redisson 客户端实现分布式锁功能，提供了完整的锁操作支持，
 * 包括可重入锁、公平锁、读写锁等多种锁类型。Redisson 提供了看门狗机制，
 * 可以自动续期，防止业务执行时间过长导致锁过期。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass(RedissonClient.class)
public class RedisDistributedLocker implements DistributedLocker {

    private final RedissonClient redissonClient;

    /**
     * 尝试获取锁
     * <p>
     * 在指定的等待时间内尝试获取锁，如果成功获取则持有锁直到指定的租期时间。
     * 该方法不会阻塞当前线程，会立即返回获取结果。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     * @param waitTime 最大等待时间，0表示不等待
     * @param leaseTime 锁的持有时间，-1表示使用看门狗自动续期
     * @param unit 时间单位
     * @return true表示成功获取锁，false表示获取失败
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            // 检查 Redis 连接状态
            if (!isRedisAvailable()) {
                throw new LockServiceUnavailableException("Redis");
            }

            RLock lock = getLock(lockKey);
            boolean acquired;
            if (leaseTime == -1) {
                // 使用看门狗自动续期，默认续期时间为30秒
                acquired = lock.tryLock(waitTime, unit);
            } else {
                acquired = lock.tryLock(waitTime, leaseTime, unit);
            }

            if (acquired) {
                log.debug("成功获取锁 - 键: {}", lockKey);
            } else {
                log.debug("获取锁失败 - 键: {} 耗时 {} {}", lockKey, waitTime, unit);
            }

            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程在尝试获取锁时被中断: {}", lockKey, e);
            return false;
        } catch (Exception e) {
            log.error("尝试获取锁时发生错误: {}", lockKey, e);
            if (isRedisConnectionException(e)) {
                throw new LockServiceUnavailableException("Redis", e);
            }
            return false;
        }
    }

    /**
     * 获取锁（阻塞模式）
     * <p>
     * 阻塞当前线程直到成功获取锁。如果指定了租期时间，锁将在该时间后自动释放；
     * 如果租期时间为-1，则使用看门狗机制自动续期。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     * @param leaseTime 锁的持有时间，-1表示使用看门狗自动续期
     * @param unit 时间单位
     */
    @Override
    public void lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            if (leaseTime == -1) {
                lock.lock();
            } else {
                lock.lock(leaseTime, unit);
            }
            log.debug("成功获取锁: {}", lockKey);
        } catch (Exception e) {
            log.error("获取锁时发生错误: {}", lockKey, e);
            throw new LockAcquisitionException(lockKey, 0L, "获取锁失败" + e.getMessage());
        }
    }

    /**
     * 释放锁
     * <p>
     * 释放当前线程持有的锁。只有锁的持有者才能释放锁，
     * 如果当前线程不是锁的持有者，则会抛出异常。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     */
    @Override
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("成功释放锁: {}", lockKey);
            } else {
                log.warn("当前线程未持有的锁: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放锁时发生错误: {}", lockKey, e);
            throw new LockReleaseException(lockKey, "释放锁时发生错误", e);
        }
    }

    /**
     * 判断当前线程是否持有锁
     *
     * @param lockKey 锁的唯一标识
     * @return true表示当前线程持有该锁
     */
    @Override
    public boolean isLocked(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.isHeldByCurrentThread();
    }

    /**
     * 判断锁是否被任何线程持有
     *
     * @param lockKey 锁的唯一标识
     * @return true表示锁被某个线程持有
     */
    @Override
    public boolean isExists(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.isLocked();
    }

    /**
     * 在锁保护下执行业务逻辑
     * <p>
     * 该方法会先尝试获取锁，成功后执行业务逻辑，最后自动释放锁。
     * 使用 try-finally 确保锁一定会被释放，避免死锁。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     * @param waitTime 最大等待时间
     * @param leaseTime 锁的持有时间
     * @param unit 时间单位
     * @param supplier 需要在锁保护下执行的业务逻辑
     * @param <T> 业务逻辑返回值类型
     * @return 业务逻辑的执行结果
     * @throws RuntimeException 获取锁失败时抛出
     */
    @Override
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        boolean acquired = false;
        try {
            acquired = tryLock(lockKey, waitTime, leaseTime, unit);
            if (!acquired) {
                throw new LockAcquisitionException(lockKey, unit.toMillis(waitTime));
            }

            log.debug("在锁定保护下执行业务逻辑: {}", lockKey);
            return supplier.get();
        } finally {
            if (acquired) {
                unlock(lockKey);
            }
        }
    }

    /**
     * 强制解锁
     * <p>
     * 强制释放锁，不管当前线程是否是锁的持有者。
     * 该方法应当谨慎使用，仅在紧急情况下使用，比如锁的持有者异常终止。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     */
    @Override
    public void forceUnlock(String lockKey) {
        RLock lock = getLock(lockKey);
        try {
            lock.forceUnlock();
            log.warn("强制解锁: {}", lockKey);
        } catch (Exception e) {
            log.error("强制解锁时发生错误: {}", lockKey, e);
        }
    }

    /**
     * 获取锁的剩余有效时间
     *
     * @param lockKey 锁的唯一标识
     * @return 剩余时间（毫秒），-1表示未设置过期时间，-2表示锁不存在
     */
    @Override
    public long getRemainTime(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.remainTimeToLive();
    }

    /**
     * 续期锁
     * <p>
     * 为已持有的锁延长持有时间，防止业务执行时间过长导致锁过期。
     * 只有锁的持有者才能续期。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     * @param leaseTime 新的持有时间
     * @param unit 时间单位
     * @return true表示续期成功
     */
    @Override
    public boolean renewLock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            if (lock.isHeldByCurrentThread()) {
                // Redisson 的续期方式：重新获取锁并设置新的过期时间
                // 对于已持有的锁，这个操作会更新过期时间
                lock.lock(leaseTime, unit);
                log.debug("已成功续期锁: {} 续期时间: {} {}", lockKey, leaseTime, unit);
                return true;
            }
            log.warn("无法续期锁: {} - 未被当前线程持有", lockKey);
            return false;
        } catch (Exception e) {
            log.error("续期锁时发生错误: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 获取锁实例
     * <p>
     * 根据锁键获取对应的 RLock 实例。
     * 这里默认返回可重入锁，子类可以重写此方法返回其他类型的锁。
     * </p>
     *
     * @param lockKey 锁的唯一标识
     * @return RLock 实例
     */
    protected RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 获取公平锁
     *
     * @param lockKey 锁的唯一标识
     * @return 公平锁实例
     */
    public RLock getFairLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    /**
     * 获取读写锁
     *
     * @param lockKey 锁的唯一标识
     * @return 读写锁实例
     */
    public RReadWriteLock getReadWriteLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey);
    }

    /**
     * 获取红锁
     *
     * @param lockKeys 多个锁键
     * @return 红锁实例
     */
    public RLock getRedLock(String... lockKeys) {
        return getMultiLock(lockKeys);
    }

    /**
     * 获取联锁
     *
     * @param lockKeys 多个锁键
     * @return 联锁实例
     */
    public RLock getMultiLock(String... lockKeys) {
        RLock[] locks = new RLock[lockKeys.length];
        for (int i = 0; i < lockKeys.length; i++) {
            locks[i] = getLock(lockKeys[i]);
        }
        return redissonClient.getMultiLock(locks);
    }

    /**
     * 检查 Redis 是否可用
     *
     * @return true 表示 Redis 服务可用
     */
    private boolean isRedisAvailable() {
        try {
            // 根据不同的 Redis 部署模式进行健康检查
            // 这里尝试获取单节点信息，如果成功则认为 Redis 可用
            var singleNode = redissonClient.getRedisNodes(RedisNodes.SINGLE);
            if (singleNode != null) {
                // 尝试 ping 单个节点来验证连接
                return singleNode.pingAll();
            }

            return false;
        } catch (Exception e) {
            // 如果是集群或其他模式，尝试其他方式
            try {
                var clusterNodes = redissonClient.getRedisNodes(RedisNodes.CLUSTER);
                if (clusterNodes != null) {
                    return clusterNodes.pingAll();
                }
            } catch (Exception clusterException) {
                // 如果不是集群模式，尝试主从模式
                try {
                    var masterSlaveNodes = redissonClient.getRedisNodes(RedisNodes.MASTER_SLAVE);
                    if (masterSlaveNodes != null) {
                        return masterSlaveNodes.pingAll();
                    }
                } catch (Exception masterSlaveException) {
                    log.debug("所有模式的 Redis 可用性检查均失败", masterSlaveException);
                }
            }

            log.error("Redis 可用性检查失败", e);
            return false;
        }
    }

    /**
     * 判断是否为 Redis 连接异常
     *
     * @param e 异常对象
     * @return true 表示是连接异常
     */
    private boolean isRedisConnectionException(Exception e) {
        if (e == null) {
            return false;
        }

        String message = e.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            return lowerMessage.contains("connection") ||
                    lowerMessage.contains("timeout") ||
                    lowerMessage.contains("redis") ||
                    lowerMessage.contains("unable to connect");
        }

        // 检查异常链
        Throwable cause = e.getCause();
        if (cause instanceof Exception exception) {
            return isRedisConnectionException(exception);
        }

        return false;
    }
}
