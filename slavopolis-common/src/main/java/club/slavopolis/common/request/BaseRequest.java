package club.slavopolis.common.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 基础请求对象, 所有请求参数的基类
 */
@Data
public class BaseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求追踪ID
     */
    @JsonIgnore
    private String traceId;

    /**
     * 请求ID
     */
    @JsonIgnore
    private String requestId;

    /**
     * 请求时间
     */
    @JsonIgnore
    private LocalDateTime requestTime;

    /**
     * 客户端IP
     */
    @JsonIgnore
    private String clientIp;

    /**
     * 用户代理
     */
    @JsonIgnore
    private String userAgent;

    /**
     * 当前用户ID
     */
    @JsonIgnore
    private Long userId;

    /**
     * 当前租户ID
     */
    @JsonIgnore
    private Long tenantId;

    /**
     * 请求来源
     */
    @JsonIgnore
    private String source;

    /**
     * 请求版本
     */
    @JsonIgnore
    private String version;

    /**
     * 初始化请求基础信息
     */
    public void initBaseInfo() {
        if (this.requestTime == null) {
            this.requestTime = LocalDateTime.now();
        }
        if (this.requestId == null) {
            this.requestId = generateRequestId();
        }
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return String.valueOf(System.nanoTime());
    }
}
