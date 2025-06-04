package club.slavopolis.common.lock.exception;

import club.slavopolis.common.enums.ResultCode;
import club.slavopolis.common.exception.BaseException;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/4
 * @description: 分布式锁异常
 *
 * <p>
 * 分布式锁相关的异常基类，所有分布式锁异常都应该继承此类。
 * </p>
 */
public class LockException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public LockException(String message) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public LockException(String message, Throwable cause) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, cause);
    }

    public LockException(int code, String message) {
        super(code, message);
    }

    public LockException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
