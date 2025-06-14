package club.slavopolis.jdbc.exception;

import club.slavopolis.jdbc.enums.ExceptionCategory;

/**
 * 安全异常 - 安全相关的异常，包括SQL注入、权限校验异常等
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class SecurityException extends JdbcException {

    public SecurityException(String message) {
        super(message, "SECURITY_ERROR", ExceptionCategory.SECURITY);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, "SECURITY_ERROR", ExceptionCategory.SECURITY, cause);
    }

    public SecurityException(String message, String errorCode) {
        super(message, errorCode, ExceptionCategory.SECURITY);
    }

    public SecurityException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, ExceptionCategory.SECURITY, cause);
    }

    /**
     * 创建SQL注入异常
     */
    public static SecurityException sqlInjectionDetected(String sql) {
        return new SecurityException(
            "Potential SQL injection detected in query: " + sql,
            "SQL_INJECTION_DETECTED"
        );
    }

    /**
     * 创建危险SQL操作异常
     */
    public static SecurityException dangerousSqlOperation(String sql) {
        return new SecurityException(
            "Dangerous SQL operation detected: " + sql,
            "DANGEROUS_SQL_OPERATION"
        );
    }

    /**
     * 创建参数验证失败异常
     */
    public static SecurityException parameterValidationFailed(String parameterName, String reason) {
        return new SecurityException(
            String.format("Parameter validation failed for '%s': %s", parameterName, reason),
            "PARAMETER_VALIDATION_FAILED"
        );
    }

    /**
     * 创建权限不足异常
     */
    public static SecurityException insufficientPermissions(String operation) {
        return new SecurityException(
            "Insufficient permissions for operation: " + operation,
            "INSUFFICIENT_PERMISSIONS"
        );
    }

    /**
     * 创建敏感数据访问异常
     */
    public static SecurityException sensitiveDataAccess(String tableName) {
        return new SecurityException(
            "Unauthorized access to sensitive data in table: " + tableName,
            "SENSITIVE_DATA_ACCESS"
        );
    }
} 