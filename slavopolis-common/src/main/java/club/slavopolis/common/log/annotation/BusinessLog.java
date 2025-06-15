package club.slavopolis.common.log.annotation;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.enums.OperationType;

import java.lang.annotation.*;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 业务日志注解
 *
 * <p>
 * 该注解用于标记需要记录业务日志的方法。通过 AOP 自动记录方法的业务操作信息，
 * 包括操作类型、业务模块、操作描述等。支持 SpEL 表达式动态生成日志内容。
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessLog {

    /**
     * 业务模块
     */
    String module() default CommonConstants.EMPTY;

    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;

    /**
     * 操作描述
     * 支持 SpEL 表达式，例如："删除用户 #{#user.name}"
     */
    String description() default CommonConstants.EMPTY;

    /**
     * 是否记录请求参数
     */
    boolean logArgs() default true;

    /**
     * 是否记录返回结果
     */
    boolean logResult() default false;

    /**
     * 是否记录异常详情
     */
    boolean logException() default true;
}
