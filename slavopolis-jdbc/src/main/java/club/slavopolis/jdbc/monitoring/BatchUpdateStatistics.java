package club.slavopolis.jdbc.monitoring;

import lombok.Builder;
import lombok.Data;

/**
 * 批处理统计信息
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
public class BatchUpdateStatistics {
    
    /**
     * 总批处理次数
     */
    private long totalBatchUpdates;
    
    /**
     * 成功批处理次数
     */
    private long successBatchUpdates;
    
    /**
     * 失败批处理次数
     */
    private long failureBatchUpdates;
    
    /**
     * 平均批处理时间（毫秒）
     */
    private double averageBatchUpdateTime;
    
    /**
     * 总批处理时间（毫秒）
     */
    private long totalBatchUpdateTime;
    
    /**
     * 批处理成功率
     */
    public double getSuccessRate() {
        return totalBatchUpdates > 0 ? (double) successBatchUpdates / totalBatchUpdates : 0.0;
    }
    
    /**
     * 批处理失败率
     */
    public double getFailureRate() {
        return totalBatchUpdates > 0 ? (double) failureBatchUpdates / totalBatchUpdates : 0.0;
    }
} 