package club.slavopolis.persistence.jdbc.Constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * JDBC 常量类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcConstant {

    //============================ 错误信息 ============================

    /**
     * NamedParameterJdbcTemplate 不能为空
     */
    public static final String NAMED_PARAMETER_JDBC_TEMPLATE_CANNOT_BE_NULL = "NamedParameterJdbcTemplate 不能为空";

    /**
     * DataSourceTransactionManager 不能为空
     */
    public static final String DATASOURCE_TRANSACTION_MANAGER_CANNOT_BE_NULL = "DataSourceTransactionManager 不能为空";

    /**
     * TransactionDefinition 不能为空
     */
    public static final String TRANSACTION_DEFINITION_CANNOT_BE_NULL = "TransactionDefinition 不能为空";

    /**
     * SqlSecurityConfig
     */
    public static final String SQL_SECURITY_CONFIG_CANNOT_BE_NULL = "SqlSecurityConfig 不能为空";

    //============================ DDL 关键词 ============================

    public static final String SELECT = "SELECT";
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
}
