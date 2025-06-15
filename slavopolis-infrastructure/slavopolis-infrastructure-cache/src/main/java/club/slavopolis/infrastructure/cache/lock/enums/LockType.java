package club.slavopolis.infrastructure.cache.lock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁类型枚举
 *
 * <p>
 * 定义了系统支持的各种分布式锁类型，每种类型适用于不同的业务场景。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum LockType {

    /**
     * 可重入锁
     * <p>
     * 最常用的锁类型，同一线程可以多次获取同一把锁。
     * 适用于大多数业务场景。
     * </p>
     */
    REENTRANT("reentrant", "可重入锁"),

    /**
     * 公平锁
     * <p>
     * 按照请求顺序分配锁，先请求的先获得锁。
     * 适用于需要保证公平性的场景，但性能略低于非公平锁。
     * </p>
     */
    FAIR("fair", "公平锁"),

    /**
     * 读锁
     * <p>
     * 读写锁中的读锁，允许多个线程同时读取。
     * 适用于读多写少的场景。
     * </p>
     */
    READ("read", "读锁"),

    /**
     * 写锁
     * <p>
     * 读写锁中的写锁，独占锁，写入时不允许读取。
     * 适用于需要保证数据一致性的写操作。
     * </p>
     */
    WRITE("write", "写锁"),

    /**
     * 联锁
     * <p>
     * 同时获取多个锁，所有锁都获取成功才算成功。
     * 适用于需要同时锁定多个资源的场景。
     * </p>
     */
    MULTI("multi", "联锁"),

    /**
     * 红锁
     * <p>
     * Redis 红锁算法实现，通过多个独立的 Redis 实例保证可靠性。
     * 适用于对锁可靠性要求极高的场景。
     * </p>
     */
    RED("red", "红锁"),

    /**
     * 自旋锁
     * <p>
     * 循环尝试获取锁，不会阻塞线程。
     * 适用于锁持有时间很短的场景。
     * </p>
     */
    SPIN("spin", "自旋锁");

    /**
     * 锁类型编码
     */
    private final String code;

    /**
     * 锁类型描述
     */
    private final String description;

    /**
     * 根据编码获取锁类型
     *
     * @param code 锁类型编码
     * @return 锁类型枚举
     */
    public static LockType fromCode(String code) {
        for (LockType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return REENTRANT;
    }
}
