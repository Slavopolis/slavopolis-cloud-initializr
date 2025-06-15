package club.slavopolis.common.domain.repository;

import java.util.List;
import java.util.Optional;

import club.slavopolis.common.domain.base.AggregateRoot;
import club.slavopolis.common.domain.specification.Specification;

/**
 * 基础仓储接口
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
public interface BaseRepository<T extends AggregateRoot<ID>, ID> {

    /**
     * 保存聚合根
     * 
     * @param aggregate 聚合根
     * @return 保存后的聚合根
     */
    T save(T aggregate);

    /**
     * 批量保存聚合根
     * 
     * @param aggregates 聚合根列表
     * @return 保存后的聚合根列表
     */
    List<T> saveAll(List<T> aggregates);

    /**
     * 根据ID查找聚合根
     * 
     * @param id 聚合根ID
     * @return 聚合根（可能为空）
     */
    Optional<T> findById(ID id);

    /**
     * 根据ID查找聚合根，如果不存在则抛出异常
     * 
     * @param id 聚合根ID
     * @return 聚合根
     * @throws IllegalArgumentException 当聚合根不存在时
     */
    default T getById(ID id) {
        return findById(id).orElseThrow(() -> 
            new IllegalArgumentException("聚合根不存在，ID: " + id));
    }

    /**
     * 根据ID列表查找聚合根
     * 
     * @param ids ID列表
     * @return 聚合根列表
     */
    List<T> findByIds(List<ID> ids);

    /**
     * 查找所有聚合根
     * 
     * @return 所有聚合根列表
     */
    List<T> findAll();

    /**
     * 根据规格查找聚合根
     * 
     * @param specification 规格
     * @return 符合规格的聚合根列表
     */
    List<T> findBySpecification(Specification<T> specification);

    /**
     * 根据规格查找第一个聚合根
     * 
     * @param specification 规格
     * @return 第一个符合规格的聚合根（可能为空）
     */
    Optional<T> findFirstBySpecification(Specification<T> specification);

    /**
     * 根据规格统计聚合根数量
     * 
     * @param specification 规格
     * @return 符合规格的聚合根数量
     */
    long countBySpecification(Specification<T> specification);

    /**
     * 根据规格判断是否存在聚合根
     * 
     * @param specification 规格
     * @return 是否存在符合规格的聚合根
     */
    boolean existsBySpecification(Specification<T> specification);

    /**
     * 根据ID判断聚合根是否存在
     * 
     * @param id 聚合根ID
     * @return 是否存在
     */
    boolean existsById(ID id);

    /**
     * 统计所有聚合根数量
     * 
     * @return 聚合根总数
     */
    long count();

    /**
     * 根据ID删除聚合根
     * 
     * @param id 聚合根ID
     */
    void deleteById(ID id);

    /**
     * 删除聚合根
     * 
     * @param aggregate 聚合根
     */
    void delete(T aggregate);

    /**
     * 根据ID列表删除聚合根
     * 
     * @param ids ID列表
     */
    void deleteByIds(List<ID> ids);

    /**
     * 批量删除聚合根
     * 
     * @param aggregates 聚合根列表
     */
    void deleteAll(List<T> aggregates);

    /**
     * 根据规格删除聚合根
     * 
     * @param specification 规格
     * @return 删除的聚合根数量
     */
    long deleteBySpecification(Specification<T> specification);

    /**
     * 删除所有聚合根
     */
    void deleteAll();

    /**
     * 刷新聚合根（从数据库重新加载）
     * 
     * @param aggregate 聚合根
     * @return 刷新后的聚合根
     */
    T refresh(T aggregate);

    /**
     * 分离聚合根（从持久化上下文中分离）
     * 
     * @param aggregate 聚合根
     */
    void detach(T aggregate);

    /**
     * 合并聚合根（将分离的聚合根重新附加到持久化上下文）
     * 
     * @param aggregate 聚合根
     * @return 合并后的聚合根
     */
    T merge(T aggregate);

    /**
     * 立即同步到数据库
     */
    void flush();

    /**
     * 清空持久化上下文
     */
    void clear();
} 