package club.slavopolis.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel专用错误码枚举（25000-25999）
 */
@Getter
@AllArgsConstructor
public enum ExcelErrorCode {

    // ==================== 文件相关错误 25000-25099 ====================
    
    /**
     * 模板不存在
     */
    TEMPLATE_NOT_FOUND(25001, "Excel模板不存在"),
    
    /**
     * 无效的文件格式
     */
    INVALID_FILE_FORMAT(25002, "无效的Excel文件格式"),
    
    /**
     * 文件损坏或无法读取
     */
    FILE_CORRUPTED(25003, "Excel文件损坏或无法读取"),
    
    /**
     * 文件大小超过限制
     */
    FILE_SIZE_EXCEEDED(25004, "Excel文件大小超过限制"),
    
    /**
     * 文件为空或无有效数据
     */
    FILE_EMPTY(25005, "Excel文件为空或无有效数据"),

    // ==================== 数据处理错误 25100-25199 ====================
    
    /**
     * 列映射错误
     */
    COLUMN_MAPPING_ERROR(25101, "Excel列映射配置错误"),
    
    /**
     * 数据验证失败
     */
    DATA_VALIDATION_FAILED(25102, "Excel数据验证失败"),
    
    /**
     * 数据类型转换失败
     */
    DATA_TYPE_CONVERSION_ERROR(25103, "Excel数据类型转换失败"),
    
    /**
     * 必填字段为空
     */
    REQUIRED_FIELD_EMPTY(25104, "Excel必填字段为空"),
    
    /**
     * 数据超出最大行数限制
     */
    MAX_ROWS_EXCEEDED(25105, "Excel数据超出最大行数限制"),
    
    /**
     * 重复数据
     */
    DUPLICATE_DATA(25106, "Excel中存在重复数据"),

    // ==================== 处理流程错误 25200-25299 ====================
    
    /**
     * 批量处理异常
     */
    BATCH_PROCESS_ERROR(25201, "Excel批量处理异常"),
    
    /**
     * 异步处理失败
     */
    ASYNC_PROCESS_FAILED(25202, "Excel异步处理失败"),
    
    /**
     * 处理超时
     */
    PROCESS_TIMEOUT(25203, "Excel处理超时"),
    
    /**
     * 内存不足
     */
    OUT_OF_MEMORY(25204, "Excel处理时内存不足"),
    
    /**
     * 并发处理冲突
     */
    CONCURRENT_PROCESS_CONFLICT(25205, "Excel并发处理冲突"),

    // ==================== 模板相关错误 25300-25399 ====================
    
    /**
     * 模板变量不存在
     */
    TEMPLATE_VARIABLE_NOT_FOUND(25301, "Excel模板变量不存在"),
    
    /**
     * 模板格式错误
     */
    TEMPLATE_FORMAT_ERROR(25302, "Excel模板格式错误"),
    
    /**
     * 模板缓存失效
     */
    TEMPLATE_CACHE_EXPIRED(25303, "Excel模板缓存失效"),
    
    /**
     * 模板填充失败
     */
    TEMPLATE_FILL_FAILED(25304, "Excel模板填充失败"),

    // ==================== 配置错误 25400-25499 ====================
    
    /**
     * 配置参数无效
     */
    INVALID_CONFIGURATION(25401, "Excel配置参数无效"),
    
    /**
     * Sheet不存在
     */
    SHEET_NOT_FOUND(25402, "Excel工作表不存在"),
    
    /**
     * 列索引超出范围
     */
    COLUMN_INDEX_OUT_OF_RANGE(25403, "Excel列索引超出范围"),
    
    /**
     * 头部行配置错误
     */
    HEADER_ROW_CONFIG_ERROR(25404, "Excel头部行配置错误"),

    // ==================== 系统错误 25500-25599 ====================
    
    /**
     * IO操作失败
     */
    IO_OPERATION_FAILED(25501, "Excel文件IO操作失败"),
    
    /**
     * 临时文件创建失败
     */
    TEMP_FILE_CREATE_FAILED(25502, "Excel临时文件创建失败"),
    
    /**
     * 资源释放失败
     */
    RESOURCE_RELEASE_FAILED(25503, "Excel资源释放失败"),
    
    /**
     * 系统资源不足
     */
    SYSTEM_RESOURCE_INSUFFICIENT(25504, "系统资源不足，无法处理Excel"),

    // ==================== 业务规则错误 25600-25699 ====================
    
    /**
     * 业务规则验证失败
     */
    BUSINESS_RULE_VALIDATION_FAILED(25601, "Excel业务规则验证失败"),
    
    /**
     * 数据状态不允许导入
     */
    DATA_STATUS_NOT_ALLOWED(25602, "数据状态不允许导入Excel"),
    
    /**
     * 权限不足
     */
    INSUFFICIENT_PERMISSION(25603, "Excel操作权限不足"),
    
    /**
     * 操作频率超限
     */
    OPERATION_FREQUENCY_EXCEEDED(25604, "Excel操作频率超出限制"),

    // ==================== 注解处理错误 25700-25799 ====================
    
    /**
     * 注解未找到
     */
    ANNOTATION_NOT_FOUND(25701, "Excel相关注解未找到"),
    
    /**
     * 注解配置错误
     */
    ANNOTATION_CONFIG_ERROR(25702, "Excel注解配置错误"),
    
    /**
     * 字段注解映射失败
     */
    FIELD_ANNOTATION_MAPPING_FAILED(25703, "Excel字段注解映射失败"),
    
    /**
     * 注解解析失败
     */
    ANNOTATION_PARSE_FAILED(25704, "Excel注解解析失败"),
    
    /**
     * 注解验证失败
     */
    ANNOTATION_VALIDATION_FAILED(25705, "Excel注解验证失败"),
    
    /**
     * 转换器注解错误
     */
    CONVERTER_ANNOTATION_ERROR(25706, "Excel转换器注解错误"),
    
    /**
     * 元数据构建失败
     */
    METADATA_BUILD_FAILED(25707, "Excel元数据构建失败"),

    // ==================== 通用错误 25900-25999 ====================
    
    /**
     * 读取文件错误
     */
    READ_FILE_ERROR(25901, "Excel文件读取失败"),
    
    /**
     * 写入文件错误
     */
    WRITE_FILE_ERROR(25902, "Excel文件写入失败"),
    
    /**
     * 填充错误
     */
    FILL_ERROR(25903, "Excel模板填充失败"),
    
    /**
     * 数据处理错误
     */
    DATA_PROCESS_ERROR(25904, "Excel数据处理失败"),
    
    /**
     * 后处理错误
     */
    POST_PROCESS_ERROR(25905, "Excel后处理失败"),
    
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(25999, "Excel处理过程中发生未知错误");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 根据错误码查找枚举
     */
    public static ExcelErrorCode fromCode(int code) {
        for (ExcelErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }

    /**
     * 检查是否为Excel相关错误码
     */
    public static boolean isExcelErrorCode(int code) {
        return code >= 25000 && code <= 25999;
    }
} 