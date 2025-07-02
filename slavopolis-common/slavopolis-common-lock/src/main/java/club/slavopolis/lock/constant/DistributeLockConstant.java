package club.slavopolis.lock.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 分布式锁常量类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DistributeLockConstant {

    /**
     * 默认的锁key
     */
    public static final String NONE_KEY = "NONE_KEY";

    /**
     * 默认的锁拥有者
     */
    public static final String DEFAULT_OWNER = "DEFAULT_OWNER";

    /**
     * 默认的锁过期时间
     */
    public static final int DEFAULT_EXPIRE_TIME = -1;

    /**
     * 默认的锁等待时间
     */
    public static final int DEFAULT_WAIT_TIME = Integer.MAX_VALUE;

    /**
     * 切面执行优先级
     */
    public static final int ASPECT_ORDER = Integer.MIN_VALUE;

    /**
     * 错误消息：未找到锁的key
     */
    public static final String ERROR_NO_KEY_FOUND = "未找到锁的key";

    /**
     * 错误消息：获取锁失败
     */
    public static final String ERROR_ACQUIRE_LOCK_FAILED = "获取锁失败";

    /**
     * 默认的非阻塞等待时间
     */
    public static final long DEFAULT_TRY_WAIT_TIME = 0L;

    /**
     * 锁管理器缓存初始容量
     */
    public static final int LOCK_CACHE_INITIAL_CAPACITY = 16;

    /**
     * 锁管理器缓存负载因子
     */
    public static final float LOCK_CACHE_LOAD_FACTOR = 0.75f;

    /**
     * 错误消息：锁未被当前线程持有
     */
    public static final String ERROR_LOCK_NOT_HELD_BY_CURRENT_THREAD = "锁未被当前线程持有";

    /**
     * 错误消息：强制释放锁操作
     */
    public static final String WARNING_FORCE_UNLOCK = "执行强制释放锁操作";
}
