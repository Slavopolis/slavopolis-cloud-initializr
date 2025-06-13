package club.slavopolis.excel.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.excel.enums.ExcelErrorCode;
import club.slavopolis.excel.exception.ExcelException;
import club.slavopolis.excel.model.ExcelError;
import lombok.extern.slf4j.Slf4j;
/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel错误收集器，用于收集和管理Excel处理过程中的错误信息
 */
@Slf4j
public class ExcelErrorCollector {

    /**
     * 错误列表（线程安全）
     */
    private final List<ExcelError> errors = Collections.synchronizedList(new ArrayList<>());

    /**
     * 最大错误数量限制
     */
    private final int maxErrors;

    /**
     * 是否快速失败模式
     */
    private final boolean failFast;

    /**
     * 错误计数器
     */
    private final AtomicInteger errorCount = new AtomicInteger(0);

    /**
     * 致命错误计数器
     */
    private final AtomicInteger fatalErrorCount = new AtomicInteger(0);

    /**
     * 按错误类型分组的计数器
     */
    private final Map<ExcelError.ErrorType, AtomicInteger> errorTypeCounters = new ConcurrentHashMap<>();

    /**
     * 创建错误收集器（静态工厂方法）
     *
     * @param failFast 是否快速失败模式
     * @return 错误收集器实例
     */
    public static ExcelErrorCollector create(boolean failFast) {
        return new ExcelErrorCollector(failFast, 1000);
    }

    /**
     * 创建错误收集器（静态工厂方法）
     *
     * @param failFast  是否快速失败模式
     * @param maxErrors 最大错误数量
     * @return 错误收集器实例
     */
    public static ExcelErrorCollector create(boolean failFast, int maxErrors) {
        return new ExcelErrorCollector(failFast, maxErrors);
    }

    /**
     * 默认构造函数（非快速失败模式，最大1000个错误）
     */
    public ExcelErrorCollector() {
        this(false, 1000);
    }

    /**
     * 构造函数
     *
     * @param failFast  是否快速失败模式
     * @param maxErrors 最大错误数量
     */
    public ExcelErrorCollector(boolean failFast, int maxErrors) {
        this.failFast = failFast;
        this.maxErrors = maxErrors;
        
        // 初始化错误类型计数器
        for (ExcelError.ErrorType errorType : ExcelError.ErrorType.values()) {
            errorTypeCounters.put(errorType, new AtomicInteger(0));
        }
    }

    /**
     * 添加错误
     *
     * @param error 错误信息
     * @throws ExcelException 如果是快速失败模式且为致命错误
     */
    public void addError(ExcelError error) {
        if (error == null) {
            return;
        }

        // 检查是否超出最大错误数量
        if (errorCount.get() >= maxErrors) {
            log.warn("Excel错误数量已达到最大限制: {}", maxErrors);
            return;
        }

        // 添加错误到列表
        errors.add(error);
        errorCount.incrementAndGet();

        // 更新错误类型计数器
        errorTypeCounters.get(error.getErrorType()).incrementAndGet();

        // 处理致命错误
        if (Boolean.TRUE.equals(error.getFatal())) {
            fatalErrorCount.incrementAndGet();
            log.error("Excel处理遇到致命错误: {}", error.getFullDescription());
            
            if (failFast) {
                throw new ExcelException(ExcelErrorCode.UNKNOWN_ERROR, "Excel处理致命错误: " + error.getMessage());
            }
        } else {
            log.warn("Excel处理遇到错误: {}", error.getFullDescription());
        }

        // 快速失败模式检查
        if (failFast && !errors.isEmpty()) {
            throw new ExcelException(ExcelErrorCode.DATA_VALIDATION_FAILED, "Excel处理错误: " + error.getMessage());
        }
    }

    /**
     * 添加简单错误
     *
     * @param rowNum    行号
     * @param fieldName 字段名
     * @param message   错误消息
     */
    public void addError(Integer rowNum, String fieldName, String message) {
        addError(ExcelError.of(rowNum, fieldName, message));
    }

    /**
     * 添加数据验证错误
     *
     * @param rowNum        行号
     * @param fieldName     字段名
     * @param message       错误消息
     * @param originalValue 原始值
     */
    public void addValidationError(Integer rowNum, String fieldName, String message, String originalValue) {
        addError(ExcelError.validationError(rowNum, fieldName, message, originalValue));
    }

    /**
     * 添加类型转换错误
     *
     * @param rowNum        行号
     * @param fieldName     字段名
     * @param originalValue 原始值
     * @param expectedValue 期望值
     */
    public void addTypeConversionError(Integer rowNum, String fieldName, String originalValue, String expectedValue) {
        addError(ExcelError.typeConversionError(rowNum, fieldName, originalValue, expectedValue));
    }

    /**
     * 添加必填字段为空错误
     *
     * @param rowNum    行号
     * @param fieldName 字段名
     */
    public void addRequiredFieldEmptyError(Integer rowNum, String fieldName) {
        addError(ExcelError.requiredFieldEmpty(rowNum, fieldName));
    }

    /**
     * 添加业务规则错误
     *
     * @param rowNum    行号
     * @param fieldName 字段名
     * @param message   错误消息
     * @param data      相关数据
     */
    public void addBusinessRuleError(Integer rowNum, String fieldName, String message, Object data) {
        addError(ExcelError.businessRuleError(rowNum, fieldName, message, data));
    }

    /**
     * 添加致命错误
     *
     * @param message 错误消息
     * @param detail  详细描述
     */
    public void addFatalError(String message, String detail) {
        addError(ExcelError.fatalError(message, detail));
    }

    /**
     * 添加致命错误（重载方法）
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param detail    详细描述
     */
    public void addFatalError(ExcelErrorCode errorCode, String message, String detail) {
        addError(ExcelError.builder()
                .errorType(ExcelError.ErrorType.FATAL_ERROR)
                .message(message)
                .detail(detail)
                .fatal(true)
                .build());
    }

    /**
     * 添加数据处理错误
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param rowNum    行号
     * @param columnNum 列号
     */
    public void addDataError(ExcelErrorCode errorCode, String message, int rowNum, int columnNum) {
        addError(ExcelError.builder()
                .errorType(ExcelError.ErrorType.DATA_ERROR)
                .message(message)
                .rowNum(rowNum)
                .columnIndex(columnNum)
                .fatal(false)
                .build());
    }

    /**
     * 添加系统错误
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public void addSystemError(ExcelErrorCode errorCode, String message) {
        addError(ExcelError.builder()
                .errorType(ExcelError.ErrorType.SYSTEM_ERROR)
                .message(message)
                .fatal(false)
                .build());
    }

    /**
     * 检查是否有错误
     *
     * @return true if has errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * 检查是否有致命错误
     *
     * @return true if has fatal errors
     */
    public boolean hasFatalErrors() {
        return fatalErrorCount.get() > 0;
    }

    /**
     * 获取所有错误（只读）
     *
     * @return 错误列表
     */
    public List<ExcelError> getErrors() {
        return List.copyOf(errors);
    }

    /**
     * 获取错误总数
     *
     * @return 错误总数
     */
    public int getErrorCount() {
        return errorCount.get();
    }

    /**
     * 获取致命错误数量
     *
     * @return 致命错误数量
     */
    public int getFatalErrorCount() {
        return fatalErrorCount.get();
    }

    /**
     * 获取指定类型的错误数量
     *
     * @param errorType 错误类型
     * @return 错误数量
     */
    public int getErrorCount(ExcelError.ErrorType errorType) {
        return errorTypeCounters.get(errorType).get();
    }

    /**
     * 获取按错误类型分组的错误
     *
     * @return 按错误类型分组的错误Map
     */
    public Map<ExcelError.ErrorType, List<ExcelError>> getErrorsByType() {
        return errors.stream()
                .collect(Collectors.groupingBy(ExcelError::getErrorType));
    }

    /**
     * 获取指定行的错误
     *
     * @param rowNum 行号
     * @return 该行的错误列表
     */
    public List<ExcelError> getErrorsForRow(Integer rowNum) {
        if (rowNum == null) {
            return Collections.emptyList();
        }
        
        return errors.stream()
                .filter(error -> rowNum.equals(error.getRowNum()))
                .toList();
    }

    /**
     * 获取指定字段的错误
     *
     * @param fieldName 字段名
     * @return 该字段的错误列表
     */
    public List<ExcelError> getErrorsForField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return Collections.emptyList();
        }
        
        return errors.stream()
                .filter(error -> fieldName.equals(error.getFieldName()))
                .toList();
    }

    /**
     * 清空所有错误
     */
    public void clear() {
        errors.clear();
        errorCount.set(0);
        fatalErrorCount.set(0);
        errorTypeCounters.values().forEach(counter -> counter.set(0));
    }

    /**
     * 获取错误摘要信息
     *
     * @return 错误摘要
     */
    public String getErrorSummary() {
        if (!hasErrors()) {
            return "无错误";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("错误总数: ").append(errorCount.get());
        
        if (hasFatalErrors()) {
            summary.append(", 致命错误: ").append(fatalErrorCount.get());
        }

        // 按错误类型统计
        for (Map.Entry<ExcelError.ErrorType, AtomicInteger> entry : errorTypeCounters.entrySet()) {
            int count = entry.getValue().get();
            if (count > 0) {
                summary
                        .append(CommonConstants.COMMA)
                        .append(CommonConstants.SPACE)
                        .append(entry.getKey())
                        .append(CommonConstants.CACHE_KEY_SEPARATOR)
                        .append(CommonConstants.SPACE)
                        .append(count);
            }
        }

        return summary.toString();
    }

    /**
     * 获取前N个错误的详细描述
     *
     * @param limit 限制数量
     * @return 错误详细描述列表
     */
    public List<String> getTopErrorDescriptions(int limit) {
        return errors.stream()
                .limit(limit)
                .map(ExcelError::getFullDescription)
                .toList();
    }

    /**
     * 检查是否可以继续处理
     *
     * @return true表示可以继续处理，false表示应该停止
     */
    public boolean canContinue() {
        // 如果有致命错误，不能继续
        return !hasFatalErrors()
                // 如果是快速失败模式且有错误，不能继续
                && !(failFast && hasErrors())
                // 如果错误数量达到上限，不能继续
                && errorCount.get() < maxErrors;
    }

    /**
     * 判断是否超过错误阈值
     *
     * @param threshold 错误阈值
     * @return true表示超过阈值
     */
    public boolean exceedsThreshold(int threshold) {
        return errorCount.get() > threshold;
    }
} 