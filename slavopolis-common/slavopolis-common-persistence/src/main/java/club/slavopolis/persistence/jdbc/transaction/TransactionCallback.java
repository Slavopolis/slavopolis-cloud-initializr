package club.slavopolis.persistence.jdbc.transaction;

import org.springframework.dao.DataAccessException;

/**
 * 事务回调接口
 * <p>在事务中执行业务逻辑的回调接口</p>
 *
 * @param <T> 返回类型
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@FunctionalInterface
public interface TransactionCallback<T> {

    /**
     * 在事务中执行业务逻辑
     * 
     * @param status 事务状态
     * @return 执行结果
     * @throws DataAccessException 数据访问异常
     */
    T doInTransaction(TransactionStatus status) throws DataAccessException;
} 