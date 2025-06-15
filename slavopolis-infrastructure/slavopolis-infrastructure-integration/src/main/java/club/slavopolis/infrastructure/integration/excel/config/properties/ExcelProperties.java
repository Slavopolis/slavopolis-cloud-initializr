package club.slavopolis.infrastructure.integration.excel.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel配置属性，支持Spring Boot自动配置
 */
@Data
@ConfigurationProperties(prefix = "slavopolis.excel")
public class ExcelProperties {

    /**
     * 是否启用Excel功能
     */
    private boolean enabled = true;

    /**
     * 默认批量处理大小
     */
    private int defaultBatchSize = 1000;

    /**
     * 最大支持的行数
     */
    private int maxRows = 100000;

    /**
     * 最大支持的文件大小（字节）：10MB
     */
    private long maxFileSize = 10 * 1024 * 1024L;

    /**
     * 临时文件目录
     */
    private String tempDir = System.getProperty("java.io.tmpdir");

    /**
     * 是否启用异步处理
     */
    private boolean enableAsync = true;

    /**
     * 异步处理的文件大小阈值（字节）：10MB
     */
    private long asyncThreshold = 10 * 1024 * 1024L;

    /**
     * 是否启用错误收集模式（false为快速失败）
     */
    private boolean enableErrorCollection = true;

    /**
     * 最大错误收集数量
     */
    private int maxErrorCount = 1000;

    /**
     * 读取配置
     */
    private Read read = new Read();

    /**
     * 写入配置
     */
    private Write write = new Write();

    /**
     * 模板配置
     */
    private Template template = new Template();

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();

    /**
     * 读取相关配置
     */
    @Data
    public static class Read {
        /**
         * 默认头部行号
         */
        private int defaultHeaderRowNumber = 1;

        /**
         * 是否忽略空行
         */
        private boolean ignoreEmptyRow = true;

        /**
         * 是否使用科学计数法读取数字
         */
        private boolean useScientificFormat = false;

        /**
         * 读取超时时间（毫秒）：5分钟
         */
        private long timeoutMs = 300000L;
    }

    /**
     * 写入相关配置
     */
    @Data
    public static class Write {
        /**
         * 默认Sheet名称
         */
        private String defaultSheetName = "Sheet1";

        /**
         * 是否自动调整列宽
         */
        private boolean autoWidth = true;

        /**
         * 是否包含头部
         */
        private boolean includeHeader = true;

        /**
         * 写入超时时间（毫秒）：5分钟
         */
        private long timeoutMs = 300000L;

        /**
         * 是否启用内存优化模式
         */
        private boolean memoryOptimized = true;
    }

    /**
     * 模板相关配置
     */
    @Data
    public static class Template {
        /**
         * 模板文件基础路径
         */
        private String basePath = "classpath:templates/excel/";

        /**
         * 是否启用模板缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 模板缓存过期时间（毫秒）：1小时
         */
        private long cacheExpiration = 3600000L;

        /**
         * 最大缓存模板数量
         */
        private int maxCacheSize = 100;

        /**
         * 是否启用模板预加载
         */
        private boolean preload = false;

        /**
         * 预加载的模板名称列表
         */
        private String[] preloadTemplates = {};
    }

    /**
     * 线程池配置
     */
    @Data
    public static class ThreadPool {
        /**
         * 核心线程数
         */
        private int corePoolSize = 2;

        /**
         * 最大线程数
         */
        private int maxPoolSize = 10;

        /**
         * 队列容量
         */
        private int queueCapacity = 100;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix = "excel-";

        /**
         * 线程空闲时间（秒）
         */
        private int keepAliveSeconds = 60;

        /**
         * 是否允许核心线程超时
         */
        private boolean allowCoreThreadTimeOut = false;
    }

    /**
     * 缓存相关配置
     */
    @Data
    public static class Cache {
        /**
         * 是否启用缓存
         */
        private boolean enabled = true;

        /**
         * 缓存类型（memory, redis等）
         */
        private String type = "memory";

        /**
         * 缓存过期时间（秒）
         */
        private long expireSeconds = 3600L;

        /**
         * 最大缓存条目数
         */
        private int maxSize = 1000;

        /**
         * 缓存key前缀
         */
        private String keyPrefix = "slavopolis:excel:";
    }

    /**
     * 监控相关配置
     */
    @Data
    public static class Monitor {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;

        /**
         * 是否记录性能指标
         */
        private boolean enableMetrics = true;

        /**
         * 是否记录详细日志
         */
        private boolean enableDetailLog = false;

        /**
         * 慢操作阈值（毫秒）
         */
        private long slowOperationThreshold = 5000L;

        /**
         * 是否启用链路追踪
         */
        private boolean enableTracing = true;
    }

    /**
     * 获取异步处理阈值（MB）
     */
    public double getAsyncThresholdMb() {
        return asyncThreshold / 1024.0 / 1024.0;
    }

    /**
     * 获取最大文件大小（MB）
     */
    public double getMaxFileSizeMb() {
        return maxFileSize / 1024.0 / 1024.0;
    }

    /**
     * 设置异步处理阈值（MB）
     */
    public void setAsyncThresholdMb(double mb) {
        this.asyncThreshold = (long) (mb * 1024 * 1024);
    }

    /**
     * 设置最大文件大小（MB）
     */
    public void setMaxFileSizeMb(double mb) {
        this.maxFileSize = (long) (mb * 1024 * 1024);
    }

    /**
     * 检查文件大小是否需要异步处理
     */
    public boolean needAsyncProcess(long fileSize) {
        return enableAsync && fileSize > asyncThreshold;
    }

    /**
     * 检查文件大小是否超过限制
     */
    public boolean exceedsMaxFileSize(long fileSize) {
        return fileSize > maxFileSize;
    }
} 