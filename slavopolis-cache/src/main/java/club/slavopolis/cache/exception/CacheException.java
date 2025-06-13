package club.slavopolis.cache.exception;

import java.io.Serial;

/**
 * 缓存操作异常
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-13
 */
public class CacheException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
} 