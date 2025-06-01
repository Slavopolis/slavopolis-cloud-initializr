package club.slavopolis.common.exception;

import club.slavopolis.common.enums.ResultCode;
import lombok.Getter;

import java.io.Serial;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 参数验证异常, 用于处理请求参数验证失败的情况
 */
@Getter
public class ValidationException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段错误信息
     */
    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(ResultCode.PARAMETER_ERROR.getCode(), message);
        this.fieldErrors = null;
    }

    public ValidationException(Map<String, String> fieldErrors) {
        super(ResultCode.PARAMETER_ERROR.getCode(), "参数验证失败", fieldErrors);
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(ResultCode.PARAMETER_ERROR.getCode(), message, fieldErrors);
        this.fieldErrors = fieldErrors;
    }

    /**
     * 创建验证异常的静态方法
     */
    public static ValidationException of(String message) {
        return new ValidationException(message);
    }

    public static ValidationException of(Map<String, String> fieldErrors) {
        return new ValidationException(fieldErrors);
    }

    public static ValidationException of(String field, String error) {
        return new ValidationException(Map.of(field, error));
    }
}
