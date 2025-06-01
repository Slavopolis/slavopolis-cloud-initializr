package club.slavopolis.common.annotation;

import java.lang.annotation.*;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 请求追踪注解, 标记需要追踪的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Traceable {

    /**
     * 是否记录请求参数
     */
    boolean logParams() default true;

    /**
     * 是否记录响应结果
     */
    boolean logResult() default true;

    /**
     * 是否记录执行时间
     */
    boolean logTime() default true;

    /**
     * 慢接口阈值（毫秒）
     */
    long slowThreshold() default 1000L;
}
