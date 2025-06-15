package club.slavopolis.infrastructure.persistence.jdbc.monitoring;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * 慢查询信息
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@AllArgsConstructor
public class SlowQueryInfo {
    
    /**
     * SQL语句
     */
    private String sql;
    
    /**
     * 最大执行时间（毫秒）
     */
    private long maxTime;
    
    /**
     * 出现次数
     */
    private int count;
    
    /**
     * 最后出现时间
     */
    private long lastOccurrence;
    
    /**
     * 增加出现次数
     */
    public void incrementCount() {
        this.count++;
    }
    
    /**
     * 设置最大执行时间
     */
    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
    
    /**
     * 设置最后出现时间
     */
    public void setLastOccurrence(long lastOccurrence) {
        this.lastOccurrence = lastOccurrence;
    }
} 