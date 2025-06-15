package club.slavopolis.infrastructure.cache.lock.core;

import club.slavopolis.infrastructure.cache.lock.enums.LockStatus;
import club.slavopolis.infrastructure.cache.lock.enums.LockType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁信息
 *
 * <p>
 * 封装分布式锁的详细信息，用于监控和管理。
 * </p>
 */
@Data
@Builder
public class LockInfo {

    /**
     * 锁键
     */
    private String lockKey;

    /**
     * 锁类型
     */
    private LockType lockType;

    /**
     * 锁状态
     */
    private LockStatus lockStatus;

    /**
     * 持有者标识（线程ID或节点ID）
     */
    private String owner;

    /**
     * 获取锁的时间
     */
    private LocalDateTime acquireTime;

    /**
     * 释放锁的时间
     */
    private LocalDateTime releaseTime;

    /**
     * 持有时长（毫秒）
     */
    private Long holdDuration;

    /**
     * 等待时长（毫秒）
     */
    private Long waitDuration;

    /**
     * 重入次数
     */
    private Integer reentrantCount;

    /**
     * 业务标识
     */
    private String business;

    /**
     * 方法签名
     */
    private String methodSignature;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 请求追踪ID
     */
    private String traceId;
}
