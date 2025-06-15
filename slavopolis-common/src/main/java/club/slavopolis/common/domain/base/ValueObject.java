package club.slavopolis.common.domain.base;

import java.util.Arrays;
import java.util.Objects;

/**
 * 值对象基类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
public abstract class ValueObject {

    /**
     * 获取相等性比较的组件
     * 子类需要实现此方法，返回用于相等性比较的属性数组
     * 
     * @return 用于相等性比较的属性数组
     */
    protected abstract Object[] getEqualityComponents();

    /**
     * 相等性比较
     * 基于getEqualityComponents()返回的组件进行比较
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        ValueObject other = (ValueObject) obj;
        return Arrays.equals(getEqualityComponents(), other.getEqualityComponents());
    }

    /**
     * 哈希码计算
     * 基于getEqualityComponents()返回的组件计算哈希码
     */
    @Override
    public int hashCode() {
        return Objects.hash(getEqualityComponents());
    }

    /**
     * 字符串表示
     * 默认实现，子类可以重写
     */
    @Override
    public String toString() {
        return String.format("%s{%s}", 
            getClass().getSimpleName(), 
            Arrays.toString(getEqualityComponents()));
    }

    /**
     * 验证值对象的有效性
     * 子类可以重写此方法来实现自定义验证逻辑
     * 
     * @throws IllegalArgumentException 当值对象无效时抛出
     */
    protected void validate() {
        // 默认实现为空，子类可以重写
    }

    /**
     * 创建值对象的副本
     * 由于值对象是不可变的，默认返回自身
     * 如果子类需要特殊的复制逻辑，可以重写此方法
     * 
     * @return 值对象的副本
     */
    public ValueObject copy() {
        return this;
    }

    /**
     * 判断值对象是否为空
     * 子类可以重写此方法来定义"空"的含义
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        Object[] components = getEqualityComponents();
        if (components == null || components.length == 0) {
            return true;
        }
        
        for (Object component : components) {
            if (component != null) {
                if (component instanceof String && !((String) component).isEmpty()) {
                    return false;
                }
                if (!(component instanceof String)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 判断值对象是否有效
     * 子类可以重写此方法来定义"有效"的含义
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 