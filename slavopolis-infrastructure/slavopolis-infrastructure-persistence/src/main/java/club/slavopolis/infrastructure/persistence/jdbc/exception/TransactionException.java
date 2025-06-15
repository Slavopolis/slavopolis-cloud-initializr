package club.slavopolis.infrastructure.persistence.jdbc.exception;

import club.slavopolis.infrastructure.persistence.jdbc.enums.ExceptionCategory;

/**
 * 事务异常 - 事务管理相关的异常，包括事务开启、提交、回滚异常等
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class TransactionException extends JdbcException {

    public TransactionException(String message) {
        super(message, "TRANSACTION_ERROR", ExceptionCategory.TRANSACTION);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, "TRANSACTION_ERROR", ExceptionCategory.TRANSACTION, cause);
    }

    public TransactionException(String message, String errorCode) {
        super(message, errorCode, ExceptionCategory.TRANSACTION);
    }

    public TransactionException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, ExceptionCategory.TRANSACTION, cause);
    }

    /**
     * 创建事务开启异常
     */
    public static TransactionException beginTransactionError(Throwable cause) {
        return new TransactionException(
            "Failed to begin transaction", 
            "TRANSACTION_BEGIN_ERROR", 
            cause
        );
    }

    /**
     * 创建事务提交异常
     */
    public static TransactionException commitTransactionError(Throwable cause) {
        return new TransactionException(
            "Failed to commit transaction", 
            "TRANSACTION_COMMIT_ERROR", 
            cause
        );
    }

    /**
     * 创建事务回滚异常
     */
    public static TransactionException rollbackTransactionError(Throwable cause) {
        return new TransactionException(
            "Failed to rollback transaction", 
            "TRANSACTION_ROLLBACK_ERROR", 
            cause
        );
    }

    /**
     * 创建事务超时异常
     */
    public static TransactionException transactionTimeoutError() {
        return new TransactionException(
            "Transaction timeout", 
            "TRANSACTION_TIMEOUT_ERROR"
        );
    }

    /**
     * 创建事务管理器未配置异常
     */
    public static TransactionException transactionManagerNotConfigured() {
        return new TransactionException(
            "Transaction manager not configured", 
            "TRANSACTION_MANAGER_NOT_CONFIGURED"
        );
    }
} 