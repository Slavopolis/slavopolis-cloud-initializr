-- =====================================================
-- Slavopolis Auth 认证授权数据库设计
-- 基于RBAC模型，支持多租户架构
-- Author: Slavopolis Team
-- Version: 1.0.0
-- Date: 2025-01-20
-- =====================================================

-- 使用数据库
CREATE DATABASE IF NOT EXISTS `slavopolis_biz`
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `slavopolis_biz`;

-- =====================================================
-- 1. 租户管理表
-- =====================================================

-- 租户表
CREATE TABLE `sys_tenant` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `tenant_code` varchar(64) NOT NULL COMMENT '租户编码',
    `tenant_name` varchar(255) NOT NULL COMMENT '租户名称',
    `tenant_type` varchar(32) NOT NULL DEFAULT 'ENTERPRISE' COMMENT '租户类型：ENTERPRISE(企业)/INDIVIDUAL(个人)/TRIAL(试用)',
    `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃)/SUSPENDED(暂停)/EXPIRED(过期)',
    `contact_person` varchar(100) COMMENT '联系人',
    `contact_phone` varchar(20) COMMENT '联系电话',
    `contact_email` varchar(255) COMMENT '联系邮箱',
    `expire_time` datetime COMMENT '过期时间',
    `max_users` int DEFAULT 100 COMMENT '最大用户数',
    `current_users` int DEFAULT 0 COMMENT '当前用户数',
    `logo_url` varchar(500) COMMENT '租户Logo地址',
    `domain` varchar(255) COMMENT '租户域名',
    `remark` text COMMENT '备注',
    `created_by` bigint COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` bigint COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`),
    UNIQUE KEY `uk_tenant_domain` (`domain`),
    KEY `idx_tenant_status` (`status`),
    KEY `idx_tenant_type` (`tenant_type`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- =====================================================
-- 2. 用户管理表
-- =====================================================

-- 用户基础信息表
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码（加密后）',
    `salt` varchar(64) COMMENT '盐值',
    `nickname` varchar(100) COMMENT '昵称',
    `real_name` varchar(100) COMMENT '真实姓名',
    `avatar` varchar(500) COMMENT '头像地址',
    `gender` varchar(10) COMMENT '性别：MALE(男)/FEMALE(女)/UNKNOWN(未知)',
    `birthday` date COMMENT '生日',
    `email` varchar(255) COMMENT '邮箱',
    `email_verified` tinyint(1) DEFAULT 0 COMMENT '邮箱验证状态：0-未验证，1-已验证',
    `phone` varchar(20) COMMENT '手机号',
    `phone_verified` tinyint(1) DEFAULT 0 COMMENT '手机验证状态：0-未验证，1-已验证',
    `id_card` varchar(18) COMMENT '身份证号',
    `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃)/LOCKED(锁定)/DISABLED(禁用)',
    `user_type` varchar(32) NOT NULL DEFAULT 'NORMAL' COMMENT '用户类型：ADMIN(管理员)/NORMAL(普通用户)/GUEST(访客)',
    `last_login_time` datetime COMMENT '最后登录时间',
    `last_login_ip` varchar(45) COMMENT '最后登录IP',
    `password_update_time` datetime COMMENT '密码更新时间',
    `login_failure_count` int DEFAULT 0 COMMENT '登录失败次数',
    `lock_time` datetime COMMENT '锁定时间',
    `remark` text COMMENT '备注',
    `created_by` bigint COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` bigint COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
    UNIQUE KEY `uk_tenant_email` (`tenant_id`, `email`),
    UNIQUE KEY `uk_tenant_phone` (`tenant_id`, `phone`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_last_login_time` (`last_login_time`),
    CONSTRAINT `fk_user_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础信息表';

-- 用户扩展信息表
CREATE TABLE `sys_user_profile` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `department` varchar(255) COMMENT '部门',
    `position` varchar(100) COMMENT '职位',
    `work_phone` varchar(20) COMMENT '工作电话',
    `address` varchar(500) COMMENT '地址',
    `postal_code` varchar(10) COMMENT '邮政编码',
    `personal_description` text COMMENT '个人描述',
    `skills` json COMMENT '技能标签',
    `social_accounts` json COMMENT '社交账号',
    `preferences` json COMMENT '用户偏好设置',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_profile_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- =====================================================
-- 3. 权限管理表（RBAC模型）
-- =====================================================

-- 权限资源表
CREATE TABLE `sys_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `parent_id` bigint DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
    `permission_code` varchar(255) NOT NULL COMMENT '权限编码',
    `permission_name` varchar(255) NOT NULL COMMENT '权限名称',
    `permission_type` varchar(32) NOT NULL COMMENT '权限类型：MENU(菜单)/BUTTON(按钮)/API(接口)/DATA(数据)',
    `resource_path` varchar(500) COMMENT '资源路径',
    `method` varchar(10) COMMENT 'HTTP方法：GET/POST/PUT/DELETE等',
    `icon` varchar(100) COMMENT '图标',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `description` varchar(500) COMMENT '权限描述',
    `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃)/DISABLED(禁用)',
    `created_by` bigint COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` bigint COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_permission_code` (`tenant_id`, `permission_code`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_sort_order` (`sort_order`),
    CONSTRAINT `fk_permission_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限资源表';

-- 角色表
CREATE TABLE `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `role_code` varchar(64) NOT NULL COMMENT '角色编码',
    `role_name` varchar(255) NOT NULL COMMENT '角色名称',
    `role_type` varchar(32) NOT NULL DEFAULT 'CUSTOM' COMMENT '角色类型：SYSTEM(系统)/CUSTOM(自定义)',
    `data_scope` varchar(32) DEFAULT 'TENANT' COMMENT '数据范围：ALL(全部)/TENANT(租户)/DEPT(部门)/SELF(个人)',
    `description` varchar(500) COMMENT '角色描述',
    `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃)/DISABLED(禁用)',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `created_by` bigint COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` bigint COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `role_code`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_role_type` (`role_type`),
    KEY `idx_sort_order` (`sort_order`),
    CONSTRAINT `fk_role_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 角色权限关联表
CREATE TABLE `sys_role_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_role_permission` (`tenant_id`, `role_id`, `permission_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`),
    CONSTRAINT `fk_role_permission_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`),
    CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_user_role` (`tenant_id`, `user_id`, `role_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_user_role_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`),
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 4. 会话管理表
-- =====================================================

-- 用户会话表
CREATE TABLE `sys_user_session` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    `session_id` varchar(255) NOT NULL COMMENT '会话标识',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `device_type` varchar(32) COMMENT '设备类型：WEB/MOBILE/APP',
    `device_id` varchar(255) COMMENT '设备标识',
    `user_agent` text COMMENT '用户代理',
    `ip_address` varchar(45) COMMENT 'IP地址',
    `location` varchar(255) COMMENT '登录地点',
    `login_time` datetime NOT NULL COMMENT '登录时间',
    `last_access_time` datetime NOT NULL COMMENT '最后访问时间',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃)/EXPIRED(过期)/LOGOUT(已登出)',
    `logout_time` datetime COMMENT '登出时间',
    `logout_type` varchar(32) COMMENT '登出类型：MANUAL(手动)/TIMEOUT(超时)/FORCE(强制)',
    `refresh_token` varchar(500) COMMENT '刷新令牌',
    `access_token_hash` varchar(255) COMMENT '访问令牌哈希',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_last_access_time` (`last_access_time`),
    KEY `idx_expire_time` (`expire_time`),
    CONSTRAINT `fk_session_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_session_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

-- OAuth2客户端信息表
CREATE TABLE `oauth2_client` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户端ID',
    `tenant_id` bigint NOT NULL COMMENT '租户ID',
    `client_id` varchar(255) NOT NULL COMMENT '客户端标识',
    `client_secret` varchar(500) NOT NULL COMMENT '客户端密钥（加密后）',
    `client_name` varchar(255) NOT NULL COMMENT '客户端名称',
    `client_type` varchar(32) NOT NULL DEFAULT 'CONFIDENTIAL' COMMENT '客户端类型：PUBLIC(公开)/CONFIDENTIAL(机密)',
    `grant_types` varchar(500) NOT NULL COMMENT '授权类型，逗号分隔',
    `redirect_uris` text COMMENT '重定向URI，逗号分隔',
    `scopes` varchar(500) COMMENT '授权范围，逗号分隔',
    `access_token_validity` int DEFAULT 3600 COMMENT '访问令牌有效期（秒）',
    `refresh_token_validity` int DEFAULT 7200 COMMENT '刷新令牌有效期（秒）',
    `auto_approve_scopes` varchar(500) COMMENT '自动批准的范围',
    `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE(活跃)/DISABLED(禁用)',
    `created_by` bigint COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` bigint COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_id` (`client_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_oauth2_client_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端信息表';

-- =====================================================
-- 5. 审计日志表
-- =====================================================

-- 操作日志表
CREATE TABLE `sys_audit_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `tenant_id` bigint COMMENT '租户ID',
    `user_id` bigint COMMENT '操作用户ID',
    `username` varchar(64) COMMENT '操作用户名',
    `operation_type` varchar(32) NOT NULL COMMENT '操作类型：LOGIN/LOGOUT/CREATE/UPDATE/DELETE/QUERY/EXPORT/IMPORT',
    `operation_module` varchar(64) COMMENT '操作模块',
    `operation_description` varchar(500) COMMENT '操作描述',
    `request_method` varchar(10) COMMENT '请求方法',
    `request_url` varchar(1000) COMMENT '请求URL',
    `request_params` json COMMENT '请求参数',
    `response_status` varchar(32) COMMENT '响应状态：SUCCESS/FAILURE',
    `response_message` text COMMENT '响应消息',
    `execution_time` int COMMENT '执行时间（毫秒）',
    `ip_address` varchar(45) COMMENT 'IP地址',
    `user_agent` text COMMENT '用户代理',
    `location` varchar(255) COMMENT '操作地点',
    `risk_level` varchar(32) DEFAULT 'LOW' COMMENT '风险级别：LOW(低)/MEDIUM(中)/HIGH(高)/CRITICAL(严重)',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_operation_module` (`operation_module`),
    KEY `idx_response_status` (`response_status`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_audit_log_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 安全事件日志表
CREATE TABLE `sys_security_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
    `tenant_id` bigint COMMENT '租户ID',
    `user_id` bigint COMMENT '用户ID',
    `event_type` varchar(64) NOT NULL COMMENT '事件类型：LOGIN_FAILURE/ACCOUNT_LOCKED/PASSWORD_CHANGE/SUSPICIOUS_ACCESS/BRUTE_FORCE',
    `event_description` varchar(500) COMMENT '事件描述',
    `severity` varchar(32) NOT NULL COMMENT '严重程度：INFO/WARNING/ERROR/CRITICAL',
    `source_ip` varchar(45) COMMENT '来源IP',
    `user_agent` text COMMENT '用户代理',
    `location` varchar(255) COMMENT '事件发生地点',
    `additional_info` json COMMENT '附加信息',
    `processed` tinyint(1) DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理',
    `processed_by` bigint COMMENT '处理人',
    `processed_time` datetime COMMENT '处理时间',
    `processing_notes` text COMMENT '处理备注',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_severity` (`severity`),
    KEY `idx_processed` (`processed`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_security_log_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安全事件日志表';

-- =====================================================
-- 6. 系统配置表
-- =====================================================

-- 系统配置表
CREATE TABLE `sys_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `tenant_id` bigint COMMENT '租户ID，NULL表示全局配置',
    `config_key` varchar(255) NOT NULL COMMENT '配置键',
    `config_value` text COMMENT '配置值',
    `config_type` varchar(32) NOT NULL DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/BOOLEAN/JSON',
    `description` varchar(500) COMMENT '配置描述',
    `editable` tinyint(1) DEFAULT 1 COMMENT '是否可编辑：0-不可编辑，1-可编辑',
    `created_by` bigint COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` bigint COMMENT '更新人',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_config_key` (`tenant_id`, `config_key`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_config_type` (`config_type`),
    CONSTRAINT `fk_config_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 7. 初始化数据
-- =====================================================

-- 插入默认租户
INSERT INTO `sys_tenant` 
    (`id`, `tenant_code`, `tenant_name`, `tenant_type`, `status`, `contact_person`, `contact_email`, `max_users`, `remark`) 
VALUES 
    (1, 'default', '默认租户', 'ENTERPRISE', 'ACTIVE', 'System Admin', 'admin@slavopolis.club', 1000, '系统默认租户');

-- 插入超级管理员用户（密码：admin123，使用BCrypt加密）
INSERT INTO `sys_user` 
    (`id`, `tenant_id`, `username`, `password`, `nickname`, `real_name`, `email`, `phone`, `status`, `user_type`) 
VALUES 
    (1, 1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgkOOEtP8YbB4y1P0YazOGqKF2', '超级管理员', '系统管理员', 'admin@slavopolis.club', '13800138000', 'ACTIVE', 'ADMIN');

-- 插入系统默认权限
INSERT INTO `sys_permission` 
    (`id`, `tenant_id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `resource_path`, `method`, `sort_order`, `description`) 
VALUES 
    (1, 1, 0, 'system', '系统管理', 'MENU', '/system', '', 1, '系统管理菜单'),
    (2, 1, 1, 'user:list', '用户查询', 'API', '/system/user/list', 'GET', 1, '查询用户列表'),
    (3, 1, 1, 'user:create', '用户新增', 'API', '/system/user', 'POST', 2, '新增用户'),
    (4, 1, 1, 'user:update', '用户修改', 'API', '/system/user', 'PUT', 3, '修改用户'),
    (5, 1, 1, 'user:delete', '用户删除', 'API', '/system/user/**', 'DELETE', 4, '删除用户'),
    (6, 1, 1, 'role:list', '角色查询', 'API', '/system/role/list', 'GET', 5, '查询角色列表'),
    (7, 1, 1, 'role:create', '角色新增', 'API', '/system/role', 'POST', 6, '新增角色'),
    (8, 1, 1, 'role:update', '角色修改', 'API', '/system/role', 'PUT', 7, '修改角色'),
    (9, 1, 1, 'role:delete', '角色删除', 'API', '/system/role/**', 'DELETE', 8, '删除角色');

-- 插入系统默认角色
INSERT INTO `sys_role` 
    (`id`, `tenant_id`, `role_code`, `role_name`, `role_type`, `data_scope`, `description`) 
VALUES 
    (1, 1, 'SUPER_ADMIN', '超级管理员', 'SYSTEM', 'ALL', '系统超级管理员，拥有所有权限'),
    (2, 1, 'TENANT_ADMIN', '租户管理员', 'SYSTEM', 'TENANT', '租户管理员，管理本租户内所有资源'),
    (3, 1, 'USER', '普通用户', 'SYSTEM', 'SELF', '普通用户，只能管理自己的资源');

-- 绑定超级管理员角色
INSERT INTO `sys_user_role` 
    (`tenant_id`, `user_id`, `role_id`) 
VALUES 
    (1, 1, 1);

-- 绑定超级管理员权限
INSERT INTO `sys_role_permission` 
    (`tenant_id`, `role_id`, `permission_id`) 
VALUES 
    (1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5),
    (1, 1, 6), (1, 1, 7), (1, 1, 8), (1, 1, 9);

-- 插入默认OAuth2客户端
INSERT INTO `oauth2_client` 
    (`id`, `tenant_id`, `client_id`, `client_secret`, `client_name`, `client_type`, `grant_types`, `redirect_uris`, `scopes`, `access_token_validity`, `refresh_token_validity`) 
VALUES 
    (1, 1, 'slavopolis-web', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgkOOEtP8YbB4y1P0YazOGqKF2', 'Slavopolis Web Client', 'CONFIDENTIAL', 'authorization_code,refresh_token', 'http://localhost:3000/callback,http://localhost:8080/callback', 'read,write,openid,profile,email', 3600, 7200),
    (2, 1, 'slavopolis-mobile', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXgkOOEtP8YbB4y1P0YazOGqKF2', 'Slavopolis Mobile Client', 'PUBLIC', 'authorization_code,refresh_token', 'app://callback', 'read,write,offline_access', 7200, 14400);

-- 插入系统默认配置
INSERT INTO `sys_config` 
    (`tenant_id`, `config_key`, `config_value`, `config_type`, `description`, `editable`) 
VALUES 
    (NULL, 'system.name', 'Slavopolis', 'STRING', '系统名称', 1),
    (NULL, 'system.version', '1.0.0', 'STRING', '系统版本', 0),
    (NULL, 'security.password.min_length', '8', 'NUMBER', '密码最小长度', 1),
    (NULL, 'security.password.require_special_char', 'true', 'BOOLEAN', '密码是否要求特殊字符', 1),
    (NULL, 'security.login.max_failure_count', '5', 'NUMBER', '登录最大失败次数', 1),
    (NULL, 'security.login.lock_duration', '1800', 'NUMBER', '账户锁定时长（秒）', 1),
    (NULL, 'session.timeout', '3600', 'NUMBER', '会话超时时间（秒）', 1),
    (NULL, 'captcha.expire_time', '300', 'NUMBER', '验证码过期时间（秒）', 1);

-- =====================================================
-- 8. 创建索引优化
-- =====================================================

-- 复合索引优化查询性能
CREATE INDEX `idx_user_tenant_status` ON `sys_user` (`tenant_id`, `status`);
CREATE INDEX `idx_user_tenant_type` ON `sys_user` (`tenant_id`, `user_type`);
CREATE INDEX `idx_permission_tenant_type` ON `sys_permission` (`tenant_id`, `permission_type`);
CREATE INDEX `idx_role_tenant_type` ON `sys_role` (`tenant_id`, `role_type`);
CREATE INDEX `idx_session_user_status` ON `sys_user_session` (`user_id`, `status`);
CREATE INDEX `idx_audit_tenant_time` ON `sys_audit_log` (`tenant_id`, `created_time`);
CREATE INDEX `idx_security_tenant_time` ON `sys_security_log` (`tenant_id`, `created_time`);

-- =====================================================
-- 完成数据库初始化
-- ===================================================== 