package club.slavopolis.email.exception;

import club.slavopolis.common.exception.BaseException;
import club.slavopolis.common.enums.ResultCode;

import java.io.Serial;

/**
 * 邮件服务基础异常类 - 所有邮件相关异常的父类
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.exception
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class EmailException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailException(ResultCode resultCode) {
        super(resultCode);
    }

    public EmailException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public EmailException(ResultCode resultCode, Object data) {
        super(resultCode, data);
    }

    public EmailException(int code, String message) {
        super(code, message);
    }

    public EmailException(int code, String message, Object data) {
        super(code, message, data);
    }

    public EmailException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public EmailException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }
} 