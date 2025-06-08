package club.slavopolis.common.log.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 异常日志信息模型
 *
 * <p>
 * 该模型用于封装系统异常的详细信息，包括异常类型、异常消息、
 * 堆栈信息等。通过结构化的异常日志，可以快速定位和分析系统问题。
 * </p>
 */
@Data
public class ExceptionLogInfo {

    /**
     * 异常ID
     */
    private String exceptionId;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 异常时间
     */
    private LocalDateTime exceptionTime;

    /**
     * 异常类型
     */
    private String exceptionType;

    /**
     * 异常消息
     */
    private String exceptionMessage;

    /**
     * 堆栈信息
     */
    private String stackTrace;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 服务器IP
     */
    private String serverIp;

    /**
     * 服务器主机名
     */
    private String serverHost;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 环境
     */
    private String environment;

    /**
     * 异常级别
     */
    private String level;

    /**
     * 是否已处理
     */
    private Boolean handled;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理备注
     */
    private String handleRemark;
}
