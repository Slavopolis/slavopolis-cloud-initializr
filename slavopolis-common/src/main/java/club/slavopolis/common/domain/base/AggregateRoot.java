package club.slavopolis.common.domain.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * 聚合根基类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@Getter
public abstract class AggregateRoot<ID> extends Entity<ID> {

    /**
     * 领域事件列表
     */
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 添加领域事件
     * 
     * @param event 领域事件
     */
    protected void addDomainEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * 移除领域事件
     * 
     * @param event 领域事件
     */
    protected void removeDomainEvent(DomainEvent event) {
        this.domainEvents.remove(event);
    }

    /**
     * 清空领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * 获取所有领域事件（只读）
     * 
     * @return 领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 是否有领域事件
     * 
     * @return 是否有领域事件
     */
    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }

    /**
     * 获取领域事件数量
     * 
     * @return 领域事件数量
     */
    public int getDomainEventCount() {
        return domainEvents.size();
    }
} 