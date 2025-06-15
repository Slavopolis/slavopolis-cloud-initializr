package club.slavopolis.infrastructure.persistence.jdbc.enums;

import lombok.Getter;

/**
 * 异常分类枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
public enum ExceptionCategory {

    /**
     * 通用异常
     */
    GENERAL("General"),

    /**
     * 系统异常
     */
    SYSTEM("System"),

    /**
     * 业务异常
     */
    BUSINESS("Business"),

    /**
     * 验证异常
     */
    VALIDATION("Validation"),

    /**
     * 安全异常
     */
    SECURITY("Security"),

    /**
     * 配置异常
     */
    CONFIGURATION("Configuration"),

    /**
     * 基础设施异常
     */
    INFRASTRUCTURE("Infrastructure"),

    /**
     * 数据访问异常
     */
    DATA_ACCESS("DataAccess"),

    /**
     * 事务异常
     */
    TRANSACTION("Transaction"),

    /**
     * 映射异常
     */
    MAPPING("Mapping");

    private final String description;

    ExceptionCategory(String description) {
        this.description = description;
    }

}
