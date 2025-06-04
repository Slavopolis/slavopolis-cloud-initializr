package club.slavopolis.common.lock.event;

import club.slavopolis.common.lock.core.LockInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁事件
 *
 * <p>
 * 用于发布分布式锁相关的事件，便于监控和审计。
 * 支持的事件类型包括：锁获取、锁释放、锁失败、锁续期等。
 * </p>
 */
@Getter
public class LockEvent extends ApplicationEvent {

    /**
     * 锁信息
     */
    private final LockInfo lockInfo;

    /**
     * 事件类型
     */
    private final EventType eventType;

    /**
     * 事件时间戳
     */
    private final long timestamp;

    public LockEvent(Object source, LockInfo lockInfo, EventType eventType) {
        super(source);
        this.lockInfo = lockInfo;
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 事件类型枚举
     */
    public enum EventType {
        /**
         * 锁获取成功
         */
        ACQUIRED,

        /**
         * 锁释放
         */
        RELEASED,

        /**
         * 锁获取失败
         */
        FAILED,

        /**
         * 锁续期
         */
        RENEWED,

        /**
         * 锁过期
         */
        EXPIRED,

        /**
         * 降级到本地锁
         */
        FALLBACK
    }
}
