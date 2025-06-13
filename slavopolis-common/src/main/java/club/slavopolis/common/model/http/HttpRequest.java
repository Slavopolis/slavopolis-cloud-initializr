package club.slavopolis.common.model.http;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/13
 * @description: HTTP请求包装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求方法
     */
    @Builder.Default
    private HttpMethod method = HttpMethod.GET;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 查询参数
     */
    private MultiValueMap<String, String> queryParams;

    /**
     * 路径参数
     */
    private Map<String, Object> pathVariables;

    /**
     * 请求体
     */
    private Object body;

    /**
     * 内容类型
     */
    @Builder.Default
    private MediaType contentType = MediaType.APPLICATION_JSON;

    /**
     * 接受类型
     */
    @Builder.Default
    private MediaType acceptType = MediaType.APPLICATION_JSON;

    /**
     * 超时时间（覆盖默认配置）
     */
    private Duration timeout;

    /**
     * 重试次数（覆盖默认配置）
     */
    private Integer maxRetries;

    /**
     * 是否跟随重定向（覆盖默认配置）
     */
    private Boolean followRedirect;

    /**
     * 请求标识（用于日志追踪）
     */
    private String requestId;

    /**
     * 追踪ID（用于分布式追踪）
     */
    private String traceId;

    /**
     * 是否记录请求日志
     */
    @Builder.Default
    private boolean logRequest = true;

    /**
     * 是否记录响应日志
     */
    @Builder.Default
    private boolean logResponse = true;

    /**
     * 自定义属性（用于扩展）
     */
    private Map<String, Object> attributes;
} 