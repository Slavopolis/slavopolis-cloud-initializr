package club.slavopolis.file.repository;

import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.domain.request.FileListRequest;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息数据访问接口
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface FileInfoRepository {

    /**
     * 保存文件信息
     *
     * @param namedJdbc JDBC模板
     * @param fileInfo  文件信息
     * @return 保存后的文件信息
     */
    FileInfo save(EnhancedJdbcTemplate namedJdbc, FileInfo fileInfo);

    /**
     * 根据文件ID查询文件信息
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 文件信息
     */
    FileInfo findById(EnhancedJdbcTemplate namedJdbc, String fileId);

    /**
     * 根据文件哈希值查询文件信息
     *
     * @param namedJdbc JDBC模板
     * @param fileHash  文件哈希值
     * @return 文件信息
     */
    FileInfo findByHash(EnhancedJdbcTemplate namedJdbc, String fileHash);

    /**
     * 根据查询条件查询文件列表
     *
     * @param namedJdbc JDBC模板
     * @param request   查询请求
     * @return 文件信息列表
     */
    List<FileInfo> findByRequest(EnhancedJdbcTemplate namedJdbc, FileListRequest request);

    /**
     * 更新文件访问信息
     *
     * @param namedJdbc    JDBC模板
     * @param fileId       文件ID
     * @param accessTime   访问时间
     * @return 是否更新成功
     */
    boolean updateAccessInfo(EnhancedJdbcTemplate namedJdbc, String fileId, LocalDateTime accessTime);

    /**
     * 标记文件为已删除
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 是否标记成功
     */
    boolean markAsDeleted(EnhancedJdbcTemplate namedJdbc, String fileId);

    /**
     * 检查文件是否存在
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 是否存在
     */
    boolean existsById(EnhancedJdbcTemplate namedJdbc, String fileId);

    /**
     * 复制文件信息
     *
     * @param namedJdbc     JDBC模板
     * @param sourceFileId  源文件ID
     * @param newFileId     新文件ID
     * @param newStorageKey 新存储键
     * @param createdBy     创建者
     * @return 是否复制成功
     */
    boolean copyFileInfo(EnhancedJdbcTemplate namedJdbc, String sourceFileId, String newFileId, String newStorageKey, String createdBy);

    /**
     * 更新文件存储键
     *
     * @param namedJdbc     JDBC模板
     * @param fileId        文件ID
     * @param newStorageKey 新存储键
     * @return 是否更新成功
     */
    boolean updateStorageKey(EnhancedJdbcTemplate namedJdbc, String fileId, String newStorageKey);
} 