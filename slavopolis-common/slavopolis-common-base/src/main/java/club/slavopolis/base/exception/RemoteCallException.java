package club.slavopolis.base.exception;

import club.slavopolis.base.enums.ErrorCode;

/**
 * 远程服务调用异常定义
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/20
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public class RemoteCallException extends SystemException {

    public RemoteCallException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RemoteCallException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public RemoteCallException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public RemoteCallException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public RemoteCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
