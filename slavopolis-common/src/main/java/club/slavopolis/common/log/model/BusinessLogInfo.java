package club.slavopolis.common.log.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 业务日志信息模型
 *
 * <p>
 * 该模型用于封装业务操作日志的详细信息，包括操作人、操作时间、
 * 操作类型、操作结果等。所有业务日志都会以结构化的形式存储，
 * 便于后续的日志分析、审计和问题追踪。
 * </p>
 */
@Data
public class BusinessLogInfo {

    /**
     * 日志ID
     */
    private String logId;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 业务模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作类型描述
     */
    private String typeDesc;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String args;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误详情
     */
    private String errorDetail;

    /**
     * 执行时长（毫秒）
     */
    private Long duration;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 备注
     */
    private String remark;
}
