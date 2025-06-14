package club.slavopolis.jdbc.mapping;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.jdbc.enums.MappingStrategy;
import org.springframework.jdbc.core.RowMapper;

import java.util.function.BiFunction;

/**
 * 智能行映射器接口
 *
 * <p>扩展Spring RowMapper，提供智能映射功能</p>
 * <p>支持下划线转驼峰、自动类型转换、缓存机制等</p>
 *
 * @param <T> 目标类型
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface IntelligentRowMapper<T> extends RowMapper<T> {

    /**
     * 获取映射策略
     * 
     * @return 映射策略
     */
    MappingStrategy getMappingStrategy();

    /**
     * 获取目标类型
     * 
     * @return 目标类型
     */
    Class<T> targetType();

    /**
     * 是否缓存映射信息
     * 
     * @return 如果缓存返回true
     */
    default boolean isCacheable() {
        return true;
    }

    /**
     * 获取缓存键
     * 
     * @return 缓存键
     */
    default String getCacheKey() {
        return targetType().getName() + CommonConstants.UNDERSCORE + getMappingStrategy().name();
    }

    /**
     * 创建简单的Bean映射器
     * 
     * @param <T> 目标类型
     * @param targetType 目标类型
     * @return 映射器实例
     */
    static <T> IntelligentRowMapper<T> of(Class<T> targetType) {
        return new BeanRowMapper<>(targetType, MappingStrategy.INTELLIGENT);
    }

    /**
     * 创建指定策略的Bean映射器
     * 
     * @param <T> 目标类型
     * @param targetType 目标类型
     * @param strategy 映射策略
     * @return 映射器实例
     */
    static <T> IntelligentRowMapper<T> of(Class<T> targetType, MappingStrategy strategy) {
        return new BeanRowMapper<>(targetType, strategy);
    }

    /**
     * 创建自定义映射器
     * 
     * @param <T> 目标类型
     * @param targetType 目标类型
     * @param mapper 自定义映射函数
     * @return 映射器实例
     */
    static <T> IntelligentRowMapper<T> custom(Class<T> targetType, RowMapper<T> mapper) {
        return new CustomRowMapper<>(targetType, mapper);
    }

    /**
     * 创建函数式映射器
     * 
     * @param <T> 目标类型
     * @param targetType 目标类型
     * @param mapper 映射函数
     * @return 映射器实例
     */
    static <T> IntelligentRowMapper<T> functional(Class<T> targetType, BiFunction<java.sql.ResultSet, Integer, T> mapper) {
        return new FunctionalRowMapper<>(targetType, mapper);
    }

    /**
     * 创建单列映射器
     * 
     * @param <T> 目标类型
     * @param targetType 目标类型
     * @param columnName 列名
     * @return 映射器实例
     */
    static <T> IntelligentRowMapper<T> singleColumn(Class<T> targetType, String columnName) {
        return new SingleColumnRowMapper<>(targetType, columnName);
    }

    /**
     * 创建单列映射器（默认使用第一列）
     * 
     * @param <T> 目标类型
     * @param targetType 目标类型
     * @return 映射器实例
     */
    static <T> IntelligentRowMapper<T> singleColumn(Class<T> targetType) {
        return new SingleColumnRowMapper<>(targetType, null);
    }
} 