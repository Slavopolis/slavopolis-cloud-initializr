package club.slavopolis.email.util;

import club.slavopolis.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 邮件地址验证工具类 - 提供邮件地址格式验证功能
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.service.impl
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Slf4j
@Component
public class EmailValidator {

    /**
     * 邮件地址正则表达式（标准模式）
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    /**
     * 邮件地址正则表达式（严格模式）
     */
    private static final String EMAIL_STRICT_REGEX = "^[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9.-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern EMAIL_STRICT_PATTERN = Pattern.compile(EMAIL_STRICT_REGEX);

    /**
     * 验证邮件地址格式（标准模式）
     *
     * @param email 邮件地址
     * @return 是否有效
     */
    public boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // 基本长度检查
        if (email.length() > 254) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * 验证邮件地址格式（严格模式）
     *
     * @param email 邮件地址
     * @return 是否有效
     */
    public boolean isValidStrict(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String trimmedEmail = email.trim();
        
        // 基本长度检查
        if (trimmedEmail.length() > 254) {
            return false;
        }
        
        // 检查本地部分长度
        String[] parts = trimmedEmail.split(CommonConstants.AT);
        if (parts.length != 2) {
            return false;
        }
        
        if (parts[0].length() > 64) {
            return false;
        }
        
        return EMAIL_STRICT_PATTERN.matcher(trimmedEmail).matches();
    }

    /**
     * 验证邮件地址列表
     *
     * @param emails 邮件地址列表
     * @return 是否全部有效
     */
    public boolean isValidList(java.util.List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return false;
        }
        
        return emails.stream().allMatch(this::isValid);
    }

    /**
     * 过滤有效的邮件地址
     *
     * @param emails 邮件地址列表
     * @return 有效的邮件地址列表
     */
    public java.util.List<String> filterValidEmails(java.util.List<String> emails) {
        if (emails == null) {
            return new java.util.ArrayList<>();
        }
        
        return emails.stream()
                .filter(this::isValid)
                .toList();
    }

    /**
     * 获取无效的邮件地址
     *
     * @param emails 邮件地址列表
     * @return 无效的邮件地址列表
     */
    public java.util.List<String> getInvalidEmails(java.util.List<String> emails) {
        if (emails == null) {
            return new java.util.ArrayList<>();
        }
        
        return emails.stream()
                .filter(email -> !isValid(email))
                .toList();
    }

    /**
     * 验证邮件地址并返回详细结果
     *
     * @param email 邮件地址
     * @return 验证结果
     */
    public ValidationResult validateWithDetails(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "邮件地址不能为空");
        }
        
        String trimmedEmail = email.trim();
        
        if (trimmedEmail.length() > 254) {
            return new ValidationResult(false, "邮件地址长度超过限制");
        }
        
        String[] parts = trimmedEmail.split(CommonConstants.AT);
        if (parts.length != 2) {
            return new ValidationResult(false, "邮件地址格式不正确");
        }
        
        if (parts[0].isEmpty()) {
            return new ValidationResult(false, "用户名部分不能为空");
        }
        
        if (parts[0].length() > 64) {
            return new ValidationResult(false, "用户名部分长度超过限制");
        }
        
        if (parts[1].isEmpty()) {
            return new ValidationResult(false, "域名部分不能为空");
        }
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return new ValidationResult(false, "邮件地址格式不正确");
        }
        
        return new ValidationResult(true, "邮件地址有效");
    }

    /**
     * 验证结果
     */
    public record ValidationResult(boolean valid, String message) {
        
        /**
         * 创建成功结果
         */
        public static ValidationResult success() {
            return new ValidationResult(true, "邮件地址有效");
        }
        
        /**
         * 创建失败结果
         */
        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
    }
} 