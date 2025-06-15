package club.slavopolis.common.core.exception;

import club.slavopolis.common.core.result.ResultCode;
import lombok.Getter;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 第三方服务异常, 用于处理调用第三方服务时的异常
 */
@Getter
public class ThirdPartyException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 第三方服务名称
     */
    private final String serviceName;

    /**
     * 第三方错误码
     */
    private final String thirdPartyCode;

    /**
     * 第三方错误信息
     */
    private final String thirdPartyMessage;

    public ThirdPartyException(String serviceName, String message) {
        super(ResultCode.THIRD_PARTY_ERROR.getCode(), message);
        this.serviceName = serviceName;
        this.thirdPartyCode = null;
        this.thirdPartyMessage = null;
    }

    public ThirdPartyException(String serviceName, String message, Throwable cause) {
        super(ResultCode.THIRD_PARTY_ERROR.getCode(), message, cause);
        this.serviceName = serviceName;
        this.thirdPartyCode = null;
        this.thirdPartyMessage = null;
    }

    public ThirdPartyException(String serviceName, String thirdPartyCode, String thirdPartyMessage) {
        super(ResultCode.THIRD_PARTY_ERROR.getCode(), String.format("第三方服务[%s]异常: %s", serviceName, thirdPartyMessage));
        this.serviceName = serviceName;
        this.thirdPartyCode = thirdPartyCode;
        this.thirdPartyMessage = thirdPartyMessage;
    }

    /**
     * 创建第三方异常的静态方法
     */
    public static ThirdPartyException of(String serviceName, String message) {
        return new ThirdPartyException(serviceName, message);
    }

    public static ThirdPartyException of(String serviceName, String message, Throwable cause) {
        return new ThirdPartyException(serviceName, message, cause);
    }

    public static ThirdPartyException timeout(String serviceName) {
        return new ThirdPartyException(serviceName, "服务调用超时");
    }

    public static ThirdPartyException unavailable(String serviceName) {
        return new ThirdPartyException(serviceName, "服务不可用");
    }
}
