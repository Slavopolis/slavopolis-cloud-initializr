package club.slavopolis.base.properties;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import club.slavopolis.base.enums.StorageType;
import lombok.Data;

/**
 * 系统配置属性类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
@ConfigurationProperties(prefix = "biz")
public class CurrentSystemProperties {

    /**
     * 调试模式开关
     */
    private boolean debug = false;

    /**
     * 文件管理配置
     */
    private FileConfig file = new FileConfig();

    /**
     * Web层配置
     */
    private WebConfig web = new WebConfig();

    /**
     * 文件管理配置类
     */
    @Data
    public static class FileConfig {

        /**
         * 存储类型：OSS、MINIO、DATABASE、LOCAL
         */
        private StorageType storageType = StorageType.OSS;

        /**
         * 文件调试开关
         */
        private boolean debug = false;

        /**
         * 分片上传配置
         */
        private ChunkConfig chunk = new ChunkConfig();

        /**
         * 安全配置
         */
        private SecurityConfig security = new SecurityConfig();

        /**
         * 性能配置
         */
        private PerformanceConfig performance = new PerformanceConfig();

        /**
         * 存储配置
         */
        private StorageConfig storage = new StorageConfig();

        /**
         * MinIO配置
         */
        private MinioConfig minio = new MinioConfig();

        /**
         * OSS配置
         */
        private OssConfig oss = new OssConfig();
    }

    /**
     * 分片上传配置
     */
    @Data
    public static class ChunkConfig {

        /**
         * 默认分片大小（5MB）
         */
        private long defaultChunkSize = 5 * 1024 * 1024L;

        /**
         * 最大分片大小（100MB）
         */
        private long maxChunkSize = 100 * 1024 * 1024L;

        /**
         * 最小分片大小（1MB）
         */
        private long minChunkSize = 1024 * 1024L;

        /**
         * 并发上传线程数
         */
        private int concurrentUploads = 3;

        /**
         * 分片上传会话过期时间（默认24小时）
         */
        private Duration sessionExpiry = Duration.ofHours(24);

        /**
         * 最大重试次数
         */
        private int maxRetries = 3;
    }

    /**
     * 安全配置
     */
    @Data
    public static class SecurityConfig {

        /**
         * 允许的文件类型
         */
        private List<String> allowedFileTypes = List.of("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "txt");

        /**
         * 禁止的文件类型
         */
        private List<String> blockedFileTypes = List.of("exe", "bat", "cmd", "sh", "com", "scr", "pif");

        /**
         * 最大文件大小（默认100MB）
         */
        private long maxFileSize = 100 * 1024 * 1024L;

        /**
         * 最小文件大小（默认1KB）
         */
        private long minFileSize = 1024L;

        /**
         * 是否启用病毒扫描
         */
        private boolean enableVirusScan = false;

        /**
         * 是否启用文件内容检测
         */
        private boolean enableContentDetection = true;

        /**
         * 临时下载链接过期时间（默认1小时）
         */
        private Duration downloadLinkExpiry = Duration.ofHours(1);
    }

    /**
     * 性能配置
     */
    @Data
    public static class PerformanceConfig {

        /**
         * 文件元数据缓存过期时间（默认1小时）
         */
        private Duration metadataCacheExpiry = Duration.ofHours(1);

        /**
         * 上传超时时间（默认30分钟）
         */
        private Duration uploadTimeout = Duration.ofMinutes(30);

        /**
         * 下载超时时间（默认5分钟）
         */
        private Duration downloadTimeout = Duration.ofMinutes(5);

        /**
         * 连接池大小
         */
        private int connectionPoolSize = 20;

        /**
         * 是否启用异步处理
         */
        private boolean enableAsyncProcessing = true;

        /**
         * 异步处理线程池大小
         */
        private int asyncThreadPoolSize = 10;
    }

    /**
     * 存储配置
     */
    @Data
    public static class StorageConfig {

        /**
         * 根路径
         */
        private String rootPath = "/data/files";

        /**
         * 临时文件目录
         */
        private String tempPath = "/tmp/uploads";

        /**
         * 是否启用文件去重
         */
        private boolean enableDeduplication = true;

        /**
         * 是否启用文件压缩
         */
        private boolean enableCompression = false;

        /**
         * 压缩级别（0-9）
         */
        private int compressionLevel = 6;
    }

    /**
     * MinIO配置
     */
    @Data
    public static class MinioConfig {

        /**
         * 服务端点
         */
        private String endpoint;

        /**
         * 访问密钥
         */
        private String accessKey;

        /**
         * 秘密密钥
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 是否使用SSL
         */
        private boolean secure = false;

        /**
         * 区域
         */
        private String region;

        /**
         * 连接超时时间（毫秒）
         */
        private int connectTimeout = 10000;

        /**
         * 写入超时时间（毫秒）
         */
        private int writeTimeout = 60000;

        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeout = 10000;
    }

    /**
     * OSS配置
     */
    @Data
    public static class OssConfig {

        /**
         * 服务端点
         */
        private String endpoint;

        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥秘密
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 是否使用HTTPS
         */
        private boolean secure = true;

        /**
         * 连接超时时间（毫秒）
         */
        private int connectionTimeout = 50000;

        /**
         * 套接字超时时间（毫秒）
         */
        private int socketTimeout = 50000;

        /**
         * 最大连接数
         */
        private int maxConnections = 1024;

        /**
         * 最大错误重试次数
         */
        private int maxErrorRetry = 3;
    }

    /**
     * Web层配置类
     */
    @Data
    public static class WebConfig {

        /**
         * Token过滤器配置
         */
        private TokenFilterConfig tokenFilter = new TokenFilterConfig();
    }

    /**
     * Token过滤器配置
     */
    @Data
    public static class TokenFilterConfig {

        /**
         * 是否启用过滤器
         */
        private boolean enabled = true;

        /**
         * 过滤器匹配的URL模式
         */
        private List<String> urlPatterns = List.of("/api/**");

        /**
         * 过滤器执行顺序
         */
        private int order = 10;

        /**
         * Token在Redis中的key前缀
         */
        private String tokenKeyPrefix = "token:";

        /**
         * 是否启用压测模式
         */
        private boolean stressTestEnabled = false;

        /**
         * Token过期时间
         */
        private Duration tokenExpiry = Duration.ofHours(2);
    }

}
