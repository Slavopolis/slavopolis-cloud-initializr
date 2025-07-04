package club.slavopolis.file.repository;

import club.slavopolis.file.domain.FileUploadSession;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;

/**
 * 文件上传会话数据访问接口
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface FileUploadSessionRepository {

    /**
     * 保存上传会话
     *
     * @param namedJdbc JDBC模板
     * @param session   上传会话
     * @return 上传会话ID
     */
    String save(EnhancedJdbcTemplate namedJdbc, FileUploadSession session);

    /**
     * 根据上传会话ID查询会话信息
     *
     * @param namedJdbc JDBC模板
     * @param uploadId  上传会话ID
     * @return 上传会话信息
     */
    FileUploadSession findById(EnhancedJdbcTemplate namedJdbc, String uploadId);

    /**
     * 更新上传进度
     *
     * @param namedJdbc       JDBC模板
     * @param uploadId        上传会话ID
     * @param uploadedChunks  已上传分片数
     * @return 是否更新成功
     */
    boolean updateProgress(EnhancedJdbcTemplate namedJdbc, String uploadId, int uploadedChunks);

    /**
     * 标记上传会话为完成状态
     *
     * @param namedJdbc JDBC模板
     * @param uploadId  上传会话ID
     * @return 是否标记成功
     */
    boolean markCompleted(EnhancedJdbcTemplate namedJdbc, String uploadId);

    /**
     * 标记上传会话为失败状态
     *
     * @param namedJdbc    JDBC模板
     * @param uploadId     上传会话ID
     * @param errorMessage 错误信息
     * @return 是否标记成功
     */
    boolean markFailed(EnhancedJdbcTemplate namedJdbc, String uploadId, String errorMessage);

    /**
     * 标记上传会话为取消状态
     *
     * @param namedJdbc JDBC模板
     * @param uploadId  上传会话ID
     * @return 是否标记成功
     */
    boolean markCancelled(EnhancedJdbcTemplate namedJdbc, String uploadId);

    /**
     * 删除上传会话
     *
     * @param namedJdbc JDBC模板
     * @param uploadId  上传会话ID
     * @return 是否删除成功
     */
    boolean delete(EnhancedJdbcTemplate namedJdbc, String uploadId);
} 