package club.slavopolis.infrastructure.persistence.jdbc.parameter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于Map的类型安全参数源实现
 * <p>扩展Spring的MapSqlParameterSource，提供类型安全的参数绑定功能</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class MapParameterSource extends MapSqlParameterSource implements TypeSafeParameterSource {

    /**
     * 参数验证器列表
     */
    private final List<ParameterValidator> validators = new ArrayList<>();

    /**
     * 无参构造函数
     */
    public MapParameterSource() {
        super();
    }

    /**
     * 基于Map的构造函数
     * 
     * @param values 参数Map
     */
    public MapParameterSource(Map<String, ?> values) {
        super(values);
    }

    /**
     * 基于单个参数的构造函数
     * 
     * @param paramName 参数名
     * @param value 参数值
     */
    public MapParameterSource(String paramName, Object value) {
        super(paramName, value);
    }

    @Override
    public <T> T getValue(String paramName, Class<T> requiredType) {
        Object value = getValue(paramName);
        if (value == null) {
            return null;
        }
        
        return convertValue(value, requiredType);
    }

    @Override
    public <T> T getValue(String paramName, Class<T> requiredType, T defaultValue) {
        T value = getValue(paramName, requiredType);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean hasParameter(String paramName) {
        return hasValue(paramName);
    }

    @Override
    public String[] getParameterNames() {
        return getValues().keySet().toArray(new String[0]);
    }

    @Override
    public void validateRequiredParameters(String... requiredParams) {
        Assert.notNull(requiredParams, "Required parameters array must not be null");
        
        for (String paramName : requiredParams) {
            if (!hasParameter(paramName) || getValue(paramName) == null) {
                throw new IllegalArgumentException("Required parameter '" + paramName + "' is missing or null");
            }
        }
    }

    @Override
    public TypeSafeParameterSource addValidator(ParameterValidator validator) {
        Assert.notNull(validator, "Validator must not be null");
        validators.add(validator);
        return this;
    }

    @Override
    public void validate() {
        for (ParameterValidator validator : validators) {
            validator.validate(this);
        }
    }

    /**
     * 类型转换方法
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> requiredType) {
        if (requiredType.isInstance(value)) {
            return (T) value;
        }
        
        // 基础类型转换
        if (requiredType == String.class) {
            return (T) value.toString();
        }
        
        if (requiredType == Integer.class || requiredType == int.class) {
            if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            } else if (value instanceof String) {
                return (T) Integer.valueOf((String) value);
            }
        }
        
        if (requiredType == Long.class || requiredType == long.class) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            } else if (value instanceof String) {
                return (T) Long.valueOf((String) value);
            }
        }
        
        if (requiredType == Double.class || requiredType == double.class) {
            if (value instanceof Number) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            } else if (value instanceof String) {
                return (T) Double.valueOf((String) value);
            }
        }
        
        if (requiredType == Boolean.class || requiredType == boolean.class) {
            if (value instanceof Boolean) {
                return (T) value;
            } else if (value instanceof String) {
                return (T) Boolean.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Boolean.valueOf(((Number) value).intValue() != 0);
            }
        }
        
        throw new IllegalArgumentException(
            "Cannot convert value of type " + value.getClass().getName() + 
            " to required type " + requiredType.getName());
    }

    /**
     * 静态工厂方法 - 创建空参数源
     */
    public static MapParameterSource empty() {
        return new MapParameterSource();
    }

    /**
     * 静态工厂方法 - 基于Map创建
     */
    public static MapParameterSource of(Map<String, ?> values) {
        return new MapParameterSource(values);
    }

    /**
     * 静态工厂方法 - 基于单个参数创建
     */
    public static MapParameterSource of(String paramName, Object value) {
        return new MapParameterSource(paramName, value);
    }

    /**
     * 链式添加参数
     */
    public MapParameterSource addParameter(String paramName, Object value) {
        addValue(paramName, value);
        return this;
    }

    /**
     * 链式添加参数Map
     */
    public MapParameterSource addParameters(Map<String, ?> values) {
        addValues(values);
        return this;
    }
} 