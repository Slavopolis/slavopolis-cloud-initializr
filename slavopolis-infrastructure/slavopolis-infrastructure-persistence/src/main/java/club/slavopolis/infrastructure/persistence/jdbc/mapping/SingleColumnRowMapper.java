package club.slavopolis.infrastructure.persistence.jdbc.mapping;

import club.slavopolis.infrastructure.persistence.jdbc.enums.MappingStrategy;
import club.slavopolis.infrastructure.persistence.jdbc.exception.MappingException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 单列行映射器实现
 * <p>将结果集中的单个列映射到目标类型</p>
 * <p>支持基本类型、包装类型、字符串等常见类型的自动转换</p>
 *
 * @param <T> 目标类型
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class SingleColumnRowMapper<T> implements IntelligentRowMapper<T> {

    /**
     * 目标类型
     */
    private final Class<T> requiredType;

    /**
     * 列名（可选）
     */
    @Nullable
    private final String columnName;

    /**
     * 构造函数（使用第一列）
     * 
     * @param requiredType 目标类型
     */
    public SingleColumnRowMapper(Class<T> requiredType) {
        this(requiredType, null);
    }

    /**
     * 构造函数（指定列名）
     * 
     * @param requiredType 目标类型
     * @param columnName 列名
     */
    public SingleColumnRowMapper(Class<T> requiredType, @Nullable String columnName) {
        Assert.notNull(requiredType, "Required type must not be null");
        this.requiredType = requiredType;
        this.columnName = columnName;
    }

    @Override
    public MappingStrategy getMappingStrategy() {
        // 单列映射使用严格策略
        return MappingStrategy.STRICT;
    }

    @Override
    public Class<T> targetType() {
        return requiredType;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public T mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Object result;
        
        if (this.columnName != null) {
            result = getColumnValue(rs, this.columnName);
        } else {
            result = getColumnValue(rs, 1);
        }

        if (result != null && this.requiredType != null && !this.requiredType.isInstance(result)) {
            // 尝试类型转换
            try {
                result = convertValueToRequiredType(result, this.requiredType);
            } catch (IllegalArgumentException ex) {
                throw new MappingException(
                    String.format("Type mismatch affecting row number %d and column type '%s': %s",
                        rowNum, result.getClass().getSimpleName(), ex.getMessage()), ex);
            }
        }

        return (T) result;
    }

    /**
     * 获取列值（通过索引）
     * 
     * @param rs 结果集
     * @param index 列索引
     * @return 列值
     * @throws SQLException SQL异常
     */
    @Nullable
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, this.requiredType);
    }

    /**
     * 获取列值（通过列名）
     * 
     * @param rs 结果集
     * @param columnName 列名
     * @return 列值
     * @throws SQLException SQL异常
     */
    @Nullable
    protected Object getColumnValue(ResultSet rs, String columnName) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), this.requiredType);
    }

    /**
     * 将值转换为所需类型
     * 
     * @param value 原始值
     * @param requiredType 目标类型
     * @return 转换后的值
     * @throws IllegalArgumentException 转换失败时抛出
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected Object convertValueToRequiredType(Object value, Class<T> requiredType) throws IllegalArgumentException {
        if (String.class == requiredType) {
            return value.toString();
        } else if (Number.class.isAssignableFrom(requiredType)) {
            if (value instanceof Number) {
                return NumberUtils.convertNumberToTargetClass(((Number) value), (Class<Number>) requiredType);
            } else {
                return NumberUtils.parseNumber(value.toString(), (Class<Number>) requiredType);
            }
        } else if (requiredType.isEnum()) {
            return convertToEnum(value, requiredType);
        } else {
            throw new IllegalArgumentException(
                String.format("Cannot convert value [%s] to required type [%s]", 
                    value, requiredType.getSimpleName()));
        }
    }

    /**
     * 转换为枚举类型
     * 
     * @param value 原始值
     * @param enumType 枚举类型
     * @return 枚举值
     * @throws IllegalArgumentException 转换失败时抛出
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object convertToEnum(Object value, Class<T> enumType) throws IllegalArgumentException {
        if (value instanceof String) {
            return Enum.valueOf((Class<Enum>) enumType, (String) value);
        } else if (value instanceof Number) {
            Object[] enumConstants = enumType.getEnumConstants();
            int ordinal = ((Number) value).intValue();
            if (ordinal >= 0 && ordinal < enumConstants.length) {
                return enumConstants[ordinal];
            } else {
                throw new IllegalArgumentException(
                    String.format("Invalid enum ordinal %d for type %s", ordinal, enumType.getSimpleName()));
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Cannot convert value [%s] to enum type [%s]", 
                    value, enumType.getSimpleName()));
        }
    }

    /**
     * 获取列名
     * 
     * @return 列名（可能为null）
     */
    @Nullable
    public String getColumnName() {
        return columnName;
    }

    /**
     * 创建新实例
     * 
     * @param <T> 目标类型
     * @param requiredType 目标类型
     * @return 映射器实例
     */
    public static <T> SingleColumnRowMapper<T> newInstance(Class<T> requiredType) {
        return new SingleColumnRowMapper<>(requiredType);
    }

    /**
     * 创建新实例
     * 
     * @param <T> 目标类型
     * @param requiredType 目标类型
     * @param columnName 列名
     * @return 映射器实例
     */
    public static <T> SingleColumnRowMapper<T> newInstance(Class<T> requiredType, String columnName) {
        return new SingleColumnRowMapper<>(requiredType, columnName);
    }
} 