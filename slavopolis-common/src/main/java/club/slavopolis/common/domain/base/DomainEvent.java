package club.slavopolis.common.domain.base;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

/**
 * 领域事件基类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@Getter
public abstract class DomainEvent {

    /**
     * 事件ID
     */
    private final String eventId;

    /**
     * 事件发生时间
     */
    private final LocalDateTime occurredOn;

    /**
     * 事件版本
     */
    private final int version;

    /**
     * 构造函数
     */
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.version = 1;
    }

    /**
     * 构造函数
     * 
     * @param eventId 事件ID
     * @param occurredOn 事件发生时间
     * @param version 事件版本
     */
    protected DomainEvent(String eventId, LocalDateTime occurredOn, int version) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.version = version;
    }

    /**
     * 获取事件类型
     * 默认返回类的简单名称，子类可以重写
     * 
     * @return 事件类型
     */
    public String getEventType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取聚合根ID
     * 子类需要实现此方法，返回相关的聚合根ID
     * 
     * @return 聚合根ID
     */
    public abstract String getAggregateId();

    /**
     * 获取聚合根类型
     * 子类可以重写此方法，返回相关的聚合根类型
     * 
     * @return 聚合根类型
     */
    public String getAggregateType() {
        return "Unknown";
    }

    /**
     * 获取事件数据
     * 子类可以重写此方法，返回事件的具体数据
     * 
     * @return 事件数据
     */
    public Object getEventData() {
        return this;
    }

    /**
     * 判断事件是否过期
     * 
     * @param expirationTime 过期时间
     * @return 是否过期
     */
    public boolean isExpired(LocalDateTime expirationTime) {
        return this.occurredOn.isBefore(expirationTime);
    }

    /**
     * 判断事件是否在指定时间之后发生
     * 
     * @param time 指定时间
     * @return 是否在指定时间之后发生
     */
    public boolean occurredAfter(LocalDateTime time) {
        return this.occurredOn.isAfter(time);
    }

    /**
     * 判断事件是否在指定时间之前发生
     * 
     * @param time 指定时间
     * @return 是否在指定时间之前发生
     */
    public boolean occurredBefore(LocalDateTime time) {
        return this.occurredOn.isBefore(time);
    }

    /**
     * 相等性比较
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        DomainEvent other = (DomainEvent) obj;
        return eventId.equals(other.eventId);
    }

    /**
     * 哈希码计算
     */
    @Override
    public int hashCode() {
        return eventId.hashCode();
    }

    /**
     * 字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s{eventId='%s', occurredOn=%s, aggregateId='%s'}", 
            getEventType(), eventId, occurredOn, getAggregateId());
    }
} 