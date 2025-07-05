-- =============================================
-- 描述: Slavopolis 文件模块表结构创建脚本
-- 版本: 1.0.0
-- 作者: slavopolis
-- 说明: 创建 Slavopolis 文件模块的完整表结构，包含文件上传、下载、删除等
-- =============================================

-- 使用数据库
USE `slav_biz`;

-- 文件信息表
CREATE TABLE file_info
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    file_id           VARCHAR(64)  NOT NULL UNIQUE COMMENT '文件唯一标识',
    original_name     VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_size         BIGINT       NOT NULL COMMENT '文件大小(字节)',
    content_type      VARCHAR(100) COMMENT 'MIME类型',
    file_hash         VARCHAR(64)  NOT NULL COMMENT 'SHA256文件哈希',
    extension         VARCHAR(20) COMMENT '文件扩展名',
    storage_type      VARCHAR(20)  NOT NULL COMMENT '存储类型(OSS/MINIO/DATABASE/LOCAL)',
    storage_key       VARCHAR(500) NOT NULL COMMENT '存储键值或路径',
    status            VARCHAR(20)           DEFAULT 'ACTIVE' COMMENT '文件状态(UPLOADING/ACTIVE/DELETED/PROCESSING/FAILED)',
    access_permission VARCHAR(20)           DEFAULT 'PRIVATE' COMMENT '访问权限(PRIVATE/PUBLIC_READ/PUBLIC_READ_WRITE/TENANT_SHARED)',
    upload_time       DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    tenant_id         VARCHAR(64) COMMENT '租户ID',
    download_count    INT                   DEFAULT 0 COMMENT '下载次数',
    last_access_time  DATETIME COMMENT '最后访问时间',
    extension_info    JSON COMMENT '扩展信息',

    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64) COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '文件信息表';

-- 创建索引
CREATE INDEX idx_file_info_hash ON file_info (file_hash);
CREATE INDEX idx_file_info_storage_type ON file_info (storage_type);
CREATE INDEX idx_file_info_tenant_created ON file_info (tenant_id, created_by);
CREATE INDEX idx_file_info_status ON file_info (status);
CREATE INDEX idx_file_info_upload_time ON file_info (upload_time);
CREATE INDEX idx_file_info_extension ON file_info (extension);
CREATE INDEX idx_file_info_delete_flag ON file_info (delete_flag);

-- 分片上传会话表
CREATE TABLE file_upload_session
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    upload_id        VARCHAR(64)  NOT NULL UNIQUE COMMENT '上传会话ID',
    file_hash        VARCHAR(64) COMMENT '文件哈希（用于秒传）',
    original_name    VARCHAR(255) NOT NULL COMMENT '原始文件名',
    total_size       BIGINT       NOT NULL COMMENT '文件总大小',
    chunk_size       INT          NOT NULL COMMENT '分片大小',
    total_chunks     INT          NOT NULL COMMENT '总分片数',
    uploaded_chunks  INT                   DEFAULT 0 COMMENT '已上传分片数',
    storage_type     VARCHAR(20)  NOT NULL COMMENT '存储类型',
    status           VARCHAR(20)           DEFAULT 'UPLOADING' COMMENT '上传状态(UPLOADING/COMPLETED/FAILED/CANCELLED)',
    expires_at       DATETIME COMMENT '过期时间',
    tenant_id        VARCHAR(64) COMMENT '租户ID',
    chunk_info       JSON COMMENT '分片详细信息(已上传分片的ETag等)',
    error_message    TEXT COMMENT '错误信息',

    -- 标准通用字段
    created_by       VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag      TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time      DATETIME NULL COMMENT '删除时间',
    last_update_by   VARCHAR(64) COMMENT '最后更新者ID',
    last_update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '分片上传会话表';

-- 创建索引
CREATE INDEX idx_upload_session_upload_id ON file_upload_session (upload_id);
CREATE INDEX idx_upload_session_file_hash ON file_upload_session (file_hash);
CREATE INDEX idx_upload_session_status ON file_upload_session (status);
CREATE INDEX idx_upload_session_expires_at ON file_upload_session (expires_at);
CREATE INDEX idx_upload_session_created_by ON file_upload_session (created_by);
CREATE INDEX idx_upload_session_delete_flag ON file_upload_session (delete_flag);

-- 文件内容表（仅用于数据库存储模式）
CREATE TABLE file_content
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    file_id          VARCHAR(64) NOT NULL COMMENT '文件ID',
    chunk_index      INT         NOT NULL COMMENT '分片索引',
    chunk_data       LONGBLOB    NOT NULL COMMENT '分片数据',
    chunk_size       INT         NOT NULL COMMENT '分片大小',
    chunk_hash       VARCHAR(64) COMMENT '分片哈希值',

    -- 标准通用字段
    created_by       VARCHAR(64) NOT NULL COMMENT '创建者ID',
    created_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag      TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time      DATETIME NULL COMMENT '删除时间',
    last_update_by   VARCHAR(64) COMMENT '最后更新者ID',
    last_update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '文件内容表（数据库存储模式）';

-- 创建索引
CREATE UNIQUE INDEX uk_file_content_file_chunk ON file_content (file_id, chunk_index);
CREATE INDEX idx_file_content_file_id ON file_content (file_id);
CREATE INDEX idx_file_content_delete_flag ON file_content (delete_flag);

-- 文件访问日志表（可选，用于统计和审计）
CREATE TABLE file_access_log
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    file_id          VARCHAR(64) NOT NULL COMMENT '文件ID',
    access_type      VARCHAR(20) NOT NULL COMMENT '访问类型(UPLOAD/DOWNLOAD/DELETE/VIEW)',
    user_id          VARCHAR(64) COMMENT '用户ID',
    client_ip        VARCHAR(45) COMMENT '客户端IP',
    user_agent       VARCHAR(500) COMMENT '用户代理',
    access_time      DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    response_time    INT COMMENT '响应时间(ms)',
    transfer_size    BIGINT COMMENT '传输大小(字节)',
    status           VARCHAR(20) COMMENT '访问状态(SUCCESS/FAILED)',
    error_code       VARCHAR(50) COMMENT '错误代码',
    error_message    TEXT COMMENT '错误信息',
    tenant_id        VARCHAR(64) COMMENT '租户ID',

    -- 标准通用字段
    created_by       VARCHAR(64) NOT NULL COMMENT '创建者ID',
    created_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag      TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time      DATETIME NULL COMMENT '删除时间',
    last_update_by   VARCHAR(64) COMMENT '最后更新者ID',
    last_update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '文件访问日志表';

-- 创建索引
CREATE INDEX idx_access_log_file_id ON file_access_log (file_id);
CREATE INDEX idx_access_log_access_time ON file_access_log (access_time);
CREATE INDEX idx_access_log_user_id ON file_access_log (user_id);
CREATE INDEX idx_access_log_access_type ON file_access_log (access_type);
CREATE INDEX idx_access_log_tenant_id ON file_access_log (tenant_id);
CREATE INDEX idx_access_log_delete_flag ON file_access_log (delete_flag);