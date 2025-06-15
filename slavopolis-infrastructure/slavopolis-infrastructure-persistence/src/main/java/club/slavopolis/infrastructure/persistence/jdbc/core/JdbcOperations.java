package club.slavopolis.infrastructure.persistence.jdbc.core;

import club.slavopolis.common.core.result.PageResult;
import club.slavopolis.infrastructure.persistence.jdbc.parameter.TypeSafeParameterSource;
import club.slavopolis.infrastructure.persistence.jdbc.transaction.TransactionCallback;
import club.slavopolis.infrastructure.persistence.jdbc.transaction.TransactionStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Blob;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 核心JDBC操作接口
 * <p>提供类型安全的数据库操作抽象，包括查询、更新、批处理、存储过程调用等功能</p>
 * <p>支持编程式事务管理，参数绑定验证，结果集智能映射</p>
 * 
 * @author Slavopolis Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface JdbcOperations {

    // ================================
    // 查询操作 - 基本类型返回
    // ================================

    /**
     * 查询单个整数值
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Integer queryForInt(String sql, Map<String, Object> params);

    /**
     * 查询单个整数值（带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Integer queryForInt(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 查询单个长整数值
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Long queryForLong(String sql, Map<String, Object> params);

    /**
     * 查询单个长整数值（带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Long queryForLong(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 查询单个字符串值
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    String queryForString(String sql, Map<String, Object> params);

    /**
     * 查询单个字符串值（带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    String queryForString(String sql, Map<String, Object> params, boolean enableLogging);

    // ================================
    // 查询操作 - 对象返回
    // ================================

    /**
     * 查询单个对象
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param requiredType 目标类型
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    <T> T queryForObject(String sql, Map<String, Object> params, Class<T> requiredType);

    /**
     * 查询单个对象（带日志控制）
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param requiredType 目标类型
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    <T> T queryForObject(String sql, Map<String, Object> params, Class<T> requiredType, boolean enableLogging);

    /**
     * 查询单个对象（使用自定义RowMapper）
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param rowMapper 行映射器
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    <T> T queryForObject(String sql, Map<String, Object> params, RowMapper<T> rowMapper);

    /**
     * 查询单个对象（使用自定义RowMapper，带日志控制）
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param rowMapper 行映射器
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    <T> T queryForObject(String sql, Map<String, Object> params, RowMapper<T> rowMapper, boolean enableLogging);

    /**
     * 查询单个对象，返回Optional包装
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param requiredType 目标类型
     * @return 查询结果的Optional包装
     * @throws DataAccessException 数据访问异常
     */
    <T> Optional<T> queryForOptional(String sql, Map<String, Object> params, Class<T> requiredType);

    // ================================
    // 查询操作 - Map返回
    // ================================

    /**
     * 查询单行数据，返回Map
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return 查询结果Map，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> queryForMap(String sql, Map<String, Object> params);

    /**
     * 查询单行数据，返回Map（带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果Map，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> queryForMap(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 查询单行数据，返回Map（指定键名大小写）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param keyCase 键名大小写策略: "lower", "upper", "camel"
     * @return 查询结果Map，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> queryForMap(String sql, Map<String, Object> params, String keyCase);

    /**
     * 查询单行数据，返回Map（指定键名大小写，带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param keyCase 键名大小写策略: "lower", "upper", "camel"
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果Map，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> queryForMap(String sql, Map<String, Object> params, String keyCase, boolean enableLogging);

    // ================================
    // 查询操作 - List返回
    // ================================

    /**
     * 查询多行数据，返回List<Map>
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return 查询结果列表
     * @throws DataAccessException 数据访问异常
     */
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> params);

    /**
     * 查询多行数据，返回List<Map>（带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果列表
     * @throws DataAccessException 数据访问异异常
     */
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 查询多行数据，返回List<Map>（指定键名大小写）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param keyCase 键名大小写策略: "lower", "upper", "camel"
     * @return 查询结果列表
     * @throws DataAccessException 数据访问异常
     */
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, String keyCase);

    /**
     * 查询多行数据，返回List<Map>（指定键名大小写，带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param keyCase 键名大小写策略: "lower", "upper", "camel"
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果列表
     * @throws DataAccessException 数据访问异常
     */
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, String keyCase, boolean enableLogging);

    /**
     * 查询多行数据，返回对象列表
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param requiredType 目标类型
     * @return 查询结果对象列表
     * @throws DataAccessException 数据访问异常
     */
    <T> List<T> queryForList(String sql, Map<String, Object> params, Class<T> requiredType);

    /**
     * 查询多行数据，返回对象列表（带日志控制）
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param requiredType 目标类型
     * @param enableLogging 是否启用SQL执行日志
     * @return 查询结果对象列表
     * @throws DataAccessException 数据访问异常
     */
    <T> List<T> queryForList(String sql, Map<String, Object> params, Class<T> requiredType, boolean enableLogging);

    /**
     * 查询多行数据，返回对象列表（使用自定义RowMapper）
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param rowMapper 行映射器
     * @return 查询结果对象列表
     * @throws DataAccessException 数据访问异常
     */
    <T> List<T> queryForList(String sql, Map<String, Object> params, RowMapper<T> rowMapper);

    // ================================
    // 查询操作 - 二进制数据
    // ================================

    /**
     * 查询Blob数据
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return Blob对象，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Blob queryForBlob(String sql, Map<String, Object> params);

    /**
     * 查询Blob数据（带日志控制）
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return Blob对象，如果无结果返回null
     * @throws DataAccessException 数据访问异常
     */
    Blob queryForBlob(String sql, Map<String, Object> params, boolean enableLogging);

    // ================================
    // 更新操作
    // ================================

    /**
     * 执行更新操作（INSERT、UPDATE、DELETE）
     * 
     * @param sql SQL语句
     * @param params 命名参数
     * @return 受影响的行数
     * @throws DataAccessException 数据访问异常
     */
    int update(String sql, Map<String, Object> params);

    /**
     * 执行更新操作（带日志控制）
     * 
     * @param sql SQL语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 受影响的行数
     * @throws DataAccessException 数据访问异常
     */
    int update(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 执行更新操作并返回生成的主键
     * 
     * @param sql SQL语句
     * @param paramSource 参数源
     * @return 生成的主键值
     * @throws DataAccessException 数据访问异常
     */
    Number updateAndReturnKey(String sql, TypeSafeParameterSource paramSource);

    /**
     * 执行更新操作并返回生成的主键（带日志控制）
     * 
     * @param sql SQL语句
     * @param paramSource 参数源
     * @param enableLogging 是否启用SQL执行日志
     * @return 生成的主键值
     * @throws DataAccessException 数据访问异常
     */
    Number updateAndReturnKey(String sql, TypeSafeParameterSource paramSource, boolean enableLogging);

    // ================================
    // 批处理操作
    // ================================

    /**
     * 批量更新操作
     * 
     * @param sql SQL语句
     * @param batchParams 批量参数数组
     * @return 每个更新操作受影响的行数数组
     * @throws DataAccessException 数据访问异常
     */
    int[] batchUpdate(String sql, Map<String, Object>[] batchParams);

    /**
     * 批量更新操作（带日志控制）
     * 
     * @param sql SQL语句
     * @param batchParams 批量参数数组
     * @param enableLogging 是否启用SQL执行日志
     * @return 每个更新操作受影响的行数数组
     * @throws DataAccessException 数据访问异常
     */
    int[] batchUpdate(String sql, Map<String, Object>[] batchParams, boolean enableLogging);

    /**
     * 批量更新操作（使用TypeSafeParameterSource）
     * 
     * @param sql SQL语句
     * @param batchParams 批量参数源数组
     * @return 每个更新操作受影响的行数数组
     * @throws DataAccessException 数据访问异常
     */
    int[] batchUpdate(String sql, TypeSafeParameterSource[] batchParams);

    // ================================
    // 存储过程调用
    // ================================

    /**
     * 调用存储过程（无返回值）
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @throws DataAccessException 数据访问异常
     */
    void callProcedure(String sql, Map<String, Object> params);

    /**
     * 调用存储过程（无返回值，带日志控制）
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @throws DataAccessException 数据访问异常
     */
    void callProcedure(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 调用存储过程并返回Map结果
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @return 存储过程返回的结果Map
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> callProcedureForMap(String sql, Map<String, Object> params);

    /**
     * 调用存储过程并返回Map结果（带日志控制）
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 存储过程返回的结果Map
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> callProcedureForMap(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 调用存储过程并返回Map结果（指定键名大小写）
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @param keyCase 键名大小写策略: "lower", "upper", "camel"
     * @return 存储过程返回的结果Map
     * @throws DataAccessException 数据访问异常
     */
    Map<String, Object> callProcedureForMap(String sql, Map<String, Object> params, String keyCase);

    /**
     * 调用存储过程并返回List结果
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @return 存储过程返回的结果列表
     * @throws DataAccessException 数据访问异常
     */
    List<Map<String, Object>> callProcedureForList(String sql, Map<String, Object> params);

    /**
     * 调用存储过程并返回List结果（带日志控制）
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 存储过程返回的结果列表
     * @throws DataAccessException 数据访问异常
     */
    List<Map<String, Object>> callProcedureForList(String sql, Map<String, Object> params, boolean enableLogging);

    /**
     * 调用存储过程并返回字符串结果
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @return 存储过程返回的字符串结果
     * @throws DataAccessException 数据访问异常
     */
    String callProcedureForString(String sql, Map<String, Object> params);

    /**
     * 调用存储过程并返回字符串结果（带日志控制）
     * 
     * @param sql 存储过程调用语句
     * @param params 命名参数
     * @param enableLogging 是否启用SQL执行日志
     * @return 存储过程返回的字符串结果
     * @throws DataAccessException 数据访问异常
     */
    String callProcedureForString(String sql, Map<String, Object> params, boolean enableLogging);

    // ================================
    // 事务管理操作
    // ================================

    /**
     * 获取当前事务状态
     * 
     * @return 事务状态，如果无事务返回null
     */
    TransactionStatus getTransactionStatus();

    /**
     * 开始新事务
     * 
     * @return 事务状态
     * @throws DataAccessException 数据访问异常
     */
    TransactionStatus beginTransaction();

    /**
     * 提交事务
     * 
     * @param transactionStatus 事务状态
     * @throws DataAccessException 数据访问异常
     */
    void commitTransaction(TransactionStatus transactionStatus);

    /**
     * 回滚事务
     * 
     * @param transactionStatus 事务状态
     * @throws DataAccessException 数据访问异常
     */
    void rollbackTransaction(TransactionStatus transactionStatus);

    /**
     * 在事务中执行操作
     * 
     * @param <T> 返回类型
     * @param callback 事务回调
     * @return 回调执行结果
     * @throws DataAccessException 数据访问异常
     */
    <T> T executeInTransaction(TransactionCallback<T> callback);

    /**
     * 在事务中执行操作（无返回值）
     * 
     * @param action 要执行的操作
     * @throws DataAccessException 数据访问异常
     */
    void executeInTransaction(Runnable action);

    // ================================
    // 高级查询操作
    // ================================

    /**
     * 分页查询
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     * @throws DataAccessException 数据访问异常
     */
    PageResult.PageData<Map<String, Object>> queryForPage(String sql, Map<String, Object> params, int pageNum, int pageSize);

    /**
     * 分页查询（对象类型）
     * 
     * @param <T> 目标类型
     * @param sql SQL查询语句
     * @param params 命名参数
     * @param requiredType 目标类型
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     * @throws DataAccessException 数据访问异常
     */
    <T> PageResult.PageData<T> queryForPage(String sql, Map<String, Object> params, Class<T> requiredType, int pageNum, int pageSize);

    /**
     * 统计查询
     * 
     * @param sql SQL查询语句
     * @param params 命名参数
     * @return 统计结果
     * @throws DataAccessException 数据访问异常
     */
    long queryForCount(String sql, Map<String, Object> params);
} 