package club.slavopolis.file.repository;

import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;

import java.util.List;

/**
 * 文件内容数据访问接口
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface FileContentRepository {

    /**
     * 保存文件分片
     *
     * @param namedJdbc  JDBC模板
     * @param fileId     文件ID
     * @param chunkIndex 分片索引
     * @param chunkData  分片数据
     * @param chunkHash  分片哈希值
     * @return 是否保存成功
     */
    boolean saveChunk(EnhancedJdbcTemplate namedJdbc, String fileId, int chunkIndex, byte[] chunkData, String chunkHash);

    /**
     * 获取指定分片数据
     *
     * @param namedJdbc  JDBC模板
     * @param fileId     文件ID
     * @param chunkIndex 分片索引
     * @return 分片数据
     */
    byte[] findChunkData(EnhancedJdbcTemplate namedJdbc, String fileId, int chunkIndex);

    /**
     * 获取文件所有分片数据
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 所有分片数据列表
     */
    List<byte[]> findAllChunks(EnhancedJdbcTemplate namedJdbc, String fileId);

    /**
     * 根据文件ID删除文件内容
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 是否删除成功
     */
    boolean deleteByFileId(EnhancedJdbcTemplate namedJdbc, String fileId);

    /**
     * 检查文件内容是否存在
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 是否存在
     */
    boolean existsByFileId(EnhancedJdbcTemplate namedJdbc, String fileId);

    /**
     * 复制文件内容
     *
     * @param namedJdbc   JDBC模板
     * @param sourceFileId 源文件ID
     * @param targetFileId 目标文件ID
     * @param createdBy   创建者
     * @return 是否复制成功
     */
    boolean copyContent(EnhancedJdbcTemplate namedJdbc, String sourceFileId, String targetFileId, String createdBy);

    /**
     * 移动文件内容（更新文件ID）
     *
     * @param namedJdbc   JDBC模板
     * @param sourceFileId 源文件ID
     * @param targetFileId 目标文件ID
     * @return 是否移动成功
     */
    boolean moveContent(EnhancedJdbcTemplate namedJdbc, String sourceFileId, String targetFileId);

    /**
     * 获取文件总大小
     *
     * @param namedJdbc JDBC模板
     * @param fileId    文件ID
     * @return 文件总大小（字节）
     */
    long getTotalSize(EnhancedJdbcTemplate namedJdbc, String fileId);
} 