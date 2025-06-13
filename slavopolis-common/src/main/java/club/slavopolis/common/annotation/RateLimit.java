package club.slavopolis.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 限流注解, 用于接口访问频率限制
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key前缀
     */
    String key() default "";

    /**
     * 时间窗口内最大请求数
     */
    int limit() default 100;

    /**
     * 时间窗口
     */
    int window() default 1;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 是否使用用户维度限流
     */
    boolean perUser() default false;

    /**
     * 是否使用IP维度限流
     */
    boolean perIp() default true;
}
