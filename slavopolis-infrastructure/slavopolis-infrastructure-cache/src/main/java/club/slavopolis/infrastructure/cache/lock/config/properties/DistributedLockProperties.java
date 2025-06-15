package club.slavopolis.infrastructure.cache.lock.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁配置属性
 *
 * <p>
 * 配置分布式锁的各项参数，支持通过 application.yml 进行配置。
 * </p>
 */
@Data
@ConfigurationProperties(prefix = "distributed.lock")
public class DistributedLockProperties {

    /**
     * 是否启用分布式锁
     */
    private boolean enabled = true;

    /**
     * 默认锁前缀
     */
    private String defaultPrefix = "lock";

    /**
     * 默认等待时间（秒）
     */
    private long defaultWaitTime = 3L;

    /**
     * 默认持有时间（秒）
     */
    private long defaultLeaseTime = 30L;

    /**
     * 是否启用锁降级
     */
    private boolean enableFallback = false;

    /**
     * 工作线程数
     */
    private int threads = 16;

    /**
     * Netty线程数
     */
    private int nettyThreads = 32;

    /**
     * Redis配置
     */
    private RedisProperties redis = new RedisProperties();

    /**
     * Redis配置属性
     */
    @Data
    public static class RedisProperties {
        /**
         * Redis模式
         */
        private RedisMode mode = RedisMode.SINGLE;

        /**
         * 单机地址
         */
        private String address = "redis://127.0.0.1:6379";

        /**
         * 集群节点地址
         */
        private List<String> nodeAddresses;

        /**
         * 哨兵地址
         */
        private List<String> sentinelAddresses;

        /**
         * 主节点名称（哨兵模式）
         */
        private String masterName = "master";

        /**
         * 密码
         */
        private String password;

        /**
         * 数据库索引
         */
        private int database = 0;

        /**
         * 连接池大小
         */
        private int connectionPoolSize = 64;

        /**
         * 最小空闲连接数
         */
        private int connectionMinimumIdleSize = 24;

        /**
         * 连接超时时间（毫秒）
         */
        private int connectTimeout = 10000;

        /**
         * 响应超时时间（毫秒）
         */
        private int timeout = 3000;

        /**
         * 重试次数
         */
        private int retryAttempts = 3;

        /**
         * 重试间隔（毫秒）
         */
        private int retryInterval = 1500;
    }

    /**
     * Redis模式枚举
     */
    public enum RedisMode {
        /**
         * 单机模式
         */
        SINGLE,

        /**
         * 集群模式
         */
        CLUSTER,

        /**
         * 哨兵模式
         */
        SENTINEL
    }
}
