package club.slavopolis.file.domain.request;

import club.slavopolis.file.enums.AccessPermission;
import club.slavopolis.file.enums.FileStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件列表查询请求
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
@Accessors(chain = true)
public class FileListRequest {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 文件名关键字（模糊匹配）
     */
    private String fileName;

    /**
     * 文件扩展名列表
     */
    private List<String> extensions;

    /**
     * 文件状态列表
     */
    private List<FileStatus> statuses;

    /**
     * 访问权限列表
     */
    private List<AccessPermission> permissions;

    /**
     * 最小文件大小
     */
    private Long minFileSize;

    /**
     * 最大文件大小
     */
    private Long maxFileSize;

    /**
     * 上传开始时间
     */
    private LocalDateTime uploadTimeStart;

    /**
     * 上传结束时间
     */
    private LocalDateTime uploadTimeEnd;

    /**
     * 页码（从1开始）
     */
    private Integer pageNumber = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    private String sortBy = "uploadTime";

    /**
     * 排序方向（ASC/DESC）
     */
    private String sortDirection = "DESC";

    /**
     * 是否包含扩展信息
     */
    private Boolean includeExtensionInfo = false;

    /**
     * 创建构建器
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 构建器
     */
    public static class Builder {
        private final FileListRequest request = new FileListRequest();

        public Builder tenantId(String tenantId) {
            request.setTenantId(tenantId);
            return this;
        }

        public Builder createdBy(String createdBy) {
            request.setCreatedBy(createdBy);
            return this;
        }

        public Builder fileName(String fileName) {
            request.setFileName(fileName);
            return this;
        }

        public Builder extensions(List<String> extensions) {
            request.setExtensions(extensions);
            return this;
        }

        public Builder statuses(List<FileStatus> statuses) {
            request.setStatuses(statuses);
            return this;
        }

        public Builder permissions(List<AccessPermission> permissions) {
            request.setPermissions(permissions);
            return this;
        }

        public Builder minFileSize(Long minFileSize) {
            request.setMinFileSize(minFileSize);
            return this;
        }

        public Builder maxFileSize(Long maxFileSize) {
            request.setMaxFileSize(maxFileSize);
            return this;
        }

        public Builder uploadTimeStart(LocalDateTime uploadTimeStart) {
            request.setUploadTimeStart(uploadTimeStart);
            return this;
        }

        public Builder uploadTimeEnd(LocalDateTime uploadTimeEnd) {
            request.setUploadTimeEnd(uploadTimeEnd);
            return this;
        }

        public Builder pageNumber(Integer pageNumber) {
            request.setPageNumber(pageNumber);
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            request.setPageSize(pageSize);
            return this;
        }

        public Builder sortBy(String sortBy) {
            request.setSortBy(sortBy);
            return this;
        }

        public Builder sortDirection(String sortDirection) {
            request.setSortDirection(sortDirection);
            return this;
        }

        public Builder includeExtensionInfo(Boolean includeExtensionInfo) {
            request.setIncludeExtensionInfo(includeExtensionInfo);
            return this;
        }

        public FileListRequest build() {
            return request;
        }
    }
} 