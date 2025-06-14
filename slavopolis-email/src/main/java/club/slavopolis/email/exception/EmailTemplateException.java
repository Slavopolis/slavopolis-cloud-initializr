package club.slavopolis.email.exception;

import club.slavopolis.email.enums.EmailErrorCode;

import java.io.Serial;

/**
 * 邮件模板异常类 - 处理模板相关异常
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.exception
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class EmailTemplateException extends EmailException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailTemplateException(String message) {
        super(EmailErrorCode.TEMPLATE_NOT_FOUND.getCode(), message);
    }

    public EmailTemplateException(String message, Throwable cause) {
        super(EmailErrorCode.TEMPLATE_NOT_FOUND.getCode(), message, cause);
    }

    public EmailTemplateException(EmailErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public EmailTemplateException(EmailErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getCode(), message, cause);
    }
} 