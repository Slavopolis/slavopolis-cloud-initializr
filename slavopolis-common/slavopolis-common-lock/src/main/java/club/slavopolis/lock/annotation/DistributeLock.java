package club.slavopolis.lock.annotation;

import club.slavopolis.lock.constant.DistributeLockConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁自定义注解
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {

    /**
     * 场景名称
     * <p>
     * 用于业务场景隔离，不同场景使用不同的锁命名空间。
     * 最终锁key格式为: scene:key
     * </p>
     * 
     * @return 业务场景名称，不能为空
     */
    String scene();

    /**
     * 锁的静态key
     * <p>
     * 当设置了静态key时，将忽略keyExpression参数。
     * 如果既未设置key也未设置keyExpression，将抛出异常。
     * </p>
     * <p>
     * 使用示例：
     * {@code @DistributeLock(scene = "order", key = "create")}
     * </p>
     *
     * @return 锁的静态key，默认为NONE_KEY表示未设置
     */
    String key() default DistributeLockConstant.NONE_KEY;

    /**
     * 锁的key表达式（SpEL）
     * <p>
     * 使用Spring Expression Language动态生成锁key。
     * 可以使用方法参数作为变量，支持复杂的key生成逻辑。
     * 当key为NONE_KEY时，将使用此表达式生成动态key。
     * </p>
     * <p>
     * 使用示例：
     * {@code @DistributeLock(scene = "user", keyExpression = "#userId + ':' + #action")}
     * </p>
     *
     * @return SpEL表达式字符串，默认为NONE_KEY表示未设置
     */
    String keyExpression() default DistributeLockConstant.NONE_KEY;

    /**
     * 锁的过期时间
     * <p>
     * 单位：毫秒
     * 默认值-1表示不设置固定过期时间，使用Redisson的自动续期机制，
     * 锁将在业务执行完成后自动释放。
     * 设置固定过期时间可以防止死锁，但需要确保时间足够业务执行完成。
     * </p>
     * <p>
     * 使用示例：
     * {@code @DistributeLock(scene = "task", key = "process", expireTime = 30000)} // 30秒过期
     * </p>
     *
     * @return 过期时间（毫秒），-1表示自动续期
     */
    int expireTime() default DistributeLockConstant.DEFAULT_EXPIRE_TIME;

    /**
     * 获取锁等待时间
     * <p>
     * 单位：毫秒
     * 默认值Integer.MAX_VALUE表示无限等待，直到获取锁成功。
     * 设置有限等待时间可以避免线程长时间阻塞，但获取锁可能失败。
     * </p>
     * <p>
     * 使用示例：
     * {@code @DistributeLock(scene = "limit", key = "action", waitTime = 5000)} // 最多等待5秒
     * </p>
     *
     * @return 等待时间（毫秒），Integer.MAX_VALUE表示无限等待
     */
    int waitTime() default DistributeLockConstant.DEFAULT_WAIT_TIME;
}
