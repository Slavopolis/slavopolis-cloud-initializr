package club.slavopolis.persistence.jdbc.monitoring;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SQL执行监控器
 * <p>提供SQL执行性能监控、统计和分析功能</p>
 * <p>支持执行时间统计、慢查询检测、异常统计等监控能力</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
@Setter
@Slf4j
public class SqlExecutionMonitor {

    /**
     * 查询执行统计
     */
    private final AtomicLong queryCount = new AtomicLong(0);
    private final AtomicLong querySuccessCount = new AtomicLong(0);
    private final AtomicLong queryFailureCount = new AtomicLong(0);
    private final AtomicLong totalQueryTime = new AtomicLong(0);

    /**
     * 更新执行统计
     */
    private final AtomicLong updateCount = new AtomicLong(0);
    private final AtomicLong updateSuccessCount = new AtomicLong(0);
    private final AtomicLong updateFailureCount = new AtomicLong(0);
    private final AtomicLong totalUpdateTime = new AtomicLong(0);

    /**
     * 批处理执行统计
     */
    private final AtomicLong batchUpdateCount = new AtomicLong(0);
    private final AtomicLong batchUpdateSuccessCount = new AtomicLong(0);
    private final AtomicLong batchUpdateFailureCount = new AtomicLong(0);
    private final AtomicLong totalBatchUpdateTime = new AtomicLong(0);

    /**
     * 慢查询统计
     */
    private final Map<String, SlowQueryInfo> slowQueries = new ConcurrentHashMap<>();
    
    /**
     * 慢查询阈值（毫秒）
     */
    private long slowQueryThreshold = 1000L;

    /**
     * 是否启用监控
     */
    private boolean monitoringEnabled = true;

    /**
     * 记录查询执行
     * 
     * @param sql SQL语句
     * @param params 参数
     * @param executionTime 执行时间（毫秒）
     * @param success 是否成功
     */
    public void recordQueryExecution(String sql, Map<String, Object> params, long executionTime, boolean success) {
        if (!monitoringEnabled) {
            return;
        }

        queryCount.incrementAndGet();
        totalQueryTime.addAndGet(executionTime);

        if (success) {
            querySuccessCount.incrementAndGet();
        } else {
            queryFailureCount.incrementAndGet();
        }

        // 慢查询检测
        if (executionTime > slowQueryThreshold) {
            recordSlowQuery(sql, params, executionTime);
        }

        if (log.isDebugEnabled()) {
            log.debug("Query execution recorded: sql={}, executionTime={}ms, success={}", 
                sql, executionTime, success);
        }
    }

    /**
     * 记录更新执行
     * 
     * @param sql SQL语句
     * @param params 参数
     * @param executionTime 执行时间（毫秒）
     * @param success 是否成功
     */
    public void recordUpdateExecution(String sql, Map<String, Object> params, long executionTime, boolean success) {
        if (!monitoringEnabled) {
            return;
        }

        updateCount.incrementAndGet();
        totalUpdateTime.addAndGet(executionTime);

        if (success) {
            updateSuccessCount.incrementAndGet();
        } else {
            updateFailureCount.incrementAndGet();
        }

        // 慢查询检测
        if (executionTime > slowQueryThreshold) {
            recordSlowQuery(sql, params, executionTime);
        }

        if (log.isDebugEnabled()) {
            log.debug("Update execution recorded: sql={}, executionTime={}ms, success={}", 
                sql, executionTime, success);
        }
    }

    /**
     * 记录批处理执行
     * 
     * @param sql SQL语句
     * @param batchSize 批处理大小
     * @param executionTime 执行时间（毫秒）
     * @param success 是否成功
     */
    public void recordBatchUpdateExecution(String sql, int batchSize, long executionTime, boolean success) {
        if (!monitoringEnabled) {
            return;
        }

        batchUpdateCount.incrementAndGet();
        totalBatchUpdateTime.addAndGet(executionTime);

        if (success) {
            batchUpdateSuccessCount.incrementAndGet();
        } else {
            batchUpdateFailureCount.incrementAndGet();
        }

        if (log.isDebugEnabled()) {
            log.debug("Batch update execution recorded: sql={}, batchSize={}, executionTime={}ms, success={}", 
                sql, batchSize, executionTime, success);
        }
    }

    /**
     * 记录慢查询
     */
    private void recordSlowQuery(String sql, Map<String, Object> params, long executionTime) {
        String sqlKey = sql.trim();
        
        slowQueries.compute(sqlKey, (key, existing) -> {
            if (existing == null) {
                return new SlowQueryInfo(sql, executionTime, 1, System.currentTimeMillis());
            } else {
                existing.incrementCount();
                if (executionTime > existing.getMaxTime()) {
                    existing.setMaxTime(executionTime);
                    existing.setLastOccurrence(System.currentTimeMillis());
                }
                return existing;
            }
        });

        log.warn("Slow query detected: sql={}, executionTime={}ms, params={}", sql, executionTime, params);
    }

    /**
     * 获取查询统计信息
     */
    public QueryStatistics getQueryStatistics() {
        return QueryStatistics.builder()
                .totalQueries(queryCount.get())
                .successQueries(querySuccessCount.get())
                .failureQueries(queryFailureCount.get())
                .averageQueryTime(queryCount.get() > 0 ? (double) totalQueryTime.get() / queryCount.get() : 0.0)
                .totalQueryTime(totalQueryTime.get())
                .build();
    }

    /**
     * 获取更新统计信息
     */
    public UpdateStatistics getUpdateStatistics() {
        return UpdateStatistics.builder()
                .totalUpdates(updateCount.get())
                .successUpdates(updateSuccessCount.get())
                .failureUpdates(updateFailureCount.get())
                .averageUpdateTime(updateCount.get() > 0 ? (double) totalUpdateTime.get() / updateCount.get() : 0.0)
                .totalUpdateTime(totalUpdateTime.get())
                .build();
    }

    /**
     * 获取批处理统计信息
     */
    public BatchUpdateStatistics getBatchUpdateStatistics() {
        return BatchUpdateStatistics.builder()
                .totalBatchUpdates(batchUpdateCount.get())
                .successBatchUpdates(batchUpdateSuccessCount.get())
                .failureBatchUpdates(batchUpdateFailureCount.get())
                .averageBatchUpdateTime(batchUpdateCount.get() > 0 ? (double) totalBatchUpdateTime.get() / batchUpdateCount.get() : 0.0)
                .totalBatchUpdateTime(totalBatchUpdateTime.get())
                .build();
    }

    /**
     * 获取慢查询统计信息
     */
    public Map<String, SlowQueryInfo> getSlowQueries() {
        return new ConcurrentHashMap<>(slowQueries);
    }

    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        queryCount.set(0);
        querySuccessCount.set(0);
        queryFailureCount.set(0);
        totalQueryTime.set(0);

        updateCount.set(0);
        updateSuccessCount.set(0);
        updateFailureCount.set(0);
        totalUpdateTime.set(0);

        batchUpdateCount.set(0);
        batchUpdateSuccessCount.set(0);
        batchUpdateFailureCount.set(0);
        totalBatchUpdateTime.set(0);

        slowQueries.clear();

        log.info("SQL execution statistics have been reset");
    }

    /**
     * 打印统计信息
     */
    public void printStatistics() {
        QueryStatistics queryStats = getQueryStatistics();
        UpdateStatistics updateStats = getUpdateStatistics();
        BatchUpdateStatistics batchStats = getBatchUpdateStatistics();

        log.info("SQL Execution Statistics:");
        log.info("  Queries: total={}, success={}, failure={}, avgTime={}ms",
            queryStats.getTotalQueries(), queryStats.getSuccessQueries(), 
            queryStats.getFailureQueries(), queryStats.getAverageQueryTime());
        log.info("  Updates: total={}, success={}, failure={}, avgTime={}ms",
            updateStats.getTotalUpdates(), updateStats.getSuccessUpdates(), 
            updateStats.getFailureUpdates(), updateStats.getAverageUpdateTime());
        log.info("  Batch Updates: total={}, success={}, failure={}, avgTime={}ms",
            batchStats.getTotalBatchUpdates(), batchStats.getSuccessBatchUpdates(), 
            batchStats.getFailureBatchUpdates(), batchStats.getAverageBatchUpdateTime());
        log.info("  Slow Queries: count={}", slowQueries.size());
    }
} 