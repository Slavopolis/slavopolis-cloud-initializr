package club.slavopolis.infrastructure.persistence.jdbc.transaction;

/**
 * 事务状态接口
 * <p>封装事务的状态信息，提供事务控制功能</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface TransactionStatus {

    /**
     * 判断是否为新事务
     *
     * @return 如果是新事务返回true
     */
    boolean isNewTransaction();

    /**
     * 判断事务是否有保存点
     *
     * @return 如果有保存点返回true
     */
    boolean hasSavepoint();

    /**
     * 设置回滚标记
     */
    void setRollbackOnly();

    /**
     * 判断是否标记为回滚
     *
     * @return 如果标记为回滚返回true
     */
    boolean isRollbackOnly();

    /**
     * 判断事务是否已完成
     *
     * @return 如果事务已完成返回true
     */
    boolean isCompleted();

    /**
     * 创建保存点
     *
     * @return 保存点对象
     * @throws RuntimeException 如果不支持保存点
     */
    Object createSavepoint() throws RuntimeException;

    /**
     * 回滚到保存点
     *
     * @param savepoint 保存点
     * @throws RuntimeException 如果回滚失败
     */
    void rollbackToSavepoint(Object savepoint) throws RuntimeException;

    /**
     * 释放保存点
     *
     * @param savepoint 保存点
     * @throws RuntimeException 如果释放失败
     */
    void releaseSavepoint(Object savepoint) throws RuntimeException;

    /**
     * 获取事务名称
     *
     * @return 事务名称
     */
    String getTransactionName();

    /**
     * 获取事务开始时间
     *
     * @return 事务开始时间戳
     */
    long getStartTime();

    /**
     * 获取事务超时时间（秒）
     *
     * @return 超时时间，-1表示无超时限制
     */
    int getTimeout();

    /**
     * 判断事务是否只读
     *
     * @return 如果是只读事务返回true
     */
    boolean isReadOnly();

    /**
     * 获取事务隔离级别
     *
     * @return 隔离级别常量
     */
    int getIsolationLevel();
} 