package club.slavopolis.common.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 正则表达式常量类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegexConstants {

    // ==================== 基础格式 ====================
    
    /**
     * 邮箱地址正则表达式
     */
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    /**
     * 手机号码正则表达式（中国大陆）
     */
    public static final String MOBILE_PHONE = "^1[3-9]\\d{9}$";
    
    /**
     * 固定电话正则表达式
     */
    public static final String LANDLINE_PHONE = "^0\\d{2,3}-?\\d{7,8}$";
    
    /**
     * 身份证号码正则表达式（18位）
     */
    public static final String ID_CARD = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    
    /**
     * 统一社会信用代码
     */
    public static final String SOCIAL_CREDIT_CODE = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
    
    // ==================== 密码强度 ====================
    
    /**
     * 弱密码：至少6位，包含字母和数字
     */
    public static final String PASSWORD_WEAK = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
    
    /**
     * 中等密码：至少8位，包含大小写字母和数字
     */
    public static final String PASSWORD_MEDIUM = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    
    /**
     * 强密码：至少8位，包含大小写字母、数字和特殊字符
     */
    public static final String PASSWORD_STRONG = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    // ==================== 网络相关 ====================
    
    /**
     * IPv4地址正则表达式
     */
    public static final String IPV4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    
    /**
     * IPv6地址正则表达式
     */
    public static final String IPV6 = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
    
    /**
     * URL正则表达式
     */
    public static final String URL = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    
    /**
     * 域名正则表达式
     */
    public static final String DOMAIN = "^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$";
    
    // ==================== 数字格式 ====================
    
    /**
     * 正整数
     */
    public static final String POSITIVE_INTEGER = "^[1-9]\\d*$";
    
    /**
     * 非负整数（包含0）
     */
    public static final String NON_NEGATIVE_INTEGER = "^\\d+$";
    
    /**
     * 正数（包含小数）
     */
    public static final String POSITIVE_NUMBER = "^[1-9]\\d*(\\.\\d+)?$";
    
    /**
     * 非负数（包含0和小数）
     */
    public static final String NON_NEGATIVE_NUMBER = "^\\d+(\\.\\d+)?$";
    
    /**
     * 金额格式（最多两位小数）
     */
    public static final String MONEY = "^\\d+(\\.\\d{1,2})?$";
    
    // ==================== 中文相关 ====================
    
    /**
     * 中文字符
     */
    public static final String CHINESE = "^[\\u4e00-\\u9fa5]+$";
    
    /**
     * 中文姓名（2-4个中文字符）
     */
    public static final String CHINESE_NAME = "^[\\u4e00-\\u9fa5]{2,4}$";
    
    /**
     * 包含中文的字符串
     */
    public static final String CONTAINS_CHINESE = ".*[\\u4e00-\\u9fa5].*";
    
    // ==================== 银行卡相关 ====================
    
    /**
     * 银行卡号（13-19位数字）
     */
    public static final String BANK_CARD = "^\\d{13,19}$";
    
    /**
     * 信用卡CVV码（3-4位数字）
     */
    public static final String CVV = "^\\d{3,4}$";
    
    // ==================== 日期时间 ====================
    
    /**
     * 日期格式 yyyy-MM-dd
     */
    public static final String DATE_YYYY_MM_DD = "^\\d{4}-\\d{2}-\\d{2}$";
    
    /**
     * 时间格式 HH:mm:ss
     */
    public static final String TIME_HH_MM_SS = "^\\d{2}:\\d{2}:\\d{2}$";
    
    /**
     * 日期时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATETIME_YYYY_MM_DD_HH_MM_SS = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    
    // ==================== 编码相关 ====================
    
    /**
     * UUID格式（带横线）
     */
    public static final String UUID_WITH_HYPHEN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    
    /**
     * UUID格式（不带横线）
     */
    public static final String UUID_WITHOUT_HYPHEN = "^[0-9a-fA-F]{32}$";
    
    /**
     * Base64编码
     */
    public static final String BASE64 = "^[A-Za-z0-9+/]*={0,2}$";
    
    /**
     * 十六进制字符串
     */
    public static final String HEX_STRING = "^[0-9a-fA-F]+$";
    
    // ==================== 文件相关 ====================
    
    /**
     * 图片文件扩展名
     */
    public static final String IMAGE_FILE = ".*\\.(jpg|jpeg|png|gif|bmp|webp)$";
    
    /**
     * 文档文件扩展名
     */
    public static final String DOCUMENT_FILE = ".*\\.(doc|docx|pdf|txt|xls|xlsx|ppt|pptx)$";
    
    /**
     * 视频文件扩展名
     */
    public static final String VIDEO_FILE = ".*\\.(mp4|avi|mov|wmv|flv|mkv)$";
    
    /**
     * 音频文件扩展名
     */
    public static final String AUDIO_FILE = ".*\\.(mp3|wav|flac|aac|ogg)$";
    
    // ==================== 业务相关 ====================
    
    /**
     * 用户名格式（4-20位字母数字下划线，不能以数字开头）
     */
    public static final String USERNAME = "^[a-zA-Z][a-zA-Z0-9_]{3,19}$";
    
    /**
     * 租户编码格式（3-20位字母数字，不能以数字开头）
     */
    public static final String TENANT_CODE = "^[a-zA-Z][a-zA-Z0-9]{2,19}$";
    
    /**
     * 角色编码格式（大写字母和下划线）
     */
    public static final String ROLE_CODE = "^[A-Z][A-Z0-9_]*$";
    
    /**
     * 权限编码格式（小写字母、数字、冒号）
     */
    public static final String PERMISSION_CODE = "^[a-z][a-z0-9:]*$";
    
    /**
     * 版本号格式（语义化版本）
     */
    public static final String VERSION = "^\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9]+)?$";
} 