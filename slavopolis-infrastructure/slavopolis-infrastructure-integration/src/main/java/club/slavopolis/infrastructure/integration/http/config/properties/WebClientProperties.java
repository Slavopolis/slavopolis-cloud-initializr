package club.slavopolis.infrastructure.integration.http.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * WebClient配置属性类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@Data
@ConfigurationProperties(prefix = "slavopolis.infrastructure.integration.http")
public class WebClientProperties {

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 10000;

    /**
     * 响应超时时间（毫秒）
     */
    private int responseTimeout = 30000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * 写入超时时间（毫秒）
     */
    private int writeTimeout = 30000;

    /**
     * 最大连接数
     */
    private int maxConnections = 500;

    /**
     * 最大空闲时间（秒）
     */
    private int maxIdleTime = 60;

    /**
     * 最大生命周期（秒）
     */
    private int maxLifeTime = 300;

    /**
     * 等待获取连接超时时间（秒）
     */
    private int pendingAcquireTimeout = 60;

    /**
     * 后台清理时间间隔（秒）
     */
    private int evictInBackground = 120;

    /**
     * 是否启用压缩
     */
    private boolean compressionEnabled = true;

    /**
     * 是否跟随重定向
     */
    private boolean followRedirect = true;

    /**
     * 最大内存缓冲区大小（字节）: 256KB
     */
    private int maxInMemorySize = 262144;

    /**
     * 用户代理
     */
    private String userAgent = "Slavopolis-HTTP-Client/1.0";

    /**
     * 重试次数
     */
    private int maxRetries = 3;

    /**
     * 重试间隔（毫秒）
     */
    private long retryInterval = 1000;

    /**
     * 重试间隔递增因子
     */
    private double retryBackoffMultiplier = 2.0;

    /**
     * 最大重试间隔（毫秒）
     */
    private long maxRetryInterval = 10000;

    /**
     * 是否启用请求日志
     */
    private boolean requestLoggingEnabled = true;

    /**
     * 是否启用响应日志
     */
    private boolean responseLoggingEnabled = true;

    /**
     * 是否记录请求体
     */
    private boolean logRequestBody = false;

    /**
     * 是否记录响应体
     */
    private boolean logResponseBody = false;

    /**
     * 最大日志体大小（字节）
     */
    private int maxLogBodySize = 2048;
} 