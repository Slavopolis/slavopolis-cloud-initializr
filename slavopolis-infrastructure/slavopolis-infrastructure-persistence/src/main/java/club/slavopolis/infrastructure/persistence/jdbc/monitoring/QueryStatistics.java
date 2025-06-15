package club.slavopolis.infrastructure.persistence.jdbc.monitoring;

import lombok.Builder;
import lombok.Data;

/**
 * 查询统计信息
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@Builder
public class QueryStatistics {
    
    /**
     * 总查询次数
     */
    private long totalQueries;
    
    /**
     * 成功查询次数
     */
    private long successQueries;
    
    /**
     * 失败查询次数
     */
    private long failureQueries;
    
    /**
     * 平均查询时间（毫秒）
     */
    private double averageQueryTime;
    
    /**
     * 总查询时间（毫秒）
     */
    private long totalQueryTime;
    
    /**
     * 查询成功率
     */
    public double getSuccessRate() {
        return totalQueries > 0 ? (double) successQueries / totalQueries : 0.0;
    }
    
    /**
     * 查询失败率
     */
    public double getFailureRate() {
        return totalQueries > 0 ? (double) failureQueries / totalQueries : 0.0;
    }
} 