package club.slavopolis.infrastructure.integration.excel.annotation;

import club.slavopolis.common.core.constants.CommonConstants;

import java.lang.annotation.*;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel模板注解，用于标注模板填充相关配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelTemplate {

    /**
     * 模板路径
     */
    String value();

    /**
     * 模板名称（用于模板注册）
     */
    String name() default CommonConstants.EMPTY;

    /**
     * 填充方向（true=横向填充，false=纵向填充）
     */
    boolean horizontal() default false;

    /**
     * 起始填充行（从0开始）
     */
    int startRow() default 0;

    /**
     * 起始填充列（从0开始）
     */
    int startColumn() default 0;

    /**
     * 是否强制新建Sheet
     */
    boolean forceNewSheet() default false;

    /**
     * 是否启用公式计算
     */
    boolean enableFormula() default true;

    /**
     * 是否自动调整行高
     */
    boolean autoRowHeight() default false;

    /**
     * 是否自动调整列宽
     */
    boolean autoColumnWidth() default false;

    /**
     * 模板缓存时间（秒，0表示不缓存）
     */
    int cacheSeconds() default 300;

    /**
     * 描述信息
     */
    String description() default CommonConstants.EMPTY;
} 