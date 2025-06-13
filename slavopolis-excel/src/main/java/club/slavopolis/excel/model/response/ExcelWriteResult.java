package club.slavopolis.excel.model.response;

import club.slavopolis.common.response.Result;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel写入结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelWriteResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 写入是否成功
     */
    private boolean success;

    /**
     * 成功写入的数据数量
     */
    private int successCount;

    /**
     * 失败的数据数量
     */
    private int failedCount;

    /**
     * 总数据数量
     */
    private int totalCount;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 生成的文件路径
     */
    private String filePath;

    /**
     * 生成的文件大小（字节）
     */
    private long fileSize;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
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
     * 写入的Sheet数量
     */
    private int sheetCount;

    /**
     * 最大列数
     */
    private int maxColumns;

    /**
     * 扩展信息
     */
    private transient Map<String, Object> extraInfo;

    /**
     * 创建成功结果
     */
    public static ExcelWriteResult success(int totalCount) {
        return ExcelWriteResult.builder()
                .success(true)
                .successCount(totalCount)
                .totalCount(totalCount)
                .failedCount(0)
                .message("写入成功")
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功结果（带文件信息）
     */
    public static ExcelWriteResult success(int totalCount, String filePath, long fileSize) {
        return ExcelWriteResult.builder()
                .success(true)
                .successCount(totalCount)
                .totalCount(totalCount)
                .failedCount(0)
                .message("写入成功")
                .filePath(filePath)
                .fileSize(fileSize)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static ExcelWriteResult partialSuccess(int successCount, int failedCount) {
        int totalCount = successCount + failedCount;
        return ExcelWriteResult.builder()
                .success(failedCount == 0)
                .successCount(successCount)
                .failedCount(failedCount)
                .totalCount(totalCount)
                .message(String.format("写入完成：成功%d条，失败%d条", successCount, failedCount))
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static ExcelWriteResult failure(String errorMessage) {
        return ExcelWriteResult.builder()
                .success(false)
                .successCount(0)
                .failedCount(0)
                .totalCount(0)
                .message("写入失败")
                .errorMessage(errorMessage)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果（带统计信息）
     */
    public static ExcelWriteResult failure(int totalCount, String errorMessage) {
        return ExcelWriteResult.builder()
                .success(false)
                .successCount(0)
                .failedCount(totalCount)
                .totalCount(totalCount)
                .message("写入失败")
                .errorMessage(errorMessage)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 设置时间信息
     */
    public ExcelWriteResult withTimeInfo(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.duration = java.time.Duration.between(startTime, endTime).toMillis();
            
            // 计算处理速度
            if (duration > 0 && totalCount > 0) {
                this.processingSpeed = totalCount / (duration / 1000.0);
            }
        }
        return this;
    }

    /**
     * 设置请求ID
     */
    public ExcelWriteResult withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置文件信息
     */
    public ExcelWriteResult withFileInfo(String filePath, long fileSize) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        return this;
    }

    /**
     * 设置Sheet信息
     */
    public ExcelWriteResult withSheetInfo(int sheetCount, int maxColumns) {
        this.sheetCount = sheetCount;
        this.maxColumns = maxColumns;
        return this;
    }

    /**
     * 添加扩展信息
     */
    public ExcelWriteResult withExtraInfo(String key, Object value) {
        if (this.extraInfo == null) {
            this.extraInfo = new java.util.HashMap<>();
        }
        this.extraInfo.put(key, value);
        return this;
    }

    /**
     * 转换为统一的Result对象
     */
    public Result<String> toResult() {
        if (success) {
            return Result.success(filePath, message);
        } else {
            return Result.failed(errorMessage != null ? errorMessage : message);
        }
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (totalCount == 0) {
            return 0.0;
        }
        return (double) successCount / totalCount * 100;
    }

    /**
     * 获取失败率
     */
    public double getFailureRate() {
        if (totalCount == 0) {
            return 0.0;
        }
        return (double) failedCount / totalCount * 100;
    }

    /**
     * 获取文件大小（MB）
     */
    public double getFileSizeMB() {
        return fileSize / 1024.0 / 1024.0;
    }

    /**
     * 检查是否有失败数据
     */
    public boolean hasFailures() {
        return failedCount > 0;
    }

    /**
     * 获取扩展信息
     */
    @SuppressWarnings("unchecked")
    public <V> V getExtraInfo(String key) {
        if (extraInfo == null) {
            return null;
        }
        return (V) extraInfo.get(key);
    }

    /**
     * 获取扩展信息（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <V> V getExtraInfo(String key, V defaultValue) {
        if (extraInfo == null) {
            return defaultValue;
        }
        V value = (V) extraInfo.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取性能统计信息
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("successCount", successCount);
        stats.put("failedCount", failedCount);
        stats.put("successRate", getSuccessRate());
        stats.put("duration", duration);
        stats.put("processingSpeed", processingSpeed);
        stats.put("fileSize", fileSize);
        stats.put("fileSizeMB", getFileSizeMB());
        
        if (startTime != null) {
            stats.put("startTime", startTime);
        }
        if (endTime != null) {
            stats.put("endTime", endTime);
        }
        
        return stats;
    }

    /**
     * 获取结果摘要
     */
    public String getSummary() {
        if (success) {
            return String.format("写入成功：总计%d条，成功%d条(%.1f%%)，失败%d条(%.1f%%)，耗时%dms，速度%.1f行/秒，文件大小%.2fMB",
                    totalCount, successCount, getSuccessRate(), failedCount, getFailureRate(), 
                    duration, processingSpeed, getFileSizeMB());
        } else {
            return String.format("写入失败：%s", errorMessage != null ? errorMessage : message);
        }
    }
} 