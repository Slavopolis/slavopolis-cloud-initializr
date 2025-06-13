package club.slavopolis.excel.model.request;

import club.slavopolis.excel.core.processor.DataProcessor;
import club.slavopolis.excel.model.config.ExcelReadConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel 读取请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelReadRequest<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 输入流
     */
    private transient InputStream inputStream;

    /**
     * 文件路径（与inputStream二选一）
     */
    private String filePath;

    /**
     * 数据类型
     */
    private Class<T> dataClass;

    /**
     * 读取配置
     */
    private transient ExcelReadConfig config;

    /**
     * 数据处理器（可选）
     */
    private transient DataProcessor<T> processor;

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
     * 进度回调接口
     */
    public interface ProgressCallback {
        /**
         * 进度更新回调
         *
         * @param current 当前处理行数
         * @param total   总行数（可能为-1表示未知）
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
     * 创建简单读取请求
     *
     * @param inputStream 输入流
     * @param dataClass   数据类型
     * @param <T>         泛型类型
     * @return 读取请求
     */
    public static <T> ExcelReadRequest<T> of(InputStream inputStream, Class<T> dataClass) {
        return ExcelReadRequest.<T>builder()
                .inputStream(inputStream)
                .dataClass(dataClass)
                .config(ExcelReadConfig.defaultConfig())
                .build();
    }

    /**
     * 创建文件路径读取请求
     *
     * @param filePath  文件路径
     * @param dataClass 数据类型
     * @param <T>       泛型类型
     * @return 读取请求
     */
    public static <T> ExcelReadRequest<T> ofFile(String filePath, Class<T> dataClass) {
        return ExcelReadRequest.<T>builder()
                .filePath(filePath)
                .dataClass(dataClass)
                .config(ExcelReadConfig.defaultConfig())
                .build();
    }

    /**
     * 创建带配置的读取请求
     *
     * @param inputStream 输入流
     * @param dataClass   数据类型
     * @param config      读取配置
     * @param <T>         泛型类型
     * @return 读取请求
     */
    public static <T> ExcelReadRequest<T> of(InputStream inputStream, Class<T> dataClass, ExcelReadConfig config) {
        return ExcelReadRequest.<T>builder()
                .inputStream(inputStream)
                .dataClass(dataClass)
                .config(config)
                .build();
    }

    /**
     * 创建快速失败模式读取请求
     *
     * @param inputStream 输入流
     * @param dataClass   数据类型
     * @param <T>         泛型类型
     * @return 读取请求
     */
    public static <T> ExcelReadRequest<T> fastFail(InputStream inputStream, Class<T> dataClass) {
        return ExcelReadRequest.<T>builder()
                .inputStream(inputStream)
                .dataClass(dataClass)
                .config(ExcelReadConfig.failFastConfig())
                .build();
    }

    /**
     * 创建大数据量读取请求
     *
     * @param inputStream 输入流
     * @param dataClass   数据类型
     * @param <T>         泛型类型
     * @return 读取请求
     */
    public static <T> ExcelReadRequest<T> bigData(InputStream inputStream, Class<T> dataClass) {
        return ExcelReadRequest.<T>builder()
                .inputStream(inputStream)
                .dataClass(dataClass)
                .config(ExcelReadConfig.bigDataConfig())
                .build();
    }

    /**
     * 设置数据处理器
     *
     * @param processor 数据处理器
     * @return 当前请求对象
     */
    public ExcelReadRequest<T> withProcessor(DataProcessor<T> processor) {
        this.processor = processor;
        return this;
    }

    /**
     * 设置进度回调
     *
     * @param callback 进度回调
     * @return 当前请求对象
     */
    public ExcelReadRequest<T> withProgressCallback(ProgressCallback callback) {
        this.progressCallback = callback;
        return this;
    }

    /**
     * 设置请求描述
     *
     * @param description 描述
     * @return 当前请求对象
     */
    public ExcelReadRequest<T> withDescription(String description) {
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
    public ExcelReadRequest<T> withContext(String key, Object value) {
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
    public ExcelReadRequest<T> withRequestId(String requestId) {
        this.requestId = requestId;
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
     * 验证请求参数
     */
    public void validate() {
        if (inputStream == null && filePath == null) {
            throw new IllegalArgumentException("inputStream和filePath不能同时为空");
        }
        
        if (inputStream != null && filePath != null) {
            throw new IllegalArgumentException("inputStream和filePath不能同时设置");
        }
        
        if (dataClass == null) {
            throw new IllegalArgumentException("dataClass不能为空");
        }
        
        if (config == null) {
            config = ExcelReadConfig.defaultConfig();
        } else {
            config.validate();
        }
    }

    /**
     * 获取配置（确保不为空）
     *
     * @return 读取配置
     */
    public ExcelReadConfig getConfigOrDefault() {
        return config != null ? config : ExcelReadConfig.defaultConfig();
    }
} 