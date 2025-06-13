package club.slavopolis.excel.exception;

import java.io.Serial;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.exception.BusinessException;
import club.slavopolis.excel.enums.ExcelErrorCode;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel专用异常类，继承BusinessException，用于Excel操作相关的异常处理
 */
public class ExcelException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 基于Excel错误码构造异常
     */
    public ExcelException(ExcelErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 基于Excel错误码和自定义消息构造异常
     */
    public ExcelException(ExcelErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    /**
     * 基于Excel错误码和数据构造异常
     */
    public ExcelException(ExcelErrorCode errorCode, Object data) {
        super(errorCode.getCode(), errorCode.getMessage(), data);
    }

    /**
     * 基于Excel错误码、自定义消息和数据构造异常
     */
    public ExcelException(ExcelErrorCode errorCode, String message, Object data) {
        super(errorCode.getCode(), message, data);
    }

    /**
     * 基于Excel错误码和原因异常构造异常
     */
    public ExcelException(ExcelErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode(), errorCode.getMessage(), cause);
    }

    /**
     * 基于Excel错误码、自定义消息和原因异常构造异常
     */
    public ExcelException(ExcelErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getCode(), message, cause);
    }

    /**
     * 基于错误码和消息构造异常
     */
    public ExcelException(int code, String message) {
        super(code, message);
    }

    /**
     * 基于错误码、消息和数据构造异常
     */
    public ExcelException(int code, String message, Object data) {
        super(code, message, data);
    }

    /**
     * 创建Excel异常的静态方法
     */
    public static ExcelException of(ExcelErrorCode errorCode) {
        return new ExcelException(errorCode);
    }

    /**
     * 创建Excel异常的静态方法（带自定义消息）
     */
    public static ExcelException of(ExcelErrorCode errorCode, String message) {
        return new ExcelException(errorCode, message);
    }

    /**
     * 创建Excel异常的静态方法（带数据）
     */
    public static ExcelException of(ExcelErrorCode errorCode, Object data) {
        return new ExcelException(errorCode, data);
    }

    /**
     * 创建Excel异常的静态方法（带原因异常）
     */
    public static ExcelException of(ExcelErrorCode errorCode, Throwable cause) {
        return new ExcelException(errorCode, cause);
    }

    /**
     * 创建Excel异常的静态方法（完整参数）
     */
    public static ExcelException of(ExcelErrorCode errorCode, String message, Object data, Throwable cause) {
        ExcelException exception = new ExcelException(errorCode, message, data);
        if (cause != null) {
            exception.initCause(cause);
        }
        return exception;
    }

    /**
     * 包装其他异常为Excel异常
     */
    public static ExcelException wrap(Throwable cause) {
        if (cause instanceof ExcelException excelException) {
            return excelException;
        }
        return new ExcelException(ExcelErrorCode.BATCH_PROCESS_ERROR, 
                                  "Excel操作异常: " + cause.getMessage(), cause);
    }

    /**
     * 包装其他异常为Excel异常（指定错误码）
     */
    public static ExcelException wrap(ExcelErrorCode errorCode, Throwable cause) {
        if (cause instanceof ExcelException excelException) {
            return excelException;
        }
        return new ExcelException(errorCode, errorCode.getMessage() + CommonConstants.CACHE_KEY_SEPARATOR + cause.getMessage(), cause);
    }

    /**
     * 包装其他异常为Excel异常（指定错误码和自定义消息）
     */
    public static ExcelException wrap(ExcelErrorCode errorCode, String message, Throwable cause) {
        if (cause instanceof ExcelException excelException) {
            return excelException;
        }
        return new ExcelException(errorCode, message, cause);
    }
} 