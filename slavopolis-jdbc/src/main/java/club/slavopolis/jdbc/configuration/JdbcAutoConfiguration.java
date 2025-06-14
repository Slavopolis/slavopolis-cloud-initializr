package club.slavopolis.jdbc.configuration;

import club.slavopolis.jdbc.core.EnhancedJdbcTemplate;
import club.slavopolis.jdbc.core.JdbcOperations;
import club.slavopolis.jdbc.security.SqlSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * JDBC自动配置类
 * <p>提供Spring Boot自动配置支持</p>
 * 
 * @author Slavopolis Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass({DataSource.class, NamedParameterJdbcTemplate.class})
@ConditionalOnProperty(prefix = "slavopolis.jdbc", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({JdbcProperties.class, SqlSecurityConfig.class})
public class JdbcAutoConfiguration {

    /**
     * 配置增强JDBC模板
     * 
     * @param namedParameterJdbcTemplate Spring命名参数JDBC模板
     * @param transactionManager 事务管理器
     * @param transactionDefinition 事务定义
     * @param properties 配置属性
     * @param securityConfig SQL安全配置
     * @return 增强JDBC模板
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(JdbcOperations.class)
    public EnhancedJdbcTemplate enhancedJdbcTemplate(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            DataSourceTransactionManager transactionManager,
            TransactionDefinition transactionDefinition,
            JdbcProperties properties,
            SqlSecurityConfig securityConfig) {
        
        EnhancedJdbcTemplate template = new EnhancedJdbcTemplate(
            namedParameterJdbcTemplate, 
            transactionManager, 
            transactionDefinition,
            securityConfig
        );
        
        // 应用基础配置
        template.setDefaultLoggingEnabled(properties.isDefaultLoggingEnabled());
        template.setDefaultPageSize(properties.getDefaultPageSize());
        template.setMaxPageSize(properties.getMaxPageSize());
        
        log.info("Enhanced JDBC Template configured with security settings - " +
                "SQL injection detection: {}, Parameter validation: {}, Data masking: {}", 
                securityConfig.isSqlInjectionDetection(),
                securityConfig.isParameterValidation(),
                securityConfig.isSensitiveDataMasking());
        
        return template;
    }

    /**
     * 配置SQL安全配置Bean
     * <p>使SqlSecurityConfig作为Spring Bean可供其他组件注入使用</p>
     * 
     * @param securityConfig 自动配置的SqlSecurityConfig
     * @return SQL安全配置Bean
     */
    @Bean
    @ConditionalOnMissingBean(SqlSecurityConfig.class)
    public SqlSecurityConfig sqlSecurityConfig(SqlSecurityConfig securityConfig) {
        log.info("SQL Security Configuration loaded - Max batch size: {}, Max parameter length: {}, " +
                "Dangerous keywords: {}, Sensitive patterns: {}", 
                securityConfig.getMaxBatchSize(),
                securityConfig.getMaxParameterLength(),
                securityConfig.getDangerousKeywords().size(),
                securityConfig.getSensitiveParameterPatterns().size());
        
        return securityConfig;
    }

    /**
     * 配置事务定义
     * 
     * @param properties 配置属性
     * @return 事务定义
     */
    @Bean
    @ConditionalOnMissingBean(TransactionDefinition.class)
    public TransactionDefinition transactionDefinition(JdbcProperties properties) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        
        JdbcProperties.Transaction txConfig = properties.getTransaction();
        
        // 设置传播行为
        definition.setPropagationBehavior(parsePropagation(txConfig.getPropagation()));
        
        // 设置隔离级别
        definition.setIsolationLevel(parseIsolation(txConfig.getIsolation()));
        
        // 设置超时时间
        definition.setTimeout(txConfig.getTimeout());
        
        // 设置只读标志
        definition.setReadOnly(txConfig.isReadOnly());
        
        return definition;
    }

    /**
     * 解析事务传播行为
     * 
     * @param propagation 传播行为字符串
     * @return 传播行为常量
     */
    private int parsePropagation(String propagation) {
        return switch (propagation.toUpperCase()) {
            case "REQUIRED" -> TransactionDefinition.PROPAGATION_REQUIRED;
            case "SUPPORTS" -> TransactionDefinition.PROPAGATION_SUPPORTS;
            case "MANDATORY" -> TransactionDefinition.PROPAGATION_MANDATORY;
            case "REQUIRES_NEW" -> TransactionDefinition.PROPAGATION_REQUIRES_NEW;
            case "NOT_SUPPORTED" -> TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
            case "NEVER" -> TransactionDefinition.PROPAGATION_NEVER;
            case "NESTED" -> TransactionDefinition.PROPAGATION_NESTED;
            default -> TransactionDefinition.PROPAGATION_REQUIRED;
        };
    }

    /**
     * 解析事务隔离级别
     * 
     * @param isolation 隔离级别字符串
     * @return 隔离级别常量
     */
    private int parseIsolation(String isolation) {
        return switch (isolation.toUpperCase()) {
            case "DEFAULT" -> TransactionDefinition.ISOLATION_DEFAULT;
            case "READ_UNCOMMITTED" -> TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
            case "READ_COMMITTED" -> TransactionDefinition.ISOLATION_READ_COMMITTED;
            case "REPEATABLE_READ" -> TransactionDefinition.ISOLATION_REPEATABLE_READ;
            case "SERIALIZABLE" -> TransactionDefinition.ISOLATION_SERIALIZABLE;
            default -> TransactionDefinition.ISOLATION_DEFAULT;
        };
    }
}