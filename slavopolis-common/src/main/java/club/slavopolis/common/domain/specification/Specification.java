package club.slavopolis.common.domain.specification;

import java.util.Objects;

/**
 * 规格模式接口
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
public interface Specification<T> {

    /**
     * 判断候选对象是否满足规格
     * 
     * @param candidate 候选对象
     * @return 是否满足规格
     */
    boolean isSatisfiedBy(T candidate);

    /**
     * 与操作
     * 
     * @param other 其他规格
     * @return 组合规格
     */
    default Specification<T> and(Specification<T> other) {
        return new CompositeSpecification.AndSpecification<>(this, other);
    }

    /**
     * 或操作
     * 
     * @param other 其他规格
     * @return 组合规格
     */
    default Specification<T> or(Specification<T> other) {
        return new CompositeSpecification.OrSpecification<>(this, other);
    }

    /**
     * 非操作
     * 
     * @return 否定规格
     */
    default Specification<T> not() {
        return new CompositeSpecification.NotSpecification<>(this);
    }

    /**
     * 创建一个总是返回true的规格
     * 
     * @param <T> 类型参数
     * @return 总是满足的规格
     */
    static <T> Specification<T> alwaysTrue() {
        return candidate -> true;
    }

    /**
     * 创建一个总是返回false的规格
     * 
     * @param <T> 类型参数
     * @return 总是不满足的规格
     */
    static <T> Specification<T> alwaysFalse() {
        return candidate -> false;
    }

    /**
     * 创建一个空值检查规格
     * 
     * @param <T> 类型参数
     * @return 非空规格
     */
    static <T> Specification<T> notNull() {
        return Objects::nonNull;
    }

    /**
     * 创建一个空值规格
     * 
     * @param <T> 类型参数
     * @return 空值规格
     */
    static <T> Specification<T> isNull() {
        return Objects::isNull;
    }
} 