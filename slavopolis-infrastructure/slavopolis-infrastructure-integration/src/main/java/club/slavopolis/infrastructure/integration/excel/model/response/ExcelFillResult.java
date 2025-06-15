package club.slavopolis.infrastructure.integration.excel.model.response;

import club.slavopolis.common.core.result.Result;
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
 * @description: Excel填充结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelFillResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 填充是否成功
     */
    private boolean success;

    /**
     * 成功填充的变量数量
     */
    private int successCount;

    /**
     * 失败的变量数量
     */
    private int failedCount;

    /**
     * 总变量数量
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
     * 使用的模板路径
     */
    private String templatePath;

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
     * 填充的Sheet数量
     */
    private int sheetCount;

    /**
     * 已填充的行数
     */
    private int filledRows;

    /**
     * 已填充的列数
     */
    private int filledColumns;

    /**
     * 扩展信息
     */
    private transient Map<String, Object> extraInfo;

    /**
     * 创建成功结果
     */
    public static ExcelFillResult success(int totalCount) {
        return ExcelFillResult.builder()
                .success(true)
                .successCount(totalCount)
                .totalCount(totalCount)
                .failedCount(0)
                .message("填充成功")
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功结果（带文件信息）
     */
    public static ExcelFillResult success(int totalCount, String filePath, long fileSize) {
        return ExcelFillResult.builder()
                .success(true)
                .successCount(totalCount)
                .totalCount(totalCount)
                .failedCount(0)
                .message("填充成功")
                .filePath(filePath)
                .fileSize(fileSize)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static ExcelFillResult partialSuccess(int successCount, int failedCount) {
        int totalCount = successCount + failedCount;
        return ExcelFillResult.builder()
                .success(failedCount == 0)
                .successCount(successCount)
                .failedCount(failedCount)
                .totalCount(totalCount)
                .message(String.format("填充完成：成功%d个变量，失败%d个变量", successCount, failedCount))
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static ExcelFillResult failure(String errorMessage) {
        return ExcelFillResult.builder()
                .success(false)
                .successCount(0)
                .failedCount(0)
                .totalCount(0)
                .message("填充失败")
                .errorMessage(errorMessage)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果（带统计信息）
     */
    public static ExcelFillResult failure(int totalCount, String errorMessage) {
        return ExcelFillResult.builder()
                .success(false)
                .successCount(0)
                .failedCount(totalCount)
                .totalCount(totalCount)
                .message("填充失败")
                .errorMessage(errorMessage)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 设置时间信息
     */
    public ExcelFillResult withTimeInfo(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.duration = java.time.Duration.between(startTime, endTime).toMillis();
        }
        return this;
    }

    /**
     * 设置请求ID
     */
    public ExcelFillResult withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置文件信息
     */
    public ExcelFillResult withFileInfo(String filePath, long fileSize) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        return this;
    }

    /**
     * 设置模板信息
     */
    public ExcelFillResult withTemplateInfo(String templatePath) {
        this.templatePath = templatePath;
        return this;
    }

    /**
     * 设置填充统计信息
     */
    public ExcelFillResult withFillStats(int sheetCount, int filledRows, int filledColumns) {
        this.sheetCount = sheetCount;
        this.filledRows = filledRows;
        this.filledColumns = filledColumns;
        return this;
    }

    /**
     * 添加扩展信息
     */
    public ExcelFillResult withExtraInfo(String key, Object value) {
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
    public double getFileSizeMb() {
        return fileSize / 1024.0 / 1024.0;
    }

    /**
     * 检查是否有失败变量
     */
    public boolean hasFailures() {
        return failedCount > 0;
    }

    /**
     * 获取填充效率（变量/秒）
     */
    public double getFillSpeed() {
        if (duration == 0 || totalCount == 0) {
            return 0.0;
        }
        return totalCount / (duration / 1000.0);
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
        stats.put("fillSpeed", getFillSpeed());
        stats.put("fileSize", fileSize);
        stats.put("fileSizeMB", getFileSizeMb());
        stats.put("sheetCount", sheetCount);
        stats.put("filledRows", filledRows);
        stats.put("filledColumns", filledColumns);
        
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
            return String.format("填充成功：总计%d个变量，成功%d个(%.1f%%)，失败%d个(%.1f%%)，耗时%dms，速度%.1f变量/秒，文件大小%.2fMB",
                    totalCount, successCount, getSuccessRate(), failedCount, getFailureRate(), 
                    duration, getFillSpeed(), getFileSizeMb());
        } else {
            return String.format("填充失败：%s", errorMessage != null ? errorMessage : message);
        }
    }
} 