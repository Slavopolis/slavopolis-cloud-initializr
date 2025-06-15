package club.slavopolis.infrastructure.integration.excel.model.metadata;

import java.lang.reflect.Field;

import club.slavopolis.infrastructure.integration.excel.core.converter.ExcelDataConverter;
import lombok.Builder;
import lombok.Data;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel字段元数据
 */
@Data
@Builder
public class ExcelFieldMeta {

    /**
     * 字段反射对象
     */
    private Field field;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private Class<?> fieldType;

    /**
     * 列标题
     */
    private String columnTitle;

    /**
     * 列索引
     */
    private int columnIndex;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 转换器类
     */
    @SuppressWarnings("rawtypes")
    private Class<? extends ExcelDataConverter> converterClass;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 数字格式
     */
    private String numberFormat;

    /**
     * 最大长度
     */
    private int maxLength;

    /**
     * 最小长度
     */
    private int minLength;

    /**
     * 正则表达式
     */
    private String pattern;

    /**
     * 验证错误消息
     */
    private String message;

    /**
     * 列宽度
     */
    private int width;

    /**
     * 单元格样式
     */
    private String cellStyle;

    /**
     * 是否自动调整列宽
     */
    private boolean autoWidth;

    /**
     * 排序序号
     */
    private int order;

    /**
     * 是否有效的列索引
     */
    public boolean hasValidColumnIndex() {
        return columnIndex >= 0;
    }

    /**
     * 是否有自定义转换器
     */
    public boolean hasCustomConverter() {
        return converterClass != null && !converterClass.equals(ExcelDataConverter.class);
    }

    /**
     * 是否有验证规则
     */
    public boolean hasValidation() {
        return required || 
               maxLength < Integer.MAX_VALUE || 
               minLength > 0 || 
               (pattern != null && !pattern.isEmpty());
    }

    /**
     * 获取有效的列标题
     */
    public String getEffectiveColumnTitle() {
        return columnTitle != null && !columnTitle.isEmpty() ? columnTitle : fieldName;
    }

    /**
     * 获取有效的错误消息
     */
    public String getEffectiveMessage() {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        return "字段[" + fieldName + "]验证失败";
    }

    @Override
    public String toString() {
        return String.format("ExcelFieldMeta{fieldName='%s', columnTitle='%s', columnIndex=%d, required=%s}", 
            fieldName, columnTitle, columnIndex, required);
    }
} 