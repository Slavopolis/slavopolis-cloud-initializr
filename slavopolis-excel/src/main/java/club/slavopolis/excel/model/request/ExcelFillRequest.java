package club.slavopolis.excel.model.request;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.constant.DateConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel填充请求模型，基于模板进行数据填充
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFillRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板输入流
     */
    private transient InputStream templateInputStream;

    /**
     * 模板文件路径（与templateInputStream二选一）
     */
    private String templatePath;

    /**
     * 输出流
     */
    private transient OutputStream outputStream;

    /**
     * 输出文件路径（与outputStream二选一）
     */
    private String outputPath;

    /**
     * 填充数据
     */
    private transient Object fillData;

    /**
     * 填充配置
     */
    private FillConfig config;

    /**
     * 进度回调（可选）
     */
    private transient ProgressCallback progressCallback;

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
     * 填充配置
     */
    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FillConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 是否启用横向填充
         */
        private Boolean horizontalFill = false;

        /**
         * 是否忽略空值
         */
        private Boolean ignoreNullValue = true;

        /**
         * 是否强制创建新的Sheet
         */
        private Boolean forceNewSheet = false;

        /**
         * 日期格式
         */
        private String dateFormat = "yyyy-MM-dd";

        /**
         * 数字格式
         */
        private String numberFormat;

        /**
         * 是否保留原始格式
         */
        private Boolean keepOriginalFormat = true;

        /**
         * 填充起始行（从0开始）
         */
        private Integer startRow = 0;

        /**
         * 填充起始列（从0开始）
         */
        private Integer startColumn = 0;

        /**
         * 是否启用公式计算
         */
        private Boolean autoCalculateFormula = true;

        /**
         * 自定义变量前缀
         */
        private String variablePrefix = "{";

        /**
         * 自定义变量后缀
         */
        private String variableSuffix = "}";

        /**
         * 是否启用图片填充
         */
        private Boolean enableImageFill = false;

        /**
         * 图片最大宽度（像素）
         */
        private Integer maxImageWidth = 200;

        /**
         * 图片最大高度（像素）
         */
        private Integer maxImageHeight = 200;

        /**
         * 创建默认配置
         */
        public static FillConfig defaultConfig() {
            return FillConfig.builder()
                    .horizontalFill(false)
                    .ignoreNullValue(true)
                    .forceNewSheet(false)
                    .dateFormat(DateConstants.DATE_PATTERN)
                    .keepOriginalFormat(true)
                    .startRow(0)
                    .startColumn(0)
                    .autoCalculateFormula(true)
                    .variablePrefix(CommonConstants.LEFT_BRACE)
                    .variableSuffix(CommonConstants.RIGHT_BRACE)
                    .enableImageFill(false)
                    .maxImageWidth(200)
                    .maxImageHeight(200)
                    .build();
        }

        /**
         * 创建横向填充配置
         */
        public static FillConfig horizontalConfig() {
            return defaultConfig().toBuilder()
                    .horizontalFill(true)
                    .build();
        }

        /**
         * 创建高性能配置（关闭公式计算和图片填充）
         */
        public static FillConfig performanceConfig() {
            return defaultConfig().toBuilder()
                    .autoCalculateFormula(false)
                    .enableImageFill(false)
                    .build();
        }
    }

    /**
     * 进度回调接口
     */
    public interface ProgressCallback {
        /**
         * 进度更新回调
         *
         * @param current 当前处理项数
         * @param total   总项数
         * @param percent 进度百分比（0-100）
         */
        void onProgress(long current, long total, double percent);

        /**
         * 阶段变更回调
         *
         * @param stage   当前阶段
         * @param message 阶段描述
         */
        default void onStageChange(String stage, String message) {
            // 默认空实现
        }
    }

    /**
     * 创建简单填充请求
     *
     * @param templateInputStream 模板输入流
     * @param outputStream       输出流
     * @param fillData           填充数据
     * @return 填充请求
     */
    public static ExcelFillRequest of(InputStream templateInputStream, OutputStream outputStream, Object fillData) {
        return ExcelFillRequest.builder()
                .templateInputStream(templateInputStream)
                .outputStream(outputStream)
                .fillData(fillData)
                .config(FillConfig.defaultConfig())
                .build();
    }

    /**
     * 创建文件路径填充请求
     *
     * @param templatePath 模板路径
     * @param outputPath   输出路径
     * @param fillData     填充数据
     * @return 填充请求
     */
    public static ExcelFillRequest ofFile(String templatePath, String outputPath, Object fillData) {
        return ExcelFillRequest.builder()
                .templatePath(templatePath)
                .outputPath(outputPath)
                .fillData(fillData)
                .config(FillConfig.defaultConfig())
                .build();
    }

    /**
     * 创建带配置的填充请求
     *
     * @param templateInputStream 模板输入流
     * @param outputStream       输出流
     * @param fillData           填充数据
     * @param config             填充配置
     * @return 填充请求
     */
    public static ExcelFillRequest of(InputStream templateInputStream, OutputStream outputStream, Object fillData, FillConfig config) {
        return ExcelFillRequest.builder()
                .templateInputStream(templateInputStream)
                .outputStream(outputStream)
                .fillData(fillData)
                .config(config)
                .build();
    }

    /**
     * 创建横向填充请求
     *
     * @param templateInputStream 模板输入流
     * @param outputStream       输出流
     * @param fillData           填充数据
     * @return 填充请求
     */
    public static ExcelFillRequest horizontal(InputStream templateInputStream, OutputStream outputStream, Object fillData) {
        return ExcelFillRequest.builder()
                .templateInputStream(templateInputStream)
                .outputStream(outputStream)
                .fillData(fillData)
                .config(FillConfig.horizontalConfig())
                .build();
    }

    /**
     * 创建高性能填充请求
     *
     * @param templateInputStream 模板输入流
     * @param outputStream       输出流
     * @param fillData           填充数据
     * @return 填充请求
     */
    public static ExcelFillRequest performance(InputStream templateInputStream, OutputStream outputStream, Object fillData) {
        return ExcelFillRequest.builder()
                .templateInputStream(templateInputStream)
                .outputStream(outputStream)
                .fillData(fillData)
                .config(FillConfig.performanceConfig())
                .build();
    }

    /**
     * 设置进度回调
     *
     * @param callback 进度回调
     * @return 当前请求对象
     */
    public ExcelFillRequest withProgressCallback(ProgressCallback callback) {
        this.progressCallback = callback;
        return this;
    }

    /**
     * 设置请求描述
     *
     * @param description 描述
     * @return 当前请求对象
     */
    public ExcelFillRequest withDescription(String description) {
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
    public ExcelFillRequest withContext(String key, Object value) {
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
    public ExcelFillRequest withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置是否覆盖文件
     *
     * @param overwrite 是否覆盖
     * @return 当前请求对象
     */
    public ExcelFillRequest withOverwrite(boolean overwrite) {
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
        if (templateInputStream == null && templatePath == null) {
            throw new IllegalArgumentException("templateInputStream和templatePath不能同时为空");
        }
        
        if (templateInputStream != null && templatePath != null) {
            throw new IllegalArgumentException("templateInputStream和templatePath不能同时设置");
        }
        
        if (outputStream == null && outputPath == null) {
            throw new IllegalArgumentException("outputStream和outputPath不能同时为空");
        }
        
        if (outputStream != null && outputPath != null) {
            throw new IllegalArgumentException("outputStream和outputPath不能同时设置");
        }
        
        if (fillData == null) {
            throw new IllegalArgumentException("fillData不能为空");
        }
        
        if (config == null) {
            config = FillConfig.defaultConfig();
        }
    }

    /**
     * 获取配置（确保不为空）
     *
     * @return 填充配置
     */
    public FillConfig getConfigOrDefault() {
        return config != null ? config : FillConfig.defaultConfig();
    }

    /**
     * 检查是否使用模板流
     *
     * @return 是否使用模板流
     */
    public boolean useTemplateStream() {
        return templateInputStream != null;
    }

    /**
     * 检查是否使用输出流
     *
     * @return 是否使用输出流
     */
    public boolean useOutputStream() {
        return outputStream != null;
    }
} 