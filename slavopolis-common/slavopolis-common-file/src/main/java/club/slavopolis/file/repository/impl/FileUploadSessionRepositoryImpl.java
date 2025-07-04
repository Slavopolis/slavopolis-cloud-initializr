package club.slavopolis.file.repository.impl;

import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.domain.FileUploadSession;
import club.slavopolis.file.enums.FileStatus;
import club.slavopolis.file.exception.FileOperationException;
import club.slavopolis.file.repository.FileUploadSessionRepository;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传会话数据访问实现类
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
public class FileUploadSessionRepositoryImpl implements FileUploadSessionRepository {

    private final CurrentSystemProperties systemProperties;

    /**
     * 默认会话过期时间（小时）
     */
    private static final int DEFAULT_SESSION_EXPIRY_HOURS = 24;

    @Override
    public String save(EnhancedJdbcTemplate namedJdbc, FileUploadSession session) {
        try {
            String sql = """
                INSERT INTO file_upload_session
                (upload_id, original_name, total_size, chunk_size, total_chunks,
                 uploaded_chunks, storage_type, status, expires_at, tenant_id,
                 created_by, delete_flag)
                VALUES (:uploadId, :originalName, :totalSize, :chunkSize, :totalChunks,
                         :uploadedChunks, :storageType, :status, DATE_ADD(NOW(), INTERVAL :expiryHours HOUR),
                         :tenantId, :createdBy, 0)
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("uploadId", session.getUploadId());
            params.put("originalName", session.getOriginalName());
            params.put("totalSize", session.getTotalSize());
            params.put("chunkSize", session.getChunkSize());
            params.put("totalChunks", session.getTotalChunks());
            params.put("uploadedChunks", session.getUploadedChunks() != null ? session.getUploadedChunks() : 0);
            params.put("storageType", session.getStorageType().name());
            params.put("status", session.getStatus() != null ? session.getStatus() : FileStatus.UPLOADING);
            params.put("expiryHours", DEFAULT_SESSION_EXPIRY_HOURS);
            params.put("tenantId", session.getTenantId());
            params.put("createdBy", session.getCreatedBy());
            
            namedJdbc.update(sql, params, systemProperties.isDebug());
            return session.getUploadId();
            
        } catch (Exception e) {
            log.error("保存上传会话失败: {}", session.getUploadId(), e);
            throw new FileOperationException("保存上传会话失败", e);
        }
    }

    @Override
    public FileUploadSession findById(EnhancedJdbcTemplate namedJdbc, String uploadId) {
        try {
            String sql = """
                SELECT upload_id, original_name, total_size, chunk_size, total_chunks,
                       uploaded_chunks, storage_type, status, expires_at, tenant_id,
                       created_by, error_message, delete_flag, create_time, last_update_time
                FROM file_upload_session
                WHERE upload_id = :uploadId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("uploadId", uploadId);
            
            List<FileUploadSession> results = namedJdbc.queryForList(sql, params, FileUploadSession.class, systemProperties.isDebug());
            return results.isEmpty() ? null : results.getFirst();
            
        } catch (Exception e) {
            log.error("查询上传会话失败: {}", uploadId, e);
            return null;
        }
    }

    @Override
    public boolean updateProgress(EnhancedJdbcTemplate namedJdbc, String uploadId, int uploadedChunks) {
        log.debug("更新上传进度: {}, uploadedChunks: {}", uploadId, uploadedChunks);
        
        try {
            String sql = """
                UPDATE file_upload_session
                SET uploaded_chunks = :uploadedChunks, last_update_time = NOW()
                WHERE upload_id = :uploadId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("uploadedChunks", uploadedChunks);
            params.put("uploadId", uploadId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("更新上传进度失败: {}", uploadId, e);
            return false;
        }
    }

    @Override
    public boolean markCompleted(EnhancedJdbcTemplate namedJdbc, String uploadId) {
        try {
            String sql = """
                UPDATE file_upload_session
                SET status = 'COMPLETED', last_update_time = NOW()
                WHERE upload_id = :uploadId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("uploadId", uploadId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("标记上传会话为完成失败: {}", uploadId, e);
            return false;
        }
    }

    @Override
    public boolean markFailed(EnhancedJdbcTemplate namedJdbc, String uploadId, String errorMessage) {
        try {
            String sql = """
                UPDATE file_upload_session
                SET status = 'FAILED', error_message = :errorMessage, last_update_time = NOW()
                WHERE upload_id = :uploadId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("errorMessage", errorMessage);
            params.put("uploadId", uploadId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("标记上传会话为失败失败: {}", uploadId, e);
            return false;
        }
    }

    @Override
    public boolean markCancelled(EnhancedJdbcTemplate namedJdbc, String uploadId) {
        try {
            String sql = """
                UPDATE file_upload_session
                SET status = 'CANCELLED', last_update_time = NOW()
                WHERE upload_id = :uploadId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("uploadId", uploadId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("标记上传会话为取消失败: {}", uploadId, e);
            return false;
        }
    }

    @Override
    public boolean delete(EnhancedJdbcTemplate namedJdbc, String uploadId) {
        try {
            String sql = """
                UPDATE file_upload_session
                SET delete_flag = 1, last_update_time = NOW()
                WHERE upload_id = :uploadId
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("uploadId", uploadId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("删除上传会话失败: {}", uploadId, e);
            return false;
        }
    }
} 