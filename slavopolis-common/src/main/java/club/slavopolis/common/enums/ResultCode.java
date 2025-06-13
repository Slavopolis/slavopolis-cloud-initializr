package club.slavopolis.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 响应状态码枚举
 *
 * 采用标准的错误码设计：
 * - 2xx: 成功
 * - 4xx: 客户端错误
 * - 5xx: 服务端错误
 * - 业务错误码: 1xxxx
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ==================== 成功状态码 ====================
    SUCCESS(200, "操作成功"),
    CREATED(201, "创建成功"),
    ACCEPTED(202, "请求已接受"),
    NO_CONTENT(204, "无内容"),

    // ==================== 客户端错误 ====================
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    NOT_ACCEPTABLE(406, "请求格式不支持"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突"),
    GONE(410, "资源已删除"),
    UNPROCESSABLE_ENTITY(422, "请求参数验证失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // ==================== 服务端错误 ====================
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    NOT_IMPLEMENTED(501, "功能未实现"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // ==================== 业务错误码 10000-19999 ====================
    // 通用业务错误 10000-10999
    BUSINESS_ERROR(10000, "业务处理失败"),
    PARAMETER_ERROR(10001, "参数错误"),
    DATA_NOT_FOUND(10002, "数据不存在"),
    DATA_ALREADY_EXISTS(10003, "数据已存在"),
    DATA_ERROR(10004, "数据异常"),
    OPERATION_FAILED(10005, "操作失败"),

    // 认证相关错误 11000-11999
    TOKEN_EXPIRED(11001, "令牌已过期"),
    TOKEN_INVALID(11002, "令牌无效"),
    ACCOUNT_LOCKED(11003, "账号已锁定"),
    ACCOUNT_DISABLED(11004, "账号已禁用"),
    PASSWORD_ERROR(11005, "密码错误"),
    CAPTCHA_ERROR(11006, "验证码错误"),
    CAPTCHA_EXPIRED(11007, "验证码已过期"),

    // 权限相关错误 12000-12999
    PERMISSION_DENIED(12001, "权限不足"),
    ROLE_NOT_FOUND(12002, "角色不存在"),
    RESOURCE_ACCESS_DENIED(12003, "资源访问被拒绝"),

    // 限流相关错误 13000-13999
    RATE_LIMIT_EXCEEDED(13001, "访问频率超限"),
    CONCURRENT_LIMIT_EXCEEDED(13002, "并发数超限"),

    // 第三方服务错误 14000-14999
    THIRD_PARTY_ERROR(14000, "第三方服务异常"),
    THIRD_PARTY_TIMEOUT(14001, "第三方服务超时"),
    THIRD_PARTY_UNAVAILABLE(14002, "第三方服务不可用");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 消息
     */
    private final String message;

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * 判断是否客户端错误
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * 判断是否服务端错误
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }

    /**
     * 判断是否业务错误
     */
    public boolean isBusinessError() {
        return code >= 10000 && code < 20000;
    }
}
