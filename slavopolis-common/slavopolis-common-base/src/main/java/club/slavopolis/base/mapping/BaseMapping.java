package club.slavopolis.base.mapping;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Stream;

/**
 * MapStruct 基础映射接口
 *
 * @param <S> 源对象类型（通常是 DO 或 Entity）
 * @param <T> 目标对象类型（通常是 DTO 或 VO）
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/5
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface BaseMapping<S, T> {

    /**
     * 单对象正向映射
     * <p>
     * 使用场景：将数据库实体转换为前端展示对象
     *
     * @param source 源对象，可以为 null
     * @return 映射后的目标对象，源对象为 null 时返回 null
     */
    @InheritConfiguration
    T to(S source);

    /**
     * 单对象反向映射
     * <p>
     * 使用场景：将前端提交的数据转换为数据库实体
     *
     * @param target 目标对象，可以为 null
     * @return 映射后的源对象，目标对象为 null 时返回 null
     */
    @InheritInverseConfiguration(name = "to")
    S from(T target);

    /**
     * 列表正向映射
     * <p>
     * 使用场景：批量查询结果的转换，如分页查询、列表展示
     *
     * @param sources 源对象列表，可以为 null 或包含 null 元素
     * @return 映射后的目标对象列表，自动过滤 null 元素
     */
    @InheritConfiguration(name = "to")
    List<T> toList(List<S> sources);

    /**
     * 列表反向映射
     * <p>
     * 使用场景：批量数据提交的转换，如批量导入、批量更新
     *
     * @param targets 目标对象列表，可以为 null 或包含 null 元素
     * @return 映射后的源对象列表，自动过滤 null 元素
     */
    @InheritConfiguration(name = "from")
    List<S> fromList(List<T> targets);

    /**
     * 流式正向映射
     * <p>
     * 使用场景：大数据量处理，支持延迟计算和内存优化
     *
     * @param sources 源对象流
     * @return 映射后的目标对象流
     */
    @InheritConfiguration(name = "to")
    Stream<T> toStream(Stream<S> sources);

    /**
     * 流式反向映射
     * <p>
     * 使用场景：流式数据处理管道中的对象转换
     *
     * @param targets 目标对象流
     * @return 映射后的源对象流
     */
    @InheritConfiguration(name = "from")
    Stream<S> fromStream(Stream<T> targets);

    /**
     * 更新映射 - 将源对象的属性更新到已存在的目标对象
     * <p>
     * 使用场景：部分字段更新，避免创建新对象的内存开销
     * 适用于缓存更新、实体状态同步等场景
     *
     * @param source 源对象，包含要更新的数据
     * @param target 目标对象，将被更新的对象（使用 @MappingTarget 标记）
     */
    void updateTo(S source, @MappingTarget T target);

    /**
     * 反向更新映射
     * <p>
     * 使用场景：从 DTO 更新实体对象的部分字段
     *
     * @param target 目标对象，包含要更新的数据
     * @param source 源对象，将被更新的对象
     */
    void updateFrom(T target, @MappingTarget S source);

    /**
     * 正向映射后置处理钩子
     * <p>
     * 执行时机：基础字段映射完成后，返回结果前
     * 使用场景：复杂业务逻辑处理、计算字段设置、关联数据填充
     *
     * @param source 源对象
     * @param target 已完成基础映射的目标对象
     */
    @AfterMapping
    default void afterTo(S source, @MappingTarget T target) {
        // 子类可覆盖此方法进行自定义处理
        // 示例：设置计算字段、格式化数据、填充关联信息等
    }

    /**
     * 反向映射后置处理钩子
     * <p>
     * 执行时机：基础字段映射完成后，返回结果前
     * 使用场景：数据校验、默认值设置、业务规则应用
     *
     * @param target 目标对象
     * @param source 已完成基础映射的源对象
     */
    @AfterMapping
    default void afterFrom(T target, @MappingTarget S source) {
        // 子类可覆盖此方法进行自定义处理
        // 示例：设置审计字段、应用业务规则、数据校验等
    }
}
