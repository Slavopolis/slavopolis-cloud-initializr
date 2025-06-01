package club.slavopolis.common.exception;

import club.slavopolis.common.enums.BusinessErrorCode;
import club.slavopolis.common.enums.ResultCode;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 业务异常, 用于处理业务逻辑相关的异常
 */
public class BusinessException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(ResultCode.BUSINESS_ERROR.getCode(), message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }

    public BusinessException(BusinessErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public BusinessException(BusinessErrorCode errorCode, Object data) {
        super(errorCode.getCode(), errorCode.getMessage(), data);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(int code, String message, Object data) {
        super(code, message, data);
    }

    /**
     * 创建业务异常的静态方法
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    public static BusinessException of(BusinessErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    public static BusinessException of(BusinessErrorCode errorCode, String message) {
        return new BusinessException(errorCode, message);
    }
}
