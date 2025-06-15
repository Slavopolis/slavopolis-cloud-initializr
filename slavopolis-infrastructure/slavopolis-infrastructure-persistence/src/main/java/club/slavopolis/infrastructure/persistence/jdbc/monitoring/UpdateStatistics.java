package club.slavopolis.infrastructure.persistence.jdbc.monitoring;

import lombok.Builder;
import lombok.Data;

/**
 * 更新统计信息
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
public class UpdateStatistics {
    
    /**
     * 总更新次数
     */
    private long totalUpdates;
    
    /**
     * 成功更新次数
     */
    private long successUpdates;
    
    /**
     * 失败更新次数
     */
    private long failureUpdates;
    
    /**
     * 平均更新时间（毫秒）
     */
    private double averageUpdateTime;
    
    /**
     * 总更新时间（毫秒）
     */
    private long totalUpdateTime;
    
    /**
     * 更新成功率
     */
    public double getSuccessRate() {
        return totalUpdates > 0 ? (double) successUpdates / totalUpdates : 0.0;
    }
    
    /**
     * 更新失败率
     */
    public double getFailureRate() {
        return totalUpdates > 0 ? (double) failureUpdates / totalUpdates : 0.0;
    }
} 