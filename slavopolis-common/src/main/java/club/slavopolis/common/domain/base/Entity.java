package club.slavopolis.common.domain.base;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * 实体基类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@Getter
@Setter
public abstract class Entity<ID> {

    /**
     * 实体ID
     */
    protected ID id;

    /**
     * 创建时间
     */
    protected LocalDateTime createdAt;

    /**
     * 更新时间
     */
    protected LocalDateTime updatedAt;

    /**
     * 创建人
     */
    protected String createdBy;

    /**
     * 更新人
     */
    protected String updatedBy;

    /**
     * 版本号（乐观锁）
     */
    protected Integer version;

    /**
     * 构造函数
     */
    protected Entity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0;
    }

    /**
     * 构造函数
     * 
     * @param id 实体ID
     */
    protected Entity(ID id) {
        this();
        this.id = id;
    }

    /**
     * 更新实体
     * 
     * @param updatedBy 更新人
     */
    public void update(String updatedBy) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
        this.version++;
    }

    /**
     * 判断是否为新实体
     * 
     * @return 是否为新实体
     */
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * 判断是否为持久化实体
     * 
     * @return 是否为持久化实体
     */
    public boolean isPersistent() {
        return this.id != null;
    }

    /**
     * 实体相等性比较
     * 基于ID进行比较，如果ID为null则使用对象引用比较
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Entity<?> other = (Entity<?>) obj;
        
        // 如果ID都为null，则认为不相等（除非是同一个对象引用）
        if (this.id == null || other.id == null) {
            return false;
        }
        
        return Objects.equals(this.id, other.id);
    }

    /**
     * 哈希码计算
     * 基于ID计算哈希码
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s{id=%s}", getClass().getSimpleName(), id);
    }
} 