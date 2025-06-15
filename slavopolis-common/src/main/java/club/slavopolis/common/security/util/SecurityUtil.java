package club.slavopolis.common.security.util;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.security.context.SecurityContext;
import club.slavopolis.common.security.context.TenantContext;
import club.slavopolis.common.util.JsonUtils;
import club.slavopolis.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 安全工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtil {

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 密码强度正则表达式
     */
    private static final Pattern WEAK_PASSWORD_PATTERN = Pattern.compile("^(\\d+|[a-zA-Z]+|[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+)$");
    private static final Pattern MEDIUM_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])|(?=.*[a-z])(?=.*\\d)|(?=.*[A-Z])(?=.*\\d)");
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])");

    /**
     * SQL注入关键词
     */
    private static final String[] SQL_INJECTION_KEYWORDS = {
        "select", "insert", "update", "delete", "drop", "create", "alter", "exec", "execute",
        "union", "script", "javascript", "vbscript", "onload", "onerror", "onclick"
    };

    /**
     * XSS攻击关键词
     */
    private static final String[] XSS_KEYWORDS = {
        "<script", "</script>", "javascript:", "vbscript:", "onload=", "onerror=", "onclick=",
        "onmouseover=", "onfocus=", "onblur=", "onchange=", "onsubmit=", "alert(", "confirm(",
        "prompt(", "document.cookie", "document.write", "window.location", "eval("
    };

    /**
     * 敏感字段列表
     */
    public static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "pwd", "secret", "token", "key", "credential",
            "cardNumber", "cvv", "pin", "ssn", "idCard"
    );

    /**
     * 需要忽略的 URL 模式
     */
    public static final Set<String> IGNORE_PATTERNS = Set.of(
            "/actuator/",
            "/swagger",
            "/api-docs",
            "/webjars/",
            "/favicon.ico",
            "/static/",
            "/public/"
    );

    // ==================== 用户认证相关 ====================

    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        return SecurityContext.getUserId();
    }

    /**
     * 获取当前用户名
     * 
     * @return 用户名
     */
    public static String getCurrentUsername() {
        return SecurityContext.getUsername();
    }

    /**
     * 获取当前租户ID
     * 
     * @return 租户ID
     */
    public static String getCurrentTenantId() {
        return TenantContext.getTenantId();
    }

    /**
     * 判断当前用户是否已认证
     * 
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        return SecurityContext.isAuthenticated();
    }

    /**
     * 判断当前用户是否为匿名用户
     * 
     * @return 是否为匿名用户
     */
    public static boolean isAnonymous() {
        return !isAuthenticated();
    }

    // ==================== 权限检查相关 ====================

    /**
     * 检查当前用户是否拥有指定角色
     * 
     * @param role 角色
     * @return 是否拥有角色
     */
    public static boolean hasRole(String role) {
        return SecurityContext.hasRole(role);
    }

    /**
     * 检查当前用户是否拥有任意一个角色
     * 
     * @param roles 角色列表
     * @return 是否拥有任意一个角色
     */
    public static boolean hasAnyRole(String... roles) {
        return SecurityContext.hasAnyRole(roles);
    }

    /**
     * 检查当前用户是否拥有所有角色
     * 
     * @param roles 角色列表
     * @return 是否拥有所有角色
     */
    public static boolean hasAllRoles(String... roles) {
        return SecurityContext.hasAllRoles(roles);
    }

    /**
     * 检查当前用户是否拥有指定权限
     * 
     * @param permission 权限
     * @return 是否拥有权限
     */
    public static boolean hasPermission(String permission) {
        return SecurityContext.hasPermission(permission);
    }

    /**
     * 检查当前用户是否拥有任意一个权限
     * 
     * @param permissions 权限列表
     * @return 是否拥有任意一个权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        return SecurityContext.hasAnyPermission(permissions);
    }

    /**
     * 检查当前用户是否拥有所有权限
     * 
     * @param permissions 权限列表
     * @return 是否拥有所有权限
     */
    public static boolean hasAllPermissions(String... permissions) {
        return SecurityContext.hasAllPermissions(permissions);
    }

    /**
     * 判断是否应该忽略该请求
     *
     * @param request HttpServletRequest 对象
     * @return 是否忽略该请求
     */
    public static boolean shouldIgnore(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return IGNORE_PATTERNS.stream().anyMatch(uri::contains);
    }

    // ==================== 管理员权限检查 ====================

    /**
     * 判断当前用户是否为超级管理员
     * 
     * @return 是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return SecurityContext.isSuperAdmin();
    }

    /**
     * 判断当前用户是否为租户管理员
     * 
     * @return 是否为租户管理员
     */
    public static boolean isTenantAdmin() {
        return SecurityContext.isTenantAdmin();
    }

    /**
     * 判断当前用户是否为普通用户
     * 
     * @return 是否为普通用户
     */
    public static boolean isNormalUser() {
        return SecurityContext.isNormalUser();
    }

    /**
     * 判断当前用户是否为管理员（超级管理员或租户管理员）
     * 
     * @return 是否为管理员
     */
    public static boolean isAdmin() {
        return isSuperAdmin() || isTenantAdmin();
    }

    // ==================== 资源访问控制 ====================

    /**
     * 检查当前用户是否可以访问指定资源
     * 
     * @param resourceId 资源ID
     * @param action 操作类型
     * @return 是否可以访问
     */
    public static boolean canAccess(String resourceId, String action) {
        if (!isAuthenticated()) {
            return false;
        }
        
        // 超级管理员拥有所有权限
        if (isSuperAdmin()) {
            return true;
        }
        
        // 构建权限字符串：resource:action
        String permission = resourceId + CommonConstants.COLON + action;
        return hasPermission(permission);
    }

    /**
     * 检查当前用户是否可以读取指定资源
     * 
     * @param resourceId 资源ID
     * @return 是否可以读取
     */
    public static boolean canRead(String resourceId) {
        return canAccess(resourceId, "read");
    }

    /**
     * 检查当前用户是否可以写入指定资源
     * 
     * @param resourceId 资源ID
     * @return 是否可以写入
     */
    public static boolean canWrite(String resourceId) {
        return canAccess(resourceId, "write");
    }

    /**
     * 检查当前用户是否可以删除指定资源
     * 
     * @param resourceId 资源ID
     * @return 是否可以删除
     */
    public static boolean canDelete(String resourceId) {
        return canAccess(resourceId, "delete");
    }

    /**
     * 检查当前用户是否拥有指定资源的所有权
     * 
     * @param ownerId 资源所有者ID
     * @return 是否拥有所有权
     */
    public static boolean isOwner(String ownerId) {
        String currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(ownerId);
    }

    /**
     * 检查当前用户是否可以访问指定用户的资源
     * 
     * @param targetUserId 目标用户ID
     * @return 是否可以访问
     */
    public static boolean canAccessUser(String targetUserId) {
        // 超级管理员可以访问所有用户
        if (isSuperAdmin()) {
            return true;
        }
        
        // 用户可以访问自己的资源
        if (isOwner(targetUserId)) {
            return true;
        }
        
        // 租户管理员可以访问同租户用户
        if (isTenantAdmin()) {
            // 这里需要根据实际业务逻辑判断是否为同租户用户
            // 暂时返回false，需要在具体业务中实现
            return false;
        }
        
        return false;
    }

    // ==================== 数据脱敏相关 ====================

    /**
     * 脱敏手机号
     * 
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 脱敏邮箱
     * 
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains(CommonConstants.AT)) {
            return email;
        }
        
        String[] parts = email.split(CommonConstants.AT);
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return email;
        }
        
        String maskedUsername = username.charAt(0) + "***" + username.substring(username.length() - 1);
        return maskedUsername + CommonConstants.AT + domain;
    }

    /**
     * 脱敏身份证号
     * 
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 脱敏银行卡号
     * 
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 脱敏姓名
     * 
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        
        if (name.length() == 2) {
            return name.charAt(0) + CommonConstants.ASTERISK;
        }

        return name.charAt(0) +
                CommonConstants.ASTERISK.repeat(name.length() - 2) +
                name.charAt(name.length() - 1);
    }

    /**
     * 脱敏处理
     *
     * @param data 原始数据
     * @return 脱敏后的数据
     */
    public static String maskSensitiveData(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }

        try {
            // 尝试解析为 JSON 进行脱敏
            if (data.trim().startsWith(CommonConstants.LEFT_BRACE) || data.trim().startsWith(CommonConstants.LEFT_BRACKET)) {
                Map<String, Object> jsonData = JsonUtils.toMap(data);
                maskSensitiveFields(jsonData);
                return JsonUtils.toJson(jsonData);
            }
        } catch (Exception e) {
            // 如果不是 JSON，返回原始数据
        }

        return data;
    }

    /**
     * 判断是否为敏感字段
     *
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    public static boolean isSensitiveField(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            return false;
        }

        String lowerFieldName = fieldName.toLowerCase();
        return SENSITIVE_FIELDS.stream().anyMatch(lowerFieldName::contains);
    }

    /**
     * 递归脱敏 JSON 中的敏感字段
     */
    @SuppressWarnings("unchecked")
    public static void maskSensitiveFields(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 检查是否为敏感字段
            if (isSensitiveField(key)) {
                data.put(key, "***MASKED***");
            } else if (value instanceof Map) {
                maskSensitiveFields((Map<String, Object>) value);
            } else if (value instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map) {
                        maskSensitiveFields((Map<String, Object>) item);
                    }
                }
            }
        }
    }

    // ==================== 密码安全相关 ====================

    /**
     * 检查密码强度
     * 
     * @param password 密码
     * @return 密码强度等级（1-弱，2-中，3-强）
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            // 无效密码
            return 0;
        }
        
        if (password.length() < 8) {
            // 弱密码
            return 1;
        }
        
        if (WEAK_PASSWORD_PATTERN.matcher(password).matches()) {
            // 弱密码
            return 1;
        }
        
        if (STRONG_PASSWORD_PATTERN.matcher(password).matches() && password.length() >= 12) {
            // 强密码
            return 3;
        }
        
        if (MEDIUM_PASSWORD_PATTERN.matcher(password).matches()) {
            // 中等密码
            return 2;
        }

        // 弱密码
        return 1;
    }

    /**
     * 判断密码是否安全
     * 
     * @param password 密码
     * @return 是否安全
     */
    public static boolean isPasswordSecure(String password) {
        return checkPasswordStrength(password) >= 2;
    }

    /**
     * 生成安全的随机密码
     * 
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateSecurePassword(int length) {
        if (length < 8) {
            length = 8;
        }
        
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        StringBuilder password = new StringBuilder();
        
        // 确保至少包含每种字符类型
        password.append(lowercase.charAt(SECURE_RANDOM.nextInt(lowercase.length())));
        password.append(uppercase.charAt(SECURE_RANDOM.nextInt(uppercase.length())));
        password.append(digits.charAt(SECURE_RANDOM.nextInt(digits.length())));
        password.append(special.charAt(SECURE_RANDOM.nextInt(special.length())));
        
        // 填充剩余长度
        String allChars = lowercase + uppercase + digits + special;
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(SECURE_RANDOM.nextInt(allChars.length())));
        }
        
        // 打乱字符顺序
        return shuffleString(password.toString());
    }

    /**
     * 打乱字符串
     * 
     * @param str 原字符串
     * @return 打乱后的字符串
     */
    private static String shuffleString(String str) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    // ==================== 输入验证相关 ====================

    /**
     * 检查输入是否包含SQL注入风险
     * 
     * @param input 输入内容
     * @return 是否包含SQL注入风险
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        for (String keyword : SQL_INJECTION_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 检查输入是否包含XSS攻击风险
     * 
     * @param input 输入内容
     * @return 是否包含XSS攻击风险
     */
    public static boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        for (String keyword : XSS_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 清理输入中的危险字符
     * 
     * @param input 输入内容
     * @return 清理后的内容
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replaceAll("<script[^>]*>.*?</script>", CommonConstants.EMPTY)
            .replaceAll("<[^>]+>", CommonConstants.EMPTY)
            .replaceAll("javascript:", CommonConstants.EMPTY)
            .replaceAll("vbscript:", CommonConstants.EMPTY)
            .replaceAll("on\\w+\\s*=", CommonConstants.EMPTY)
            .replaceAll("eval\\s*\\(", CommonConstants.EMPTY)
            .replaceAll("expression\\s*\\(", CommonConstants.EMPTY);
    }

    /**
     * 验证输入是否安全
     * 
     * @param input 输入内容
     * @return 是否安全
     */
    public static boolean isInputSafe(String input) {
        return !containsSqlInjection(input) && !containsXss(input);
    }

    // ==================== 会话管理相关 ====================

    /**
     * 获取当前会话ID
     * 
     * @return 会话ID
     */
    public static String getCurrentSessionId() {
        return SecurityContext.getSessionId();
    }

    /**
     * 获取当前请求ID
     * 
     * @return 请求ID
     */
    public static String getCurrentRequestId() {
        return SecurityContext.getRequestId();
    }

    /**
     * 获取当前追踪ID
     * 
     * @return 追踪ID
     */
    public static String getCurrentTraceId() {
        return SecurityContext.getTraceId();
    }

    /**
     * 获取客户端IP
     * 
     * @return 客户端IP
     */
    public static String getClientIp() {
        return SecurityContext.getClientIp();
    }

    /**
     * 获取用户代理
     * 
     * @return 用户代理
     */
    public static String getUserAgent() {
        return SecurityContext.getUserAgent();
    }

    // ==================== 便捷方法 ====================

    /**
     * 要求用户已认证，否则抛出异常
     * 
     * @throws SecurityException 如果用户未认证
     */
    public static void requireAuthenticated() {
        if (!isAuthenticated()) {
            throw new SecurityException("用户未经过身份验证");
        }
    }

    /**
     * 要求用户拥有指定角色，否则抛出异常
     * 
     * @param role 角色
     * @throws SecurityException 如果用户没有指定角色
     */
    public static void requireRole(String role) {
        requireAuthenticated();
        if (!hasRole(role)) {
            throw new SecurityException("用户不具备所需角色: " + role);
        }
    }

    /**
     * 要求用户拥有指定权限，否则抛出异常
     * 
     * @param permission 权限
     * @throws SecurityException 如果用户没有指定权限
     */
    public static void requirePermission(String permission) {
        requireAuthenticated();
        if (!hasPermission(permission)) {
            throw new SecurityException("用户没有所需权限: " + permission);
        }
    }

    /**
     * 要求用户为管理员，否则抛出异常
     * 
     * @throws SecurityException 如果用户不是管理员
     */
    public static void requireAdmin() {
        requireAuthenticated();
        if (!isAdmin()) {
            throw new SecurityException("用户不是管理员");
        }
    }

    /**
     * 要求用户为超级管理员，否则抛出异常
     * 
     * @throws SecurityException 如果用户不是超级管理员
     */
    public static void requireSuperAdmin() {
        requireAuthenticated();
        if (!isSuperAdmin()) {
            throw new SecurityException("用户不是超级管理员");
        }
    }

    /**
     * 要求用户为资源所有者或管理员，否则抛出异常
     * 
     * @param ownerId 资源所有者ID
     * @throws SecurityException 如果用户既不是所有者也不是管理员
     */
    public static void requireOwnerOrAdmin(String ownerId) {
        requireAuthenticated();
        if (!isOwner(ownerId) && !isAdmin()) {
            throw new SecurityException("用户不是所有者或管理员");
        }
    }
} 