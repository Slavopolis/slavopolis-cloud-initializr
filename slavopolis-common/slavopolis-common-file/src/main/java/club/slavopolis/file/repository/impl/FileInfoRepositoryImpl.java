package club.slavopolis.file.repository.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.base.utils.UniqueIdUtil;
import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.domain.request.FileListRequest;
import club.slavopolis.file.exception.FileOperationException;
import club.slavopolis.file.repository.FileInfoRepository;
import club.slavopolis.persistence.jdbc.core.EnhancedJdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件信息数据访问实现类
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
public class FileInfoRepositoryImpl implements FileInfoRepository {

    private final CurrentSystemProperties systemProperties;

    @Override
    public FileInfo save(EnhancedJdbcTemplate namedJdbc, FileInfo fileInfo) {
        try {
            String fileId = UniqueIdUtil.compactUuid();
            fileInfo.setFileId(fileId);
            
            String sql = """
                INSERT INTO file_info
                (file_id, original_name, file_size, content_type, file_hash, extension,
                 storage_type, storage_key, status, access_permission, upload_time,
                 tenant_id, download_count, created_by, delete_flag)
                VALUES (:fileId, :originalName, :fileSize, :contentType, :fileHash, :extension,
                        :storageType, :storageKey, :status, :accessPermission, NOW(),
                        :tenantId, 0, :createdBy, 0)
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileInfo.getFileId());
            params.put("originalName", fileInfo.getOriginalName());
            params.put("fileSize", fileInfo.getFileSize());
            params.put("contentType", fileInfo.getContentType());
            params.put("fileHash", fileInfo.getFileHash());
            params.put("extension", fileInfo.getExtension());
            params.put("storageType", fileInfo.getStorageType().name());
            params.put("storageKey", fileInfo.getStorageKey());
            params.put("status", fileInfo.getStatus().name());
            params.put("accessPermission", fileInfo.getAccessPermission().name());
            params.put("tenantId", fileInfo.getTenantId());
            params.put("createdBy", fileInfo.getCreatedBy());
            
            namedJdbc.update(sql, params, systemProperties.isDebug());
            return fileInfo;
            
        } catch (Exception e) {
            log.error("保存文件信息失败: {}", fileInfo.getOriginalName(), e);
            throw new FileOperationException("保存文件信息失败", e);
        }
    }

    @Override
    public FileInfo findById(EnhancedJdbcTemplate namedJdbc, String fileId) {
        try {
            String sql = """
                SELECT file_id, original_name, file_size, content_type, file_hash, extension,
                       storage_type, storage_key, status, access_permission, upload_time,
                       tenant_id, download_count, last_access_time, extension_info, created_by
                FROM file_info
                WHERE file_id = :fileId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            
            List<FileInfo> results = namedJdbc.queryForList(sql, params, FileInfo.class, systemProperties.isDebug());
            return results.isEmpty() ? null : results.getFirst();
            
        } catch (Exception e) {
            log.error("查询文件信息失败: {}", fileId, e);
            return null;
        }
    }

    @Override
    public FileInfo findByHash(EnhancedJdbcTemplate namedJdbc, String fileHash) {
        try {
            String sql = """
                SELECT file_id, original_name, file_size, content_type, file_hash, extension,
                       storage_type, storage_key, status, access_permission, upload_time,
                       tenant_id, download_count, last_access_time, extension_info, created_by
                FROM file_info
                WHERE file_hash = :fileHash AND delete_flag = 0
                LIMIT 1
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("fileHash", fileHash);
            
            List<FileInfo> results = namedJdbc.queryForList(sql, params, FileInfo.class, systemProperties.isDebug());
            return results.isEmpty() ? null : results.getFirst();
            
        } catch (Exception e) {
            log.error("根据哈希值查询文件信息失败: {}", fileHash, e);
            return null;
        }
    }

    @Override
    public List<FileInfo> findByRequest(EnhancedJdbcTemplate namedJdbc, FileListRequest request) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("""
                SELECT file_id, original_name, file_size, content_type, file_hash, extension,
                       storage_type, storage_key, status, access_permission, upload_time,
                       tenant_id, download_count, last_access_time, extension_info, created_by
                FROM file_info
                WHERE delete_flag = 0
                """);
            
            Map<String, Object> params = buildQueryParams(request);
            appendQueryConditions(sql, request);
            
            // 添加排序
            sql.append(" ORDER BY upload_time DESC");
            
            // 添加分页
            sql.append(" LIMIT :limit OFFSET :offset");
            params.put("limit", request.getPageSize());
            params.put("offset", (request.getPageNumber() - 1) * request.getPageSize());

            return namedJdbc.queryForList(sql.toString(), params, FileInfo.class, systemProperties.isDebug());
            
        } catch (Exception e) {
            log.error("根据条件查询文件列表失败", e);
            return List.of();
        }
    }

    @Override
    public long countByRequest(EnhancedJdbcTemplate namedJdbc, FileListRequest request) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM file_info WHERE delete_flag = 0");
            
            Map<String, Object> params = buildQueryParams(request);
            appendQueryConditions(sql, request);

            Long count = namedJdbc.queryForLong(sql.toString(), params, systemProperties.isDebug());
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            log.error("根据条件统计文件总数失败", e);
            return 0L;
        }
    }

    /**
     * 构建查询参数
     */
    private Map<String, Object> buildQueryParams(FileListRequest request) {
        Map<String, Object> params = new HashMap<>();
        
        // 添加文件名过滤条件
        if (StringUtils.hasText(request.getFileName())) {
            params.put("fileName", "%" + request.getFileName() + "%");
        }
        
        // 添加租户ID过滤条件
        if (StringUtils.hasText(request.getTenantId())) {
            params.put("tenantId", request.getTenantId());
        }
        
        // 添加创建者过滤条件
        if (StringUtils.hasText(request.getCreatedBy())) {
            params.put("createdBy", request.getCreatedBy());
        }
        
        return params;
    }

    /**
     * 添加查询条件
     */
    private void appendQueryConditions(StringBuilder sql, FileListRequest request) {
        // 添加文件名过滤条件
        if (StringUtils.hasText(request.getFileName())) {
            sql.append(" AND original_name LIKE :fileName");
        }
        
        // 添加租户ID过滤条件
        if (StringUtils.hasText(request.getTenantId())) {
            sql.append(" AND tenant_id = :tenantId");
        }
        
        // 添加创建者过滤条件
        if (StringUtils.hasText(request.getCreatedBy())) {
            sql.append(" AND created_by = :createdBy");
        }
    }

    @Override
    public boolean updateAccessInfo(EnhancedJdbcTemplate namedJdbc, String fileId, LocalDateTime accessTime) {
        try {
            String sql = """
                UPDATE file_info
                SET download_count = download_count + 1, last_access_time = :accessTime
                WHERE file_id = :fileId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            params.put("accessTime", accessTime);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("更新文件访问信息失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public boolean markAsDeleted(EnhancedJdbcTemplate namedJdbc, String fileId) {
        try {
            String sql = """
                UPDATE file_info
                SET delete_flag = 1, last_update_time = NOW()
                WHERE file_id = :fileId
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("标记文件为已删除失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public boolean existsById(EnhancedJdbcTemplate namedJdbc, String fileId) {
        try {
            String sql = "SELECT COUNT(*) FROM file_info WHERE file_id = :fileId AND delete_flag = 0";
            
            Map<String, Object> params = new HashMap<>();
            params.put("fileId", fileId);
            
            Integer count = namedJdbc.queryForInt(sql, params, systemProperties.isDebug());
            return count != null && count > 0;
            
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public boolean copyFileInfo(EnhancedJdbcTemplate namedJdbc, String sourceFileId, String newFileId, String newStorageKey, String createdBy) {
        try {
            String sql = """
                INSERT INTO file_info
                (file_id, original_name, file_size, content_type, file_hash, extension,
                 storage_type, storage_key, status, access_permission, upload_time,
                 tenant_id, download_count, created_by, delete_flag)
                SELECT :newFileId, original_name, file_size, content_type, file_hash, extension,
                       storage_type, :newStorageKey, status, access_permission, NOW(),
                       tenant_id, 0, :createdBy, 0
                FROM file_info
                WHERE file_id = :sourceFileId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("newFileId", newFileId);
            params.put("newStorageKey", newStorageKey);
            params.put("sourceFileId", sourceFileId);
            params.put("createdBy", createdBy);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("复制文件信息失败: {} -> {}", sourceFileId, newFileId, e);
            return false;
        }
    }

    @Override
    public boolean updateStorageKey(EnhancedJdbcTemplate namedJdbc, String fileId, String newStorageKey) {
        try {
            String sql = """
                UPDATE file_info
                SET storage_key = :newStorageKey, last_update_time = NOW()
                WHERE file_id = :fileId AND delete_flag = 0
                """;
                
            Map<String, Object> params = new HashMap<>();
            params.put("newStorageKey", newStorageKey);
            params.put("fileId", fileId);
            
            int rows = namedJdbc.update(sql, params, systemProperties.isDebug());
            return rows > 0;
            
        } catch (Exception e) {
            log.error("更新文件存储键失败: {}", fileId, e);
            return false;
        }
    }
} 