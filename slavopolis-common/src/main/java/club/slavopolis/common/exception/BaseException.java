package club.slavopolis.common.exception;

import club.slavopolis.common.enums.ResultCode;
import lombok.Getter;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 基础异常类, 所有自定义异常的父类
 */
@Getter
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 错误详情
     */
    private final Object data;

    /**
     * 时间戳
     */
    private final long timestamp;

    public BaseException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public BaseException(ResultCode resultCode, String message) {
        this(resultCode.getCode(), message, null);
    }

    public BaseException(ResultCode resultCode, Object data) {
        this(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public BaseException(int code, String message) {
        this(code, message, null);
    }

    public BaseException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.data = null;
        this.timestamp = System.currentTimeMillis();
    }

    public BaseException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = null;
        this.timestamp = System.currentTimeMillis();
    }
}
