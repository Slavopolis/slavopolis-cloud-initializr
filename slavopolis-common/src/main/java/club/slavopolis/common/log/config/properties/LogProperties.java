package club.slavopolis.common.log.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 日志配置属性
 *
 * <p>
 * 该类定义了日志模块的配置属性，支持通过 application.yml 进行配置。
 * 包括是否启用各种日志功能、排除路径、日志级别等配置项。
 * </p>
 */
@Data
@ConfigurationProperties(prefix = "common.log")
public class LogProperties {

    /**
     * 是否启用日志模块
     */
    private boolean enabled = true;

    /**
     * 排除的路径模式
     */
    private List<String> excludePatterns = new ArrayList<>(List.of(
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico",
            "/error"
    ));

    /**
     * 访问日志配置
     */
    private AccessLogConfig access = new AccessLogConfig();

    /**
     * 性能日志配置
     */
    private PerformanceLogConfig performance = new PerformanceLogConfig();

    /**
     * 业务日志配置
     */
    private BusinessLogConfig business = new BusinessLogConfig();

    /**
     * 访问日志配置
     */
    @Data
    public static class AccessLogConfig {
        /**
         * 是否启用访问日志
         */
        private boolean enabled = true;

        /**
         * 是否记录请求体
         */
        private boolean logRequestBody = true;

        /**
         * 是否记录响应体
         */
        private boolean logResponseBody = false;

        /**
         * 最大请求体记录大小（字节）
         */
        private int maxPayloadLength = 2048;

        /**
         * 慢请求阈值（毫秒）
         */
        private long slowRequestThreshold = 3000;
    }

    /**
     * 性能日志配置
     */
    @Data
    public static class PerformanceLogConfig {
        /**
         * 是否启用性能日志
         */
        private boolean enabled = true;

        /**
         * 默认慢方法阈值（毫秒）
         */
        private long defaultSlowThreshold = 1000;

        /**
         * 是否默认记录参数
         */
        private boolean logArgs = true;

        /**
         * 是否默认记录结果
         */
        private boolean logResult = false;
    }

    /**
     * 业务日志配置
     */
    @Data
    public static class BusinessLogConfig {
        /**
         * 是否启用业务日志
         */
        private boolean enabled = true;

        /**
         * 是否异步记录
         */
        private boolean async = true;

        /**
         * 队列大小
         */
        private int queueSize = 1024;
    }
}
