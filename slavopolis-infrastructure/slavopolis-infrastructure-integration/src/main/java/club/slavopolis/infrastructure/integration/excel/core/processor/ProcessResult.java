package club.slavopolis.infrastructure.integration.excel.core.processor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 数据处理结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 处理是否成功
     */
    private boolean success;

    /**
     * 处理后的数据列表
     */
    private transient List<T> processedData;

    /**
     * 成功处理的数据数量
     */
    private int successCount;

    /**
     * 失败处理的数据数量
     */
    private int failedCount;

    /**
     * 跳过的数据数量
     */
    private int skippedCount;

    /**
     * 处理消息
     */
    private String message;

    /**
     * 处理开始时间
     */
    private LocalDateTime startTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime endTime;

    /**
     * 处理耗时（毫秒）
     */
    private long duration;

    /**
     * 处理速度（行/秒）
     */
    private double processingSpeed;

    /**
     * 扩展数据
     */
    private transient Map<String, Object> extraData;

    /**
     * 创建成功结果
     */
    public static <T> ProcessResult<T> success(List<T> processedData) {
        return ProcessResult.<T>builder()
                .success(true)
                .processedData(processedData)
                .successCount(processedData != null ? processedData.size() : 0)
                .failedCount(0)
                .skippedCount(0)
                .message("处理成功")
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功结果（带统计信息）
     */
    public static <T> ProcessResult<T> success(List<T> processedData, int successCount, int failedCount, int skippedCount) {
        return ProcessResult.<T>builder()
                .success(true)
                .processedData(processedData)
                .successCount(successCount)
                .failedCount(failedCount)
                .skippedCount(skippedCount)
                .message(String.format("处理完成：成功%d，失败%d，跳过%d", successCount, failedCount, skippedCount))
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static <T> ProcessResult<T> failure(String message) {
        return ProcessResult.<T>builder()
                .success(false)
                .processedData(null)
                .successCount(0)
                .failedCount(0)
                .skippedCount(0)
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static <T> ProcessResult<T> partialSuccess(List<T> processedData, int successCount, int failedCount, int skippedCount, String message) {
        return ProcessResult.<T>builder()
                .success(failedCount == 0)
                .processedData(processedData)
                .successCount(successCount)
                .failedCount(failedCount)
                .skippedCount(skippedCount)
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 设置时间信息
     */
    public ProcessResult<T> withTimeInfo(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.duration = java.time.Duration.between(startTime, endTime).toMillis();
            
            // 计算处理速度
            int totalCount = successCount + failedCount + skippedCount;
            if (duration > 0 && totalCount > 0) {
                this.processingSpeed = totalCount / (duration / 1000.0);
            }
        }
        return this;
    }

    /**
     * 添加扩展数据
     */
    public ProcessResult<T> withExtraData(String key, Object value) {
        if (this.extraData == null) {
            this.extraData = new java.util.HashMap<>();
        }
        this.extraData.put(key, value);
        return this;
    }

    /**
     * 设置扩展数据
     */
    public ProcessResult<T> withExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
        return this;
    }

    /**
     * 获取总处理数量
     */
    public int getTotalCount() {
        return successCount + failedCount + skippedCount;
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        int total = getTotalCount();
        if (total == 0) {
            return 0.0;
        }
        return (double) successCount / total * 100;
    }

    /**
     * 获取失败率
     */
    public double getFailureRate() {
        int total = getTotalCount();
        if (total == 0) {
            return 0.0;
        }
        return (double) failedCount / total * 100;
    }

    /**
     * 获取跳过率
     */
    public double getSkipRate() {
        int total = getTotalCount();
        if (total == 0) {
            return 0.0;
        }
        return (double) skippedCount / total * 100;
    }

    /**
     * 检查是否有数据
     */
    public boolean hasData() {
        return processedData != null && !processedData.isEmpty();
    }

    /**
     * 检查是否有失败数据
     */
    public boolean hasFailures() {
        return failedCount > 0;
    }

    /**
     * 检查是否有跳过数据
     */
    public boolean hasSkipped() {
        return skippedCount > 0;
    }

    /**
     * 获取扩展数据
     */
    @SuppressWarnings("unchecked")
    public <V> V getExtraData(String key) {
        if (extraData == null) {
            return null;
        }
        return (V) extraData.get(key);
    }

    /**
     * 获取扩展数据（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <V> V getExtraData(String key, V defaultValue) {
        if (extraData == null) {
            return defaultValue;
        }
        V value = (V) extraData.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取处理摘要
     */
    public String getSummary() {
        int total = getTotalCount();
        return String.format("处理结果：总计%d条，成功%d条(%.1f%%)，失败%d条(%.1f%%)，跳过%d条(%.1f%%)，耗时%dms，速度%.1f行/秒",
                total, successCount, getSuccessRate(), failedCount, getFailureRate(), 
                skippedCount, getSkipRate(), duration, processingSpeed);
    }
} 