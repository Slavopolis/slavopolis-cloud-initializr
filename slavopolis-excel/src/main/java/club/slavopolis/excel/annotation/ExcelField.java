package club.slavopolis.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import club.slavopolis.excel.core.converter.ExcelDataConverter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel字段注解，用于标注字段与Excel列的映射关系
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelField {

    /**
     * 列标题
     */
    String value() default "";

    /**
     * 列索引（从0开始）
     */
    int index() default -1;

    /**
     * 是否必填
     */
    boolean required() default false;

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 数据转换器
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ExcelDataConverter> converter() default ExcelDataConverter.class;

    /**
     * 日期格式（针对日期类型）
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 数字格式（针对数字类型）
     */
    String numberFormat() default "";

    /**
     * 最大长度
     */
    int maxLength() default Integer.MAX_VALUE;

    /**
     * 最小长度
     */
    int minLength() default 0;

    /**
     * 正则表达式验证
     */
    String pattern() default "";

    /**
     * 验证错误消息
     */
    String message() default "";

    /**
     * 是否忽略该字段
     */
    boolean ignore() default false;

    /**
     * 列宽度（字符数）
     */
    int width() default -1;

    /**
     * 单元格样式
     */
    String cellStyle() default "";

    /**
     * 是否自动调整列宽
     */
    boolean autoWidth() default false;

    /**
     * 排序序号（用于控制列的显示顺序）
     */
    int order() default Integer.MAX_VALUE;
} 