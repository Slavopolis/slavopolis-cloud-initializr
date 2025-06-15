package club.slavopolis.common.domain.specification;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.function.Function;

/**
 * 组合规格模式基类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public abstract class CompositeSpecification<T> implements Specification<T> {

    /**
     * 与规格实现
     */
    @RequiredArgsConstructor
    static class AndSpecification<T> implements Specification<T> {
        private final Specification<T> left;
        private final Specification<T> right;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
        }

        @Override
        public String toString() {
            return String.format("(%s AND %s)", left, right);
        }
    }

    /**
     * 或规格实现
     */
    @RequiredArgsConstructor
    static class OrSpecification<T> implements Specification<T> {
        private final Specification<T> left;
        private final Specification<T> right;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
        }

        @Override
        public String toString() {
            return String.format("(%s OR %s)", left, right);
        }
    }

    /**
     * 非规格实现
     */
    @RequiredArgsConstructor
    static class NotSpecification<T> implements Specification<T> {
        private final Specification<T> specification;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            return !specification.isSatisfiedBy(candidate);
        }

        @Override
        public String toString() {
            return String.format("(NOT %s)", specification);
        }
    }

    /**
     * 空规格实现
     */
    static class TrueSpecification<T> implements Specification<T> {
        @Override
        public boolean isSatisfiedBy(T candidate) {
            return true;
        }

        @Override
        public String toString() {
            return "TRUE";
        }
    }

    /**
     * 假规格实现
     */
    static class FalseSpecification<T> implements Specification<T> {
        @Override
        public boolean isSatisfiedBy(T candidate) {
            return false;
        }

        @Override
        public String toString() {
            return "FALSE";
        }
    }

    /**
     * 非空规格实现
     */
    static class NotNullSpecification<T> implements Specification<T> {
        @Override
        public boolean isSatisfiedBy(T candidate) {
            return candidate != null;
        }

        @Override
        public String toString() {
            return "NOT_NULL";
        }
    }

    /**
     * 空规格实现
     */
    static class NullSpecification<T> implements Specification<T> {
        @Override
        public boolean isSatisfiedBy(T candidate) {
            return candidate == null;
        }

        @Override
        public String toString() {
            return "NULL";
        }
    }

    /**
     * 相等规格实现
     */
    @RequiredArgsConstructor
    static class EqualSpecification<T> implements Specification<T> {
        private final T expectedValue;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            if (expectedValue == null) {
                return candidate == null;
            }
            return expectedValue.equals(candidate);
        }

        @Override
        public String toString() {
            return String.format("EQUAL(%s)", expectedValue);
        }
    }

    /**
     * 不相等规格实现
     */
    @RequiredArgsConstructor
    static class NotEqualSpecification<T> implements Specification<T> {
        private final T expectedValue;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            if (expectedValue == null) {
                return candidate != null;
            }
            return !expectedValue.equals(candidate);
        }

        @Override
        public String toString() {
            return String.format("NOT_EQUAL(%s)", expectedValue);
        }
    }

    /**
     * 包含规格实现（用于集合）
     */
    @RequiredArgsConstructor
    static class ContainsSpecification<T, E> implements Specification<T> {
        private final E element;
        private final java.util.function.Function<T, java.util.Collection<E>> collectionExtractor;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            if (candidate == null) {
                return false;
            }
            java.util.Collection<E> collection = collectionExtractor.apply(candidate);
            return collection != null && collection.contains(element);
        }

        @Override
        public String toString() {
            return String.format("CONTAINS(%s)", element);
        }
    }

    /**
     * 范围规格实现（用于可比较对象）
     */
    @RequiredArgsConstructor
    static class RangeSpecification<T extends Comparable<T>> implements Specification<T> {
        private final T min;
        private final T max;
        private final boolean includeMin;
        private final boolean includeMax;

        @Override
        public boolean isSatisfiedBy(T candidate) {
            if (candidate == null) {
                return false;
            }

            boolean minSatisfied = min == null || 
                (includeMin ? candidate.compareTo(min) >= 0 : candidate.compareTo(min) > 0);
            
            boolean maxSatisfied = max == null || 
                (includeMax ? candidate.compareTo(max) <= 0 : candidate.compareTo(max) < 0);

            return minSatisfied && maxSatisfied;
        }

        @Override
        public String toString() {
            String minBracket = includeMin ? "[" : "(";
            String maxBracket = includeMax ? "]" : ")";
            return String.format("RANGE%s%s,%s%s", minBracket, min, max, maxBracket);
        }
    }

    // ==================== 便捷工厂方法 ====================

    /**
     * 创建相等规格
     * 
     * @param expectedValue 期望值
     * @param <T> 类型参数
     * @return 相等规格
     */
    public static <T> Specification<T> equal(T expectedValue) {
        return new EqualSpecification<>(expectedValue);
    }

    /**
     * 创建不相等规格
     * 
     * @param expectedValue 期望值
     * @param <T> 类型参数
     * @return 不相等规格
     */
    public static <T> Specification<T> notEqual(T expectedValue) {
        return new NotEqualSpecification<>(expectedValue);
    }

    /**
     * 创建包含规格
     * 
     * @param element 元素
     * @param collectionExtractor 集合提取器
     * @param <T> 对象类型
     * @param <E> 元素类型
     * @return 包含规格
     */
    public static <T, E> Specification<T> contains(E element, Function<T, Collection<E>> collectionExtractor) {
        return new ContainsSpecification<>(element, collectionExtractor);
    }

    /**
     * 创建范围规格（包含边界）
     * 
     * @param min 最小值
     * @param max 最大值
     * @param <T> 类型参数
     * @return 范围规格
     */
    public static <T extends Comparable<T>> Specification<T> between(T min, T max) {
        return new RangeSpecification<>(min, max, true, true);
    }

    /**
     * 创建范围规格（不包含边界）
     * 
     * @param min 最小值
     * @param max 最大值
     * @param <T> 类型参数
     * @return 范围规格
     */
    public static <T extends Comparable<T>> Specification<T> betweenExclusive(T min, T max) {
        return new RangeSpecification<>(min, max, false, false);
    }

    /**
     * 创建大于规格
     * 
     * @param value 比较值
     * @param <T> 类型参数
     * @return 大于规格
     */
    public static <T extends Comparable<T>> Specification<T> greaterThan(T value) {
        return new RangeSpecification<>(value, null, false, false);
    }

    /**
     * 创建大于等于规格
     * 
     * @param value 比较值
     * @param <T> 类型参数
     * @return 大于等于规格
     */
    public static <T extends Comparable<T>> Specification<T> greaterThanOrEqual(T value) {
        return new RangeSpecification<>(value, null, true, false);
    }

    /**
     * 创建小于规格
     * 
     * @param value 比较值
     * @param <T> 类型参数
     * @return 小于规格
     */
    public static <T extends Comparable<T>> Specification<T> lessThan(T value) {
        return new RangeSpecification<>(null, value, false, false);
    }

    /**
     * 创建小于等于规格
     * 
     * @param value 比较值
     * @param <T> 类型参数
     * @return 小于等于规格
     */
    public static <T extends Comparable<T>> Specification<T> lessThanOrEqual(T value) {
        return new RangeSpecification<>(null, value, false, true);
    }
} 