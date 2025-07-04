package club.slavopolis.persistence.jdbc.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * JDBC配置属性
 * <p>用于Spring Boot自动配置的属性绑定</p>
 * 
 * @author Slavopolis Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "slavopolis.jdbc")
public class JdbcProperties {

    /**
     * 是否启用增强JDBC功能
     */
    private boolean enabled = true;

    /**
     * 默认启用日志
     */
    private boolean defaultLoggingEnabled = true;

    /**
     * 默认分页大小
     */
    private int defaultPageSize = 20;

    /**
     * 最大分页大小
     */
    private int maxPageSize = 1000;

    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();

    /**
     * 安全配置
     */
    private Security security = new Security();

    /**
     * 事务配置
     */
    private Transaction transaction = new Transaction();

    /**
     * 映射配置
     */
    private Mapping mapping = new Mapping();

    /**
     * 监控配置
     */
    @Data
    public static class Monitor {
        
        /**
         * 是否启用SQL执行监控
         */
        private boolean enabled = true;

        /**
         * 慢查询阈值（毫秒）
         */
        private long slowQueryThreshold = 1000L;

        /**
         * 是否启用统计信息收集
         */
        private boolean statisticsEnabled = true;

        /**
         * 统计信息清理间隔
         */
        private Duration statisticsCleanupInterval = Duration.ofHours(1);

        /**
         * 最大统计条目数
         */
        private int maxStatisticsEntries = 10000;
    }

    /**
     * 安全配置
     */
    @Data
    public static class Security {
        
        /**
         * 是否启用SQL注入检测
         */
        private boolean sqlInjectionDetectionEnabled = true;

        /**
         * 是否启用参数验证
         */
        private boolean parameterValidationEnabled = true;

        /**
         * 危险关键词列表
         */
        private String[] dangerousKeywords = {
            "DROP", "DELETE", "TRUNCATE", "ALTER", "CREATE", "INSERT", 
            "UPDATE", "GRANT", "REVOKE", "EXEC", "EXECUTE", "UNION", 
            "SCRIPT", "JAVASCRIPT", "VBSCRIPT", "ONLOAD", "ONERROR"
        };

        /**
         * 是否启用敏感表访问控制
         */
        private boolean sensitiveTableControlEnabled = false;

        /**
         * 敏感表名列表
         */
        private String[] sensitiveTables = {"user", "password", "credential", "token", "session"};
    }

    /**
     * 事务配置
     */
    @Data
    public static class Transaction {
        
        /**
         * 默认事务传播行为
         */
        private String propagation = "REQUIRED";

        /**
         * 默认事务隔离级别
         */
        private String isolation = "DEFAULT";

        /**
         * 默认事务超时时间（秒）
         */
        private int timeout = 30;

        /**
         * 是否只读事务
         */
        private boolean readOnly = false;

        /**
         * 回滚异常类名列表
         */
        private String[] rollbackFor = {"java.lang.Exception"};
    }

    /**
     * 映射配置
     */
    @Data
    public static class Mapping {
        
        /**
         * 默认映射策略
         */
        private String defaultStrategy = "INTELLIGENT";

        /**
         * 是否启用映射缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 映射缓存最大条目数
         */
        private int maxCacheEntries = 1000;

        /**
         * 是否检查完整映射
         */
        private boolean checkFullyPopulated = false;

        /**
         * 原始类型是否默认为null值
         */
        private boolean primitivesDefaultedForNullValue = false;

        /**
         * 下划线转驼峰映射
         */
        private boolean underscoreToCamelCase = true;
    }
} 