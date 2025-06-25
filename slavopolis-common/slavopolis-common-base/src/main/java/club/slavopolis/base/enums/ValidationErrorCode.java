package club.slavopolis.base.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 校验错误码枚举
 * 
 * 定义数据验证过程中可能出现的各种错误类型，
 * 为验证工具类提供统一的错误码标准
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/1/15
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ValidationErrorCode implements ErrorCode {

    // ================================ 基础验证错误 ================================
    
    /**
     * 值为空错误
     */
    VALUE_IS_NULL("VALIDATION_VALUE_IS_NULL", "值不能为空"),
    
    /**
     * 字符串为空错误
     */
    STRING_IS_EMPTY("VALIDATION_STRING_IS_EMPTY", "字符串不能为空"),
    
    /**
     * 集合为空错误
     */
    COLLECTION_IS_EMPTY("VALIDATION_COLLECTION_IS_EMPTY", "集合不能为空"),
    
    /**
     * 数组为空错误
     */
    ARRAY_IS_EMPTY("VALIDATION_ARRAY_IS_EMPTY", "数组不能为空"),

    // ================================ 长度验证错误 ================================
    
    /**
     * 字符串长度超出范围错误
     */
    STRING_LENGTH_OUT_OF_RANGE("VALIDATION_STRING_LENGTH_OUT_OF_RANGE", "字符串长度超出允许范围"),
    
    /**
     * 字符串长度过短错误
     */
    STRING_TOO_SHORT("VALIDATION_STRING_TOO_SHORT", "字符串长度过短"),
    
    /**
     * 字符串长度过长错误
     */
    STRING_TOO_LONG("VALIDATION_STRING_TOO_LONG", "字符串长度过长"),
    
    /**
     * 集合大小超出范围错误
     */
    COLLECTION_SIZE_OUT_OF_RANGE("VALIDATION_COLLECTION_SIZE_OUT_OF_RANGE", "集合大小超出允许范围"),
    
    /**
     * 集合大小过小错误
     */
    COLLECTION_TOO_SMALL("VALIDATION_COLLECTION_TOO_SMALL", "集合大小过小"),
    
    /**
     * 集合大小过大错误
     */
    COLLECTION_TOO_LARGE("VALIDATION_COLLECTION_TOO_LARGE", "集合大小过大"),

    // ================================ 格式验证错误 ================================
    
    /**
     * 邮箱格式错误
     */
    INVALID_EMAIL_FORMAT("VALIDATION_INVALID_EMAIL_FORMAT", "邮箱格式不正确"),
    
    /**
     * 手机号格式错误
     */
    INVALID_MOBILE_FORMAT("VALIDATION_INVALID_MOBILE_FORMAT", "手机号格式不正确"),
    
    /**
     * 身份证号格式错误
     */
    INVALID_ID_CARD_FORMAT("VALIDATION_INVALID_ID_CARD_FORMAT", "身份证号格式不正确"),
    
    /**
     * URL格式错误
     */
    INVALID_URL_FORMAT("VALIDATION_INVALID_URL_FORMAT", "URL格式不正确"),
    
    /**
     * IP地址格式错误
     */
    INVALID_IP_FORMAT("VALIDATION_INVALID_IP_FORMAT", "IP地址格式不正确"),
    
    /**
     * 正则表达式不匹配错误
     */
    PATTERN_NOT_MATCH("VALIDATION_PATTERN_NOT_MATCH", "格式不匹配"),

    // ================================ 数值验证错误 ================================
    
    /**
     * 数值超出范围错误
     */
    NUMBER_OUT_OF_RANGE("VALIDATION_NUMBER_OUT_OF_RANGE", "数值超出允许范围"),
    
    /**
     * 数值不是正数错误
     */
    NUMBER_NOT_POSITIVE("VALIDATION_NUMBER_NOT_POSITIVE", "数值必须为正数"),
    
    /**
     * 数值不是负数错误
     */
    NUMBER_NOT_NEGATIVE("VALIDATION_NUMBER_NOT_NEGATIVE", "数值必须为负数"),
    
    /**
     * 数值不是零错误
     */
    NUMBER_NOT_ZERO("VALIDATION_NUMBER_NOT_ZERO", "数值不能为零"),
    
    /**
     * 数值格式错误
     */
    INVALID_NUMBER_FORMAT("VALIDATION_INVALID_NUMBER_FORMAT", "数值格式不正确"),

    // ================================ 日期验证错误 ================================
    
    /**
     * 日期超出范围错误
     */
    DATE_OUT_OF_RANGE("VALIDATION_DATE_OUT_OF_RANGE", "日期超出允许范围"),
    
    /**
     * 日期格式错误
     */
    INVALID_DATE_FORMAT("VALIDATION_INVALID_DATE_FORMAT", "日期格式不正确"),
    
    /**
     * 日期时间格式错误
     */
    INVALID_DATETIME_FORMAT("VALIDATION_INVALID_DATETIME_FORMAT", "日期时间格式不正确"),

    // ================================ 条件验证错误 ================================
    
    /**
     * 条件不满足错误
     */
    CONDITION_NOT_SATISFIED("VALIDATION_CONDITION_NOT_SATISFIED", "条件不满足"),
    
    /**
     * 自定义验证失败错误
     */
    CUSTOM_VALIDATION_FAILED("VALIDATION_CUSTOM_VALIDATION_FAILED", "自定义验证失败"),

    // ================================ 特殊验证错误 ================================
    
    /**
     * 银行卡号格式错误
     */
    INVALID_BANK_CARD_FORMAT("VALIDATION_INVALID_BANK_CARD_FORMAT", "银行卡号格式不正确"),
    
    /**
     * 统一社会信用代码格式错误
     */
    INVALID_CREDIT_CODE_FORMAT("VALIDATION_INVALID_CREDIT_CODE_FORMAT", "统一社会信用代码格式不正确"),
    
    /**
     * 车牌号格式错误
     */
    INVALID_LICENSE_PLATE_FORMAT("VALIDATION_INVALID_LICENSE_PLATE_FORMAT", "车牌号格式不正确"),
    
    /**
     * 密码强度不足错误
     */
    WEAK_PASSWORD("VALIDATION_WEAK_PASSWORD", "密码强度不足"),
    
    /**
     * 颜色代码格式错误
     */
    INVALID_COLOR_CODE_FORMAT("VALIDATION_INVALID_COLOR_CODE_FORMAT", "颜色代码格式不正确"),
    
    /**
     * JSON格式错误
     */
    INVALID_JSON_FORMAT("VALIDATION_INVALID_JSON_FORMAT", "JSON格式不正确"),
    
    /**
     * XML格式错误
     */
    INVALID_XML_FORMAT("VALIDATION_INVALID_XML_FORMAT", "XML格式不正确"),
    
    /**
     * Base64格式错误
     */
    INVALID_BASE64_FORMAT("VALIDATION_INVALID_BASE64_FORMAT", "Base64格式不正确");

    /**
     * 错误码
     */
    private final String code;
    
    /**
     * 错误信息
     */
    private final String message;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
} 