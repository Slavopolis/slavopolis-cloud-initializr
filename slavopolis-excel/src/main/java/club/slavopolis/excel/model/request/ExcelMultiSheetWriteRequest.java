package club.slavopolis.excel.model.request;

import club.slavopolis.excel.model.config.ExcelWriteConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel多Sheet写入请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelMultiSheetWriteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 输出流
     */
    private transient OutputStream outputStream;

    /**
     * 文件路径（与outputStream二选一）
     */
    private String filePath;

    /**
     * 多Sheet数据
     */
    private List<SheetData> sheetDataList;

    /**
     * 全局写入配置（可被Sheet级配置覆盖）
     */
    private ExcelWriteConfig globalConfig;

    /**
     * 进度回调（可选）
     */
    private ExcelWriteRequest.ProgressCallback progressCallback;

    /**
     * 请求上下文（用于传递额外参数）
     */
    private transient Map<String, Object> context;

    /**
     * 请求ID（用于异步处理追踪）
     */
    private String requestId;

    /**
     * 请求描述
     */
    private String description;

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overwrite = true;

    /**
     * Sheet数据模型
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SheetData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Sheet名称
         */
        private String sheetName;

        /**
         * Sheet数据
         */
        private transient List<?> data;

        /**
         * 数据类型
         */
        private Class<?> dataClass;

        /**
         * Sheet级配置（可选，会覆盖全局配置）
         */
        private ExcelWriteConfig config;

        /**
         * 创建Sheet数据
         */
        public static SheetData of(String sheetName, List<?> data) {
            return SheetData.builder()
                    .sheetName(sheetName)
                    .data(data)
                    .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                    .build();
        }

        /**
         * 创建带配置的Sheet数据
         */
        public static SheetData of(String sheetName, List<?> data, ExcelWriteConfig config) {
            return SheetData.builder()
                    .sheetName(sheetName)
                    .data(data)
                    .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                    .config(config)
                    .build();
        }

        /**
         * 获取数据数量
         */
        public int getDataSize() {
            return data != null ? data.size() : 0;
        }

        /**
         * 检查是否有数据
         */
        public boolean hasData() {
            return data != null && !data.isEmpty();
        }

        /**
         * 获取有效的配置（优先使用Sheet级配置）
         */
        public ExcelWriteConfig getEffectiveConfig(ExcelWriteConfig globalConfig) {
            if (config != null) {
                return config;
            }
            return globalConfig != null ? globalConfig : ExcelWriteConfig.defaultConfig();
        }
    }

    /**
     * 创建简单多Sheet写入请求
     *
     * @param outputStream   输出流
     * @param sheetDataList  Sheet数据列表
     * @return 多Sheet写入请求
     */
    public static ExcelMultiSheetWriteRequest of(OutputStream outputStream, List<SheetData> sheetDataList) {
        return ExcelMultiSheetWriteRequest.builder()
                .outputStream(outputStream)
                .sheetDataList(sheetDataList)
                .globalConfig(ExcelWriteConfig.defaultConfig())
                .build();
    }

    /**
     * 创建文件路径多Sheet写入请求
     *
     * @param filePath      文件路径
     * @param sheetDataList Sheet数据列表
     * @return 多Sheet写入请求
     */
    public static ExcelMultiSheetWriteRequest ofFile(String filePath, List<SheetData> sheetDataList) {
        return ExcelMultiSheetWriteRequest.builder()
                .filePath(filePath)
                .sheetDataList(sheetDataList)
                .globalConfig(ExcelWriteConfig.defaultConfig())
                .build();
    }

    /**
     * 创建带全局配置的多Sheet写入请求
     *
     * @param outputStream  输出流
     * @param sheetDataList Sheet数据列表
     * @param globalConfig  全局配置
     * @return 多Sheet写入请求
     */
    public static ExcelMultiSheetWriteRequest of(OutputStream outputStream, List<SheetData> sheetDataList, ExcelWriteConfig globalConfig) {
        return ExcelMultiSheetWriteRequest.builder()
                .outputStream(outputStream)
                .sheetDataList(sheetDataList)
                .globalConfig(globalConfig)
                .build();
    }

    /**
     * 设置进度回调
     *
     * @param callback 进度回调
     * @return 当前请求对象
     */
    public ExcelMultiSheetWriteRequest withProgressCallback(ExcelWriteRequest.ProgressCallback callback) {
        this.progressCallback = callback;
        return this;
    }

    /**
     * 设置请求描述
     *
     * @param description 描述
     * @return 当前请求对象
     */
    public ExcelMultiSheetWriteRequest withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置上下文参数
     *
     * @param key   参数key
     * @param value 参数值
     * @return 当前请求对象
     */
    public ExcelMultiSheetWriteRequest withContext(String key, Object value) {
        if (this.context == null) {
            this.context = new java.util.HashMap<>();
        }
        this.context.put(key, value);
        return this;
    }

    /**
     * 设置请求ID
     *
     * @param requestId 请求ID
     * @return 当前请求对象
     */
    public ExcelMultiSheetWriteRequest withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置是否覆盖文件
     *
     * @param overwrite 是否覆盖
     * @return 当前请求对象
     */
    public ExcelMultiSheetWriteRequest withOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    /**
     * 获取上下文参数
     *
     * @param key 参数key
     * @param <V> 值类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <V> V getContextValue(String key) {
        return context != null ? (V) context.get(key) : null;
    }

    /**
     * 获取上下文参数（带默认值）
     *
     * @param key          参数key
     * @param defaultValue 默认值
     * @param <V>          值类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <V> V getContextValue(String key, V defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        V value = (V) context.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 检查是否有进度回调
     *
     * @return 是否有进度回调
     */
    public boolean hasProgressCallback() {
        return progressCallback != null;
    }

    /**
     * 验证请求参数
     */
    public void validate() {
        validateOutputStreamAndFilePath();
        validateSheetDataListNotEmpty();

        Set<String> sheetNames = new HashSet<>();
        for (SheetData sheetData : sheetDataList) {
            validateSheetName(sheetData, sheetNames);
            validateSheetHasData(sheetData);
        }

        validateGlobalConfig();
        validateSheetConfigs();
    }

    /**
     * 验证输出流和文件路径
     */
    private void validateOutputStreamAndFilePath() {
        if (outputStream == null && filePath == null) {
            throw new IllegalArgumentException("outputStream和filePath不能同时为空");
        }
        if (outputStream != null && filePath != null) {
            throw new IllegalArgumentException("outputStream和filePath不能同时设置");
        }
    }

    /**
     * 验证Sheet数据列表是否为空
     */
    private void validateSheetDataListNotEmpty() {
        if (sheetDataList == null || sheetDataList.isEmpty()) {
            throw new IllegalArgumentException("sheetDataList不能为空");
        }
    }

    /**
     * 验证Sheet名称是否重复
     *
     * @param sheetData  Sheet数据
     * @param sheetNames 已存在的Sheet名称集合
     */
    private void validateSheetName(SheetData sheetData, Set<String> sheetNames) {
        String sheetName = sheetData.getSheetName();
        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sheet名称不能为空");
        }
        if (!sheetNames.add(sheetName)) {
            throw new IllegalArgumentException("Sheet名称不能重复: " + sheetName);
        }
    }

    /**
     * 验证Sheet是否有数据
     *
     * @param sheetData Sheet数据
     */
    private void validateSheetHasData(SheetData sheetData) {
        if (!sheetData.hasData()) {
            throw new IllegalArgumentException("Sheet '" + sheetData.getSheetName() + "' 没有数据");
        }
    }

    /**
     * 验证全局配置
     */
    private void validateGlobalConfig() {
        if (globalConfig == null) {
            globalConfig = ExcelWriteConfig.defaultConfig();
        } else {
            globalConfig.validate();
        }
    }

    /**
     * 验证Sheet级配置
     */
    private void validateSheetConfigs() {
        for (SheetData sheetData : sheetDataList) {
            if (sheetData.getConfig() != null) {
                sheetData.getConfig().validate();
            }
        }
    }

    /**
     * 获取全局配置（确保不为空）
     *
     * @return 全局配置
     */
    public ExcelWriteConfig getGlobalConfigOrDefault() {
        return globalConfig != null ? globalConfig : ExcelWriteConfig.defaultConfig();
    }

    /**
     * 获取总数据数量
     *
     * @return 总数据数量
     */
    public int getTotalDataSize() {
        if (sheetDataList == null) {
            return 0;
        }
        return sheetDataList.stream()
                .mapToInt(SheetData::getDataSize)
                .sum();
    }

    /**
     * 获取Sheet数量
     *
     * @return Sheet数量
     */
    public int getSheetCount() {
        return sheetDataList != null ? sheetDataList.size() : 0;
    }

    /**
     * 获取所有Sheet名称
     *
     * @return Sheet名称列表
     */
    public List<String> getSheetNames() {
        if (sheetDataList == null) {
            return java.util.Collections.emptyList();
        }
        return sheetDataList.stream()
                .map(SheetData::getSheetName)
                .toList();
    }
} 