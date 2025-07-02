package club.slavopolis.lock.aspect;

import club.slavopolis.lock.annotation.DistributeLock;
import club.slavopolis.lock.constant.DistributeLockConstant;
import club.slavopolis.lock.exception.DistributeLockException;
import club.slavopolis.lock.util.LockKeyUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 * <p>
 * 通过AOP拦截{@link DistributeLock}注解标注的方法，实现基于Redis的分布式锁功能。
 * 支持静态key和SpEL表达式动态key生成，提供灵活的锁超时和等待时间配置。
 * </p>
 * <p>
 * 锁的生命周期：
 * 1. 解析锁配置参数
 * 2. 生成锁key (格式: scene:key)
 * 3. 尝试获取分布式锁
 * 4. 执行业务方法
 * 5. 释放锁资源
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * @see DistributeLock 分布式锁注解
 * @see RedissonClient Redisson客户端
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Aspect
@Component
@Order(DistributeLockConstant.ASPECT_ORDER) // 设置一个最小的优先级，保证该切面优先执行
@RequiredArgsConstructor
public class DistributeLockAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeLockAspect.class);

    private final RedissonClient redissonClient;

    /**
     * 提取锁的key
     * <p>
     * 优先使用静态key，如果未设置则解析SpEL表达式获取动态key
     * </p>
     *
     * @param method 目标方法
     * @param distributeLock 分布式锁注解
     * @param joinPoint 切点信息
     * @return 锁的key
     * @throws DistributeLockException 当key和keyExpression都未设置时抛出
     */
    private String extractLockKey(Method method, DistributeLock distributeLock, ProceedingJoinPoint joinPoint) {
        String key = distributeLock.key();
        
        if (DistributeLockConstant.NONE_KEY.equals(key)) {
            if (DistributeLockConstant.NONE_KEY.equals(distributeLock.keyExpression())) {
                throw new DistributeLockException(DistributeLockConstant.ERROR_NO_KEY_FOUND);
            }
            key = parseSpelExpression(distributeLock.keyExpression(), method, joinPoint.getArgs());
        }
        
        return key;
    }

    /**
     * 解析SpEL表达式获取动态key
     * <p>
     * 使用方法参数名作为SpEL上下文变量，支持复杂的key生成逻辑
     * </p>
     *
     * @param expression SpEL表达式字符串
     * @param method 目标方法
     * @param args 方法参数值
     * @return 解析后的key值
     */
    private String parseSpelExpression(String expression, Method method, Object[] args) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression spelExpression = parser.parseExpression(expression);
        EvaluationContext context = new StandardEvaluationContext();

        StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);

        if (Objects.nonNull(parameterNames)) {
            for (int i = 0; i < parameterNames.length && i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        return String.valueOf(spelExpression.getValue(context));
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
     */
    private String buildLockKey(String scene, String key) {
        return LockKeyUtil.buildLockKey(scene, key);
    }

    /**
     * 获取分布式锁
     * <p>
     * 根据配置的等待时间和过期时间选择合适的锁获取策略：
     * 1. 无限等待 + 自动续期
     * 2. 无限等待 + 固定过期时间  
     * 3. 限时等待 + 自动续期
     * 4. 限时等待 + 固定过期时间
     * </p>
     *
     * @param rLock Redis锁对象
     * @param waitTime 等待时间(毫秒)
     * @param expireTime 过期时间(毫秒)
     * @param lockKey 锁的完整key
     * @return 是否成功获取锁
     * @throws InterruptedException 当获取锁被中断时抛出
     */
    private boolean acquireLock(RLock rLock, int waitTime, int expireTime, String lockKey) throws InterruptedException {
        boolean lockResult = false;

        if (waitTime == DistributeLockConstant.DEFAULT_WAIT_TIME) {
            if (expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
                LOGGER.info("尝试获取锁, key: {}", lockKey);
                rLock.lock();
            } else {
                LOGGER.info("尝试获取锁, key: {}, expire: {}", lockKey, expireTime);
                rLock.lock(expireTime, TimeUnit.MILLISECONDS);
            }
            lockResult = true;
        } else {
            if (expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
                LOGGER.info("尝试获取锁, key: {}, wait: {}", lockKey, waitTime);
                lockResult = rLock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            } else {
                LOGGER.info("尝试获取锁, key: {}, wait: {}, expire: {}", lockKey, waitTime, expireTime);
                lockResult = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
            }
        }

        return lockResult;
    }

    /**
     * 分布式锁切面处理逻辑
     * <p>
     * 拦截被{@link DistributeLock}注解标注的方法，实现分布式锁的获取、执行、释放流程
     * </p>
     *
     * @param joinPoint AOP切点信息
     * @return 目标方法的返回值
     * @throws Exception 当业务方法执行异常或锁获取失败时抛出
     */
    @Around("@annotation(club.slavopolis.lock.annotation.DistributeLock)")
    public Object process(ProceedingJoinPoint joinPoint) throws Exception {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);
        
        // 验证注解不为空（虽然通过切点进入理论上不会为空，但增强代码健壮性）
        if (distributeLock == null) {
            throw new DistributeLockException("DistributeLock注解不能为空");
        }
        
        // 提取锁的key
        String key = extractLockKey(method, distributeLock, joinPoint);

        // 构建完整锁key
        String lockKey = buildLockKey(distributeLock.scene(), key);
        
        int expireTime = distributeLock.expireTime();
        int waitTime = distributeLock.waitTime();
        
        RLock rLock = redissonClient.getLock(lockKey);
        
        try {
            // 获取分布式锁
            boolean lockResult = acquireLock(rLock, waitTime, expireTime, lockKey);
            
            if (!lockResult) {
                LOGGER.warn("获取锁失败, key: {}, waitTime: {}, expireTime: {}", lockKey, waitTime, expireTime);
                throw new DistributeLockException(DistributeLockConstant.ERROR_ACQUIRE_LOCK_FAILED + 
                    ", key: " + lockKey + ", waitTime: " + waitTime + ", expireTime: " + expireTime);
            }
            
            LOGGER.info("获取锁成功, key: {}, waitTime: {}, expireTime: {}", lockKey, waitTime, expireTime);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            // 恢复中断状态，遵循Java并发最佳实践
            Thread.currentThread().interrupt();

            String contextInfo = String.format("key: %s, waitTime: %s, expireTime: %s, 线程: %s", 
                lockKey, waitTime, expireTime, Thread.currentThread().getName());
            LOGGER.error("分布式锁获取被中断（切面处理）, {}", contextInfo, e);

            throw new DistributeLockException("分布式锁获取被中断（切面处理）, " + contextInfo, e);
        } catch (Throwable throwable) {
            LOGGER.error("执行业务逻辑异常, key: {}", lockKey, throwable);

            switch (throwable) {
                case RuntimeException runtimeException -> throw runtimeException;
                case Exception exception -> throw exception;
                default -> throw new DistributeLockException("业务执行异常", throwable);
            }
        } finally {
            // 确保当前线程持有锁才释放
            if (rLock.isHeldByCurrentThread()) {
                LOGGER.info("释放锁, key: {}", lockKey);
                rLock.unlock();
            }
        }
    }
}
