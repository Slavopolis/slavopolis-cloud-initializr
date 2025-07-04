package club.slavopolis.persistence.jdbc.exception;

import club.slavopolis.persistence.jdbc.enums.ExceptionCategory;
import lombok.Getter;
import org.springframework.dao.DataAccessException;

/**
 * JDBC异常基类 - 扩展Spring的DataAccessException，提供统一的异常处理基础
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
public class JdbcException extends DataAccessException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 异常分类
     */
    private final ExceptionCategory category;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public JdbcException(String message) {
        super(message);
        this.errorCode = "JDBC_ERROR";
        this.category = ExceptionCategory.GENERAL;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JdbcException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "JDBC_ERROR";
        this.category = ExceptionCategory.GENERAL;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errorCode 错误码
     * @param category 异常分类
     */
    public JdbcException(String message, String errorCode, ExceptionCategory category) {
        super(message);
        this.errorCode = errorCode;
        this.category = category;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errorCode 错误码
     * @param category 异常分类
     * @param cause 原始异常
     */
    public JdbcException(String message, String errorCode, ExceptionCategory category, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.category = category;
    }

    /**
     * 是否为系统异常
     * 
     * @return 如果是系统异常返回true
     */
    public boolean isSystemException() {
        return category == ExceptionCategory.SYSTEM || 
               category == ExceptionCategory.CONFIGURATION ||
               category == ExceptionCategory.INFRASTRUCTURE;
    }

    /**
     * 是否为业务异常
     * 
     * @return 如果是业务异常返回true
     */
    public boolean isBusinessException() {
        return category == ExceptionCategory.BUSINESS ||
               category == ExceptionCategory.VALIDATION;
    }

    /**
     * 是否为安全异常
     * 
     * @return 如果是安全异常返回true
     */
    public boolean isSecurityException() {
        return category == ExceptionCategory.SECURITY;
    }

    @Override
    public String toString() {
        return String.format("JdbcException{errorCode='%s', category=%s, message='%s'}", 
                errorCode, category, getMessage());
    }
} 