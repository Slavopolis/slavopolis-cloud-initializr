package club.slavopolis.infrastructure.cache.lock.core;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 锁键生成器接口
 *
 * <p>
 * 负责根据方法参数和SpEL表达式生成最终的锁键。
 * </p>
 */
public interface LockKeyGenerator {

    /**
     * 生成锁键
     *
     * @param prefix 前缀
     * @param key SpEL表达式
     * @param joinPoint 切点信息
     * @return 最终的锁键
     */
    String generate(String prefix, String key, ProceedingJoinPoint joinPoint);
}
