package club.slavopolis.jdbc.exception;

import club.slavopolis.jdbc.enums.ExceptionCategory;

/**
 * 数据访问异常 - 包括连接异常、SQL执行异常等
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class DataAccessException extends JdbcException {

    public DataAccessException(String message) {
        super(message, "DATA_ACCESS_ERROR", ExceptionCategory.DATA_ACCESS);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, "DATA_ACCESS_ERROR", ExceptionCategory.DATA_ACCESS, cause);
    }

    public DataAccessException(String message, String errorCode) {
        super(message, errorCode, ExceptionCategory.DATA_ACCESS);
    }

    public DataAccessException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, ExceptionCategory.DATA_ACCESS, cause);
    }

    /**
     * 创建SQL执行异常
     */
    public static DataAccessException sqlExecutionError(String sql, Throwable cause) {
        return new DataAccessException(
            "SQL execution failed: " + sql, 
            "SQL_EXECUTION_ERROR", 
            cause
        );
    }

    /**
     * 创建数据库连接异常
     */
    public static DataAccessException connectionError(Throwable cause) {
        return new DataAccessException(
            "Database connection failed", 
            "CONNECTION_ERROR", 
            cause
        );
    }

    /**
     * 创建数据类型转换异常
     */
    public static DataAccessException typeConversionError(String fromType, String toType, Throwable cause) {
        return new DataAccessException(
            String.format("Failed to convert from %s to %s", fromType, toType),
            "TYPE_CONVERSION_ERROR",
            cause
        );
    }
} 