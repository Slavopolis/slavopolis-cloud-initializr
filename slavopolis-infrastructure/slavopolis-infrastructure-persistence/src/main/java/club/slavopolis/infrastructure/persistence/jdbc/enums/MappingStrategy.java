package club.slavopolis.infrastructure.persistence.jdbc.enums;

/**
 * 映射策略枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public enum MappingStrategy {

    /**
     * 严格映射：字段名必须完全匹配
     */
    STRICT,

    /**
     * 宽松映射：忽略大小写
     */
    LOOSE,

    /**
     * 智能映射：下划线转驼峰
     */
    INTELLIGENT,

    /**
     * 自定义映射：使用自定义转换器
     */
    CUSTOM
}
