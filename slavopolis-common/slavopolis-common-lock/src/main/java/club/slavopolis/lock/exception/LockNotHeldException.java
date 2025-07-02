package club.slavopolis.lock.exception;

/**
 * 锁未持有异常
 * <p>
 * 当尝试释放未被当前线程持有的锁时抛出此异常。
 * 通常在编程式API中使用，确保锁的释放操作安全性。
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public class LockNotHeldException extends DistributeLockException {

    /**
     * 构造一个新的锁未持有异常
     */
    public LockNotHeldException() {
        super();
    }

    /**
     * 构造一个新的锁未持有异常
     *
     * @param message 异常消息
     */
    public LockNotHeldException(String message) {
        super(message);
    }

    /**
     * 构造一个新的锁未持有异常
     *
     * @param message 异常消息
     * @param cause 原因异常
     */
    public LockNotHeldException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个新的锁未持有异常
     *
     * @param cause 原因异常
     */
    public LockNotHeldException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个新的锁未持有异常
     *
     * @param message 异常消息
     * @param cause 原因异常
     * @param enableSuppression 是否启用异常抑制
     * @param writableStackTrace 是否可写入堆栈跟踪
     */
    public LockNotHeldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
} 