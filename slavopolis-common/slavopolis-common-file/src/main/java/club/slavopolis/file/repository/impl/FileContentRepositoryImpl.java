package club.slavopolis.file.repository.impl;

import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.repository.FileContentRepository;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件内容数据访问实现类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FileContentRepositoryImpl implements FileContentRepository {

    private final CurrentSystemProperties systemProperties;

    @Override
    public boolean saveChunk(EnhancedJdbcTemplate namedJdbc, String fileId, int chunkIndex, byte[] chunkData, String chunkHash) {
        try {
            // 如果没有提供哈希值，则计算MD5哈希
            String finalChunkHash = chunkHash != null ? chunkHash : DigestUtils.md5DigestAsHex(chunkData);
            
            String sql = """
                INSERT INTO file_content
                (file_id, chunk_index, chunk_data, chunk_size, chunk_hash, created_by, created_time, delete_flag, last_update_time)
                VALUES (:fileId, :chunkIndex, :chunkData, :chunkSize, :chunkHash, :createdBy, NOW(), 0, NOW())
                """;

            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            params.put("chunkIndex", chunkIndex);
            params.put("chunkData", chunkData);
            params.put("chunkSize", chunkData.length);
            params.put("chunkHash", finalChunkHash);
            params.put("createdBy", "SYSTEM");

            namedJdbc.update(sql, params, systemProperties.isDebug());
            return true;
            
        } catch (Exception e) {
            log.error("保存文件分片失败: {}, chunkIndex: {}", fileId, chunkIndex, e);
            return false;
        }
    }

    @Override
    public byte[] findChunkData(EnhancedJdbcTemplate namedJdbc, String fileId, int chunkIndex) {
        try {
            String sql = """
                SELECT chunk_data
                FROM file_content
                WHERE file_id = :fileId AND chunk_index = :chunkIndex AND delete_flag = 0
                """;

            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            params.put("chunkIndex", chunkIndex);

            List<Map<String, Object>> results = namedJdbc.queryForList(sql, params, systemProperties.isDebug());
            if (results.isEmpty()) {
                log.error("文件分片不存在: {}, chunkIndex: {}", fileId, chunkIndex);
                return null;
            }
            
            Object chunkData = results.getFirst().get("chunk_data");
            if (chunkData instanceof byte[] byteArray) {
                return byteArray;
            }
            return null;
            
        } catch (Exception e) {
            log.error("查询文件分片失败: {}, chunkIndex: {}", fileId, chunkIndex, e);
            return null;
        }
    }

    @Override
    public List<byte[]> findAllChunks(EnhancedJdbcTemplate namedJdbc, String fileId) {
        log.debug("查询文件所有分片: {}", fileId);
        
        try {
            String sql = """
                SELECT chunk_data
                FROM file_content
                WHERE file_id = :fileId AND delete_flag = 0
                ORDER BY chunk_index ASC
                """;

            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);

            List<Map<String, Object>> results = namedJdbc.queryForList(sql, params, systemProperties.isDebug());
            
            List<byte[]> chunks = new ArrayList<>();
            for (Map<String, Object> row : results) {
                Object chunkData = row.get("chunk_data");
                if (chunkData instanceof byte[] byteArray) {
                    chunks.add(byteArray);
                }
            }

            return chunks;
            
        } catch (Exception e) {
            log.error("查询文件所有分片失败: {}", fileId, e);
            return List.of();
        }
    }

    @Override
    public boolean deleteByFileId(EnhancedJdbcTemplate namedJdbc, String fileId) {
        try {
            String sql = """
                UPDATE file_content
                SET delete_flag = 1, last_update_time = NOW()
                WHERE file_id = :fileId
                """;

            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);

            namedJdbc.update(sql, params, systemProperties.isDebug());
            return true;
            
        } catch (Exception e) {
            log.error("删除文件内容失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public boolean existsByFileId(EnhancedJdbcTemplate namedJdbc, String fileId) {
        log.debug("检查文件内容是否存在: {}", fileId);
        
        try {
            String sql = "SELECT COUNT(*) FROM file_content WHERE file_id = :fileId AND delete_flag = 0";

            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);

            Integer count = namedJdbc.queryForInt(sql, params, systemProperties.isDebug());
            return count != null && count > 0;
            
        } catch (Exception e) {
            log.error("检查文件内容是否存在失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public boolean copyContent(EnhancedJdbcTemplate namedJdbc, String sourceFileId, String targetFileId, String createdBy) {
        try {
            String sql = """
                INSERT INTO file_content
                (file_id, chunk_index, chunk_data, chunk_size, chunk_hash, created_by, created_time, delete_flag, last_update_time)
                SELECT :targetFileId, chunk_index, chunk_data, chunk_size, chunk_hash, :createdBy, NOW(), 0, NOW()
                FROM file_content
                WHERE file_id = :sourceFileId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("targetFileId", targetFileId);
            params.put("sourceFileId", sourceFileId);
            params.put("createdBy", createdBy);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("复制文件内容失败: {} -> {}", sourceFileId, targetFileId, e);
            return false;
        }
    }

    @Override
    public boolean moveContent(EnhancedJdbcTemplate namedJdbc, String sourceFileId, String targetFileId) {
        try {
            String sql = """
                UPDATE file_content
                SET file_id = :targetFileId, last_update_time = NOW()
                WHERE file_id = :sourceFileId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("targetFileId", targetFileId);
            params.put("sourceFileId", sourceFileId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("移动文件内容失败: {} -> {}", sourceFileId, targetFileId, e);
            return false;
        }
    }

    @Override
    public long getTotalSize(EnhancedJdbcTemplate namedJdbc, String fileId) {
        try {
            String sql = "SELECT SUM(chunk_size) FROM file_content WHERE file_id = :fileId AND delete_flag = 0";
            
            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            
            Long size = namedJdbc.queryForLong(sql, params, systemProperties.isDebug());
            return size != null ? size : 0L;
            
        } catch (Exception e) {
            log.error("获取文件总大小失败: {}", fileId, e);
            return 0L;
        }
    }
} 