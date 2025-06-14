package club.slavopolis.email.exception;

import club.slavopolis.email.enums.EmailErrorCode;

import java.io.Serial;

/**
 * 邮件发送异常类 - 处理邮件发送过程中的异常
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.exception
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class EmailSendException extends EmailException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailSendException(String message) {
        super(EmailErrorCode.SEND_FAILED.getCode(), message);
    }

    public EmailSendException(String message, Throwable cause) {
        super(EmailErrorCode.SEND_FAILED.getCode(), message, cause);
    }

    public EmailSendException(EmailErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public EmailSendException(EmailErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getCode(), message, cause);
    }
} 