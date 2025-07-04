package club.slavopolis.persistence.jdbc.exception;

import club.slavopolis.persistence.jdbc.enums.ExceptionCategory;

/**
 * 映射异常 - 结果集映射相关的异常，包括类型转换、字段映射异常等
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public class MappingException extends JdbcException {

    public MappingException(String message) {
        super(message, "MAPPING_ERROR", ExceptionCategory.MAPPING);
    }

    public MappingException(String message, Throwable cause) {
        super(message, "MAPPING_ERROR", ExceptionCategory.MAPPING, cause);
    }

    public MappingException(String message, String errorCode) {
        super(message, errorCode, ExceptionCategory.MAPPING);
    }

    public MappingException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, ExceptionCategory.MAPPING, cause);
    }

    /**
     * 创建字段映射异常
     */
    public static MappingException fieldMappingError(String fieldName, String targetType, Throwable cause) {
        return new MappingException(
            String.format("Failed to map field '%s' to type %s", fieldName, targetType),
            "FIELD_MAPPING_ERROR",
            cause
        );
    }

    /**
     * 创建类型转换异常
     */
    public static MappingException typeConversionError(Object value, Class<?> targetType, Throwable cause) {
        return new MappingException(
            String.format("Failed to convert value '%s' to type %s", value, targetType.getSimpleName()),
            "TYPE_CONVERSION_ERROR",
            cause
        );
    }

    /**
     * 创建行映射器创建异常
     */
    public static MappingException rowMapperCreationError(Class<?> targetType, Throwable cause) {
        return new MappingException(
            String.format("Failed to create row mapper for type %s", targetType.getSimpleName()),
            "ROW_MAPPER_CREATION_ERROR",
            cause
        );
    }

    /**
     * 创建必需字段缺失异常
     */
    public static MappingException requiredFieldMissingError(String fieldName, Class<?> targetType) {
        return new MappingException(
            String.format("Required field '%s' is missing for type %s", fieldName, targetType.getSimpleName()),
            "REQUIRED_FIELD_MISSING_ERROR"
        );
    }

    /**
     * 创建无效映射配置异常
     */
    public static MappingException invalidMappingConfiguration(String message) {
        return new MappingException(
            "Invalid mapping configuration: " + message,
            "INVALID_MAPPING_CONFIGURATION"
        );
    }
} 