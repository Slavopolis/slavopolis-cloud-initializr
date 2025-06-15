package club.slavopolis.infrastructure.integration.excel.core.converter;

import com.alibaba.excel.converters.Converter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel数据转换器接口，扩展EasyExcel的Converter功能
 */
public interface ExcelDataConverter<T> extends Converter<T> {

    /**
     * 获取转换器名称
     */
    default String getConverterName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取转换器版本
     */
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * 是否支持空值转换
     */
    default boolean supportNullValue() {
        return true;
    }

    /**
     * 转换失败时的默认值
     */
    default T getDefaultValue() {
        return null;
    }

    /**
     * 验证数据有效性
     */
    default boolean validate(Object value) {
        return true;
    }

    /**
     * 格式化错误消息
     */
    default String formatErrorMessage(Object value, Exception e) {
        return String.format("数据转换失败: %s -> %s, 错误: %s", 
            value, supportJavaTypeKey().getSimpleName(), e.getMessage());
    }

    /**
     * 转换前的预处理
     */
    default Object preProcess(Object value) {
        return value;
    }

    /**
     * 转换后的后处理
     */
    default T postProcess(T convertedValue) {
        return convertedValue;
    }
} 