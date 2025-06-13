package club.slavopolis.common.util;

import club.slavopolis.common.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 验证工具类, 提供参数验证相关的工具方法
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

    private static Validator validator;

    /**
     * 设置验证器
     */
    public static void setValidator(Validator validator) {
        ValidationUtils.validator = validator;
    }

    /**
     * 验证对象
     */
    public static <T> void validate(T object, Class<?>... groups) {
        if (validator == null) {
            throw new IllegalStateException("Validator not initialized");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw ValidationException.of(errors);
        }
    }

    /**
     * 验证单个属性
     */
    public static <T> void validateProperty(T object, String propertyName, Class<?>... groups) {
        if (validator == null) {
            throw new IllegalStateException("Validator not initialized");
        }

        Set<ConstraintViolation<T>> violations = validator.validateProperty(object, propertyName, groups);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> violation = violations.iterator().next();
            throw ValidationException.of(propertyName, violation.getMessage());
        }
    }

    /**
     * 校验非空
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验字符串非空
     */
    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验字符串非空白
     */
    public static void notBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验为真
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验为假
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验邮箱格式
     */
    public static void isEmail(String email, String message) {
        if (!StringUtils.isEmail(email)) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验手机号格式
     */
    public static void isMobile(String mobile, String message) {
        if (!StringUtils.isMobile(mobile)) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验数值范围
     */
    public static void range(long value, long min, long max, String message) {
        if (value < min || value > max) {
            throw ValidationException.of(message);
        }
    }

    /**
     * 校验字符串长度
     */
    public static void length(String str, int min, int max, String message) {
        if (str == null || str.length() < min || str.length() > max) {
            throw ValidationException.of(message);
        }
    }
}
