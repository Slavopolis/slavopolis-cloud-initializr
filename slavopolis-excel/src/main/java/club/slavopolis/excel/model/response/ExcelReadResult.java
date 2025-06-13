package club.slavopolis.excel.model.response;

import club.slavopolis.common.response.Result;
import club.slavopolis.excel.model.ExcelError;
import club.slavopolis.excel.util.ExcelErrorCollector;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel读取结果模型，继承Result统一响应体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelReadResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 读取是否成功
     */
    private boolean success;

    /**
     * 读取的数据列表
     */
    private List<T> data;

    /**
     * 成功处理的数据数量
     */
    private int successCount;

    /**
     * 失败处理的数据数量
     */
    private int failedCount;

    /**
     * 总数据数量
     */
    private int totalCount;

    /**
     * 错误信息列表
     */
    private List<ExcelError> errors;

    /**
     * 错误摘要信息
     */
    private String errorSummary;

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
     * 读取的Sheet信息
     */
    private List<SheetInfo> sheetInfos;

    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;

    /**
     * Sheet信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SheetInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Sheet索引
         */
        private int sheetIndex;

        /**
         * Sheet名称
         */
        private String sheetName;

        /**
         * 数据行数
         */
        private int rowCount;

        /**
         * 列数
         */
        private int columnCount;

        /**
         * 成功行数
         */
        private int successRows;

        /**
         * 失败行数
         */
        private int failedRows;

        /**
         * 错误信息
         */
        private List<ExcelError> errors;
    }

    /**
     * 创建成功结果
     */
    public static <T> ExcelReadResult<T> success(List<T> data) {
        return ExcelReadResult.<T>builder()
                .success(true)
                .data(data)
                .successCount(data != null ? data.size() : 0)
                .totalCount(data != null ? data.size() : 0)
                .failedCount(0)
                .errors(Collections.emptyList())
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建部分成功结果（有错误但未完全失败）
     */
    public static <T> ExcelReadResult<T> partialSuccess(List<T> data, ExcelErrorCollector errorCollector) {
        return ExcelReadResult.<T>builder()
                .success(!errorCollector.hasFatalErrors())
                .data(data)
                .successCount(data != null ? data.size() : 0)
                .failedCount(errorCollector.getErrorCount())
                .totalCount((data != null ? data.size() : 0) + errorCollector.getErrorCount())
                .errors(errorCollector.getErrors())
                .errorSummary(errorCollector.getErrorSummary())
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static <T> ExcelReadResult<T> failure(ExcelErrorCollector errorCollector) {
        return ExcelReadResult.<T>builder()
                .success(false)
                .data(Collections.emptyList())
                .successCount(0)
                .failedCount(errorCollector.getErrorCount())
                .totalCount(errorCollector.getErrorCount())
                .errors(errorCollector.getErrors())
                .errorSummary(errorCollector.getErrorSummary())
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果（带单个错误）
     */
    public static <T> ExcelReadResult<T> failure(String errorMessage) {
        return ExcelReadResult.<T>builder()
                .success(false)
                .data(Collections.emptyList())
                .successCount(0)
                .failedCount(1)
                .totalCount(1)
                .errors(List.of(ExcelError.fatalError(errorMessage, null)))
                .errorSummary("处理失败: " + errorMessage)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 设置时间信息
     */
    public ExcelReadResult<T> withTimeInfo(LocalDateTime startTime, LocalDateTime endTime) {
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
    public ExcelReadResult<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置Sheet信息
     */
    public ExcelReadResult<T> withSheetInfos(List<SheetInfo> sheetInfos) {
        this.sheetInfos = sheetInfos;
        return this;
    }

    /**
     * 添加扩展信息
     */
    public ExcelReadResult<T> withExtraInfo(String key, Object value) {
        if (this.extraInfo == null) {
            this.extraInfo = new java.util.HashMap<>();
        }
        this.extraInfo.put(key, value);
        return this;
    }

    /**
     * 转换为统一的Result对象
     */
    public Result<List<T>> toResult() {
        if (success) {
            return Result.success("Excel读取成功", data);
        } else {
            return Result.failed(errorSummary != null ? errorSummary : "Excel读取失败");
        }
    }

    /**
     * 检查是否有错误
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * 检查是否有致命错误
     */
    public boolean hasFatalErrors() {
        return errors != null && errors.stream()
                .anyMatch(error -> Boolean.TRUE.equals(error.getFatal()));
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
     * 获取处理速度（行/秒）
     */
    public double getProcessingSpeed() {
        if (duration == 0 || totalCount == 0) {
            return 0.0;
        }
        return totalCount / (duration / 1000.0);
    }

    /**
     * 获取指定错误类型的数量
     */
    public long getErrorCount(ExcelError.ErrorType errorType) {
        if (errors == null) {
            return 0;
        }
        return errors.stream()
                .filter(error -> error.getErrorType() == errorType)
                .count();
    }

    /**
     * 获取前N个错误的描述
     */
    public List<String> getTopErrorDescriptions(int limit) {
        if (errors == null || errors.isEmpty()) {
            return Collections.emptyList();
        }
        
        return errors.stream()
                .limit(limit)
                .map(ExcelError::getFullDescription)
                .toList();
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
        stats.put("processingSpeed", getProcessingSpeed());
        
        if (startTime != null) {
            stats.put("startTime", startTime);
        }
        if (endTime != null) {
            stats.put("endTime", endTime);
        }
        
        return stats;
    }
} 