package club.slavopolis.infrastructure.integration.excel.core.converter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.core.util.DateTimeUtil;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: LocalDateTime类型转换器
 */
@Slf4j
public class LocalDateTimeConverter implements ExcelDataConverter<LocalDateTime> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DateTimeUtil.DATETIME_PATTERN);

    @Override
    public Class<LocalDateTime> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (cellData.getType() == CellDataTypeEnum.EMPTY) {
            return supportNullValue() ? getDefaultValue() : null;
        }

        String cellValue;
        if (cellData.getType() == CellDataTypeEnum.STRING) {
            cellValue = cellData.getStringValue();
        } else if (cellData.getType() == CellDataTypeEnum.NUMBER) {
            // 处理Excel数字类型（可能是日期的数字表示）
            BigDecimal numberValue = cellData.getNumberValue();
            if (numberValue != null) {
                // Excel 日期是从1900年1月1日开始的天数
                return convertExcelNumberToLocalDateTime(numberValue);
            }
            cellValue = cellData.toString();
        } else {
            cellValue = cellData.toString();
        }

        if (cellValue == null || cellValue.trim().isEmpty()) {
            return supportNullValue() ? getDefaultValue() : null;
        }

        try {
            // 预处理
            Object preprocessed = preProcess(cellValue.trim());
            
            // 尝试多种格式解析
            LocalDateTime result = parseWithMultipleFormats(preprocessed.toString());
            
            // 后处理
            return postProcess(result);
            
        } catch (Exception e) {
            log.warn("LocalDateTime转换失败: {}", formatErrorMessage(cellValue, e));
            return getDefaultValue();
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (value == null) {
            return new WriteCellData<>(CommonConstants.EMPTY);
        }

        try {
            // 预处理
            LocalDateTime preprocessed = (LocalDateTime) preProcess(value);
            
            // 格式化
            String formatted = preprocessed.format(getFormatter(contentProperty));
            
            // 后处理（这里不需要类型转换，直接返回格式化的字符串）
            return new WriteCellData<>(formatted);
            
        } catch (Exception e) {
            log.warn("LocalDateTime格式化失败: {}", formatErrorMessage(value, e));
            return new WriteCellData<>(value.toString());
        }
    }

    /**
     * 使用多种格式尝试解析
     */
    private LocalDateTime parseWithMultipleFormats(String value) {
        for (String pattern : DateTimeUtil.getCommonDateTimePatterns()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                if (pattern.contains("HH")) {
                    return LocalDateTime.parse(value, formatter);
                } else {
                    // 只有日期的情况，补充时间为00:00:00
                    return LocalDateTime.parse(value + DateTimeUtil.DEFAULT_TIME_SUFFIX,
                        DateTimeFormatter.ofPattern(pattern + DateTimeUtil.DEFAULT_TIME_SUFFIX.trim()));
                }
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }

        throw new DateTimeParseException("无法解析日期时间: " + value, value, 0);
    }

    /**
     * 将Excel数字转换为LocalDateTime
     */
    private LocalDateTime convertExcelNumberToLocalDateTime(BigDecimal excelNumber) {
        if (excelNumber == null) {
            return null;
        }
        
        try {
            // Excel日期基准：1900年1月1日（但Excel错误地认为1900年是闰年）
            // 所以需要减去2天来修正
            long days = excelNumber.longValue() - 2;
            long millisInDay = (long) ((excelNumber.doubleValue() - excelNumber.longValue()) * 24 * 60 * 60 * 1000);
            
            LocalDateTime baseDate = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
            return baseDate.plusDays(days).plusNanos(millisInDay * 1_000_000);
        } catch (Exception e) {
            log.warn("Excel数字转换为LocalDateTime失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取格式化器
     */
    private DateTimeFormatter getFormatter(ExcelContentProperty contentProperty) {
        // 可以从注解或配置中获取自定义格式
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null 
            && contentProperty.getDateTimeFormatProperty().getFormat() != null) {
            try {
                String customFormat = contentProperty.getDateTimeFormatProperty().getFormat();
                return DateTimeFormatter.ofPattern(customFormat);
            } catch (Exception e) {
                log.warn("自定义日期格式解析失败，使用默认格式: {}", e.getMessage());
            }
        }
        return DEFAULT_FORMATTER;
    }

    @Override
    public boolean validate(Object value) {
        if (value == null) {
            return supportNullValue();
        }
        
        if (value instanceof String) {
            try {
                parseWithMultipleFormats(value.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        return value instanceof LocalDateTime || value instanceof Date || value instanceof BigDecimal;
    }

    @Override
    public LocalDateTime getDefaultValue() {
        // 返回当前时间作为默认值，比 null 更有意义
        return LocalDateTime.now();
    }

    @Override
    public Object preProcess(Object value) {
        if (value instanceof String) {
            String str = value.toString().trim();
            // 清理常见的格式问题
            str = str.replace("T", " ");
            // 移除毫秒和时区
            str = str.replaceAll("\\.\\d{3}Z?$", "");
            return str;
        }
        return value;
    }

    @Override
    public boolean supportNullValue() {
        return ExcelDataConverter.super.supportNullValue();
    }
} 