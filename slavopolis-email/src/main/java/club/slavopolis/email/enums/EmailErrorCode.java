package club.slavopolis.email.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Slavopolis Email Error Code Enum
 *
 * @author slavopolis
 * @date 2025/1/20
 * @description 邮件服务错误码枚举 - 定义邮件模块专用的错误码
 * 
 * 错误码范围：15000-15999 (邮件模块专用)
 */
@Getter
@AllArgsConstructor
public enum EmailErrorCode {

    // ==================== 配置相关错误 15000-15099 ====================
    CONFIG_ERROR(15000, "邮件配置错误"),
    SMTP_CONFIG_ERROR(15001, "SMTP服务器配置错误"),
    TEMPLATE_CONFIG_ERROR(15002, "邮件模板配置错误"),
    SENDER_CONFIG_ERROR(15003, "发送方配置错误"),

    // ==================== 连接相关错误 15100-15199 ====================
    CONNECTION_FAILED(15100, "邮件服务器连接失败"),
    CONNECTION_TIMEOUT(15101, "邮件服务器连接超时"),
    AUTHENTICATION_FAILED(15102, "邮件服务器认证失败"),
    SSL_HANDSHAKE_FAILED(15103, "SSL握手失败"),

    // ==================== 发送相关错误 15200-15299 ====================
    SEND_FAILED(15200, "邮件发送失败"),
    SEND_TIMEOUT(15201, "邮件发送超时"),
    RECIPIENT_INVALID(15202, "收件人地址无效"),
    SENDER_INVALID(15203, "发送方地址无效"),
    SUBJECT_EMPTY(15204, "邮件主题为空"),
    CONTENT_EMPTY(15205, "邮件内容为空"),
    ATTACHMENT_ERROR(15206, "附件处理错误"),
    ATTACHMENT_SIZE_EXCEEDED(15207, "附件大小超出限制"),
    PARAMETER_ERROR(15208, "请求参数错误"),

    // ==================== 模板相关错误 15300-15399 ====================
    TEMPLATE_NOT_FOUND(15300, "邮件模板不存在"),
    TEMPLATE_PARSE_ERROR(15301, "邮件模板解析错误"),
    TEMPLATE_RENDER_ERROR(15302, "邮件模板渲染错误"),
    TEMPLATE_PARAM_MISSING(15303, "模板参数缺失"),
    TEMPLATE_ENGINE_ERROR(15304, "模板引擎错误"),

    // ==================== 限流相关错误 15400-15499 ====================
    RATE_LIMIT_EXCEEDED(15400, "发送频率超出限制"),
    RATE_LIMIT_ERROR(15401, "限流检查失败"),
    DAILY_LIMIT_EXCEEDED(15402, "日发送量超出限制"),
    HOURLY_LIMIT_EXCEEDED(15403, "小时发送量超出限制"),
    RECIPIENT_LIMIT_EXCEEDED(15404, "收件人发送量超出限制"),
    CONCURRENT_LIMIT_EXCEEDED(15405, "并发发送量超出限制"),

    // ==================== 队列相关错误 15500-15599 ====================
    QUEUE_FULL(15500, "发送队列已满"),
    QUEUE_PROCESSING_ERROR(15501, "队列处理错误"),
    ASYNC_TASK_FAILED(15502, "异步发送任务失败"),
    BATCH_PROCESSING_ERROR(15503, "批量处理错误"),

    // ==================== 监控相关错误 15600-15699 ====================
    MONITOR_ERROR(15600, "邮件监控错误"),
    METRIC_COLLECTION_ERROR(15601, "指标收集错误"),
    ALARM_NOTIFICATION_ERROR(15602, "告警通知错误"),

    // ==================== 其他错误 15900-15999 ====================
    UNKNOWN_ERROR(15900, "未知错误"),
    SYSTEM_BUSY(15901, "系统繁忙，请稍后重试"),
    SERVICE_UNAVAILABLE(15902, "邮件服务不可用");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 错误码枚举
     */
    public static EmailErrorCode fromCode(int code) {
        for (EmailErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }
} 