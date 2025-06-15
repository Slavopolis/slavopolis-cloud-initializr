package club.slavopolis.infrastructure.integration.excel.annotation;

import club.slavopolis.common.core.constants.CommonConstants;

import java.lang.annotation.*;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel工作表注解，用于标注类与Excel Sheet的映射关系
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelSheet {

    /**
     * Sheet名称
     */
    String value() default CommonConstants.EMPTY;

    /**
     * Sheet索引（从0开始）
     */
    int index() default 0;

    /**
     * 标题行索引（从0开始）
     */
    int headerIndex() default 0;

    /**
     * 数据开始行索引（从0开始）
     */
    int dataStartIndex() default 1;

    /**
     * 是否包含标题行
     */
    boolean includeHeader() default true;

    /**
     * 是否忽略空行
     */
    boolean ignoreEmptyRow() default true;

    /**
     * 是否自动去除空格
     */
    boolean autoTrim() default true;

    /**
     * 最大读取行数（0表示不限制）
     */
    int maxRows() default 0;

    /**
     * 最大读取列数（0表示不限制）
     */
    int maxColumns() default 0;

    /**
     * 密码（如果Excel文件有密码保护）
     */
    String password() default CommonConstants.EMPTY;

    /**
     * 数据处理器类
     */
    Class<?> processor() default Void.class;

    /**
     * 是否启用数据验证
     */
    boolean enableValidation() default true;

    /**
     * 是否快速失败（遇到错误立即停止）
     */
    boolean failFast() default false;

    /**
     * 描述信息
     */
    String description() default CommonConstants.EMPTY;
} 