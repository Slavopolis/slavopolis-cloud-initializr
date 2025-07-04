package club.slavopolis.persistence.jdbc.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * SQL注入验证器
 * <p>提供SQL注入安全检测和防护功能</p>
 * <p>支持SQL语句检测、参数验证、黑名单过滤等安全措施</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Setter
@Getter
@Slf4j
public class SqlInjectionValidator {

    /**
     * SQL注入风险关键词黑名单
     */
    private static final Set<String> SQL_INJECTION_KEYWORDS = Set.of(
        "union", "select", "insert", "update", "delete", "drop", "create", "alter",
        "exec", "execute", "sp_", "xp_", "declare", "cast", "convert", "char",
        "nchar", "varchar", "nvarchar", "ascii", "substring", "length", "len",
        "user", "system_user", "session_user", "current_user", "database",
        "version", "@@", "waitfor", "delay", "benchmark", "sleep",
        "information_schema", "sysobjects", "syscolumns", "sysusers"
    );

    /**
     * 危险SQL模式
     */
    private static final Pattern[] DANGEROUS_PATTERNS = {
        // 单引号注入
        Pattern.compile("'.*(union|select|insert|update|delete).*", Pattern.CASE_INSENSITIVE),
        // 注释注入
        Pattern.compile(".*(-{2}|/\\*|\\*/).*", Pattern.CASE_INSENSITIVE),
        // 分号分隔的多语句
        Pattern.compile(".*;\\s*(select|insert|update|delete|drop|create)", Pattern.CASE_INSENSITIVE),
        // 十六进制注入
        Pattern.compile(".*0x[0-9a-f]+.*", Pattern.CASE_INSENSITIVE),
        // 函数调用注入
        Pattern.compile(".*(exec|execute|sp_|xp_)\\s*\\(.*", Pattern.CASE_INSENSITIVE)
    };

    /**
     * 是否启用验证
     */
    private boolean validationEnabled = true;

    /**
     * 是否启用严格模式
     */
    private boolean strictMode = false;

    /**
     * 验证SQL语句安全性
     * 
     * @param sql SQL语句
     * @throws SecurityException 如果检测到SQL注入风险
     */
    public void validateSql(String sql) {
        if (!validationEnabled || !StringUtils.hasText(sql)) {
            return;
        }

        String normalizedSql = sql.toLowerCase().trim();
        
        // 检查危险模式
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(normalizedSql).matches()) {
                String message = "Potential SQL injection detected in SQL: " + sql;
                log.warn(message);
                throw new SecurityException(message);
            }
        }

        // 严格模式下进行更多检查
        if (strictMode) {
            validateSqlInStrictMode(normalizedSql, sql);
        }
    }

    /**
     * 验证参数安全性
     * 
     * @param paramName 参数名
     * @param paramValue 参数值
     * @throws SecurityException 如果检测到SQL注入风险
     */
    public void validateParameter(String paramName, String paramValue) {
        if (!validationEnabled || !StringUtils.hasText(paramValue)) {
            return;
        }

        String normalizedValue = paramValue.toLowerCase().trim();
        
        // 检查参数值中的危险关键词
        for (String keyword : SQL_INJECTION_KEYWORDS) {
            if (normalizedValue.contains(keyword)) {
                String message = String.format(
                    "Potential SQL injection detected in parameter '%s': %s", 
                    paramName, paramValue);
                log.warn(message);
                
                if (strictMode) {
                    throw new SecurityException(message);
                }
            }
        }

        // 检查危险字符
        if (containsDangerousCharacters(paramValue)) {
            String message = String.format(
                "Dangerous characters detected in parameter '%s': %s", 
                paramName, paramValue);
            log.warn(message);
            
            if (strictMode) {
                throw new SecurityException(message);
            }
        }
    }

    /**
     * 严格模式下的SQL验证
     */
    private void validateSqlInStrictMode(String normalizedSql, String originalSql) {
        // 检查是否包含多个SQL语句
        if (normalizedSql.contains(";")) {
            long selectCount = normalizedSql.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .filter(s -> normalizedSql.contains("select"))
                .count();
            
            if (selectCount > 1) {
                String message = "Multiple SQL statements detected: " + originalSql;
                log.warn(message);
                throw new SecurityException(message);
            }
        }

        // 检查是否有未闭合的引号
        if (hasUnbalancedQuotes(normalizedSql)) {
            String message = "Unbalanced quotes detected: " + originalSql;
            log.warn(message);
            throw new SecurityException(message);
        }
    }

    /**
     * 检查是否包含危险字符
     */
    private boolean containsDangerousCharacters(String value) {
        return value.contains("'") || 
               value.contains("\"") || 
               value.contains("--") || 
               value.contains("/*") || 
               value.contains("*/") ||
               value.contains(";") ||
               value.contains("\\");
    }

    /**
     * 检查引号是否平衡
     */
    private boolean hasUnbalancedQuotes(String sql) {
        int singleQuoteCount = 0;
        int doubleQuoteCount = 0;
        
        for (char c : sql.toCharArray()) {
            if (c == '\'') {
                singleQuoteCount++;
            } else if (c == '"') {
                doubleQuoteCount++;
            }
        }
        
        return (singleQuoteCount % 2 != 0) || (doubleQuoteCount % 2 != 0);
    }

    /**
     * 清理和转义危险字符
     * 
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public String sanitizeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        return input.replace("'", "''")
                   .replace("\"", "\\\"")
                   .replace("--", "")
                   .replace("/*", "")
                   .replace("*/", "")
                   .replace(";", "");
    }

    /**
     * 检查SQL是否为只读操作
     * 
     * @param sql SQL语句
     * @return 如果是只读操作返回true
     */
    public boolean isReadOnlyOperation(String sql) {
        if (!StringUtils.hasText(sql)) {
            return true;
        }

        String normalizedSql = sql.trim().toLowerCase();
        return normalizedSql.startsWith("select") || 
               normalizedSql.startsWith("show") || 
               normalizedSql.startsWith("describe") || 
               normalizedSql.startsWith("explain");
    }

    /**
     * 检查SQL是否为写操作
     * 
     * @param sql SQL语句
     * @return 如果是写操作返回true
     */
    public boolean isWriteOperation(String sql) {
        if (!StringUtils.hasText(sql)) {
            return false;
        }

        String normalizedSql = sql.trim().toLowerCase();
        return normalizedSql.startsWith("insert") || 
               normalizedSql.startsWith("update") || 
               normalizedSql.startsWith("delete") || 
               normalizedSql.startsWith("replace") ||
               normalizedSql.startsWith("merge");
    }

    /**
     * 检查SQL是否为DDL操作
     * 
     * @param sql SQL语句
     * @return 如果是DDL操作返回true
     */
    public boolean isDdlOperation(String sql) {
        if (!StringUtils.hasText(sql)) {
            return false;
        }

        String normalizedSql = sql.trim().toLowerCase();
        return normalizedSql.startsWith("create") || 
               normalizedSql.startsWith("alter") || 
               normalizedSql.startsWith("drop") || 
               normalizedSql.startsWith("truncate");
    }

}