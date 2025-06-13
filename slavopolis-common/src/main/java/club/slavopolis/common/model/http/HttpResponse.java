package club.slavopolis.common.model.http;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/13
 * @description: HTTP响应包装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse<T> {

    /**
     * 响应状态码
     */
    private HttpStatus status;

    /**
     * 响应状态码值
     */
    private int statusCode;

    /**
     * 响应头
     */
    private HttpHeaders headers;

    /**
     * 响应体
     */
    private T body;

    /**
     * 原始响应体（字符串形式）
     */
    private String rawBody;

    /**
     * 请求开始时间
     */
    private Instant requestTime;

    /**
     * 响应接收时间
     */
    private Instant responseTime;

    /**
     * 请求耗时
     */
    private Duration duration;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 是否成功（2xx状态码）
     */
    public boolean isSuccessful() {
        return status != null && status.is2xxSuccessful();
    }

    /**
     * 是否客户端错误（4xx状态码）
     */
    public boolean isClientError() {
        return status != null && status.is4xxClientError();
    }

    /**
     * 是否服务端错误（5xx状态码）
     */
    public boolean isServerError() {
        return status != null && status.is5xxServerError();
    }

    /**
     * 是否重定向（3xx状态码）
     */
    public boolean isRedirection() {
        return status != null && status.is3xxRedirection();
    }

    /**
     * 获取响应耗时（毫秒）
     */
    public long getDurationMillis() {
        return duration != null ? duration.toMillis() : 0;
    }

    /**
     * 创建成功响应
     */
    public static <T> HttpResponse<T> success(T body) {
        return HttpResponse.<T>builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .body(body)
                .build();
    }

    /**
     * 创建失败响应
     */
    public static <T> HttpResponse<T> error(HttpStatus status, String message) {
        return HttpResponse.<T>builder()
                .status(status)
                .statusCode(status.value())
                .rawBody(message)
                .build();
    }
} 