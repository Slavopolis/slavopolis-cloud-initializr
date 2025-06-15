package club.slavopolis.infrastructure.integration.excel.model.config;

import java.util.Set;

import club.slavopolis.infrastructure.integration.excel.util.ExcelErrorCollector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel读取配置模型
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExcelReadConfig {

    /**
     * Sheet编号（从0开始）
     */
    private Integer sheetNo = 0;

    /**
     * Sheet名称（优先级高于sheetNo）
     */
    private String sheetName;

    /**
     * 头部行号（从1开始）
     */
    private Integer headerRowNumber = 1;

    /**
     * 批量处理大小
     */
    private Integer batchSize = 1000;

    /**
     * 包含的列名（字段名）
     */
    private Set<String> includeColumns;

    /**
     * 排除的列名（字段名）
     */
    private Set<String> excludeColumns;

    /**
     * 是否忽略空行
     */
    private Boolean ignoreEmptyRow = true;

    /**
     * 是否自动去除字符串两端空白
     */
    private Boolean autoTrim = true;

    /**
     * 是否启用数据验证
     */
    private Boolean enableValidation = true;

    /**
     * 是否快速失败模式
     */
    private Boolean failFast = false;

    /**
     * 最大错误数量
     */
    private Integer maxErrorCount = 1000;

    /**
     * 最大读取行数
     */
    private Integer maxRows = Integer.MAX_VALUE;

    /**
     * 文件密码（如果有）
     */
    private String password;

    /**
     * 是否忽略验证失败的行
     */
    private Boolean ignoreValidationErrors = false;

    /**
     * 是否启用重复数据检查
     */
    private Boolean enableDuplicateCheck = false;

    /**
     * 重复数据检查的字段名列表
     */
    private Set<String> duplicateCheckFields;

    /**
     * 是否使用科学计数法读取数字
     */
    private Boolean useScientificFormat = false;

    /**
     * 自定义错误收集器
     */
    private ExcelErrorCollector errorCollector;

    /**
     * 读取超时时间（毫秒）: 默认5分钟
     */
    private Long timeoutMs = 300000L;

    /**
     * 是否启用异步处理
     */
    private Boolean enableAsync = false;

    /**
     * 异步处理的回调线程数
     */
    private Integer asyncCallbackThreads = 1;

    /**
     * 是否启用进度监控
     */
    private Boolean enableProgressMonitor = false;

    /**
     * 进度更新间隔（行数）
     */
    private Integer progressUpdateInterval = 1000;

    /**
     * 自定义配置参数
     */
    private java.util.Map<String, Object> customProperties;

    /**
     * 创建默认配置
     */
    public static ExcelReadConfig defaultConfig() {
        return ExcelReadConfig.builder()
                .sheetNo(0)
                .headerRowNumber(1)
                .batchSize(1000)
                .ignoreEmptyRow(true)
                .autoTrim(true)
                .enableValidation(true)
                .failFast(false)
                .maxErrorCount(1000)
                .maxRows(Integer.MAX_VALUE)
                .ignoreValidationErrors(false)
                .enableDuplicateCheck(false)
                .useScientificFormat(false)
                .timeoutMs(300000L)
                .enableAsync(false)
                .enableProgressMonitor(false)
                .progressUpdateInterval(1000)
                .build();
    }

    /**
     * 创建快速失败配置
     */
    public static ExcelReadConfig failFastConfig() {
        return defaultConfig().toBuilder()
                .failFast(true)
                .maxErrorCount(1)
                .build();
    }

    /**
     * 创建大数据量配置
     */
    public static ExcelReadConfig bigDataConfig() {
        return defaultConfig().toBuilder()
                .batchSize(5000)
                .enableAsync(true)
                .enableProgressMonitor(true)
                .progressUpdateInterval(5000)
                .build();
    }

    /**
     * 创建严格验证配置
     */
    public static ExcelReadConfig strictValidationConfig() {
        return defaultConfig().toBuilder()
                .enableValidation(true)
                .failFast(false)
                .ignoreValidationErrors(false)
                .enableDuplicateCheck(true)
                .maxErrorCount(100)
                .build();
    }

    /**
     * 获取错误收集器
     */
    public ExcelErrorCollector getOrCreateErrorCollector() {
        if (errorCollector == null) {
            errorCollector = new ExcelErrorCollector(!failFast, maxErrorCount);
        }
        return errorCollector;
    }

    /**
     * 设置包含列
     */
    public ExcelReadConfig includeColumns(String... columns) {
        this.includeColumns = Set.of(columns);
        return this;
    }

    /**
     * 设置排除列
     */
    public ExcelReadConfig excludeColumns(String... columns) {
        this.excludeColumns = Set.of(columns);
        return this;
    }

    /**
     * 设置重复检查字段
     */
    public ExcelReadConfig duplicateCheckFields(String... fields) {
        this.duplicateCheckFields = Set.of(fields);
        this.enableDuplicateCheck = true;
        return this;
    }

    /**
     * 启用快速失败
     */
    public ExcelReadConfig enableFailFast() {
        this.failFast = true;
        this.maxErrorCount = 1;
        return this;
    }

    /**
     * 启用异步处理
     */
    public ExcelReadConfig enableAsync() {
        this.enableAsync = true;
        return this;
    }

    /**
     * 启用进度监控
     */
    public ExcelReadConfig enableProgressMonitor() {
        this.enableProgressMonitor = true;
        return this;
    }

    /**
     * 设置自定义属性
     */
    public ExcelReadConfig setCustomProperty(String key, Object value) {
        if (customProperties == null) {
            customProperties = new java.util.HashMap<>();
        }
        customProperties.put(key, value);
        return this;
    }

    /**
     * 获取自定义属性
     */
    public <T> T getCustomProperty(String key, Class<T> type) {
        if (customProperties == null) {
            return null;
        }
        Object value = customProperties.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    /**
     * 验证配置有效性
     */
    public void validate() {
        validateNumericParameters();
        validateColumnLists();
    }
    
    /**
     * 验证数值参数
     */
    private void validateNumericParameters() {
        if (batchSize != null && batchSize <= 0) {
            throw new IllegalArgumentException("批量大小必须大于0");
        }
        
        if (headerRowNumber != null && headerRowNumber < 0) {
            throw new IllegalArgumentException("头部行号不能为负数");
        }
        
        if (maxErrorCount != null && maxErrorCount < 0) {
            throw new IllegalArgumentException("最大错误数量不能为负数");
        }
        
        if (timeoutMs != null && timeoutMs <= 0) {
            throw new IllegalArgumentException("超时时间必须大于0");
        }
        
        if (progressUpdateInterval != null && progressUpdateInterval <= 0) {
            throw new IllegalArgumentException("进度更新间隔必须大于0");
        }
    }
    
    /**
     * 验证列列表
     */
    private void validateColumnLists() {
        if (includeColumns != null && excludeColumns != null) {
            for (String column : includeColumns) {
                if (excludeColumns.contains(column)) {
                    throw new IllegalArgumentException("列 '" + column + "' 不能同时在包含和排除列表中");
                }
            }
        }
    }
} 