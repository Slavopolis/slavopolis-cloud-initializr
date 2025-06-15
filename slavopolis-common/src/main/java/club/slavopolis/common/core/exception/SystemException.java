package club.slavopolis.common.core.exception;

import club.slavopolis.common.core.result.ResultCode;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 系统异常, 用于处理系统级别的异常
 */
public class SystemException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SystemException(String message) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public SystemException(String message, Throwable cause) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, cause);
    }

    public SystemException(ResultCode resultCode) {
        super(resultCode);
    }

    public SystemException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public SystemException(int code, String message) {
        super(code, message);
    }

    public SystemException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * 创建系统异常的静态方法
     */
    public static SystemException of(String message) {
        return new SystemException(message);
    }

    public static SystemException of(String message, Throwable cause) {
        return new SystemException(message, cause);
    }

    public static SystemException of(ResultCode resultCode) {
        return new SystemException(resultCode);
    }
}
