package club.slavopolis.excel.model.request;

import club.slavopolis.excel.core.processor.DataProcessor;
import club.slavopolis.excel.model.config.ExcelWriteConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel写入请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelWriteRequest implements Serializable {

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
     * 写入数据列表
     */
    private transient List<?> data;

    /**
     * 数据类型
     */
    private Class<?> dataClass;

    /**
     * 写入配置
     */
    private ExcelWriteConfig config;

    /**
     * 数据处理器（可选）
     */
    private DataProcessor<?> processor;

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
     * 模板路径（可选，用于基于模板写入）
     */
    private String templatePath;

    /**
     * 是否覆盖已存在的文件
     */
    private Boolean overwrite = true;

    /**
     * 进度回调接口
     */
    public interface ProgressCallback {
        /**
         * 进度更新回调
         *
         * @param current 当前处理行数
         * @param total   总行数
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
     * 创建简单写入请求
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @return 写入请求
     */
    public static ExcelWriteRequest of(OutputStream outputStream, List<?> data) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.get(0).getClass() : Object.class)
                .config(ExcelWriteConfig.defaultConfig())
                .build();
    }

    /**
     * 创建文件路径写入请求
     *
     * @param filePath 文件路径
     * @param data     数据列表
     * @return 写入请求
     */
    public static ExcelWriteRequest ofFile(String filePath, List<?> data) {
        return ExcelWriteRequest.builder()
                .filePath(filePath)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                .config(ExcelWriteConfig.defaultConfig())
                .build();
    }

    /**
     * 创建带配置的写入请求
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @param config       写入配置
     * @return 写入请求
     */
    public static ExcelWriteRequest of(OutputStream outputStream, List<?> data, ExcelWriteConfig config) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                .config(config)
                .build();
    }

    /**
     * 创建简单配置写入请求
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @return 写入请求
     */
    public static ExcelWriteRequest simple(OutputStream outputStream, List<?> data) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                .config(ExcelWriteConfig.simpleConfig())
                .build();
    }

    /**
     * 创建大数据量写入请求
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @return 写入请求
     */
    public static ExcelWriteRequest bigData(OutputStream outputStream, List<?> data) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                .config(ExcelWriteConfig.bigDataConfig())
                .build();
    }

    /**
     * 创建高性能写入请求
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @return 写入请求
     */
    public static ExcelWriteRequest performance(OutputStream outputStream, List<?> data) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                .config(ExcelWriteConfig.performanceConfig())
                .build();
    }

    /**
     * 创建基于模板的写入请求
     *
     * @param outputStream 输出流
     * @param data         数据列表
     * @param templatePath 模板路径
     * @return 写入请求
     */
    public static ExcelWriteRequest template(OutputStream outputStream, List<?> data, String templatePath) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(data != null && !data.isEmpty() ? data.getFirst().getClass() : Object.class)
                .config(ExcelWriteConfig.defaultConfig())
                .templatePath(templatePath)
                .build();
    }

    /**
     * 设置数据处理器
     *
     * @param processor 数据处理器
     * @return 当前请求对象
     */
    public ExcelWriteRequest withProcessor(DataProcessor<?> processor) {
        this.processor = processor;
        return this;
    }

    /**
     * 设置进度回调
     *
     * @param callback 进度回调
     * @return 当前请求对象
     */
    public ExcelWriteRequest withProgressCallback(ProgressCallback callback) {
        this.progressCallback = callback;
        return this;
    }

    /**
     * 设置请求描述
     *
     * @param description 描述
     * @return 当前请求对象
     */
    public ExcelWriteRequest withDescription(String description) {
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
    public ExcelWriteRequest withContext(String key, Object value) {
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
    public ExcelWriteRequest withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 设置模板路径
     *
     * @param templatePath 模板路径
     * @return 当前请求对象
     */
    public ExcelWriteRequest withTemplate(String templatePath) {
        this.templatePath = templatePath;
        return this;
    }

    /**
     * 设置是否覆盖文件
     *
     * @param overwrite 是否覆盖
     * @return 当前请求对象
     */
    public ExcelWriteRequest withOverwrite(boolean overwrite) {
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
     * 检查是否有数据处理器
     *
     * @return 是否有处理器
     */
    public boolean hasProcessor() {
        return processor != null;
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
     * 检查是否基于模板
     *
     * @return 是否基于模板
     */
    public boolean isTemplateMode() {
        return templatePath != null && !templatePath.trim().isEmpty();
    }

    /**
     * 验证请求参数
     */
    public void validate() {
        if (outputStream == null && filePath == null) {
            throw new IllegalArgumentException("outputStream和filePath不能同时为空");
        }
        
        if (outputStream != null && filePath != null) {
            throw new IllegalArgumentException("outputStream和filePath不能同时设置");
        }
        
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("data不能为空");
        }
        
        if (config == null) {
            config = ExcelWriteConfig.defaultConfig();
        } else {
            config.validate();
        }
    }

    /**
     * 获取配置（确保不为空）
     *
     * @return 写入配置
     */
    public ExcelWriteConfig getConfigOrDefault() {
        return config != null ? config : ExcelWriteConfig.defaultConfig();
    }

    /**
     * 获取数据数量
     *
     * @return 数据数量
     */
    public int getDataSize() {
        return data != null ? data.size() : 0;
    }
} 