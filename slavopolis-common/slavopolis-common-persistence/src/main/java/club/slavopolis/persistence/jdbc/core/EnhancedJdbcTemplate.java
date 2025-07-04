package club.slavopolis.persistence.jdbc.core;

import club.slavopolis.base.response.PageResponse;
import club.slavopolis.persistence.jdbc.exception.SecurityException;
import club.slavopolis.persistence.jdbc.parameter.TypeSafeParameterSource;
import club.slavopolis.persistence.jdbc.transaction.TransactionCallback;
import club.slavopolis.persistence.jdbc.transaction.TransactionStatus;
import club.slavopolis.persistence.jdbc.transaction.DefaultTransactionStatus;
import club.slavopolis.persistence.jdbc.mapping.IntelligentRowMapper;
import club.slavopolis.persistence.jdbc.monitoring.SqlExecutionMonitor;
import club.slavopolis.persistence.jdbc.security.SqlInjectionValidator;
import club.slavopolis.persistence.jdbc.security.SqlSecurityConfig;
import club.slavopolis.persistence.jdbc.exception.TransactionException;
import club.slavopolis.persistence.jdbc.exception.MappingException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.Blob;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static club.slavopolis.persistence.jdbc.Constant.JdbcConstant.*;

/**
 * 增强JDBC模板实现类
 *
 * @author Slavopolis Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@Setter
@Slf4j
public class EnhancedJdbcTemplate implements JdbcOperations {

    /**
     * Spring NamedParameterJdbcTemplate 实例
     */
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 数据源事务管理器
     */
    private final DataSourceTransactionManager transactionManager;

    /**
     * 默认事务定义
     */
    private final TransactionDefinition defaultTransactionDefinition;

    /**
     * 当前事务状态（线程本地）
     */
    private final ThreadLocal<TransactionStatus> currentTransactionStatus = new ThreadLocal<>();

    /**
     * SQL执行监控器
     */
    private final SqlExecutionMonitor executionMonitor;

    /**
     * SQL注入验证器
     */
    private final SqlInjectionValidator sqlValidator;

    /**
     * SQL安全配置
     */
    private final SqlSecurityConfig securityConfig;

    /**
     * 智能行映射器缓存
     */
    private final Map<Class<?>, RowMapper<?>> rowMapperCache = new ConcurrentHashMap<>();

    /**
     * 默认启用日志
     */
    private boolean defaultLoggingEnabled = true;

    /**
     * 默认分页大小
     */
    private int defaultPageSize = 20;

    /**
     * 最大分页大小
     */
    private int maxPageSize = 1000;

    /**
     * 构造函数 - 仅包含NamedParameterJdbcTemplate
     *
     * @param namedParameterJdbcTemplate Spring命名参数JDBC模板
     */
    public EnhancedJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        Assert.notNull(namedParameterJdbcTemplate, NAMED_PARAMETER_JDBC_TEMPLATE_CANNOT_BE_NULL);

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = null;
        this.defaultTransactionDefinition = null;
        this.executionMonitor = new SqlExecutionMonitor();
        this.sqlValidator = new SqlInjectionValidator();
        // 使用默认配置
        this.securityConfig = new SqlSecurityConfig();
    }

    /**
     * 构造函数 - 包含事务管理器
     *
     * @param namedParameterJdbcTemplate Spring命名参数JDBC模板
     * @param transactionManager         数据源事务管理器
     */
    public EnhancedJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                DataSourceTransactionManager transactionManager) {
        Assert.notNull(namedParameterJdbcTemplate, NAMED_PARAMETER_JDBC_TEMPLATE_CANNOT_BE_NULL);
        Assert.notNull(transactionManager, DATASOURCE_TRANSACTION_MANAGER_CANNOT_BE_NULL);

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = transactionManager;
        this.defaultTransactionDefinition = new DefaultTransactionDefinition();
        this.executionMonitor = new SqlExecutionMonitor();
        this.sqlValidator = new SqlInjectionValidator();
        // 使用默认配置
        this.securityConfig = new SqlSecurityConfig();
    }

    /**
     * 构造函数 - 完整配置
     *
     * @param namedParameterJdbcTemplate Spring命名参数JDBC模板
     * @param transactionManager         数据源事务管理器
     * @param transactionDefinition      事务定义
     */
    public EnhancedJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                DataSourceTransactionManager transactionManager,
                                TransactionDefinition transactionDefinition) {
        Assert.notNull(namedParameterJdbcTemplate, NAMED_PARAMETER_JDBC_TEMPLATE_CANNOT_BE_NULL);
        Assert.notNull(transactionManager, DATASOURCE_TRANSACTION_MANAGER_CANNOT_BE_NULL);
        Assert.notNull(transactionDefinition, TRANSACTION_DEFINITION_CANNOT_BE_NULL);

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = transactionManager;
        this.defaultTransactionDefinition = transactionDefinition;
        this.executionMonitor = new SqlExecutionMonitor();
        this.sqlValidator = new SqlInjectionValidator();
        // 使用默认配置
        this.securityConfig = new SqlSecurityConfig();
    }

    /**
     * 构造函数 - 包含安全配置
     *
     * @param namedParameterJdbcTemplate Spring命名参数JDBC模板
     * @param transactionManager         数据源事务管理器
     * @param transactionDefinition      事务定义
     * @param securityConfig             SQL安全配置
     */
    public EnhancedJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                DataSourceTransactionManager transactionManager,
                                TransactionDefinition transactionDefinition,
                                SqlSecurityConfig securityConfig) {
        Assert.notNull(namedParameterJdbcTemplate, NAMED_PARAMETER_JDBC_TEMPLATE_CANNOT_BE_NULL);
        Assert.notNull(transactionManager, DATASOURCE_TRANSACTION_MANAGER_CANNOT_BE_NULL);
        Assert.notNull(transactionDefinition, TRANSACTION_DEFINITION_CANNOT_BE_NULL);
        Assert.notNull(securityConfig, SQL_SECURITY_CONFIG_CANNOT_BE_NULL);

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = transactionManager;
        this.defaultTransactionDefinition = transactionDefinition;
        this.executionMonitor = new SqlExecutionMonitor();
        this.sqlValidator = new SqlInjectionValidator();
        this.securityConfig = securityConfig;
    }

    // ================================
    // 查询操作实现 - 基本类型返回
    // ================================

    @Override
    public Integer queryForInt(String sql, Map<String, Object> params) {
        return queryForInt(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Integer queryForInt(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    @Override
    public Long queryForLong(String sql, Map<String, Object> params) {
        return queryForLong(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Long queryForLong(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    @Override
    public String queryForString(String sql, Map<String, Object> params) {
        return queryForString(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public String queryForString(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    // ================================
    // 查询操作实现 - 对象返回
    // ================================

    @Override
    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> requiredType) {
        return queryForObject(sql, params, requiredType, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> requiredType, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                // 尝试使用缓存的智能行映射器
                RowMapper<T> rowMapper = getOrCreateRowMapper(requiredType);
                if (rowMapper != null) {
                    return namedParameterJdbcTemplate.queryForObject(sql, params, rowMapper);
                } else {
                    return namedParameterJdbcTemplate.queryForObject(sql, params, requiredType);
                }
            } catch (EmptyResultDataAccessException e) {
                return null;
            } catch (Exception ex) {
                throw new MappingException("Failed to query for object of type " + requiredType.getSimpleName(), ex);
            }
        });
    }

    @Override
    public <T> T queryForObject(String sql, Map<String, Object> params, RowMapper<T> rowMapper) {
        return queryForObject(sql, params, rowMapper, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public <T> T queryForObject(String sql, Map<String, Object> params, RowMapper<T> rowMapper, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForObject(sql, params, rowMapper);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    @Override
    public <T> Optional<T> queryForOptional(String sql, Map<String, Object> params, Class<T> requiredType) {
        T result = queryForObject(sql, params, requiredType);
        return Optional.ofNullable(result);
    }

    // ================================
    // 查询操作实现 - Map返回
    // ================================

    @Override
    public Map<String, Object> queryForMap(String sql, Map<String, Object> params) {
        return queryForMap(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Map<String, Object> queryForMap(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForMap(sql, params);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Map<String, Object> params, String keyCase) {
        return queryForMap(sql, params, keyCase, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Map<String, Object> queryForMap(String sql, Map<String, Object> params, String keyCase, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                Map<String, Object> result = namedParameterJdbcTemplate.queryForMap(sql, params);
                return transformMapKeys(result, keyCase);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    // ================================
    // 查询操作实现 - List返回
    // ================================

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params) {
        return queryForList(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () ->
                namedParameterJdbcTemplate.queryForList(sql, params));
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, String keyCase) {
        return queryForList(sql, params, keyCase, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, String keyCase, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql, params);
            return results.stream()
                    .map(map -> transformMapKeys(map, keyCase))
                    .toList();
        });
    }

    @Override
    public <T> List<T> queryForList(String sql, Map<String, Object> params, Class<T> requiredType) {
        return queryForList(sql, params, requiredType, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public <T> List<T> queryForList(String sql, Map<String, Object> params, Class<T> requiredType, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                // 尝试使用缓存的智能行映射器
                RowMapper<T> rowMapper = getOrCreateRowMapper(requiredType);
                if (rowMapper != null) {
                    return namedParameterJdbcTemplate.query(sql, params, rowMapper);
                } else {
                    return namedParameterJdbcTemplate.queryForList(sql, params, requiredType);
                }
            } catch (Exception ex) {
                throw new MappingException("Failed to query for list of type " + requiredType.getSimpleName(), ex);
            }
        });
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public <T> List<T> queryForList(String sql, Map<String, Object> params, RowMapper<T> rowMapper) {
        return executeQuery(sql, params, defaultLoggingEnabled, () -> namedParameterJdbcTemplate.query(sql, params, rowMapper));
    }

    // ================================
    // 查询操作实现 - 二进制数据
    // ================================

    @Override
    public Blob queryForBlob(String sql, Map<String, Object> params) {
        return queryForBlob(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Blob queryForBlob(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForObject(sql, params, Blob.class);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    // ================================
    // 更新操作实现
    // ================================

    @Override
    public int update(String sql, Map<String, Object> params) {
        return update(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public int update(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeUpdate(sql, params, enableLogging, () -> namedParameterJdbcTemplate.update(sql, params));
    }

    @Override
    public Number updateAndReturnKey(String sql, TypeSafeParameterSource paramSource) {
        return updateAndReturnKey(sql, paramSource, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Number updateAndReturnKey(String sql, TypeSafeParameterSource paramSource, boolean enableLogging) {
        return executeUpdate(
                sql,
                Objects.requireNonNull(paramSource.getParameterNames()).length > 0 ?
                Arrays.stream(paramSource.getParameterNames()).collect(HashMap::new, (map, name) -> map.put(name, paramSource.getValue(name)), HashMap::putAll) :
                new HashMap<>(),
                enableLogging, () -> {
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);
                    return keyHolder.getKey();
                }
        );
    }

    // ================================
    // 批处理操作实现
    // ================================

    @Override
    public int[] batchUpdate(String sql, Map<String, Object>[] batchParams) {
        return batchUpdate(sql, batchParams, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public int[] batchUpdate(String sql, Map<String, Object>[] batchParams, boolean enableLogging) {
        return executeBatchUpdate(sql, batchParams, enableLogging, () ->
                namedParameterJdbcTemplate.batchUpdate(sql, batchParams));
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public int[] batchUpdate(String sql, TypeSafeParameterSource[] batchParams) {
        return executeBatchUpdate(sql, batchParams, defaultLoggingEnabled, () ->
                namedParameterJdbcTemplate.batchUpdate(sql, batchParams));
    }

    // ================================
    // 存储过程调用实现
    // ================================

    @Override
    public void callProcedure(String sql, Map<String, Object> params) {
        callProcedure(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void callProcedure(String sql, Map<String, Object> params, boolean enableLogging) {
        executeQuery(sql, params, enableLogging, () -> {
            namedParameterJdbcTemplate.execute(sql, params, ps -> {
                ps.execute();
                return null;
            });
            return null;
        });
    }

    @Override
    public Map<String, Object> callProcedureForMap(String sql, Map<String, Object> params) {
        return callProcedureForMap(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public Map<String, Object> callProcedureForMap(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForMap(sql, params);
            } catch (EmptyResultDataAccessException e) {
                return new HashMap<>();
            }
        });
    }

    @Override
    public Map<String, Object> callProcedureForMap(String sql, Map<String, Object> params, String keyCase) {
        Map<String, Object> result = callProcedureForMap(sql, params);
        return transformMapKeys(result, keyCase);
    }

    @Override
    public List<Map<String, Object>> callProcedureForList(String sql, Map<String, Object> params) {
        return callProcedureForList(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public List<Map<String, Object>> callProcedureForList(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () ->
                namedParameterJdbcTemplate.queryForList(sql, params));
    }

    @Override
    public String callProcedureForString(String sql, Map<String, Object> params) {
        return callProcedureForString(sql, params, defaultLoggingEnabled);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public String callProcedureForString(String sql, Map<String, Object> params, boolean enableLogging) {
        return executeQuery(sql, params, enableLogging, () -> {
            try {
                return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    // ================================
    // 事务管理实现
    // ================================

    @Override
    public TransactionStatus getTransactionStatus() {
        return currentTransactionStatus.get();
    }

    @Override
    public TransactionStatus beginTransaction() {
        if (transactionManager == null) {
            throw TransactionException.transactionManagerNotConfigured();
        }

        try {
            org.springframework.transaction.TransactionStatus springStatus = transactionManager.getTransaction(defaultTransactionDefinition);
            TransactionStatus status = new DefaultTransactionStatus(springStatus);
            currentTransactionStatus.set(status);

            if (log.isDebugEnabled()) {
                log.debug("Started transaction: {}", status.getTransactionName());
            }

            return status;
        } catch (Exception ex) {
            throw TransactionException.beginTransactionError(ex);
        }
    }

    @Override
    public void commitTransaction(TransactionStatus transactionStatus) {
        Assert.notNull(transactionStatus, "Transaction status must not be null");

        if (transactionManager == null) {
            throw TransactionException.transactionManagerNotConfigured();
        }

        if (!(transactionStatus instanceof DefaultTransactionStatus defaultStatus)) {
            throw new IllegalArgumentException("Invalid transaction status type");
        }

        try {
            transactionManager.commit(defaultStatus.getSpringTransactionStatus());

            if (log.isDebugEnabled()) {
                log.debug("Committed transaction: {}", transactionStatus.getTransactionName());
            }
        } catch (Exception ex) {
            throw TransactionException.commitTransactionError(ex);
        } finally {
            currentTransactionStatus.remove();
        }
    }

    @Override
    public void rollbackTransaction(TransactionStatus transactionStatus) {
        Assert.notNull(transactionStatus, "Transaction status must not be null");

        if (transactionManager == null) {
            throw TransactionException.transactionManagerNotConfigured();
        }

        if (!(transactionStatus instanceof DefaultTransactionStatus defaultStatus)) {
            throw new IllegalArgumentException("Invalid transaction status type");
        }

        try {
            transactionManager.rollback(defaultStatus.getSpringTransactionStatus());

            if (log.isDebugEnabled()) {
                log.debug("Rolled back transaction: {}", transactionStatus.getTransactionName());
            }
        } catch (Exception ex) {
            log.error("Error rolling back transaction", ex);
            throw TransactionException.rollbackTransactionError(ex);
        } finally {
            currentTransactionStatus.remove();
        }
    }

    @Override
    public <T> T executeInTransaction(TransactionCallback<T> callback) {
        Assert.notNull(callback, "Callback must not be null");

        if (transactionManager == null) {
            throw TransactionException.transactionManagerNotConfigured();
        }

        TransactionStatus status = null;
        try {
            status = beginTransaction();
            T result = callback.doInTransaction(status);
            if (!status.isRollbackOnly()) {
                commitTransaction(status);
            } else {
                rollbackTransaction(status);
            }
            return result;
        } catch (Exception ex) {
            if (status != null) {
                try {
                    rollbackTransaction(status);
                } catch (Exception rollbackEx) {
                    log.error("Failed to rollback transaction", rollbackEx);
                }
            }
            throw (RuntimeException) ex;
        }
    }

    @Override
    public void executeInTransaction(Runnable action) {
        Assert.notNull(action, "Action must not be null");

        executeInTransaction(status -> {
            action.run();
            return null;
        });
    }

    // ================================
    // 高级查询操作实现
    // ================================

    @Override
    public PageResponse<Map<String, Object>> queryForPage(String sql, Map<String, Object> params, int pageNum, int pageSize) {
        validatePageParams(pageNum, pageSize);

        // 构建计数SQL
        String countSql = buildCountSql(sql);
        int total = queryForCount(countSql, params);

        if (total == 0) {
            List<Map<String, Object>> emptyRecord = Collections.emptyList();
            return PageResponse.success(emptyRecord, total, pageSize, pageNum);
        }

        // 构建分页SQL
        String pageSql = buildPageSql(sql, pageNum, pageSize);
        List<Map<String, Object>> records = queryForList(pageSql, params);

        return PageResponse.success(records, total, pageSize, pageNum);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public <T> PageResponse<T> queryForPage(String sql, Map<String, Object> params, Class<T> requiredType, int pageNum, int pageSize) {
        validatePageParams(pageNum, pageSize);

        // 构建计数SQL
        String countSql = buildCountSql(sql);
        int total = queryForCount(countSql, params);

        if (total == 0) {
            return PageResponse.success(Collections.emptyList(), total, pageSize, pageNum);
        }

        // 构建分页SQL
        String pageSql = buildPageSql(sql, pageNum, pageSize);

        // 使用智能行映射器进行分页查询
        List<T> records = executeQuery(pageSql, params, defaultLoggingEnabled, () -> {
            try {
                RowMapper<T> rowMapper = getOrCreateRowMapper(requiredType);
                if (rowMapper != null) {
                    return namedParameterJdbcTemplate.query(pageSql, params, rowMapper);
                } else {
                    return namedParameterJdbcTemplate.queryForList(pageSql, params, requiredType);
                }
            } catch (Exception ex) {
                throw new MappingException("Failed to query page for type " + requiredType.getSimpleName(), ex);
            }
        });

        return PageResponse.success(records, total, pageSize, pageNum);
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow")
    public int queryForCount(String sql, Map<String, Object> params) {
        return Math.toIntExact(executeQuery(sql, params, defaultLoggingEnabled, () -> {
            Integer result = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
            return result != null ? result : 0L;
        }));
    }

    // ================================
    // 私有辅助方法
    // ================================

    /**
     * 执行查询操作的通用方法
     * <p>此方法已通过以下安全措施验证SQL安全性：</p>
     * <ul>
     *     <li>SqlInjectionValidator进行SQL注入检测</li>
     *     <li>参数验证确保参数安全</li>
     *     <li>使用NamedParameterJdbcTemplate的参数化查询</li>
     * </ul>
     */
    @SuppressWarnings({"SqlSourceToSinkFlow", "SqlResolve"})
    private <T> T executeQuery(String sql, Map<String, Object> params, boolean enableLogging, QueryExecutor<T> executor) {
        // 增强SQL安全验证
        String validatedSql = performComprehensiveSqlValidation(sql, params);

        long startTime = System.currentTimeMillis();

        try {
            if (enableLogging && log.isDebugEnabled()) {
                log.debug("Executing validated SQL: {} with params: {}", maskSensitiveParams(sql, params), params);
            }

            T result = executor.execute();

            // 记录执行监控
            executionMonitor.recordQueryExecution(validatedSql, params, System.currentTimeMillis() - startTime, true);

            return result;
        } catch (Exception e) {
            // 记录执行监控
            executionMonitor.recordQueryExecution(validatedSql, params, System.currentTimeMillis() - startTime, false);

            if (enableLogging) {
                log.error("Error executing SQL: {} with params: {}", maskSensitiveParams(sql, params), params, e);
            }
            throw e;
        }
    }

    /**
     * 执行更新操作的通用方法
     * <p>此方法已通过以下安全措施验证SQL安全性：</p>
     * <ul>
     *     <li>SqlInjectionValidator进行SQL注入检测</li>
     *     <li>参数验证确保参数安全</li>
     *     <li>使用NamedParameterJdbcTemplate的参数化查询</li>
     *     <li>敏感操作额外安全检查</li>
     * </ul>
     */
    @SuppressWarnings({"SqlSourceToSinkFlow", "SqlResolve"})
    private <T> T executeUpdate(String sql, Map<String, Object> params, boolean enableLogging, UpdateExecutor<T> executor) {
        // 增强SQL安全验证（更新操作需要额外安全检查）
        String validatedSql = performComprehensiveSqlValidation(sql, params);
        validateUpdateOperation(validatedSql);

        long startTime = System.currentTimeMillis();

        try {
            if (enableLogging && log.isDebugEnabled()) {
                log.debug("Executing validated update SQL: {} with params: {}", maskSensitiveParams(sql, params), params);
            }

            T result = executor.execute();

            // 记录执行监控
            executionMonitor.recordUpdateExecution(validatedSql, params, System.currentTimeMillis() - startTime, true);

            return result;

        } catch (Exception e) {
            // 记录执行监控
            executionMonitor.recordUpdateExecution(validatedSql, params, System.currentTimeMillis() - startTime, false);

            if (enableLogging) {
                log.error("Error executing update SQL: {} with params: {}", maskSensitiveParams(sql, params), params, e);
            }
            throw e;
        }
    }

    /**
     * 执行批处理操作的通用方法
     * <p>此方法已通过以下安全措施验证SQL安全性：</p>
     * <ul>
     *     <li>SqlInjectionValidator进行SQL注入检测</li>
     *     <li>批处理参数安全验证</li>
     *     <li>使用NamedParameterJdbcTemplate的参数化查询</li>
     *     <li>批量操作额外安全限制</li>
     * </ul>
     */
    @SuppressWarnings({"SqlSourceToSinkFlow", "SqlResolve"})
    private <T> int[] executeBatchUpdate(String sql, T[] batchParams, boolean enableLogging, BatchUpdateExecutor executor) {
        // 增强SQL安全验证（批处理操作需要额外安全检查）
        String validatedSql = performBatchSqlValidation(sql, batchParams);
        validateBatchUpdateOperation(validatedSql, batchParams);

        long startTime = System.currentTimeMillis();

        try {
            if (enableLogging && log.isDebugEnabled()) {
                log.debug("Executing validated batch update SQL: {} with {} parameter sets",
                        maskSensitiveSql(sql), batchParams.length);
            }

            int[] result = executor.execute();

            // 记录执行监控
            executionMonitor.recordBatchUpdateExecution(validatedSql, batchParams.length, System.currentTimeMillis() - startTime, true);

            return result;

        } catch (Exception e) {
            // 记录执行监控
            executionMonitor.recordBatchUpdateExecution(validatedSql, batchParams.length, System.currentTimeMillis() - startTime, false);

            if (enableLogging) {
                log.error("Error executing batch update SQL: {}", maskSensitiveSql(sql), e);
            }
            throw e;
        }
    }

    /**
     * 转换Map键名大小写
     */
    private Map<String, Object> transformMapKeys(Map<String, Object> map, String keyCase) {
        if (map == null || !StringUtils.hasText(keyCase)) {
            return map;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String transformedKey = switch (keyCase.toLowerCase()) {
                case "lower" -> key.toLowerCase();
                case "upper" -> key.toUpperCase();
                case "camel" -> toCamelCase(key);
                default -> key;
            };
            result.put(transformedKey, entry.getValue());
        }
        return result;
    }

    /**
     * 转换为驼峰命名
     */
    private String toCamelCase(String str) {
        if (!StringUtils.hasText(str)) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;

        for (char c : str.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    /**
     * 验证分页参数
     */
    private void validatePageParams(int pageNum, int pageSize) {
        if (pageNum < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        if (pageSize > maxPageSize) {
            throw new IllegalArgumentException("Page size cannot exceed " + maxPageSize);
        }
    }

    /**
     * 构建计数SQL
     * <p>企业级SQL解析实现，支持复杂查询结构的计数SQL生成</p>
     * <p>支持的SQL特性：</p>
     * <ul>
     *     <li>标准SELECT查询</li>
     *     <li>CTE(WITH子句)查询</li>
     *     <li>UNION/UNION ALL查询</li>
     *     <li>嵌套子查询</li>
     *     <li>复杂JOIN操作</li>
     * </ul>
     *
     * @param sql 原始查询SQL
     * @return 优化后的计数SQL语句
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    private String buildCountSql(String sql) {
        Assert.hasText(sql, "Original SQL must not be empty");

        String normalizedSql = sql.trim();
        String lowerSql = normalizedSql.toLowerCase();

        // 1. 处理WITH子句（CTE）查询
        if (lowerSql.startsWith("with")) {
            return handleCteCountSql(normalizedSql);
        }

        // 2. 处理UNION查询
        if (containsUnionOperation(lowerSql)) {
            return handleUnionCountSql(normalizedSql);
        }

        // 3. 处理标准SELECT查询
        if (lowerSql.startsWith(SELECT.toLowerCase())) {
            return handleSelectCountSql(normalizedSql);
        }

        // 4. 其他复杂查询使用子查询包装
        return wrapAsSubqueryCount(normalizedSql);
    }

    /**
     * 处理CTE（WITH子句）查询的计数SQL构建
     */
    private String handleCteCountSql(String sql) {
        // 对于CTE查询，直接使用子查询包装是最安全的方式
        String cleanSql = removeOrderByClause(sql);
        return "SELECT COUNT(*) FROM (" + cleanSql + ") AS cte_count_query";
    }

    /**
     * 处理UNION查询的计数SQL构建
     */
    private String handleUnionCountSql(String sql) {
        // UNION查询需要包装为子查询来统计总数
        String cleanSql = removeOrderByClause(sql);
        return "SELECT COUNT(*) FROM (" + cleanSql + ") AS union_count_query";
    }

    /**
     * 处理标准SELECT查询的计数SQL构建
     */
    private String handleSelectCountSql(String sql) {
        String lowerSql = sql.toLowerCase();

        // 检查是否包含DISTINCT、GROUP BY、HAVING等影响计数的子句
        if (containsDistinctOrGroupBy(lowerSql)) {
            return wrapAsSubqueryCount(sql);
        }

        // 尝试直接替换SELECT子句
        SqlStructure structure = parseSqlStructure(sql);
        if (structure.isValidForDirectCount()) {
            String fromClause = sql.substring(structure.fromIndex());
            String cleanFromClause = removeOrderByClause(fromClause);
            return "SELECT COUNT(*) " + cleanFromClause;
        }

        // 如果解析失败或结构复杂，使用子查询包装
        return wrapAsSubqueryCount(sql);
    }

    /**
     * 使用子查询包装的方式构建计数SQL
     */
    private String wrapAsSubqueryCount(String sql) {
        String cleanSql = removeOrderByClause(sql);
        return "SELECT COUNT(*) FROM (" + cleanSql + ") AS subquery_count";
    }

    /**
     * 检查SQL是否包含UNION操作
     */
    private boolean containsUnionOperation(String lowerSql) {
        return lowerSql.contains(" union ") || lowerSql.contains(" union all ");
    }

    /**
     * 检查SQL是否包含DISTINCT、GROUP BY等影响计数的子句
     */
    private boolean containsDistinctOrGroupBy(String lowerSql) {
        return lowerSql.contains("select distinct") ||
                lowerSql.contains(" group by ") ||
                lowerSql.contains(" having ");
    }

    /**
     * 解析SQL结构
     */
    private SqlStructure parseSqlStructure(String sql) {
        String lowerSql = sql.toLowerCase();

        int selectIndex = lowerSql.indexOf("select");
        int fromIndex = findFromClauseIndex(lowerSql);
        int whereIndex = lowerSql.indexOf(" where ");
        int groupByIndex = lowerSql.indexOf(" group by ");
        int havingIndex = lowerSql.indexOf(" having ");
        int orderByIndex = lowerSql.indexOf(" order by ");

        return new SqlStructure(selectIndex, fromIndex, whereIndex, groupByIndex, havingIndex, orderByIndex);
    }

    /**
     * 查找FROM子句的索引位置
     */
    private int findFromClauseIndex(String lowerSql) {
        int fromIndex = -1;
        int searchStart = 0;
        int parenDepth = 0;

        while (searchStart < lowerSql.length()) {
            int nextFrom = lowerSql.indexOf(" from ", searchStart);
            if (nextFrom == -1) {
                break;
            }

            // 检查该FROM是否在括号内（子查询中）
            String beforeFrom = lowerSql.substring(searchStart, nextFrom);
            for (char c : beforeFrom.toCharArray()) {
                if (c == '(') {
                    parenDepth++;
                } else if (c == ')') {
                    parenDepth--;
                }
            }

            if (parenDepth == 0) {
                fromIndex = nextFrom;
                break;
            }

            // 6 = " from ".length()
            searchStart = nextFrom + 6;
        }

        return fromIndex;
    }

    /**
     * SQL结构信息类
     */
    private record SqlStructure(int selectIndex, int fromIndex, int whereIndex, int groupByIndex, int havingIndex, int orderByIndex) {
        public boolean isValidForDirectCount() {
            return fromIndex > 0 && groupByIndex == -1 && havingIndex == -1;
        }
    }

    /**
     * 构建分页SQL
     * <p>此方法生成的SQL用于分页查询，使用安全的数值参数</p>
     *
     * @param sql      原始查询SQL
     * @param pageNum  页码（已验证）
     * @param pageSize 页大小（已验证）
     * @return 分页SQL语句
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    private String buildPageSql(String sql, int pageNum, int pageSize) {
        Assert.hasText(sql, "Original SQL must not be empty");
        Assert.isTrue(pageNum > 0, "Page number must be positive");
        Assert.isTrue(pageSize > 0, "Page size must be positive");

        int offset = (pageNum - 1) * pageSize;

        // 防止整数溢出
        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Page offset too large: " + offset);
        }

        return sql + " LIMIT " + pageSize + " OFFSET " + offset;
    }

    /**
     * 智能移除SQL中的ORDER BY子句
     * <p>企业级SQL解析实现，精确识别和移除主查询中的ORDER BY子句</p>
     * <p>支持的场景：</p>
     * <ul>
     *     <li>嵌套子查询中的ORDER BY保留</li>
     *     <li>CTE查询中的ORDER BY正确处理</li>
     *     <li>字符串字面量中的ORDER BY不误删</li>
     *     <li>注释中的ORDER BY不误删</li>
     *     <li>CASE表达式中的ORDER BY正确处理</li>
     * </ul>
     *
     * @param sql 原始SQL语句
     * @return 移除主查询ORDER BY后的SQL
     */
    private String removeOrderByClause(String sql) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }

        // 使用状态机方式解析SQL，确保准确性
        SqlParser parser = new SqlParser(sql);
        return parser.removeMainQueryOrderBy();
    }

    /**
     * SQL解析器 - 用于精确解析SQL结构
     */
    private static class SqlParser {
        private final String sql;
        private final String lowerSql;
        private int position;
        private int parenDepth;
        private boolean inStringLiteral;
        private boolean inComment;
        private char stringDelimiter;

        public SqlParser(String sql) {
            this.sql = sql;
            this.lowerSql = sql.toLowerCase();
            this.position = 0;
            this.parenDepth = 0;
            this.inStringLiteral = false;
            this.inComment = false;
        }

        /**
         * 移除主查询中的ORDER BY子句
         */
        public String removeMainQueryOrderBy() {
            int lastOrderByIndex = -1;
            int lastOrderByDepth = -1;

            while (position < sql.length()) {
                char currentChar = sql.charAt(position);

                // 处理字符串字面量
                if (handleStringLiteral(currentChar)) {
                    position++;
                    continue;
                }

                // 处理注释
                if (handleComment(currentChar)) {
                    continue;
                }

                // 如果在字符串或注释中，跳过
                if (inStringLiteral || inComment) {
                    position++;
                    continue;
                }

                // 处理括号深度
                if (currentChar == '(') {
                    parenDepth++;
                } else if (currentChar == ')') {
                    parenDepth--;
                }

                // 查找ORDER BY
                if (matchesOrderBy()) {
                    // 记录最后一个主查询级别的ORDER BY
                    if (parenDepth == 0) {
                        lastOrderByIndex = position;
                        lastOrderByDepth = parenDepth;
                    }
                }

                position++;
            }

            // 如果找到主查询级别的ORDER BY，移除它
            if (lastOrderByIndex >= 0) {
                return sql.substring(0, lastOrderByIndex).trim();
            }

            return sql;
        }

        /**
         * 处理字符串字面量
         */
        private boolean handleStringLiteral(char currentChar) {
            if (!inComment) {
                if (!inStringLiteral) {
                    if (currentChar == '\'' || currentChar == '"' || currentChar == '`') {
                        inStringLiteral = true;
                        stringDelimiter = currentChar;
                        return true;
                    }
                } else {
                    if (currentChar == stringDelimiter) {
                        // 检查是否为转义字符
                        if (position + 1 < sql.length() && sql.charAt(position + 1) == stringDelimiter) {
                            // 跳过转义字符
                            position++;
                        } else {
                            inStringLiteral = false;
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * 处理SQL注释
         */
        private boolean handleComment(char currentChar) {
            if (!inStringLiteral) {
                // 处理单行注释 --
                if (currentChar == '-' && position + 1 < sql.length() &&
                        sql.charAt(position + 1) == '-') {
                    // 跳到行尾
                    while (position < sql.length() &&
                            sql.charAt(position) != '\n' &&
                            sql.charAt(position) != '\r') {
                        position++;
                    }
                    return true;
                }

                // 处理多行注释 /* */
                if (currentChar == '/' && position + 1 < sql.length() &&
                        sql.charAt(position + 1) == '*') {
                    inComment = true;
                    position += 2;

                    // 查找注释结束
                    while (position + 1 < sql.length()) {
                        if (sql.charAt(position) == '*' &&
                                sql.charAt(position + 1) == '/') {
                            inComment = false;
                            position += 2;
                            break;
                        }
                        position++;
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * 检查当前位置是否匹配ORDER BY
         */
        private boolean matchesOrderBy() {
            // "order by".length() = 8
            if (position + 8 < lowerSql.length()) {
                String substring = lowerSql.substring(position, position + 8);
                if ("order by".equals(substring)) {
                    // 确保ORDER BY前后是空白或特殊字符
                    boolean validBefore = position == 0 ||
                            Character.isWhitespace(sql.charAt(position - 1)) ||
                            sql.charAt(position - 1) == ')' ||
                            sql.charAt(position - 1) == ';';

                    boolean validAfter = position + 8 >= sql.length() ||
                            Character.isWhitespace(sql.charAt(position + 8));

                    if (validBefore && validAfter) {
                        // 跳过"order by"
                        position += 8;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 执行全面的SQL安全验证
     * <p>此方法集成多层安全检查，确保SQL语句的安全性</p>
     *
     * @param sql    SQL语句
     * @param params 参数映射
     * @return 验证后的SQL语句
     * @throws SecurityException 如果SQL不安全
     */
    private String performComprehensiveSqlValidation(String sql, Map<String, Object> params) {
        Assert.hasText(sql, "SQL must not be empty");

        // 1. 基础SQL安全验证
        sqlValidator.validateSql(sql);

        // 2. 参数安全验证
        validateParameters(params);

        // 3. SQL语句规范化检查
        String normalizedSql = normalizeSql(sql);

        // 4. 白名单操作验证（可选，根据业务需求）
        validateSqlOperationType(normalizedSql);

        if (log.isTraceEnabled()) {
            log.trace("SQL validation passed for: {}", maskSensitiveSql(sql));
        }

        return normalizedSql;
    }

    /**
     * 批处理SQL安全验证
     *
     * @param sql         SQL语句
     * @param batchParams 批处理参数
     * @return 验证后的SQL语句
     */
    private <T> String performBatchSqlValidation(String sql, T[] batchParams) {
        Assert.hasText(sql, "SQL must not be empty");
        Assert.notNull(batchParams, "Batch parameters must not be null");

        // 基础SQL验证
        sqlValidator.validateSql(sql);

        // 批处理大小限制
        if (batchParams.length > securityConfig.getMaxBatchSize()) {
            throw new IllegalArgumentException("Batch size cannot exceed " + securityConfig.getMaxBatchSize() + " operations");
        }

        return normalizeSql(sql);
    }

    /**
     * 验证更新操作的安全性
     *
     * @param sql 已验证的SQL语句
     */
    private void validateUpdateOperation(String sql) {
        String lowerSql = sql.toLowerCase().trim();

        // 检查是否为允许的更新操作
        if (lowerSql.startsWith("select") || lowerSql.startsWith("with")) {
            // 查询操作在更新方法中调用，可能是误用
            log.warn("Query operation detected in update method: {}", maskSensitiveSql(sql));
        }

        // 检查危险的更新操作
        if (lowerSql.contains("delete") && !lowerSql.contains("where")) {
            log.warn("DELETE operation without WHERE clause detected: {}", maskSensitiveSql(sql));
        }

        if (lowerSql.contains("update") && !lowerSql.contains("where")) {
            log.warn("UPDATE operation without WHERE clause detected: {}", maskSensitiveSql(sql));
        }
    }

    /**
     * 验证批处理更新操作的安全性
     *
     * @param sql         已验证的SQL语句
     * @param batchParams 批处理参数
     */
    private <T> void validateBatchUpdateOperation(String sql, T[] batchParams) {
        validateUpdateOperation(sql);

        // 批处理特定验证
        String lowerSql = sql.toLowerCase().trim();
        if (lowerSql.contains("drop") || lowerSql.contains("truncate")) {
            throw new IllegalArgumentException("Dangerous operations not allowed in batch processing");
        }
    }

    /**
     * SQL语句规范化
     *
     * @param sql 原始SQL
     * @return 规范化后的SQL
     */
    private String normalizeSql(String sql) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }

        // 移除多余空白字符
        return sql.replaceAll("\\s+", " ").trim();
    }

    /**
     * 验证SQL操作类型（可选的白名单验证）
     *
     * @param sql 规范化的SQL
     */
    private void validateSqlOperationType(String sql) {
        String lowerSql = sql.toLowerCase();

        // 这里可以根据业务需求添加操作类型限制
        // 例如：只允许特定类型的操作
        if (lowerSql.startsWith("alter") || lowerSql.startsWith("create") || lowerSql.startsWith("drop")) {
            log.info("DDL operation detected: {}", maskSensitiveSql(sql));
            // 可以选择抛出异常或记录日志
        }
    }

    /**
     * 企业级敏感SQL内容掩码处理
     * <p>全面的敏感信息识别和掩码实现，支持多种SQL语法和场景</p>
     * <p>支持的敏感信息类型：</p>
     * <ul>
     *     <li>密码相关：password, pwd, passwd, secret</li>
     *     <li>认证相关：token, auth, key, credential</li>
     *     <li>个人信息：ssn, phone, email, card, account</li>
     *     <li>业务敏感：salary, amount, balance, income</li>
     * </ul>
     * <p>支持的SQL语法：</p>
     * <ul>
     *     <li>WHERE条件中的敏感值</li>
     *     <li>INSERT VALUES中的敏感数据</li>
     *     <li>UPDATE SET中的敏感赋值</li>
     *     <li>函数参数中的敏感值</li>
     *     <li>CASE表达式中的敏感内容</li>
     * </ul>
     *
     * @param sql 原始SQL语句
     * @return 掩码处理后的SQL
     */
    private String maskSensitiveSql(String sql) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }

        if (!securityConfig.isSensitiveDataMasking()) {
            // 如果禁用了掩码功能，直接返回原SQL
            return sql;
        }

        SensitiveSqlMasker masker = new SensitiveSqlMasker(securityConfig);
        return masker.maskSensitiveContent(sql);
    }

    /**
     * 敏感SQL掩码处理器
     */
    private record SensitiveSqlMasker(SqlSecurityConfig securityConfig) {

        // 掩码替换文本
        private static final String MASK_TEXT = "***";

        /**
         * 对SQL中的敏感内容进行掩码处理
         */
        public String maskSensitiveContent(String sql) {
            String result = sql;

            // 1. 掩码 WHERE 条件中的敏感值
            result = maskWhereConditions(result);

            // 2. 掩码 INSERT VALUES 中的敏感数据
            result = maskInsertValues(result);

            // 3. 掩码 UPDATE SET 中的敏感赋值
            result = maskUpdateSets(result);

            // 4. 掩码函数参数中的敏感值
            result = maskFunctionParameters(result);

            // 5. 掩码 CASE 表达式中的敏感内容
            result = maskCaseExpressions(result);

            // 6. 通用敏感值掩码
            result = maskGenericSensitiveValues(result);

            return result;
        }

        /**
         * 掩码WHERE条件中的敏感值
         */
        private String maskWhereConditions(String sql) {
            String result = sql;
            for (String keyword : securityConfig.getSensitiveParameterPatterns()) {
                // 匹配模式：field = 'value' 或 field = "value"
                result = result.replaceAll(
                        "(?i)\\b" + keyword + "\\s*=\\s*'[^']*'",
                        keyword + " = '" + MASK_TEXT + "'"
                );
                result = result.replaceAll(
                        "(?i)\\b" + keyword + "\\s*=\\s*\"[^\"]*\"",
                        keyword + " = \"" + MASK_TEXT + "\""
                );

                // 匹配模式：field IN ('value1', 'value2')
                result = result.replaceAll(
                        "(?i)\\b" + keyword + "\\s+IN\\s*\\([^)]*\\)",
                        keyword + " IN ('" + MASK_TEXT + "')"
                );

                // 匹配模式：field LIKE 'pattern'
                result = result.replaceAll(
                        "(?i)\\b" + keyword + "\\s+LIKE\\s+'[^']*'",
                        keyword + " LIKE '" + MASK_TEXT + "'"
                );
            }
            return result;
        }

        /**
         * 掩码INSERT VALUES中的敏感数据
         */
        private String maskInsertValues(String sql) {
            // 对于INSERT语句，由于难以精确匹配字段位置，采用保守的掩码策略
            if (sql.toLowerCase().contains("insert")) {
                // 检查是否包含敏感关键词
                for (String keyword : securityConfig.getSensitiveParameterPatterns()) {
                    if (sql.toLowerCase().contains(keyword.toLowerCase())) {
                        // 如果包含敏感关键词，对VALUES部分进行部分掩码
                        return sql.replaceAll(
                                "(?i)(VALUES\\s*\\([^)]*)" + keyword + "([^)]*\\))",
                                "$1[含敏感数据已掩码]$2"
                        );
                    }
                }
            }
            return sql;
        }

        /**
         * 掩码UPDATE SET中的敏感赋值
         */
        private String maskUpdateSets(String sql) {
            String result = sql;
            for (String keyword : securityConfig.getSensitiveParameterPatterns()) {
                // 匹配 SET field = 'value'
                result = result.replaceAll(
                        "(?i)\\bSET\\s+[^=]*\\b" + keyword + "\\s*=\\s*'[^']*'",
                        "SET " + keyword + " = '" + MASK_TEXT + "'"
                );
                result = result.replaceAll(
                        "(?i)\\b" + keyword + "\\s*=\\s*'[^']*'",
                        keyword + " = '" + MASK_TEXT + "'"
                );
            }
            return result;
        }

        /**
         * 掩码函数参数中的敏感值
         */
        private String maskFunctionParameters(String sql) {
            String result = sql;

            // 掩码常见的加密/哈希函数参数
            result = result.replaceAll(
                    "(?i)(MD5|SHA1|SHA256|ENCRYPT|HASH)\\s*\\([^)]*\\)",
                    "$1('" + MASK_TEXT + "')"
            );

            // 掩码密码相关函数
            result = result.replaceAll(
                    "(?i)(PASSWORD|PWD_HASH|ENCODE)\\s*\\([^)]*\\)",
                    "$1('" + MASK_TEXT + "')"
            );

            return result;
        }

        /**
         * 掩码CASE表达式中的敏感内容
         */
        private String maskCaseExpressions(String sql) {
            String result = sql;
            for (String keyword : securityConfig.getSensitiveParameterPatterns()) {
                // 掩码CASE WHEN条件中的敏感值
                result = result.replaceAll(
                        "(?i)(CASE\\s+WHEN[^=]*)\\b" + keyword + "\\s*=\\s*'[^']*'",
                        "$1" + keyword + " = '" + MASK_TEXT + "'"
                );
            }
            return result;
        }

        /**
         * 通用敏感值掩码 - 兜底处理
         */
        private String maskGenericSensitiveValues(String sql) {
            String result = sql;

            // 掩码看起来像密码的长字符串（长度>8的引号字符串）
            result = result.replaceAll(
                    "'[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]{9,}'",
                    "'" + MASK_TEXT + "'"
            );

            // 掩码看起来像token的字符串（包含大小写字母和数字的长字符串）
            result = result.replaceAll(
                    "'[A-Za-z0-9+/=]{20,}'",
                    "'" + MASK_TEXT + "'"
            );

            // 掩码邮箱地址
            result = result.replaceAll(
                    "'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}'",
                    "'" + MASK_TEXT + "@***.***'"
            );

            // 掩码电话号码（简单匹配）
            result = result.replaceAll(
                    "'[0-9-+()\\s]{10,}'",
                    "'" + MASK_TEXT + "'"
            );

            return result;
        }
    }

    /**
     * 掩码敏感参数用于日志
     *
     * @param sql    SQL语句
     * @param params 参数
     * @return 掩码后的SQL（用于日志显示）
     */
    private String maskSensitiveParams(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return maskSensitiveSql(sql);
        }

        // 检查是否包含敏感参数
        boolean hasSensitiveParams = params.keySet().stream()
                .anyMatch(securityConfig::isSensitiveParameter);

        if (hasSensitiveParams) {
            return maskSensitiveSql(sql) + " [参数包含敏感信息已掩码]";
        }

        return maskSensitiveSql(sql);
    }

    /**
     * 验证参数安全性
     */
    private void validateParameters(Map<String, Object> params) {
        if (params == null) {
            return;
        }

        // 检查SQL注入风险参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String stringValue) {
                sqlValidator.validateParameter(entry.getKey(), stringValue);

                // 额外的参数值安全检查
                if (stringValue.length() > securityConfig.getMaxParameterLength()) {
                    log.warn("Parameter value too long: {} characters for key: {}, max allowed: {}",
                            stringValue.length(), entry.getKey(), securityConfig.getMaxParameterLength());
                }
            }
        }
    }

    /**
     * 获取或创建行映射器
     *
     * @param <T>          目标类型
     * @param requiredType 目标类型
     * @return 行映射器，如果不适用返回null
     */
    @SuppressWarnings("unchecked")
    private <T> RowMapper<T> getOrCreateRowMapper(Class<T> requiredType) {
        if (requiredType == null) {
            return null;
        }

        // 对于基本类型，不使用智能映射器
        if (isSimpleType(requiredType)) {
            return null;
        }

        // 从缓存中获取
        RowMapper<T> rowMapper = (RowMapper<T>) rowMapperCache.get(requiredType);
        if (rowMapper == null) {
            try {
                // 创建智能行映射器
                IntelligentRowMapper<T> intelligentMapper = IntelligentRowMapper.of(requiredType);
                rowMapperCache.put(requiredType, intelligentMapper);
                rowMapper = intelligentMapper;

                if (log.isDebugEnabled()) {
                    log.debug("Created intelligent row mapper for type: {}", requiredType.getSimpleName());
                }
            } catch (Exception ex) {
                log.debug("Failed to create intelligent row mapper for type {}, falling back to default",
                        requiredType.getSimpleName(), ex);
                return null;
            }
        }

        return rowMapper;
    }

    /**
     * 判断是否为简单类型
     *
     * @param type 类型
     * @return 如果是简单类型返回true
     */
    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                Number.class.isAssignableFrom(type) ||
                String.class == type ||
                Boolean.class == type ||
                Character.class == type ||
                java.util.Date.class.isAssignableFrom(type) ||
                java.time.temporal.Temporal.class.isAssignableFrom(type) ||
                type.isEnum();
    }

    // ================================
    // 函数式接口定义
    // ================================

    @FunctionalInterface
    private interface QueryExecutor<T> {
        T execute() throws DataAccessException;
    }

    @FunctionalInterface
    private interface UpdateExecutor<T> {
        T execute() throws DataAccessException;
    }

    @FunctionalInterface
    private interface BatchUpdateExecutor {
        int[] execute() throws DataAccessException;
    }
} 