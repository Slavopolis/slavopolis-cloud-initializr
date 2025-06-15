package club.slavopolis.common.log.annotation;

import club.slavopolis.common.core.constants.CommonConstants;

import java.lang.annotation.*;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 性能监控注解
 *
 * <p>
 * 标记需要进行性能监控的方法。支持配置是否记录参数、返回值，
 * 以及慢速执行的阈值。当方法执行时间超过阈值时，会记录警告日志。
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogPerformance {

    /**
     * 是否记录方法参数
     */
    boolean logArgs() default true;

    /**
     * 是否记录返回值
     */
    boolean logResult() default false;

    /**
     * 慢速执行阈值（毫秒）
     */
    long slowThreshold() default 1000L;

    /**
     * 是否记录慢速方法的堆栈信息
     */
    boolean logStackTrace() default false;

    /**
     * 业务描述
     */
    String description() default CommonConstants.EMPTY;
}
