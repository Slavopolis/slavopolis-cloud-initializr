package club.slavopolis.excel.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel错误信息模型，用于收集和记录Excel处理过程中的详细错误
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误发生的行号（从1开始）
     */
    private Integer rowNum;

    /**
     * 错误发生的列名或字段名
     */
    private String fieldName;

    /**
     * 错误发生的列索引（从0开始）
     */
    private Integer columnIndex;

    /**
     * 错误发生的Sheet名称
     */
    private String sheetName;

    /**
     * 错误发生的Sheet索引（从0开始）
     */
    private Integer sheetIndex;

    /**
     * 错误类型
     */
    private ErrorType errorType;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 详细错误描述
     */
    private String detail;

    /**
     * 原始数据值
     */
    private String originalValue;

    /**
     * 期望的数据值或格式
     */
    private String expectedValue;

    /**
     * 错误发生的时间
     */
    private LocalDateTime occurTime;

    /**
     * 是否为致命错误（true表示致命错误，会中断处理）
     */
    private Boolean fatal;

    /**
     * 错误相关的额外数据
     */
    private transient Object data;

    /**
     * 错误类型枚举
     */
    public enum ErrorType {
        /**
         * 数据验证错误
         */
        VALIDATION_ERROR,

        /**
         * 数据类型转换错误
         */
        TYPE_CONVERSION_ERROR,

        /**
         * 必填字段为空
         */
        REQUIRED_FIELD_EMPTY,

        /**
         * 数据格式错误
         */
        FORMAT_ERROR,

        /**
         * 业务规则验证失败
         */
        BUSINESS_RULE_ERROR,

        /**
         * 重复数据
         */
        DUPLICATE_DATA,

        /**
         * 数据超出范围
         */
        OUT_OF_RANGE,

        /**
         * 数据处理错误
         */
        DATA_ERROR,

        /**
         * 致命错误
         */
        FATAL_ERROR,

        /**
         * 系统处理错误
         */
        SYSTEM_ERROR,

        /**
         * 其他错误
         */
        OTHER
    }

    /**
     * 创建简单错误信息的静态方法
     */
    public static ExcelError of(Integer rowNum, String fieldName, String message) {
        return ExcelError.builder()
                .rowNum(rowNum)
                .fieldName(fieldName)
                .message(message)
                .errorType(ErrorType.OTHER)
                .occurTime(LocalDateTime.now())
                .fatal(false)
                .build();
    }

    /**
     * 创建数据验证错误的静态方法
     */
    public static ExcelError validationError(Integer rowNum, String fieldName, String message, String originalValue) {
        return ExcelError.builder()
                .rowNum(rowNum)
                .fieldName(fieldName)
                .message(message)
                .originalValue(originalValue)
                .errorType(ErrorType.VALIDATION_ERROR)
                .occurTime(LocalDateTime.now())
                .fatal(false)
                .build();
    }

    /**
     * 创建类型转换错误的静态方法
     */
    public static ExcelError typeConversionError(Integer rowNum, String fieldName, String originalValue, String expectedValue) {
        return ExcelError.builder()
                .rowNum(rowNum)
                .fieldName(fieldName)
                .message("数据类型转换失败")
                .originalValue(originalValue)
                .expectedValue(expectedValue)
                .errorType(ErrorType.TYPE_CONVERSION_ERROR)
                .occurTime(LocalDateTime.now())
                .fatal(false)
                .build();
    }

    /**
     * 创建必填字段为空错误的静态方法
     */
    public static ExcelError requiredFieldEmpty(Integer rowNum, String fieldName) {
        return ExcelError.builder()
                .rowNum(rowNum)
                .fieldName(fieldName)
                .message("必填字段不能为空")
                .errorType(ErrorType.REQUIRED_FIELD_EMPTY)
                .occurTime(LocalDateTime.now())
                .fatal(false)
                .build();
    }

    /**
     * 创建业务规则验证失败错误的静态方法
     */
    public static ExcelError businessRuleError(Integer rowNum, String fieldName, String message, Object data) {
        return ExcelError.builder()
                .rowNum(rowNum)
                .fieldName(fieldName)
                .message(message)
                .data(data)
                .errorType(ErrorType.BUSINESS_RULE_ERROR)
                .occurTime(LocalDateTime.now())
                .fatal(false)
                .build();
    }

    /**
     * 创建致命错误的静态方法
     */
    public static ExcelError fatalError(String message, String detail) {
        return ExcelError.builder()
                .message(message)
                .detail(detail)
                .errorType(ErrorType.SYSTEM_ERROR)
                .occurTime(LocalDateTime.now())
                .fatal(true)
                .build();
    }

    /**
     * 获取格式化的错误位置信息
     */
    public String getLocationInfo() {
        StringBuilder location = new StringBuilder();
        
        if (sheetName != null) {
            location.append("工作表: ").append(sheetName);
        } else if (sheetIndex != null) {
            location.append("工作表: Sheet").append(sheetIndex + 1);
        }
        
        if (rowNum != null) {
            if (!location.isEmpty()) {
                location.append(", ");
            }
            location.append("行: ").append(rowNum);
        }
        
        if (fieldName != null) {
            if (!location.isEmpty()) {
                location.append(", ");
            }
            location.append("字段: ").append(fieldName);
        } else if (columnIndex != null) {
            if (!location.isEmpty()) {
                location.append(", ");
            }
            location.append("列: ").append((char) ('A' + columnIndex));
        }
        
        return location.toString();
    }

    /**
     * 获取完整的错误描述
     */
    public String getFullDescription() {
        StringBuilder description = new StringBuilder();
        
        String location = getLocationInfo();
        if (!location.isEmpty()) {
            description.append("[").append(location).append("] ");
        }
        
        description.append(message);
        
        if (detail != null && !detail.isEmpty()) {
            description.append(" - ").append(detail);
        }
        
        if (originalValue != null) {
            description.append(" (原始值: ").append(originalValue).append(")");
        }
        
        if (expectedValue != null) {
            description.append(" (期望值: ").append(expectedValue).append(")");
        }
        
        return description.toString();
    }
} 