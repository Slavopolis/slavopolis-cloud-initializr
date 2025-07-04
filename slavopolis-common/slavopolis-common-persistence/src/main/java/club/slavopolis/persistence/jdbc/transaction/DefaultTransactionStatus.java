package club.slavopolis.persistence.jdbc.transaction;

import club.slavopolis.common.core.constants.CommonConstants;
import lombok.Getter;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

/**
 * 默认事务状态实现类
 * <p>包装Spring的TransactionStatus，提供统一的事务状态接口</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
public class DefaultTransactionStatus implements TransactionStatus {

    /**
     * Spring事务状态
     */
    private final org.springframework.transaction.TransactionStatus springTransactionStatus;

    /**
     * 事物名称
     */
    private final String transactionName;

    /**
     * 开始时间
     */
    private final long startTime;

    /**
     * 超时时间
     */
    private final int timeout;

    /**
     * 是否只读
     */
    private final boolean readOnly;

    /**
     * 隔离级别
     */
    private final int isolationLevel;

    /**
     * 构造函数
     * 
     * @param springTransactionStatus Spring事务状态
     */
    public DefaultTransactionStatus(org.springframework.transaction.TransactionStatus springTransactionStatus) {
        Assert.notNull(springTransactionStatus, "Spring TransactionStatus must not be null");
        this.springTransactionStatus = springTransactionStatus;
        this.transactionName = generateTransactionName();
        this.startTime = System.currentTimeMillis();
        // 默认无超时
        this.timeout = -1;
        // 默认非只读
        this.readOnly = false;
        this.isolationLevel = DefaultTransactionDefinition.ISOLATION_DEFAULT;
    }

    /**
     * 完整构造函数
     * 
     * @param springTransactionStatus Spring事务状态
     * @param transactionName 事务名称
     * @param timeout 超时时间
     * @param readOnly 是否只读
     * @param isolationLevel 隔离级别
     */
    public DefaultTransactionStatus(org.springframework.transaction.TransactionStatus springTransactionStatus,
                                   String transactionName,
                                   int timeout,
                                   boolean readOnly,
                                   int isolationLevel) {
        Assert.notNull(springTransactionStatus, "Spring TransactionStatus must not be null");
        this.springTransactionStatus = springTransactionStatus;
        this.transactionName = transactionName != null ? transactionName : generateTransactionName();
        this.startTime = System.currentTimeMillis();
        this.timeout = timeout;
        this.readOnly = readOnly;
        this.isolationLevel = isolationLevel;
    }

    @Override
    public boolean isNewTransaction() {
        return springTransactionStatus.isNewTransaction();
    }

    @Override
    public boolean hasSavepoint() {
        return springTransactionStatus.hasSavepoint();
    }

    @Override
    public void setRollbackOnly() {
        springTransactionStatus.setRollbackOnly();
    }

    @Override
    public boolean isRollbackOnly() {
        return springTransactionStatus.isRollbackOnly();
    }

    @Override
    public boolean isCompleted() {
        return springTransactionStatus.isCompleted();
    }

    @Override
    public Object createSavepoint() throws RuntimeException {
        return springTransactionStatus.createSavepoint();
    }

    @Override
    public void rollbackToSavepoint(Object savepoint) throws RuntimeException {
        springTransactionStatus.rollbackToSavepoint(savepoint);
    }

    @Override
    public void releaseSavepoint(Object savepoint) throws RuntimeException {
        springTransactionStatus.releaseSavepoint(savepoint);
    }

    /**
     * 生成事务名称
     */
    private String generateTransactionName() {
        return "tx-" + Thread.currentThread().threadId() + CommonConstants.HYPHEN + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "DefaultTransactionStatus{" +
                "transactionName='" + transactionName + '\'' +
                ", startTime=" + startTime +
                ", isNewTransaction=" + isNewTransaction() +
                ", isRollbackOnly=" + isRollbackOnly() +
                ", isCompleted=" + isCompleted() +
                ", timeout=" + timeout +
                ", readOnly=" + readOnly +
                ", isolationLevel=" + isolationLevel +
                '}';
    }
} 