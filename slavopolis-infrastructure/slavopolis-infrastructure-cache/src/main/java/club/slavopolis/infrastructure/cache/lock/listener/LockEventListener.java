package club.slavopolis.infrastructure.cache.lock.listener;

import club.slavopolis.infrastructure.cache.lock.core.LockInfo;
import club.slavopolis.infrastructure.cache.lock.event.LockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 分布式锁事件监听器
 *
 * <p>
 * 监听分布式锁事件，提供默认的日志记录和监控功能。
 * 应用可以继承此类实现自定义的监控逻辑。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LockEventListener {

    /**
     * 锁统计信息
     */
    private final ConcurrentHashMap<String, LockStatistics> statisticsMap = new ConcurrentHashMap<>();

    /**
     * 处理锁事件
     */
    @Async
    @EventListener
    public void handleLockEvent(LockEvent event) {
        LockInfo lockInfo = event.getLockInfo();
        LockEvent.EventType eventType = event.getEventType();

        // 记录日志
        logEvent(lockInfo, eventType);

        // 更新统计信息
        updateStatistics(lockInfo, eventType);

        // 处理特定事件
        switch (eventType) {
            case FAILED -> handleLockFailed(lockInfo);
            case EXPIRED -> handleLockExpired(lockInfo);
            case FALLBACK -> handleLockFallback(lockInfo);
        }
    }

    /**
     * 记录事件日志
     */
    private void logEvent(LockInfo lockInfo, LockEvent.EventType eventType) {
        switch (eventType) {
            case ACQUIRED -> log.info("Lock acquired - key: {}, type: {}, owner: {}, business: {}",
                    lockInfo.getLockKey(), lockInfo.getLockType(), lockInfo.getOwner(), lockInfo.getBusiness());

            case RELEASED -> log.info("Lock released - key: {}, hold: {}ms, business: {}",
                    lockInfo.getLockKey(), lockInfo.getHoldDuration(), lockInfo.getBusiness());

            case FAILED -> log.warn("Lock acquisition failed - key: {}, wait: {}ms, business: {}",
                    lockInfo.getLockKey(), lockInfo.getWaitDuration(), lockInfo.getBusiness());

            case RENEWED -> log.debug("Lock renewed - key: {}, business: {}",
                    lockInfo.getLockKey(), lockInfo.getBusiness());

            case EXPIRED -> log.warn("Lock expired - key: {}, business: {}",
                    lockInfo.getLockKey(), lockInfo.getBusiness());

            case FALLBACK -> log.info("Lock fallback to local - key: {}, business: {}",
                    lockInfo.getLockKey(), lockInfo.getBusiness());
        }
    }

    /**
     * 更新统计信息
     */
    private void updateStatistics(LockInfo lockInfo, LockEvent.EventType eventType) {
        String key = lockInfo.getBusiness() != null ? lockInfo.getBusiness() : lockInfo.getLockKey();
        LockStatistics stats = statisticsMap.computeIfAbsent(key, k -> new LockStatistics());

        switch (eventType) {
            case ACQUIRED -> {
                stats.getTotalAcquired().incrementAndGet();
                if (lockInfo.getWaitDuration() != null) {
                    stats.getTotalWaitTime().addAndGet(lockInfo.getWaitDuration());
                }
            }
            case RELEASED -> {
                stats.getTotalReleased().incrementAndGet();
                if (lockInfo.getHoldDuration() != null) {
                    stats.getTotalHoldTime().addAndGet(lockInfo.getHoldDuration());
                }
            }
            case FAILED -> stats.getTotalFailed().incrementAndGet();
            case RENEWED -> stats.getTotalRenewed().incrementAndGet();
            case EXPIRED -> stats.getTotalExpired().incrementAndGet();
            case FALLBACK -> stats.getTotalFallback().incrementAndGet();
        }
    }

    /**
     * 处理锁获取失败
     */
    private void handleLockFailed(LockInfo lockInfo) {
        // 可以在这里添加告警逻辑
        if (lockInfo.getWaitDuration() != null && lockInfo.getWaitDuration() > 5000) {
            log.error("Lock acquisition took too long - key: {}, wait: {}ms",
                    lockInfo.getLockKey(), lockInfo.getWaitDuration());
        }
    }

    /**
     * 处理锁过期
     */
    private void handleLockExpired(LockInfo lockInfo) {
        // 锁过期可能导致业务问题，需要特别关注
        log.error("Lock expired while still in use - key: {}, business: {}",
                lockInfo.getLockKey(), lockInfo.getBusiness());
    }

    /**
     * 处理锁降级
     */
    private void handleLockFallback(LockInfo lockInfo) {
        // 降级说明分布式锁服务可能有问题
        log.warn("Lock service might be unavailable, using local lock - key: {}",
                lockInfo.getLockKey());
    }

    /**
     * 获取统计信息
     */
    public LockStatistics getStatistics(String key) {
        return statisticsMap.get(key);
    }

    /**
     * 清空统计信息
     */
    public void clearStatistics() {
        statisticsMap.clear();
    }

    /**
     * 锁统计信息
     */
    @lombok.Data
    public static class LockStatistics {
        private final AtomicLong totalAcquired = new AtomicLong();
        private final AtomicLong totalReleased = new AtomicLong();
        private final AtomicLong totalFailed = new AtomicLong();
        private final AtomicLong totalRenewed = new AtomicLong();
        private final AtomicLong totalExpired = new AtomicLong();
        private final AtomicLong totalFallback = new AtomicLong();
        private final AtomicLong totalWaitTime = new AtomicLong();
        private final AtomicLong totalHoldTime = new AtomicLong();

        /**
         * 获取平均等待时间
         */
        public double getAverageWaitTime() {
            long acquired = totalAcquired.get();
            return acquired > 0 ? (double) totalWaitTime.get() / acquired : 0;
        }

        /**
         * 获取平均持有时间
         */
        public double getAverageHoldTime() {
            long released = totalReleased.get();
            return released > 0 ? (double) totalHoldTime.get() / released : 0;
        }

        /**
         * 获取成功率
         */
        public double getSuccessRate() {
            long total = totalAcquired.get() + totalFailed.get();
            return total > 0 ? (double) totalAcquired.get() / total * 100 : 0;
        }
    }
}
