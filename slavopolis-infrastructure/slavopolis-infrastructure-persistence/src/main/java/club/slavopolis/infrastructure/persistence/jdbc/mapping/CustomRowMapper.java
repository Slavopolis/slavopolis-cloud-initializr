package club.slavopolis.infrastructure.persistence.jdbc.mapping;

import club.slavopolis.infrastructure.persistence.jdbc.enums.MappingStrategy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义行映射器实现
 * <p>包装用户自定义的RowMapper，提供IntelligentRowMapper接口</p>
 *
 * @param <T>          目标类型
 * @param targetType   目标类型
 * @param customMapper 用户自定义映射器
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public record CustomRowMapper<T>(Class<T> targetType, RowMapper<T> customMapper) implements IntelligentRowMapper<T> {

    /**
     * 构造函数
     *
     * @param targetType   目标类型
     * @param customMapper 自定义映射器
     */
    public CustomRowMapper {
        Assert.notNull(targetType, "Target type must not be null");
        Assert.notNull(customMapper, "Custom mapper must not be null");
    }

    @Override
    public MappingStrategy getMappingStrategy() {
        return MappingStrategy.CUSTOM;
    }

    @Override
    public boolean isCacheable() {
        // 自定义映射器默认不缓存，因为我们不知道其内部实现
        return false;
    }

    @Override
    @Nullable
    public T mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        return customMapper.mapRow(rs, rowNum);
    }
} 