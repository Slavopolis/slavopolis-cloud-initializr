package club.slavopolis.common.lock.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/4
 * @description: 锁释放异常
 *
 * <p>
 * 当释放分布式锁时发生错误时抛出此异常。
 * </p>
 */
@Getter
public class LockReleaseException extends LockException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 锁键
     */
    private final String lockKey;

    public LockReleaseException(String lockKey, String message) {
        super(message);
        this.lockKey = lockKey;
    }

    public LockReleaseException(String lockKey, String message, Throwable cause) {
        super(message, cause);
        this.lockKey = lockKey;
    }
}
