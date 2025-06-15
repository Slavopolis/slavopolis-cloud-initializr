package club.slavopolis.infrastructure.cache.lock.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/4
 * @description: 锁获取异常
 *
 * <p>
 * 当无法在指定时间内获取到分布式锁时抛出此异常。
 * </p>
 */
@Getter
public class LockAcquisitionException extends LockException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 锁键
     */
    private final String lockKey;

    /**
     * 等待时间（毫秒）
     */
    private final Long waitTime;

    public LockAcquisitionException(String lockKey, Long waitTime) {
        super(String.format("获取锁失败 - 键: %s 等待耗时 %d ms", lockKey, waitTime));
        this.lockKey = lockKey;
        this.waitTime = waitTime;
    }

    public LockAcquisitionException(String lockKey, Long waitTime, String message) {
        super(message);
        this.lockKey = lockKey;
        this.waitTime = waitTime;
    }
}
