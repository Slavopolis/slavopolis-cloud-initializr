package club.slavopolis.infrastructure.integration.excel.core.processor;

import club.slavopolis.infrastructure.integration.excel.util.ExcelErrorCollector;

import java.util.List;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel数据处理器接口，用于自定义数据处理逻辑
 */
public interface DataProcessor<T> {

    /**
     * 批量处理数据
     *
     * @param dataList       待处理的数据列表
     * @param context        处理上下文
     * @param errorCollector 错误收集器
     * @return 处理结果
     */
    ProcessResult<T> process(List<T> dataList, ProcessContext context, ExcelErrorCollector errorCollector);

    /**
     * 单条数据处理（可选实现）
     *
     * @param data           单条数据
     * @param rowIndex       行索引
     * @param context        处理上下文
     * @param errorCollector 错误收集器
     * @return 处理后的数据
     */
    default T processOne(T data, int rowIndex, ProcessContext context, ExcelErrorCollector errorCollector) {
        return data;
    }

    /**
     * 数据验证（在处理前执行）
     *
     * @param data           待验证的数据
     * @param rowIndex       行索引
     * @param context        处理上下文
     * @param errorCollector 错误收集器
     * @return 验证是否通过
     */
    default boolean validate(T data, int rowIndex, ProcessContext context, ExcelErrorCollector errorCollector) {
        return true;
    }

    /**
     * 预处理钩子（在所有数据处理前执行一次）
     *
     * @param context 处理上下文
     */
    default void beforeProcess(ProcessContext context) {
        // 默认空实现
    }

    /**
     * 后处理钩子（在所有数据处理后执行一次）
     *
     * @param context        处理上下文
     * @param processedData  已处理的数据
     * @param errorCollector 错误收集器
     */
    default void afterProcess(ProcessContext context, List<T> processedData, ExcelErrorCollector errorCollector) {
        // 默认空实现
    }

    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取处理器版本
     *
     * @return 版本号
     */
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * 是否支持并行处理
     *
     * @return true表示支持并行处理
     */
    default boolean supportParallel() {
        return false;
    }

    /**
     * 获取建议的批量处理大小
     *
     * @return 批量大小
     */
    default int getSuggestedBatchSize() {
        return 1000;
    }
} 