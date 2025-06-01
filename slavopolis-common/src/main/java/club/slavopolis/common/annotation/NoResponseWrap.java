package club.slavopolis.common.annotation;

import java.lang.annotation.*;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 不包装响应注解, 标记在 Controller 类或方法上，表示不需要统一响应包装
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoResponseWrap {
}
