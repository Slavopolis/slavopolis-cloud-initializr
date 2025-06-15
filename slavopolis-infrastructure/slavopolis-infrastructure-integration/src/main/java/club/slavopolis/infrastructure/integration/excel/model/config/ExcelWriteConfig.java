package club.slavopolis.infrastructure.integration.excel.model.config;

import java.util.Set;

import club.slavopolis.infrastructure.integration.excel.enums.ExcelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel写入配置模型
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExcelWriteConfig {

    /**
     * 默认Sheet名称
     */
    public static final String DEFAULT_SHEET_NAME = "Sheet1";

    /**
     * Sheet名称
     */
    private String sheetName = DEFAULT_SHEET_NAME;

    /**
     * Excel文件类型
     */
    private ExcelTypeEnum excelType = ExcelTypeEnum.XLSX;

    /**
     * 是否包含头部
     */
    private Boolean includeHeader = true;

    /**
     * 是否自动调整列宽
     */
    private Boolean autoWidth = true;

    /**
     * 批量写入大小
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
     * 是否启用内存优化模式（大数据量时建议开启）
     */
    private Boolean memoryOptimized = true;

    /**
     * 写入超时时间（毫秒）: 默认5分钟
     */
    private Long timeoutMs = 300000L;

    /**
     * 是否启用异步写入
     */
    private Boolean enableAsync = false;

    /**
     * 异步写入的回调线程数
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
     * 是否冻结头部行
     */
    private Boolean freezeHeader = false;

    /**
     * 冻结的行数
     */
    private Integer freezeRowCount = 1;

    /**
     * 冻结的列数
     */
    private Integer freezeColumnCount = 0;

    /**
     * 是否启用数据验证
     */
    private Boolean enableDataValidation = false;

    /**
     * 最大行数限制
     */
    private Integer maxRows = 1000000;

    /**
     * 最大列数限制
     */
    private Integer maxColumns = 256;

    /**
     * 是否启用样式
     */
    private Boolean enableStyle = true;

    /**
     * 头部样式名称
     */
    private String headerStyleName = "headerStyle";

    /**
     * 数据样式名称
     */
    private String dataStyleName = "dataStyle";

    /**
     * 是否启用筛选
     */
    private Boolean enableFilter = false;

    /**
     * 筛选开始行（从0开始）
     */
    private Integer filterStartRow = 0;

    /**
     * 筛选结束行（从0开始，-1表示最后一行）
     */
    private Integer filterEndRow = -1;

    /**
     * 自定义配置参数
     */
    private java.util.Map<String, Object> customProperties;

    /**
     * 创建默认配置
     */
    public static ExcelWriteConfig defaultConfig() {
        return ExcelWriteConfig.builder()
                .sheetName(DEFAULT_SHEET_NAME)
                .excelType(ExcelTypeEnum.XLSX)
                .includeHeader(true)
                .autoWidth(true)
                .batchSize(1000)
                .memoryOptimized(true)
                .timeoutMs(300000L)
                .enableAsync(false)
                .enableProgressMonitor(false)
                .progressUpdateInterval(1000)
                .freezeHeader(false)
                .freezeRowCount(1)
                .freezeColumnCount(0)
                .enableDataValidation(false)
                .maxRows(1000000)
                .maxColumns(256)
                .enableStyle(true)
                .headerStyleName("headerStyle")
                .dataStyleName("dataStyle")
                .enableFilter(false)
                .filterStartRow(0)
                .filterEndRow(-1)
                .build();
    }

    /**
     * 创建简单配置（无样式、无筛选、无冻结）
     */
    public static ExcelWriteConfig simpleConfig() {
        return defaultConfig().toBuilder()
                .enableStyle(false)
                .enableFilter(false)
                .freezeHeader(false)
                .autoWidth(false)
                .build();
    }

    /**
     * 创建大数据量配置
     */
    public static ExcelWriteConfig bigDataConfig() {
        return defaultConfig().toBuilder()
                .batchSize(5000)
                .memoryOptimized(true)
                .enableAsync(true)
                .enableProgressMonitor(true)
                .progressUpdateInterval(5000)
                .enableStyle(false)
                .autoWidth(false)
                .build();
    }

    /**
     * 创建高性能配置（关闭所有非必要功能）
     */
    public static ExcelWriteConfig performanceConfig() {
        return ExcelWriteConfig.builder()
                .sheetName(DEFAULT_SHEET_NAME)
                .excelType(ExcelTypeEnum.XLSX)
                .includeHeader(true)
                .autoWidth(false)
                .batchSize(10000)
                .memoryOptimized(true)
                .enableStyle(false)
                .enableFilter(false)
                .freezeHeader(false)
                .enableDataValidation(false)
                .enableProgressMonitor(false)
                .build();
    }

    /**
     * 设置包含列
     */
    public ExcelWriteConfig includeColumns(String... columns) {
        this.includeColumns = Set.of(columns);
        return this;
    }

    /**
     * 设置排除列
     */
    public ExcelWriteConfig excludeColumns(String... columns) {
        this.excludeColumns = Set.of(columns);
        return this;
    }

    /**
     * 启用异步写入
     */
    public ExcelWriteConfig enableAsync() {
        this.enableAsync = true;
        return this;
    }

    /**
     * 启用进度监控
     */
    public ExcelWriteConfig enableProgressMonitor() {
        this.enableProgressMonitor = true;
        return this;
    }

    /**
     * 启用冻结头部
     */
    public ExcelWriteConfig enableFreezeHeader() {
        this.freezeHeader = true;
        return this;
    }

    /**
     * 启用筛选
     */
    public ExcelWriteConfig enableFilter() {
        this.enableFilter = true;
        return this;
    }

    /**
     * 设置自定义属性
     */
    public ExcelWriteConfig setCustomProperty(String key, Object value) {
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
        validateBasicProperties();
        validateSizeProperties();
        validateFreezeProperties();
        validateColumnLists();
    }
    
    /**
     * 验证基本属性
     */
    private void validateBasicProperties() {
        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sheet名称不能为空");
        }
        
        if (timeoutMs != null && timeoutMs <= 0) {
            throw new IllegalArgumentException("超时时间必须大于0");
        }
    }
    
    /**
     * 验证大小相关属性
     */
    private void validateSizeProperties() {
        if (batchSize != null && batchSize <= 0) {
            throw new IllegalArgumentException("批量大小必须大于0");
        }
        
        if (progressUpdateInterval != null && progressUpdateInterval <= 0) {
            throw new IllegalArgumentException("进度更新间隔必须大于0");
        }
        
        if (maxRows != null && maxRows <= 0) {
            throw new IllegalArgumentException("最大行数必须大于0");
        }
        
        if (maxColumns != null && maxColumns <= 0) {
            throw new IllegalArgumentException("最大列数必须大于0");
        }
    }
    
    /**
     * 验证冻结相关属性
     */
    private void validateFreezeProperties() {
        if (freezeRowCount != null && freezeRowCount < 0) {
            throw new IllegalArgumentException("冻结行数不能为负数");
        }
        
        if (freezeColumnCount != null && freezeColumnCount < 0) {
            throw new IllegalArgumentException("冻结列数不能为负数");
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

    /**
     * 检查是否需要冻结
     */
    public boolean needFreeze() {
        return Boolean.TRUE.equals(freezeHeader) && 
               (freezeRowCount != null && freezeRowCount > 0) || 
               (freezeColumnCount != null && freezeColumnCount > 0);
    }

    /**
     * 检查是否需要筛选
     */
    public boolean needFilter() {
        return Boolean.TRUE.equals(enableFilter);
    }

    /**
     * 检查是否需要样式
     */
    public boolean needStyle() {
        return Boolean.TRUE.equals(enableStyle);
    }

    /**
     * 检查是否需要数据验证
     */
    public boolean needDataValidation() {
        return Boolean.TRUE.equals(enableDataValidation);
    }
} 