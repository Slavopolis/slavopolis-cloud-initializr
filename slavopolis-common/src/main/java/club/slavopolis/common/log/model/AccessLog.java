package club.slavopolis.common.log.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 访问日志数据模型
 */
@Data
public class AccessLog {

    /**
     * TraceId
     */
    private String traceId;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 执行方法
     */
    private String method;

    /**
     * 请求URI
     */
    private String uri;

    /**
     * 请求参数
     */
    private String queryString;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 响应状态
     */
    private Integer status;

    /**
     * 执行市场
     */
    private Long duration;

    /**
     * 错误信息
     */
    private String error;
}
