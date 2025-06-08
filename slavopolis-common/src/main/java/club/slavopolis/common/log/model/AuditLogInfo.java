package club.slavopolis.common.log.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 审计日志信息模型
 *
 * <p>
 * 该模型用于记录系统的审计日志，包括数据变更、权限变更、
 * 系统配置变更等敏感操作。审计日志需要长期保存，不可删除。
 * </p>
 */
@Data
public class AuditLogInfo {

    /**
     * 审计ID
     */
    private String auditId;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 操作前数据
     */
    private String beforeData;

    /**
     * 操作后数据
     */
    private String afterData;

    /**
     * 变更字段
     */
    private String changedFields;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 操作来源
     */
    private String source;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 是否敏感操作
     */
    private Boolean sensitive;
}
