package club.slavopolis.infrastructure.persistence.jdbc.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * SQL安全配置类
 * <p>集中管理SQL执行过程中的安全相关配置</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@ConfigurationProperties(prefix = "slavopolis.jdbc.security")
public class SqlSecurityConfig {

    /**
     * 是否启用SQL安全检查
     */
    private boolean enabled = true;

    /**
     * 是否启用SQL注入检测
     */
    private boolean sqlInjectionDetection = true;

    /**
     * 是否启用参数验证
     */
    private boolean parameterValidation = true;

    /**
     * 是否启用敏感信息掩码
     */
    private boolean sensitiveDataMasking = true;

    /**
     * 危险SQL关键词列表
     */
    private Set<String> dangerousKeywords = new HashSet<>(Arrays.asList(
        "DROP", "DELETE", "TRUNCATE", "ALTER", "CREATE", "GRANT", "REVOKE"
    ));

    /**
     * 敏感参数名称模式
     */
    private Set<String> sensitiveParameterPatterns = new HashSet<>(Arrays.asList(
        "password", "pwd", "secret", "token", "key", "auth"
    ));

    /**
     * 最大参数值长度
     */
    private int maxParameterLength = 10000;

    /**
     * 最大批处理大小
     */
    private int maxBatchSize = 1000;

    /**
     * 是否允许DDL操作
     */
    private boolean allowDdlOperations = false;

    /**
     * 是否允许无WHERE子句的UPDATE/DELETE
     */
    private boolean allowUnsafeOperations = false;

    /**
     * SQL长度限制
     */
    private int maxSqlLength = 50000;

    /**
     * 是否记录安全警告
     */
    private boolean logSecurityWarnings = true;

    /**
     * 检查参数名称是否敏感
     * 
     * @param parameterName 参数名称
     * @return 如果是敏感参数返回true
     */
    public boolean isSensitiveParameter(String parameterName) {
        if (parameterName == null) {
            return false;
        }
        
        String lowerName = parameterName.toLowerCase();
        return sensitiveParameterPatterns.stream()
            .anyMatch(pattern -> lowerName.contains(pattern.toLowerCase()));
    }

    /**
     * 检查SQL是否包含危险关键词
     * 
     * @param sql SQL语句
     * @return 如果包含危险关键词返回true
     */
    public boolean containsDangerousKeywords(String sql) {
        if (sql == null) {
            return false;
        }
        
        String upperSql = sql.toUpperCase();
        return dangerousKeywords.stream()
            .anyMatch(keyword -> upperSql.contains(keyword.toUpperCase()));
    }
} 