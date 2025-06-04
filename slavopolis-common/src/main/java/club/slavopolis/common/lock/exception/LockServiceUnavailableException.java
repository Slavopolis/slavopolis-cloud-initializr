package club.slavopolis.common.lock.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 锁服务不可用异常
 *
 * <p>
 * 当分布式锁服务（如Redis）不可用时抛出此异常。
 * </p>
 */
@Getter
public class LockServiceUnavailableException extends LockException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 服务名称
     */
    private final String serviceName;

    public LockServiceUnavailableException(String serviceName) {
        super(String.format("Lock service [%s] is unavailable", serviceName));
        this.serviceName = serviceName;
    }

    public LockServiceUnavailableException(String serviceName, Throwable cause) {
        super(String.format("Lock service [%s] is unavailable", serviceName), cause);
        this.serviceName = serviceName;
    }

}
