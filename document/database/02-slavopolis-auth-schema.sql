-- =============================================
-- 描述: Slavopolis 健全模块表结构创建脚本
-- 版本: 1.0.0
-- 作者: slavopolis
-- 说明: 创建 Slavopolis 健全模块的完整表结构，包含多租户、RBAC权限、组织架构等
-- =============================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 选择数据库
USE `slav_biz`;

-- 开始事务
START TRANSACTION;

-- =============================================
-- 1. 基础表创建（无依赖表）
-- =============================================

-- 1.1 租户表
DROP TABLE IF EXISTS sys_tenant;
CREATE TABLE sys_tenant (
    tenant_id         VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '租户ID',
    tenant_code       VARCHAR(32)  NOT NULL UNIQUE COMMENT '租户编码',
    tenant_name       VARCHAR(100) NOT NULL COMMENT '租户名称',
    tenant_type       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '租户类型(1:企业,2:个人,3:试用)',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    expire_time       DATETIME     NULL COMMENT '过期时间',
    max_user_count    INT          NOT NULL DEFAULT 100 COMMENT '最大用户数',
    contact_name      VARCHAR(50)  NULL COMMENT '联系人姓名',
    contact_phone     VARCHAR(20)  NULL COMMENT '联系人电话',
    contact_email     VARCHAR(100) NULL COMMENT '联系人邮箱',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 1.2 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    permission_id     VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '权限ID',
    parent_id         VARCHAR(64)  NULL COMMENT '父权限ID',
    permission_code   VARCHAR(100) NOT NULL COMMENT '权限编码',
    permission_name   VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_type   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '权限类型(1:菜单,2:按钮,3:API)',
    menu_url          VARCHAR(200) NULL COMMENT '菜单URL',
    menu_icon         VARCHAR(100) NULL COMMENT '菜单图标',
    api_url           VARCHAR(200) NULL COMMENT 'API地址',
    api_method        VARCHAR(10)  NULL COMMENT 'API方法',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    sort_order        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 1.3 字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    dict_type_id      VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '字典类型ID',
    tenant_id         VARCHAR(64)  NULL COMMENT '租户ID(NULL表示全局字典)',
    dict_type_code    VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    dict_type_name    VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- =============================================
-- 2. 核心表创建（依赖基础表）
-- =============================================

-- 2.1 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id           VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '用户ID',
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '租户ID',
    username          VARCHAR(50)  NULL COMMENT '用户名',
    password          VARCHAR(100) NULL COMMENT '密码',
    mobile            VARCHAR(20)  NULL COMMENT '手机号',
    email             VARCHAR(100) NULL COMMENT '邮箱',
    nickname          VARCHAR(50)  NULL COMMENT '昵称',
    real_name         VARCHAR(50)  NULL COMMENT '真实姓名',
    avatar            VARCHAR(500) NULL COMMENT '头像URL',
    gender            TINYINT(1)   NULL COMMENT '性别(0:未知,1:男,2:女)',
    birthday          DATE         NULL COMMENT '生日',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    login_type        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '登录类型(1:用户名,2:手机号,3:邮箱)',
    last_login_time   DATETIME     NULL COMMENT '最后登录时间',
    last_login_ip     VARCHAR(50)  NULL COMMENT '最后登录IP',
    login_count       INT          NOT NULL DEFAULT 0 COMMENT '登录次数',
    pwd_update_time   DATETIME     NULL COMMENT '密码更新时间',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2.2 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id           VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '角色ID',
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '租户ID',
    role_code         VARCHAR(50)  NOT NULL COMMENT '角色编码',
    role_name         VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_type         TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '角色类型(1:系统角色,2:租户角色)',
    data_scope        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '数据范围(1:全部,2:本部门,3:本部门及下级,4:仅本人)',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    sort_order        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 2.3 组织机构表
DROP TABLE IF EXISTS sys_organization;
CREATE TABLE sys_organization (
    org_id            VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '组织ID',
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '租户ID',
    parent_id         VARCHAR(64)  NULL COMMENT '父组织ID',
    org_code          VARCHAR(50)  NOT NULL COMMENT '组织编码',
    org_name          VARCHAR(100) NOT NULL COMMENT '组织名称',
    org_type          TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '组织类型(1:公司,2:部门,3:岗位)',
    org_level         INT          NOT NULL DEFAULT 1 COMMENT '组织层级',
    org_path          VARCHAR(500) NULL COMMENT '组织路径',
    leader_id         VARCHAR(64)  NULL COMMENT '负责人ID',
    sort_order        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织机构表';

-- 2.4 系统配置表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    config_id         VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '配置ID',
    tenant_id         VARCHAR(64)  NULL COMMENT '租户ID(NULL表示全局配置)',
    config_key        VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value      TEXT         NULL COMMENT '配置值',
    config_type       VARCHAR(20)  NOT NULL DEFAULT 'string' COMMENT '配置类型(string,number,boolean,json)',
    config_desc       VARCHAR(500) NULL COMMENT '配置描述',
    is_system         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否系统配置(0:否,1:是)',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =============================================
-- 3. 字典数据表创建（依赖字典类型表）
-- =============================================

-- 3.1 字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    dict_data_id      VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '字典数据ID',
    dict_type_id      VARCHAR(64)  NOT NULL COMMENT '字典类型ID',
    dict_label        VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value        VARCHAR(100) NOT NULL COMMENT '字典值',
    dict_sort         INT          NOT NULL DEFAULT 0 COMMENT '字典排序',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    is_default        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否默认(0:否,1:是)',
    css_class         VARCHAR(100) NULL COMMENT '样式属性',
    list_class        VARCHAR(100) NULL COMMENT '表格样式',
    remark            VARCHAR(500) NULL COMMENT '备注',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- =============================================
-- 4. 关联表创建（依赖核心表）
-- =============================================

-- 4.1 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id                BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id           VARCHAR(64)  NOT NULL COMMENT '角色ID',
    permission_id     VARCHAR(64)  NOT NULL COMMENT '权限ID',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 4.2 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id                BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id           VARCHAR(64)  NOT NULL COMMENT '用户ID',
    role_id           VARCHAR(64)  NOT NULL COMMENT '角色ID',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 4.3 用户组织关联表
DROP TABLE IF EXISTS sys_user_org;
CREATE TABLE sys_user_org (
    id                BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id           VARCHAR(64)  NOT NULL COMMENT '用户ID',
    org_id            VARCHAR(64)  NOT NULL COMMENT '组织ID',
    is_primary        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否主要组织(0:否,1:是)',
    -- 标准通用字段
    created_by        VARCHAR(64)  NOT NULL COMMENT '创建者ID',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    delete_flag       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    delete_time       DATETIME     NULL COMMENT '删除时间',
    last_update_by    VARCHAR(64)  NULL COMMENT '最后更新者ID',
    last_update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户组织关联表';

-- =============================================
-- 5. 会话和日志表创建
-- =============================================

-- 5.1 用户会话表
DROP TABLE IF EXISTS sys_user_session;
CREATE TABLE sys_user_session (
    session_id        VARCHAR(64)  NOT NULL PRIMARY KEY COMMENT '会话ID',
    user_id           VARCHAR(64)  NOT NULL COMMENT '用户ID',
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '租户ID',
    token             VARCHAR(500) NOT NULL COMMENT '访问令牌',
    refresh_token     VARCHAR(500) NULL COMMENT '刷新令牌',
    login_ip          VARCHAR(50)  NULL COMMENT '登录IP',
    login_location    VARCHAR(100) NULL COMMENT '登录地点',
    browser           VARCHAR(50)  NULL COMMENT '浏览器类型',
    os                VARCHAR(50)  NULL COMMENT '操作系统',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:已失效,1:有效)',
    login_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    expire_time       DATETIME     NOT NULL COMMENT '过期时间',
    logout_time       DATETIME     NULL COMMENT '退出时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

-- 5.2 操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    log_id            BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    tenant_id         VARCHAR(64)  NOT NULL COMMENT '租户ID',
    user_id           VARCHAR(64)  NULL COMMENT '用户ID',
    username          VARCHAR(50)  NULL COMMENT '用户名',
    operation_type    VARCHAR(50)  NOT NULL COMMENT '操作类型',
    operation_desc    VARCHAR(500) NULL COMMENT '操作描述',
    request_method    VARCHAR(10)  NULL COMMENT '请求方法',
    request_url       VARCHAR(500) NULL COMMENT '请求URL',
    request_params    TEXT         NULL COMMENT '请求参数',
    response_data     TEXT         NULL COMMENT '响应数据',
    error_msg         TEXT         NULL COMMENT '错误信息',
    operation_ip      VARCHAR(50)  NULL COMMENT '操作IP',
    operation_location VARCHAR(100) NULL COMMENT '操作地点',
    browser           VARCHAR(50)  NULL COMMENT '浏览器类型',
    os                VARCHAR(50)  NULL COMMENT '操作系统',
    status            TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(0:失败,1:成功)',
    cost_time         BIGINT       NULL COMMENT '耗时(毫秒)',
    operation_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =============================================
-- 6. 创建索引
-- =============================================

-- 租户表索引
CREATE INDEX idx_tenant_code ON sys_tenant (tenant_code);
CREATE INDEX idx_tenant_status ON sys_tenant (status);

-- 用户表索引
CREATE INDEX idx_user_tenant_id ON sys_user (tenant_id);
CREATE INDEX idx_user_username ON sys_user (username);
CREATE INDEX idx_user_mobile ON sys_user (mobile);
CREATE INDEX idx_user_email ON sys_user (email);
CREATE INDEX idx_user_status ON sys_user (status);
CREATE UNIQUE INDEX uk_user_tenant_username ON sys_user (tenant_id, username);
CREATE UNIQUE INDEX uk_user_tenant_mobile ON sys_user (tenant_id, mobile);
CREATE UNIQUE INDEX uk_user_tenant_email ON sys_user (tenant_id, email);

-- 角色表索引
CREATE INDEX idx_role_tenant_id ON sys_role (tenant_id);
CREATE INDEX idx_role_status ON sys_role (status);
CREATE UNIQUE INDEX uk_role_tenant_code ON sys_role (tenant_id, role_code);

-- 权限表索引
CREATE INDEX idx_permission_parent_id ON sys_permission (parent_id);
CREATE INDEX idx_permission_type ON sys_permission (permission_type);
CREATE INDEX idx_permission_status ON sys_permission (status);
CREATE UNIQUE INDEX uk_permission_code ON sys_permission (permission_code);

-- 组织机构表索引
CREATE INDEX idx_org_tenant_id ON sys_organization (tenant_id);
CREATE INDEX idx_org_parent_id ON sys_organization (parent_id);
CREATE INDEX idx_org_path ON sys_organization (org_path(100));
CREATE INDEX idx_org_status ON sys_organization (status);
CREATE UNIQUE INDEX uk_org_tenant_code ON sys_organization (tenant_id, org_code);

-- 系统配置表索引
CREATE INDEX idx_config_tenant_id ON sys_config (tenant_id);
CREATE INDEX idx_config_type ON sys_config (config_type);
CREATE INDEX idx_config_system ON sys_config (is_system);
CREATE UNIQUE INDEX uk_config_tenant_key ON sys_config (tenant_id, config_key);

-- 字典类型表索引
CREATE INDEX idx_dict_type_tenant_id ON sys_dict_type (tenant_id);
CREATE INDEX idx_dict_type_status ON sys_dict_type (status);
CREATE UNIQUE INDEX uk_dict_type_tenant_code ON sys_dict_type (tenant_id, dict_type_code);

-- 字典数据表索引
CREATE INDEX idx_dict_data_type_id ON sys_dict_data (dict_type_id);
CREATE INDEX idx_dict_data_sort ON sys_dict_data (dict_sort);
CREATE INDEX idx_dict_data_status ON sys_dict_data (status);

-- 角色权限关联表索引
CREATE INDEX idx_role_permission_role_id ON sys_role_permission (role_id);
CREATE INDEX idx_role_permission_permission_id ON sys_role_permission (permission_id);
CREATE UNIQUE INDEX uk_role_permission ON sys_role_permission (role_id, permission_id);

-- 用户角色关联表索引
CREATE INDEX idx_user_role_user_id ON sys_user_role (user_id);
CREATE INDEX idx_user_role_role_id ON sys_user_role (role_id);
CREATE UNIQUE INDEX uk_user_role ON sys_user_role (user_id, role_id);

-- 用户组织关联表索引
CREATE INDEX idx_user_org_user_id ON sys_user_org (user_id);
CREATE INDEX idx_user_org_org_id ON sys_user_org (org_id);
CREATE INDEX idx_user_org_primary ON sys_user_org (is_primary);
CREATE UNIQUE INDEX uk_user_org ON sys_user_org (user_id, org_id);

-- 用户会话表索引
CREATE INDEX idx_session_user_id ON sys_user_session (user_id);
CREATE INDEX idx_session_tenant_id ON sys_user_session (tenant_id);
CREATE INDEX idx_session_token ON sys_user_session (token(100));
CREATE INDEX idx_session_expire_time ON sys_user_session (expire_time);
CREATE INDEX idx_session_status ON sys_user_session (status);

-- 操作日志表索引
CREATE INDEX idx_operation_log_tenant_id ON sys_operation_log (tenant_id);
CREATE INDEX idx_operation_log_user_id ON sys_operation_log (user_id);
CREATE INDEX idx_operation_log_operation_time ON sys_operation_log (operation_time);
CREATE INDEX idx_operation_log_operation_type ON sys_operation_log (operation_type);
CREATE INDEX idx_operation_log_status ON sys_operation_log (status);

-- =============================================
-- 7. 创建外键约束
-- =============================================

-- 用户表外键
ALTER TABLE sys_user ADD CONSTRAINT fk_user_tenant 
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant (tenant_id) ON DELETE CASCADE;

-- 角色表外键
ALTER TABLE sys_role ADD CONSTRAINT fk_role_tenant 
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant (tenant_id) ON DELETE CASCADE;

-- 组织机构表外键
ALTER TABLE sys_organization ADD CONSTRAINT fk_org_tenant 
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant (tenant_id) ON DELETE CASCADE;

-- 角色权限关联表外键
ALTER TABLE sys_role_permission ADD CONSTRAINT fk_role_permission_role 
    FOREIGN KEY (role_id) REFERENCES sys_role (role_id) ON DELETE CASCADE;

ALTER TABLE sys_role_permission ADD CONSTRAINT fk_role_permission_permission 
    FOREIGN KEY (permission_id) REFERENCES sys_permission (permission_id) ON DELETE CASCADE;

-- 用户角色关联表外键
ALTER TABLE sys_user_role ADD CONSTRAINT fk_user_role_user 
    FOREIGN KEY (user_id) REFERENCES sys_user (user_id) ON DELETE CASCADE;

ALTER TABLE sys_user_role ADD CONSTRAINT fk_user_role_role 
    FOREIGN KEY (role_id) REFERENCES sys_role (role_id) ON DELETE CASCADE;

-- 用户组织关联表外键
ALTER TABLE sys_user_org ADD CONSTRAINT fk_user_org_user 
    FOREIGN KEY (user_id) REFERENCES sys_user (user_id) ON DELETE CASCADE;

ALTER TABLE sys_user_org ADD CONSTRAINT fk_user_org_org 
    FOREIGN KEY (org_id) REFERENCES sys_organization (org_id) ON DELETE CASCADE;

-- 用户会话表外键
ALTER TABLE sys_user_session ADD CONSTRAINT fk_session_user 
    FOREIGN KEY (user_id) REFERENCES sys_user (user_id) ON DELETE CASCADE;

ALTER TABLE sys_user_session ADD CONSTRAINT fk_session_tenant 
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant (tenant_id) ON DELETE CASCADE;

-- 字典数据表外键
ALTER TABLE sys_dict_data ADD CONSTRAINT fk_dict_data_type 
    FOREIGN KEY (dict_type_id) REFERENCES sys_dict_type (dict_type_id) ON DELETE CASCADE;

-- =============================================
-- 8. 创建常用视图
-- =============================================

-- 8.1 用户权限视图
CREATE OR REPLACE VIEW v_user_permissions AS
SELECT DISTINCT
    u.user_id,
    u.tenant_id,
    u.username,
    u.real_name,
    p.permission_id,
    p.permission_code,
    p.permission_name,
    p.permission_type,
    p.menu_url,
    p.api_url,
    p.api_method
FROM sys_user u
    INNER JOIN sys_user_role ur ON u.user_id = ur.user_id AND ur.delete_flag = 0
    INNER JOIN sys_role r ON ur.role_id = r.role_id AND r.delete_flag = 0 AND r.status = 1
    INNER JOIN sys_role_permission rp ON r.role_id = rp.role_id AND rp.delete_flag = 0
    INNER JOIN sys_permission p ON rp.permission_id = p.permission_id AND p.delete_flag = 0 AND p.status = 1
WHERE u.delete_flag = 0 AND u.status = 1;

-- 8.2 组织架构层级视图
CREATE OR REPLACE VIEW v_organization_hierarchy AS
SELECT 
    o.org_id,
    o.tenant_id,
    o.parent_id,
    o.org_code,
    o.org_name,
    o.org_type,
    o.org_level,
    o.org_path,
    o.leader_id,
    o.sort_order,
    o.status,
    p.org_name AS parent_name,
    u.real_name AS leader_name
FROM sys_organization o
    LEFT JOIN sys_organization p ON o.parent_id = p.org_id
    LEFT JOIN sys_user u ON o.leader_id = u.user_id
WHERE o.delete_flag = 0;

-- 8.3 用户角色汇总视图
CREATE OR REPLACE VIEW v_user_role_summary AS
SELECT 
    u.user_id,
    u.tenant_id,
    u.username,
    u.real_name,
    u.status AS user_status,
    GROUP_CONCAT(r.role_name ORDER BY r.sort_order) AS role_names,
    COUNT(DISTINCT r.role_id) AS role_count
FROM sys_user u
    LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id AND ur.delete_flag = 0
    LEFT JOIN sys_role r ON ur.role_id = r.role_id AND r.delete_flag = 0 AND r.status = 1
WHERE u.delete_flag = 0
GROUP BY u.user_id, u.tenant_id, u.username, u.real_name, u.status;

-- =============================================
-- 9. 创建存储过程
-- =============================================

-- 9.1 获取用户权限列表存储过程
DELIMITER //
CREATE PROCEDURE sp_get_user_permissions(
    IN p_user_id VARCHAR(64),
    IN p_permission_type TINYINT
)
BEGIN
    SELECT DISTINCT
        p.permission_id,
        p.permission_code,
        p.permission_name,
        p.permission_type,
        p.menu_url,
        p.menu_icon,
        p.api_url,
        p.api_method,
        p.sort_order
    FROM sys_user u
        INNER JOIN sys_user_role ur ON u.user_id = ur.user_id AND ur.delete_flag = 0
        INNER JOIN sys_role r ON ur.role_id = r.role_id AND r.delete_flag = 0 AND r.status = 1
        INNER JOIN sys_role_permission rp ON r.role_id = rp.role_id AND rp.delete_flag = 0
        INNER JOIN sys_permission p ON rp.permission_id = p.permission_id AND p.delete_flag = 0 AND p.status = 1
    WHERE u.user_id = p_user_id 
        AND u.delete_flag = 0 
        AND u.status = 1
        AND (p_permission_type IS NULL OR p.permission_type = p_permission_type)
    ORDER BY p.sort_order;
END //
DELIMITER ;

-- 9.2 获取组织架构树存储过程
DELIMITER //
CREATE PROCEDURE sp_get_organization_tree(
    IN p_tenant_id VARCHAR(64),
    IN p_parent_id VARCHAR(64)
)
BEGIN
    WITH RECURSIVE org_tree AS (
        SELECT 
            org_id,
            tenant_id,
            parent_id,
            org_code,
            org_name,
            org_type,
            org_level,
            org_path,
            leader_id,
            sort_order,
            status,
            0 as depth
        FROM sys_organization
        WHERE tenant_id = p_tenant_id 
            AND (p_parent_id IS NULL OR parent_id = p_parent_id)
            AND delete_flag = 0
            AND status = 1
        
        UNION ALL
        
        SELECT 
            o.org_id,
            o.tenant_id,
            o.parent_id,
            o.org_code,
            o.org_name,
            o.org_type,
            o.org_level,
            o.org_path,
            o.leader_id,
            o.sort_order,
            o.status,
            ot.depth + 1
        FROM sys_organization o
            INNER JOIN org_tree ot ON o.parent_id = ot.org_id
        WHERE o.delete_flag = 0 AND o.status = 1
    )
    SELECT * FROM org_tree ORDER BY depth, sort_order;
END //
DELIMITER ;

-- 9.3 清理过期会话存储过程
DELIMITER //
CREATE PROCEDURE sp_cleanup_expired_sessions()
BEGIN
    -- 更新过期会话状态
    UPDATE sys_user_session 
    SET status = 0, logout_time = NOW()
    WHERE expire_time < NOW() AND status = 1;
    
    -- 删除超过30天的过期会话记录
    DELETE FROM sys_user_session 
    WHERE status = 0 AND logout_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 返回清理统计信息
    SELECT 
        ROW_COUNT() as cleaned_sessions,
        NOW() as cleanup_time;
END //
DELIMITER ;

-- =============================================
-- 10. 创建触发器
-- =============================================

-- 10.1 用户密码更新触发器
DELIMITER //
CREATE TRIGGER tr_user_password_update
    BEFORE UPDATE ON sys_user
    FOR EACH ROW
BEGIN
    IF OLD.password != NEW.password THEN
        SET NEW.pwd_update_time = NOW();
    END IF;
END //
DELIMITER ;

-- 10.2 组织路径维护触发器
DELIMITER //
CREATE TRIGGER tr_organization_path_update
    BEFORE INSERT ON sys_organization
    FOR EACH ROW
BEGIN
    DECLARE parent_path VARCHAR(500);
    
    IF NEW.parent_id IS NOT NULL THEN
        SELECT org_path INTO parent_path 
        FROM sys_organization 
        WHERE org_id = NEW.parent_id;
        
        SET NEW.org_path = CONCAT(IFNULL(parent_path, ''), '/', NEW.org_id);
        SET NEW.org_level = (LENGTH(NEW.org_path) - LENGTH(REPLACE(NEW.org_path, '/', '')));
    ELSE
        SET NEW.org_path = CONCAT('/', NEW.org_id);
        SET NEW.org_level = 1;
    END IF;
END //
DELIMITER ;

-- 10.3 操作日志自动记录触发器（用户表）
DELIMITER //
CREATE TRIGGER tr_user_operation_log
    AFTER UPDATE ON sys_user
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO sys_operation_log (
            tenant_id, user_id, username, operation_type, operation_desc,
            status, operation_time
        ) VALUES (
            NEW.tenant_id, NEW.user_id, NEW.username, 'USER_STATUS_CHANGE',
            CONCAT('用户状态变更：', IF(NEW.status = 1, '启用', '禁用')),
            1, NOW()
        );
    END IF;
END //
DELIMITER ;

DELIMITER ;

-- 提交事务
COMMIT;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;