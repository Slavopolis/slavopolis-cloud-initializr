package club.slavopolis.common.lock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 锁状态枚举
 *
 * <p>
 * 定义分布式锁的各种状态，用于监控和日志记录。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum LockStatus {

    /**
     * 等待中
     */
    WAITING("waiting", "等待获取锁"),

    /**
     * 已获取
     */
    ACQUIRED("acquired", "已获取锁"),

    /**
     * 已释放
     */
    RELEASED("released", "已释放锁"),

    /**
     * 获取失败
     */
    FAILED("failed", "获取锁失败"),

    /**
     * 已过期
     */
    EXPIRED("expired", "锁已过期"),

    /**
     * 续期中
     */
    RENEWING("renewing", "正在续期"),

    /**
     * 降级
     */
    FALLBACK("fallback", "降级为本地锁");

    /**
     * 状态编码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;
}
