package club.slavopolis.infrastructure.integration.excel.core.processor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 数据处理上下文，包含处理过程中的环境信息和状态
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProcessContext {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 处理开始时间
     */
    private LocalDateTime startTime;

    /**
     * 当前批次号
     */
    private int currentBatch;

    /**
     * 总批次数
     */
    private int totalBatches;

    /**
     * 当前处理的数据行索引（Excel中的行号）
     */
    private int currentRowIndex;

    /**
     * 总数据行数
     */
    private int totalRows;

    /**
     * 当前Sheet索引
     */
    private int currentSheetIndex;

    /**
     * 当前Sheet名称
     */
    private String currentSheetName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private long fileSize;

    /**
     * 处理模式（sync/async）
     */
    private String processMode;

    /**
     * 用户ID（可选）
     */
    private String userId;

    /**
     * 租户ID（可选）
     */
    private String tenantId;

    /**
     * 自定义属性
     */
    @Builder.Default
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * 处理统计信息
     */
    @Builder.Default
    private ProcessStats stats = new ProcessStats();

    /**
     * 处理统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessStats {
        /**
         * 已处理行数
         */
        private long processedRows = 0;

        /**
         * 成功行数
         */
        private long successRows = 0;

        /**
         * 失败行数
         */
        private long failedRows = 0;

        /**
         * 跳过行数
         */
        private long skippedRows = 0;

        /**
         * 处理开始时间
         */
        private LocalDateTime processStartTime;

        /**
         * 上次更新时间
         */
        private LocalDateTime lastUpdateTime;

        /**
         * 增加已处理行数
         */
        public void incrementProcessed() {
            processedRows++;
            lastUpdateTime = LocalDateTime.now();
        }

        /**
         * 增加成功行数
         */
        public void incrementSuccess() {
            successRows++;
            lastUpdateTime = LocalDateTime.now();
        }

        /**
         * 增加失败行数
         */
        public void incrementFailed() {
            failedRows++;
            lastUpdateTime = LocalDateTime.now();
        }

        /**
         * 增加跳过行数
         */
        public void incrementSkipped() {
            skippedRows++;
            lastUpdateTime = LocalDateTime.now();
        }

        /**
         * 获取处理进度百分比
         */
        public double getProgressPercent(long totalRows) {
            if (totalRows <= 0) {
                return 0.0;
            }
            return (double) processedRows / totalRows * 100;
        }

        /**
         * 获取成功率
         */
        public double getSuccessRate() {
            if (processedRows <= 0) {
                return 0.0;
            }
            return (double) successRows / processedRows * 100;
        }

        /**
         * 获取处理速度（行/秒）
         */
        public double getProcessingSpeed() {
            if (processStartTime == null || processedRows <= 0) {
                return 0.0;
            }
            
            LocalDateTime now = LocalDateTime.now();
            long durationMs = java.time.Duration.between(processStartTime, now).toMillis();
            
            if (durationMs <= 0) {
                return 0.0;
            }
            
            return processedRows / (durationMs / 1000.0);
        }
    }

    /**
     * 创建默认上下文
     */
    public static ProcessContext create() {
        return ProcessContext.builder()
                .startTime(LocalDateTime.now())
                .currentBatch(0)
                .currentRowIndex(0)
                .currentSheetIndex(0)
                .processMode("sync")
                .build();
    }

    /**
     * 创建带请求ID的上下文
     */
    public static ProcessContext create(String requestId) {
        return create().toBuilder()
                .requestId(requestId)
                .build();
    }

    /**
     * 设置属性
     */
    public ProcessContext setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    /**
     * 获取属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    /**
     * 获取属性（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        T value = (T) attributes.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 移除属性
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    /**
     * 检查是否包含属性
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * 更新当前处理位置
     */
    public ProcessContext updatePosition(int rowIndex) {
        this.currentRowIndex = rowIndex;
        return this;
    }

    /**
     * 更新当前批次
     */
    public ProcessContext updateBatch(int batchIndex) {
        this.currentBatch = batchIndex;
        return this;
    }

    /**
     * 更新Sheet信息
     */
    public ProcessContext updateSheet(int sheetIndex, String sheetName) {
        this.currentSheetIndex = sheetIndex;
        this.currentSheetName = sheetName;
        return this;
    }

    /**
     * 检查是否为异步处理模式
     */
    public boolean isAsync() {
        return "async".equals(processMode);
    }

    /**
     * 设置为异步处理模式
     */
    public ProcessContext setAsync() {
        this.processMode = "async";
        return this;
    }

    /**
     * 设置为同步处理模式
     */
    public ProcessContext setSync() {
        this.processMode = "sync";
        return this;
    }

    /**
     * 获取当前进度百分比
     */
    public double getProgressPercent() {
        return stats.getProgressPercent(totalRows);
    }

    /**
     * 初始化统计信息
     */
    public ProcessContext initStats() {
        this.stats.setProcessStartTime(LocalDateTime.now());
        return this;
    }

    /**
     * 获取上下文摘要信息
     */
    public String getSummary() {
        return String.format("ProcessContext[requestId=%s, batch=%d/%d, row=%d/%d, sheet=%s, mode=%s]",
                requestId, currentBatch, totalBatches, currentRowIndex, totalRows, 
                currentSheetName, processMode);
    }
} 