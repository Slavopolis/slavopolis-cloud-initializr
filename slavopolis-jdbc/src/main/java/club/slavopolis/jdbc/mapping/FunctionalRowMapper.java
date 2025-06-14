package club.slavopolis.jdbc.mapping;

import club.slavopolis.jdbc.enums.MappingStrategy;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;

/**
 * 函数式行映射器实现
 * <p>使用函数式接口进行行映射，支持Lambda表达式</p>
 *
 * @param <T> 目标类型
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
public class FunctionalRowMapper<T> implements IntelligentRowMapper<T> {

    /**
     * 目标类型
     */
    private final Class<T> targetType;

    /**
     * 映射函数
     */
    private final BiFunction<ResultSet, Integer, T> mapperFunction;

    /**
     * 构造函数
     * 
     * @param targetType 目标类型
     * @param mapperFunction 映射函数
     */
    public FunctionalRowMapper(Class<T> targetType, BiFunction<ResultSet, Integer, T> mapperFunction) {
        Assert.notNull(targetType, "Target type must not be null");
        Assert.notNull(mapperFunction, "Mapper function must not be null");
        this.targetType = targetType;
        this.mapperFunction = mapperFunction;
    }

    @Override
    public MappingStrategy getMappingStrategy() {
        return MappingStrategy.CUSTOM;
    }

    @Override
    public Class<T> targetType() {
        return targetType;
    }

    @Override
    public boolean isCacheable() {
        // 函数式映射器默认不缓存
        return false;
    }

    @Override
    @Nullable
    public T mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        try {
            return mapperFunction.apply(rs, rowNum);
        } catch (Exception ex) {
            throw new SQLException("Error in functional row mapper", ex);
        }
    }
} 