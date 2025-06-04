package club.slavopolis.common.lock.annotation;

import club.slavopolis.common.lock.enums.LockType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁注解
 *
 * <p>
 * 该注解用于方法级别，标记需要使用分布式锁保护的业务方法。
 * 支持多种锁类型、自定义超时时间、重试机制等特性。
 *
 * 使用示例：
 * <pre>
 * {@code
 * @DistributedLock(key = "order:create:#{#order.userId}")
 * public Order createOrder(Order order) {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁的唯一标识键
     * <p>
     * 支持 SpEL 表达式，可以动态生成锁键。
     * 例如：
     * - 固定键："user:lock"
     * - SpEL表达式："user:#{#userId}"
     * - 复杂表达式："order:#{#order.userId}:#{#order.productId}"
     * </p>
     *
     * @return 锁键表达式
     */
    String key();

    /**
     * 锁键前缀
     * <p>
     * 用于区分不同业务模块的锁，避免键冲突。
     * 最终的锁键为：prefix + ":" + key
     * </p>
     *
     * @return 锁键前缀
     */
    String prefix() default "";

    /**
     * 锁类型
     * <p>
     * 支持多种锁实现：
     * - REENTRANT：可重入锁（默认）
     * - FAIR：公平锁
     * - READ：读锁
     * - WRITE：写锁
     * - MULTI：联锁
     * - RED：红锁
     * </p>
     *
     * @return 锁类型
     */
    LockType type() default LockType.REENTRANT;

    /**
     * 等待获取锁的最大时间
     * <p>
     * 0 表示不等待，立即返回获取结果
     * -1 表示无限等待直到获取到锁
     * </p>
     *
     * @return 等待时间
     */
    long waitTime() default 3L;

    /**
     * 锁的持有时间
     * <p>
     * 超过该时间后锁将自动释放，防止死锁。
     * -1 表示不自动释放，需要手动释放（不推荐）
     * </p>
     *
     * @return 持有时间
     */
    long leaseTime() default 30L;

    /**
     * 时间单位
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时是否抛出异常
     * <p>
     * true：抛出 LockAcquisitionException
     * false：返回 null 或默认值
     * </p>
     *
     * @return 是否抛出异常
     */
    boolean throwException() default true;

    /**
     * 获取锁失败时的提示信息
     *
     * @return 错误提示
     */
    String errorMessage() default "获取分布式锁失败";

    /**
     * 是否自动续期
     * <p>
     * 开启后将在锁即将过期时自动延长持有时间，
     * 适用于执行时间不确定的长任务。
     * </p>
     *
     * @return 是否自动续期
     */
    boolean autoRenew() default false;

    /**
     * 自动续期的检查间隔
     * <p>
     * 仅在 autoRenew = true 时生效
     * </p>
     *
     * @return 续期间隔（秒）
     */
    long renewInterval() default 10L;

    /**
     * 是否启用锁降级
     * <p>
     * 在分布式锁不可用时降级为本地锁，
     * 保证业务的可用性。
     * </p>
     *
     * @return 是否启用降级
     */
    boolean enableFallback() default false;

    /**
     * 业务标识
     * <p>
     * 用于监控和日志记录，方便问题排查
     * </p>
     *
     * @return 业务标识
     */
    String business() default "";
}
