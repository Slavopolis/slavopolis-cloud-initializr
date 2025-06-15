package club.slavopolis.infrastructure.messaging.email.config.properties;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.messaging.email.enums.SendMode;
import club.slavopolis.infrastructure.messaging.email.enums.TemplateEngine;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务配置属性类
 * <p>
 * <ul>
 *   <li>支持多SMTP服务器配置</li>
 *   <li>邮件模板配置</li>
 *   <li>发送策略配置</li>
 *   <li>监控和限流配置</li>
 * </ul>
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.config.properties
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "slavopolis.email")
public class EmailProperties {

    /**
     * 是否启用邮件服务
     */
    private boolean enabled = true;

    /**
     * 默认发送方信息
     */
    @Valid
    private SenderInfo defaultSender = new SenderInfo();

    /**
     * SMTP服务器配置
     */
    @Valid
    private SmtpConfig smtp = new SmtpConfig();

    /**
     * 邮件模板配置
     */
    @Valid
    private TemplateConfig template = new TemplateConfig();

    /**
     * 发送策略配置
     */
    @Valid
    private SendStrategy sendStrategy = new SendStrategy();

    /**
     * 监控配置
     */
    @Valid
    private MonitorConfig monitor = new MonitorConfig();

    /**
     * 限流配置
     */
    @Valid
    private RateLimit rateLimit = new RateLimit();

    /**
     * 是否使用Redis缓存存储邮件发送结果
     * true: 使用Redis缓存（推荐生产环境，支持分布式状态查询）
     * false: 使用内存缓存（默认，适合单实例）
     */
    private boolean useRedisCacheForResults = false;

    /**
     * 发送方信息配置
     */
    @Data
    public static class SenderInfo {
        /**
         * 发送方邮箱地址
         */
        @NotBlank(message = "发送方邮箱地址不能为空")
        private String from;

        /**
         * 发送方显示名称
         */
        private String fromName;

        /**
         * 回复地址
         */
        private String replyTo;
    }

    /**
     * SMTP服务器配置
     */
    @Data
    public static class SmtpConfig {
        /**
         * SMTP服务器主机
         */
        @NotBlank(message = "SMTP服务器主机不能为空")
        private String host;

        /**
         * SMTP服务器端口
         */
        @Positive(message = "SMTP服务器端口必须为正数")
        private int port = 587;

        /**
         * 用户名
         */
        @NotBlank(message = "SMTP用户名不能为空")
        private String username;

        /**
         * 密码
         */
        @NotBlank(message = "SMTP密码不能为空")
        private String password;

        /**
         * 是否启用SSL
         */
        private boolean ssl = false;

        /**
         * 是否启用STARTTLS
         */
        private boolean starttls = true;

        /**
         * 是否启用认证
         */
        private boolean auth = true;

        /**
         * 连接超时时间（毫秒）
         */
        private int connectionTimeout = 5000;

        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeout = 10000;

        /**
         * 写入超时时间（毫秒）
         */
        private int writeTimeout = 10000;

        /**
         * 额外的SMTP属性
         */
        private Map<String, String> properties;
    }

    /**
     * 邮件模板配置
     */
    @Data
    public static class TemplateConfig {
        /**
         * 模板引擎类型
         */
        private TemplateEngine engine = TemplateEngine.FREEMARKER;

        /**
         * 模板文件路径
         */
        private String templatePath = "classpath:/templates/email/";

        /**
         * 模板文件后缀
         */
        private String templateSuffix = ".ftl";

        /**
         * 模板编码
         */
        private String encoding = CommonConstants.CHARSET_UTF8;

        /**
         * 是否启用模板缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 模板缓存大小
         */
        private int cacheSize = 100;

        /**
         * 模板缓存更新延迟（秒）
         */
        private int cacheUpdateDelay = 300;

        /**
         * 预定义模板配置
         */
        private Map<String, TemplateInfo> predefined;
    }

    /**
     * 发送策略配置
     */
    @Data
    public static class SendStrategy {
        /**
         * 发送模式
         */
        private SendMode mode = SendMode.ASYNC;

        /**
         * 异步发送线程池大小
         */
        private int asyncPoolSize = 10;

        /**
         * 异步发送队列大小
         */
        private int asyncQueueSize = 1000;

        /**
         * 是否启用重试机制
         */
        private boolean retryEnabled = true;

        /**
         * 最大重试次数
         */
        private int maxRetries = 3;

        /**
         * 重试间隔（毫秒）
         */
        private long retryInterval = 1000;

        /**
         * 批量发送大小
         */
        private int batchSize = 50;

        /**
         * 批量发送间隔（毫秒）
         */
        private long batchInterval = 100;
    }

    /**
     * 监控配置
     */
    @Data
    public static class MonitorConfig {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;

        /**
         * 是否记录发送详情
         */
        private boolean recordDetails = true;

        /**
         * 发送记录保留天数
         */
        private int recordRetentionDays = 30;

        /**
         * 告警阈值配置
         */
        private AlarmThreshold alarmThreshold = new AlarmThreshold();
    }

    /**
     * 限流配置
     */
    @Data
    public static class RateLimit {
        /**
         * 是否启用限流
         */
        private boolean enabled = true;

        /**
         * 时间窗口大小
         */
        private Duration window = Duration.ofMinutes(1);

        /**
         * 窗口内最大请求数
         */
        private long maxRequests = 100;

        /**
         * 限流算法类型
         */
        private String algorithm = "sliding_window";

        /**
         * 是否按发送方限流
         */
        private boolean perSender = false;

        /**
         * 是否按收件人限流
         */
        private boolean perRecipient = false;

        /**
         * 是否按业务标签限流
         */
        private boolean perBusinessTag = false;

        /**
         * 限流检查失败时是否抛出异常
         */
        private boolean failOnLimitError = true;

        /**
         * 每秒最大发送数（兼容旧配置）
         */
        private int maxSendPerSecond = 10;

        /**
         * 每分钟最大发送数（兼容旧配置）
         */
        private int maxSendPerMinute = 100;

        /**
         * 每小时最大发送数（兼容旧配置）
         */
        private int maxSendPerHour = 1000;

        /**
         * 单个收件人每小时最大接收数（兼容旧配置）
         */
        private int maxSendPerRecipientPerHour = 10;
    }

    /**
     * 告警阈值配置
     */
    @Data
    public static class AlarmThreshold {
        /**
         * 发送失败率阈值（百分比）
         */
        private double failureRateThreshold = 10.0;

        /**
         * 发送超时率阈值（百分比）
         */
        private double timeoutRateThreshold = 5.0;

        /**
         * 队列积压阈值
         */
        private int queueBacklogThreshold = 500;
    }

    /**
     * 预定义模板信息
     */
    @Data
    public static class TemplateInfo {
        /**
         * 模板名称
         */
        private String name;

        /**
         * 模板文件路径
         */
        private String path;

        /**
         * 默认主题
         */
        private String defaultSubject;

        /**
         * 模板描述
         */
        private String description;

        /**
         * 必需参数列表
         */
        private List<String> requiredParams;
    }
} 