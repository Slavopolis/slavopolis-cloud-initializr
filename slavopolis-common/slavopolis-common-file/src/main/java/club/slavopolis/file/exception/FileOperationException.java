package club.slavopolis.file.exception;

import lombok.Getter;

/**
 * 文件操作异常
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
public class FileOperationException extends RuntimeException {

    /**
     * 错误代码
     */
    private final String errorCode;

    /**
     * 文件ID
     */
    private final String fileId;

    public FileOperationException(String message) {
        super(message);
        this.errorCode = "FILE_OPERATION_ERROR";
        this.fileId = null;
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_OPERATION_ERROR";
        this.fileId = null;
    }

    public FileOperationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fileId = null;
    }

    public FileOperationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.fileId = null;
    }

    public FileOperationException(String errorCode, String message, String fileId) {
        super(message);
        this.errorCode = errorCode;
        this.fileId = fileId;
    }

    public FileOperationException(String errorCode, String message, String fileId, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.fileId = fileId;
    }

}