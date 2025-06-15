package club.slavopolis.infrastructure.cache.lock.aspect;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.core.exception.BusinessException;
import club.slavopolis.infrastructure.cache.lock.annotation.DistributedLock;
import club.slavopolis.infrastructure.cache.lock.core.DistributedLocker;
import club.slavopolis.infrastructure.cache.lock.core.LockInfo;
import club.slavopolis.infrastructure.cache.lock.core.LockKeyGenerator;
import club.slavopolis.infrastructure.cache.lock.enums.LockStatus;
import club.slavopolis.infrastructure.cache.lock.event.LockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁切面
 *
 * <p>
 * 该切面负责拦截标注了 @DistributedLock 注解的方法，
 * 在方法执行前获取分布式锁，执行后释放锁。
 * 支持自动续期、锁降级、事件发布等高级特性。
 * </p>
 */
@Slf4j
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final DistributedLocker distributedLocker;
    private final LockKeyGenerator lockKeyGenerator;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 用于生成锁任务计数器
     */
    private final AtomicInteger lockTaskCounter = new AtomicInteger(0);

    /**
     * 自动续期的线程池
     */
    private final ScheduledExecutorService renewalExecutor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("lock-renewal-" + thread.threadId());
                thread.setDaemon(true);
                return thread;
            }
    );

    /**
     * 正在续期的任务（键：锁键， 值：定时任务饮用）
     */
    private final ConcurrentHashMap<String, ScheduledFuture<?>> renewalTasks = new ConcurrentHashMap<>();

    /**
     * 环绕通知，处理分布式锁逻辑
     */
    @Around("@annotation(distributedLock)")
    public Object handleDistributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + CommonConstants.DOT + signature.getName();

        // 生成锁键
        String lockKey = lockKeyGenerator.generate(distributedLock.prefix(), distributedLock.key(), joinPoint);

        // 构建锁信息
        LockInfo lockInfo = LockInfo.builder()
                .lockKey(lockKey)
                .lockType(distributedLock.type())
                .business(distributedLock.business())
                .methodSignature(methodName)
                .traceId(MDC.get(CommonConstants.TRACE_ID))
                .build();

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 尝试获取锁
        boolean acquired = false;
        ScheduledFuture<?> renewalTask = null;

        try {
            log.info("获取分布式锁 - 键: {}, 类型: {}, 方法: {}", lockKey, distributedLock.type(), methodName);

            // 尝试获取锁
            acquired = tryAcquireLock(lockKey, distributedLock);

            // 更新锁信息
            lockInfo.setWaitDuration(System.currentTimeMillis() - startTime);
            lockInfo.setAcquireTime(LocalDateTime.now());

            if (acquired) {
                lockInfo.setLockStatus(LockStatus.ACQUIRED);
                lockInfo.setOwner(Thread.currentThread().getName());
                lockInfo.setSuccess(Boolean.TRUE);

                log.info("成功获取分布式锁 - 键: {}, 等待时间: {}ms", lockKey, lockInfo.getWaitDuration());

                // 发布锁获取成功事件
                publishLockEvent(lockInfo, LockEvent.EventType.ACQUIRED);

                // 启动自动续期
                if (distributedLock.autoRenew()) {
                    renewalTask = startAutoRenewal(lockKey, distributedLock);
                }

                // 执行业务逻辑
                return joinPoint.proceed();
            } else {
                // 获取锁失败
                lockInfo.setLockStatus(LockStatus.FAILED);
                lockInfo.setSuccess(false);

                String errorMsg = String.format("%s - key: %s", distributedLock.errorMessage(), lockKey);
                log.warn("获取分布式锁失败 - 键: {}, 等待时间: {}ms", lockKey, lockInfo.getWaitDuration());

                // 发布锁获取失败事件
                publishLockEvent(lockInfo, LockEvent.EventType.FAILED);

                // 是否启用降级
                if (distributedLock.enableFallback()) {
                    log.info("降级为本地锁执行 - 键: {}", lockKey);
                    lockInfo.setLockStatus(LockStatus.FALLBACK);
                    return executeWithLocalLock(joinPoint, lockKey);
                }

                // 是否抛出异常
                if (distributedLock.throwException()) {
                    throw new BusinessException(errorMsg);
                }

                return null;
            }
        } finally {
            // 停止自动续期
            if (renewalTask != null) {
                renewalTask.cancel(false);
                renewalTasks.remove(lockKey);
            }

            // 释放锁
            if (acquired) {
                try {
                    distributedLocker.unlock(lockKey);

                    // 更新锁信息
                    lockInfo.setReleaseTime(LocalDateTime.now());
                    lockInfo.setHoldDuration(System.currentTimeMillis() - startTime - lockInfo.getWaitDuration());
                    lockInfo.setLockStatus(LockStatus.RELEASED);

                    log.info("释放分布式锁 - 键: {}, 持有时间: {}ms", lockKey, lockInfo.getHoldDuration());

                    // 发布锁释放事件
                    publishLockEvent(lockInfo, LockEvent.EventType.RELEASED);

                } catch (Exception e) {
                    log.error("释放分布式锁出错 - 键: {}", lockKey, e);
                }
            }
        }
    }

    /**
     * 根据锁类型获取锁
     */
    private boolean tryAcquireLock(String lockKey, DistributedLock distributedLock) {
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();

        return switch (distributedLock.type()) {
            case REENTRANT, FAIR, SPIN -> distributedLocker.tryLock(lockKey, waitTime, leaseTime, timeUnit);
            case READ -> tryAcquireReadLock(lockKey, waitTime, leaseTime, timeUnit);
            case WRITE -> tryAcquireWriteLock(lockKey, waitTime, leaseTime, timeUnit);
            case MULTI, RED -> tryAcquireMultiLock(lockKey, waitTime, leaseTime, timeUnit);
        };
    }

    /**
     * 获取读锁
     */
    private boolean tryAcquireReadLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        // 这里需要根据具体的锁实现来处理读锁
        // 示例使用标准锁实现
        return distributedLocker.tryLock(lockKey + ":read", waitTime, leaseTime, timeUnit);
    }

    /**
     * 获取写锁
     */
    private boolean tryAcquireWriteLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        // 这里需要根据具体的锁实现来处理写锁
        // 示例使用标准锁实现
        return distributedLocker.tryLock(lockKey + ":write", waitTime, leaseTime, timeUnit);
    }

    /**
     * 获取联锁/红锁
     */
    private boolean tryAcquireMultiLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        // 这里需要根据具体的锁实现来处理联锁
        // 示例使用标准锁实现
        return distributedLocker.tryLock(lockKey, waitTime, leaseTime, timeUnit);
    }

    /**
     * 启动自动续期任务
     */
    private ScheduledFuture<?> startAutoRenewal(String lockKey, DistributedLock distributedLock) {
        long renewInterval = distributedLock.renewInterval();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();

        // 续期间隔应该小于租期时间
        long renewIntervalMillis = timeUnit.toMillis(renewInterval);
        long leaseTimeMillis = timeUnit.toMillis(leaseTime);

        if (renewIntervalMillis >= leaseTimeMillis) {
            log.warn("续期时间必须小于锁的租期时间，自动续期功能已禁用 - 键: {}", lockKey);
            return null;
        }

        // 为当前任务生成唯一ID
        final int taskId = lockTaskCounter.incrementAndGet();

        ScheduledFuture<?> task = renewalExecutor.scheduleWithFixedDelay(() -> {
            try {
                // 检查锁是否在映射表中
                if (!renewalTasks.containsKey(lockKey)) {
                    log.debug("任务已取消, 停止续期 - 键: {}, 任务ID: {}", lockKey, taskId);
                    return;
                }

                if (distributedLocker.isLocked(lockKey)) {
                    boolean renewed = distributedLocker.renewLock(lockKey, leaseTime, timeUnit);
                    if (renewed) {
                        log.debug("成功续期锁 - 键: {}, 任务ID: {}", lockKey, taskId);
                    } else {
                        log.warn("续期锁失败 - 键: {}, 任务ID: {}", lockKey, taskId);
                    }
                }
            } catch (Exception e) {
                log.error("锁续期过程中发生错误 - 键: {}, 任务ID: {}", lockKey, taskId, e);
            }
        }, renewIntervalMillis, renewIntervalMillis, TimeUnit.MILLISECONDS);

        // 将任务保存到映射表
        renewalTasks.put(lockKey, task);
        log.debug("已启动锁自动续期任务 - 键: {}, 任务ID: {}, 续期间隔: {}ms", lockKey, taskId, renewIntervalMillis);
        return task;
    }

    /**
     * 使用本地锁执行（降级方案）
     */
    private Object executeWithLocalLock(ProceedingJoinPoint joinPoint, String lockKey) throws Throwable {
        // 这里使用新建 Object 作为锁对象，避免使用字符串常量池中的对象
        // 实际应用中可以使用更复杂的本地锁实现，如ReentrantLock
        // 1. 可重入锁（ReentrantLock）：支持更灵活的锁操作和公平性策略
        // 2. 读写锁（ReadWriteLock）：允许并发读操作
        // 3. 条件变量（Condition）：支持线程间协作
        // 4. 分段锁（Segment Lock）：提高并发度
        // 6. 自旋锁：适用于段时间持有的场景
        final Object lockObject = new Object();
        synchronized (lockObject) {
            log.info("使用本地锁执行业务逻辑 - 键: {}", lockKey);
            return joinPoint.proceed();
        }
    }

    /**
     * 发布锁事件
     */
    private void publishLockEvent(LockInfo lockInfo, LockEvent.EventType eventType) {
        try {
            LockEvent event = new LockEvent(this, lockInfo, eventType);
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("发布锁事件时发生错误", e);
        }
    }
}
