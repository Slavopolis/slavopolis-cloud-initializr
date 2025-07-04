package club.slavopolis.persistence.jdbc.parameter;

/**
 * 参数验证器接口
 * <p>提供参数验证逻辑的抽象，支持自定义验证规则</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@FunctionalInterface
public interface ParameterValidator {

    /**
     * 验证参数
     * 
     * @param parameterSource 参数源
     * @throws IllegalArgumentException 如果验证失败
     */
    void validate(TypeSafeParameterSource parameterSource);

    /**
     * 组合验证器
     * 
     * @param other 另一个验证器
     * @return 组合后的验证器
     */
    default ParameterValidator and(ParameterValidator other) {
        return parameterSource -> {
            this.validate(parameterSource);
            other.validate(parameterSource);
        };
    }

    /**
     * 创建必需参数验证器
     * 
     * @param paramNames 必需参数名
     * @return 验证器
     */
    static ParameterValidator required(String... paramNames) {
        return parameterSource -> {
            for (String paramName : paramNames) {
                if (!parameterSource.hasParameter(paramName) || parameterSource.getValue(paramName) == null) {
                    throw new IllegalArgumentException("Required parameter '" + paramName + "' is missing or null");
                }
            }
        };
    }

    /**
     * 创建非空字符串验证器
     * 
     * @param paramNames 参数名
     * @return 验证器
     */
    static ParameterValidator nonEmptyString(String... paramNames) {
        return parameterSource -> {
            for (String paramName : paramNames) {
                Object value = parameterSource.getValue(paramName);
                if (value instanceof String && ((String) value).trim().isEmpty()) {
                    throw new IllegalArgumentException("Parameter '" + paramName + "' cannot be empty string");
                }
            }
        };
    }

    /**
     * 创建数值范围验证器
     * 
     * @param paramName 参数名
     * @param min 最小值
     * @param max 最大值
     * @return 验证器
     */
    static ParameterValidator numberRange(String paramName, Number min, Number max) {
        return parameterSource -> {
            Object value = parameterSource.getValue(paramName);
            if (value instanceof Number) {
                double num = ((Number) value).doubleValue();
                double minVal = min.doubleValue();
                double maxVal = max.doubleValue();
                if (num < minVal || num > maxVal) {
                    throw new IllegalArgumentException(
                        String.format("Parameter '%s' value %s is out of range [%s, %s]", 
                        paramName, num, minVal, maxVal));
                }
            }
        };
    }
} 