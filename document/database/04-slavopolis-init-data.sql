-- =============================================
-- 文件: 04-slavopolis-init-data.sql
-- 描述: Slavopolis 鉴权模块初始化数据脚本
-- 版本: 1.0.0
-- 作者: slavopolis
-- 说明: 创建系统运行必需的基础数据，包含字典、角色、权限、配置等
-- 注意: 所有ID均采用32位无连接符UUID格式
-- =============================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 选择数据库
USE slav_biz;

-- 开始事务
START TRANSACTION;

-- =============================================
-- 1. 默认租户创建
-- =============================================

-- 1.1 创建默认租户
INSERT IGNORE INTO sys_tenant (
    tenant_id, tenant_code, tenant_name, tenant_type, status, 
    max_user_count, contact_name, contact_phone, contact_email,
    created_by, created_time
) VALUES (
    'a1b2c3d4e5f6789012345678901234ab', 'DEFAULT', '默认租户', 1, 1,
    1000, '系统管理员', '400-000-0000', 'admin@slavopolis.club',
    'system', NOW()
);

-- =============================================
-- 2. 字典类型初始化
-- =============================================

-- 2.1 用户相关字典类型
INSERT IGNORE INTO sys_dict_type (
    dict_type_id, tenant_id, dict_type_code, dict_type_name, status, remark,
    created_by, created_time
) VALUES 
('1a2b3c4d5e6f7890123456789abcdef1', NULL, 'user_status', '用户状态', 1, '用户账号状态枚举', 'system', NOW()),
('2a2b3c4d5e6f7890123456789abcdef2', NULL, 'user_gender', '用户性别', 1, '用户性别枚举', 'system', NOW()),
('3a2b3c4d5e6f7890123456789abcdef3', NULL, 'login_type', '登录类型', 1, '用户登录方式枚举', 'system', NOW());

-- 2.2 组织相关字典类型
INSERT IGNORE INTO sys_dict_type (
    dict_type_id, tenant_id, dict_type_code, dict_type_name, status, remark,
    created_by, created_time
) VALUES 
('4a2b3c4d5e6f7890123456789abcdef4', NULL, 'org_type', '组织类型', 1, '组织机构类型枚举', 'system', NOW()),
('5a2b3c4d5e6f7890123456789abcdef5', NULL, 'data_scope', '数据权限范围', 1, '角色数据权限范围枚举', 'system', NOW());

-- 2.3 权限相关字典类型
INSERT IGNORE INTO sys_dict_type (
    dict_type_id, tenant_id, dict_type_code, dict_type_name, status, remark,
    created_by, created_time
) VALUES 
('6a2b3c4d5e6f7890123456789abcdef6', NULL, 'permission_type', '权限类型', 1, '系统权限类型枚举', 'system', NOW()),
('7a2b3c4d5e6f7890123456789abcdef7', NULL, 'role_type', '角色类型', 1, '系统角色类型枚举', 'system', NOW());

-- 2.4 系统相关字典类型
INSERT IGNORE INTO sys_dict_type (
    dict_type_id, tenant_id, dict_type_code, dict_type_name, status, remark,
    created_by, created_time
) VALUES 
('8a2b3c4d5e6f7890123456789abcdef8', NULL, 'operation_type', '操作类型', 1, '系统操作类型枚举', 'system', NOW()),
('9a2b3c4d5e6f7890123456789abcdef9', NULL, 'config_type', '配置类型', 1, '系统配置数据类型枚举', 'system', NOW()),
('aa2b3c4d5e6f7890123456789abcdefa', NULL, 'tenant_type', '租户类型', 1, '租户类型枚举', 'system', NOW());

-- 2.5 业务相关字典类型
INSERT IGNORE INTO sys_dict_type (
    dict_type_id, tenant_id, dict_type_code, dict_type_name, status, remark,
    created_by, created_time
) VALUES 
('ba2b3c4d5e6f7890123456789abcdefb', NULL, 'file_storage_type', '文件存储类型', 1, '文件存储策略枚举', 'system', NOW()),
('ca2b3c4d5e6f7890123456789abcdefc', NULL, 'audit_level', '审计级别', 1, '系统审计级别枚举', 'system', NOW()),
('da2b3c4d5e6f7890123456789abcdefd', NULL, 'notice_type', '通知类型', 1, '系统通知类型枚举', 'system', NOW()),
('ea2b3c4d5e6f7890123456789abcdefe', NULL, 'approval_status', '审批状态', 1, '业务审批状态枚举', 'system', NOW()),
('fa2b3c4d5e6f7890123456789abcdeff', NULL, 'priority_level', '优先级', 1, '业务优先级枚举', 'system', NOW());

-- =============================================
-- 3. 字典数据初始化
-- =============================================

-- 3.1 用户状态字典数据
INSERT IGNORE INTO sys_dict_data (
    dict_data_id, dict_type_id, dict_label, dict_value, dict_sort, status, is_default,
    css_class, list_class, remark, created_by, created_time
) VALUES 
('1b2c3d4e5f6789012345678901234ba1', '1a2b3c4d5e6f7890123456789abcdef1', '启用', '1', 1, 1, 1, 'success', 'success', '用户正常状态', 'system', NOW()),
('1b2c3d4e5f6789012345678901234ba2', '1a2b3c4d5e6f7890123456789abcdef1', '禁用', '0', 2, 1, 0, 'danger', 'danger', '用户被禁用', 'system', NOW()),
('1b2c3d4e5f6789012345678901234ba3', '1a2b3c4d5e6f7890123456789abcdef1', '锁定', '2', 3, 1, 0, 'warning', 'warning', '用户被锁定', 'system', NOW()),
('1b2c3d4e5f6789012345678901234ba4', '1a2b3c4d5e6f7890123456789abcdef1', '待激活', '3', 4, 1, 0, 'info', 'info', '用户待激活', 'system', NOW());

-- 3.2 用户性别字典数据
INSERT IGNORE INTO sys_dict_data (
    dict_data_id, dict_type_id, dict_label, dict_value, dict_sort, status, is_default,
    css_class, list_class, remark, created_by, created_time
) VALUES 
('2b2c3d4e5f6789012345678901234bb1', '2a2b3c4d5e6f7890123456789abcdef2', '未知', '0', 1, 1, 1, 'info', 'info', '性别未知', 'system', NOW()),
('2b2c3d4e5f6789012345678901234bb2', '2a2b3c4d5e6f7890123456789abcdef2', '男', '1', 2, 1, 0, 'primary', 'primary', '男性', 'system', NOW()),
('2b2c3d4e5f6789012345678901234bb3', '2a2b3c4d5e6f7890123456789abcdef2', '女', '2', 3, 1, 0, 'pink', 'pink', '女性', 'system', NOW());

-- 3.3 登录类型字典数据
INSERT IGNORE INTO sys_dict_data (
    dict_data_id, dict_type_id, dict_label, dict_value, dict_sort, status, is_default,
    css_class, list_class, remark, created_by, created_time
) VALUES 
('3b2c3d4e5f6789012345678901234bc1', '3a2b3c4d5e6f7890123456789abcdef3', '用户名', '1', 1, 1, 1, 'primary', 'primary', '用户名密码登录', 'system', NOW()),
('3b2c3d4e5f6789012345678901234bc2', '3a2b3c4d5e6f7890123456789abcdef3', '手机号', '2', 2, 1, 0, 'success', 'success', '手机号密码登录', 'system', NOW()),
('3b2c3d4e5f6789012345678901234bc3', '3a2b3c4d5e6f7890123456789abcdef3', '邮箱', '3', 3, 1, 0, 'info', 'info', '邮箱密码登录', 'system', NOW());

-- 3.4 组织类型字典数据
INSERT IGNORE INTO sys_dict_data (
    dict_data_id, dict_type_id, dict_label, dict_value, dict_sort, status, is_default,
    css_class, list_class, remark, created_by, created_time
) VALUES 
('4b2c3d4e5f6789012345678901234bd1', '4a2b3c4d5e6f7890123456789abcdef4', '公司', '1', 1, 1, 0, 'primary', 'primary', '公司级组织', 'system', NOW()),
('4b2c3d4e5f6789012345678901234bd2', '4a2b3c4d5e6f7890123456789abcdef4', '部门', '2', 2, 1, 1, 'success', 'success', '部门级组织', 'system', NOW()),
('4b2c3d4e5f6789012345678901234bd3', '4a2b3c4d5e6f7890123456789abcdef4', '岗位', '3', 3, 1, 0, 'info', 'info', '岗位级组织', 'system', NOW()),
('4b2c3d4e5f6789012345678901234bd4', '4a2b3c4d5e6f7890123456789abcdef4', '小组', '4', 4, 1, 0, 'warning', 'warning', '小组级组织', 'system', NOW());

-- 3.5 数据权限范围字典数据
INSERT IGNORE INTO sys_dict_data (
    dict_data_id, dict_type_id, dict_label, dict_value, dict_sort, status, is_default,
    css_class, list_class, remark, created_by, created_time
) VALUES 
('5b2c3d4e5f6789012345678901234be1', '5a2b3c4d5e6f7890123456789abcdef5', '全部数据', '1', 1, 1, 0, 'danger', 'danger', '可查看全部数据', 'system', NOW()),
('5b2c3d4e5f6789012345678901234be2', '5a2b3c4d5e6f7890123456789abcdef5', '本部门', '2', 2, 1, 1, 'warning', 'warning', '仅本部门数据', 'system', NOW()),
('5b2c3d4e5f6789012345678901234be3', '5a2b3c4d5e6f7890123456789abcdef5', '本部门及下级', '3', 3, 1, 0, 'info', 'info', '本部门及下级部门数据', 'system', NOW()),
('5b2c3d4e5f6789012345678901234be4', '5a2b3c4d5e6f7890123456789abcdef5', '仅本人', '4', 4, 1, 0, 'success', 'success', '仅本人创建的数据', 'system', NOW()),
('5b2c3d4e5f6789012345678901234be5', '5a2b3c4d5e6f7890123456789abcdef5', '自定义', '5', 5, 1, 0, 'primary', 'primary', '自定义数据权限', 'system', NOW());

-- =============================================
-- 4. 基础角色初始化
-- =============================================

-- 4.1 系统级角色 (tenant_id = NULL)
INSERT IGNORE INTO sys_role (
    role_id, tenant_id, role_code, role_name, role_type, data_scope, status, sort_order, remark,
    created_by, created_time
) VALUES 
('1c2d3e4f5g6789012345678901234ca1', NULL, 'SUPER_ADMIN', '超级管理员', 1, 1, 1, 1, '系统最高权限角色，拥有所有权限', 'system', NOW()),
('1c2d3e4f5g6789012345678901234ca2', NULL, 'SYS_OPS', '系统运维员', 1, 1, 1, 2, '系统运维监控角色，负责系统维护', 'system', NOW()),
('1c2d3e4f5g6789012345678901234ca3', NULL, 'PLATFORM_ADMIN', '平台管理员', 1, 1, 1, 3, '平台配置管理角色，负责平台配置', 'system', NOW());

-- 4.2 租户级角色
INSERT IGNORE INTO sys_role (
    role_id, tenant_id, role_code, role_name, role_type, data_scope, status, sort_order, remark,
    created_by, created_time
) VALUES 
('2c2d3e4f5g6789012345678901234cb1', 'a1b2c3d4e5f6789012345678901234ab', 'TENANT_ADMIN', '租户管理员', 2, 1, 1, 1, '租户内最高权限角色', 'system', NOW()),
('2c2d3e4f5g6789012345678901234cb2', 'a1b2c3d4e5f6789012345678901234ab', 'DEPT_ADMIN', '部门管理员', 2, 2, 1, 2, '部门管理权限角色', 'system', NOW()),
('2c2d3e4f5g6789012345678901234cb3', 'a1b2c3d4e5f6789012345678901234ab', 'NORMAL_USER', '普通用户', 2, 4, 1, 3, '普通业务用户角色', 'system', NOW()),
('2c2d3e4f5g6789012345678901234cb4', 'a1b2c3d4e5f6789012345678901234ab', 'READONLY_USER', '只读用户', 2, 4, 1, 4, '只读查看权限角色', 'system', NOW()),
('2c2d3e4f5g6789012345678901234cb5', 'a1b2c3d4e5f6789012345678901234ab', 'GUEST_USER', '访客用户', 2, 4, 1, 5, '访客临时权限角色', 'system', NOW());

-- =============================================
-- 5. 系统权限初始化
-- =============================================

-- 5.1 系统管理权限模块
INSERT IGNORE INTO sys_permission (
    permission_id, tenant_id, permission_code, permission_name, permission_type, parent_id, path, 
    component, icon, sort_order, status, remark, created_by, created_time
) VALUES 
-- 一级菜单：系统管理
('1d2e3f4g5h6789012345678901234da1', NULL, 'system', '系统管理', 1, '0', '/system', 'Layout', 'system', 1, 1, '系统管理根菜单', 'system', NOW()),
-- 二级菜单：用户管理
('1d2e3f4g5h6789012345678901234da2', NULL, 'system:user', '用户管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/user', 'system/user/index', 'user', 1, 1, '用户管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da3', NULL, 'system:user:list', '用户查询', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 1, 1, '用户查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da4', NULL, 'system:user:create', '用户新增', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 2, 1, '用户新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da5', NULL, 'system:user:update', '用户修改', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 3, 1, '用户修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da6', NULL, 'system:user:delete', '用户删除', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 4, 1, '用户删除权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da7', NULL, 'system:user:reset', '密码重置', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 5, 1, '密码重置权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da8', NULL, 'system:user:export', '用户导出', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 6, 1, '用户导出权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da9', NULL, 'system:user:import', '用户导入', 2, '1d2e3f4g5h6789012345678901234da2', '', '', '', 7, 1, '用户导入权限', 'system', NOW()),
-- 二级菜单：角色管理
('1d2e3f4g5h6789012345678901234daa', NULL, 'system:role', '角色管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/role', 'system/role/index', 'role', 2, 1, '角色管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dab', NULL, 'system:role:list', '角色查询', 2, '1d2e3f4g5h6789012345678901234daa', '', '', '', 1, 1, '角色查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dac', NULL, 'system:role:create', '角色新增', 2, '1d2e3f4g5h6789012345678901234daa', '', '', '', 2, 1, '角色新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dad', NULL, 'system:role:update', '角色修改', 2, '1d2e3f4g5h6789012345678901234daa', '', '', '', 3, 1, '角色修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dae', NULL, 'system:role:delete', '角色删除', 2, '1d2e3f4g5h6789012345678901234daa', '', '', '', 4, 1, '角色删除权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234daf', NULL, 'system:role:permission', '分配权限', 2, '1d2e3f4g5h6789012345678901234daa', '', '', '', 5, 1, '角色权限分配', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db0', NULL, 'system:role:export', '角色导出', 2, '1d2e3f4g5h6789012345678901234daa', '', '', '', 6, 1, '角色导出权限', 'system', NOW()),
-- 二级菜单：权限管理
('1d2e3f4g5h6789012345678901234db1', NULL, 'system:permission', '权限管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/permission', 'system/permission/index', 'permission', 3, 1, '权限管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db2', NULL, 'system:permission:list', '权限查询', 2, '1d2e3f4g5h6789012345678901234db1', '', '', '', 1, 1, '权限查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db3', NULL, 'system:permission:create', '权限新增', 2, '1d2e3f4g5h6789012345678901234db1', '', '', '', 2, 1, '权限新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db4', NULL, 'system:permission:update', '权限修改', 2, '1d2e3f4g5h6789012345678901234db1', '', '', '', 3, 1, '权限修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db5', NULL, 'system:permission:delete', '权限删除', 2, '1d2e3f4g5h6789012345678901234db1', '', '', '', 4, 1, '权限删除权限', 'system', NOW()),
-- 二级菜单：组织管理
('1d2e3f4g5h6789012345678901234db6', NULL, 'system:org', '组织管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/org', 'system/org/index', 'org', 4, 1, '组织管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db7', NULL, 'system:org:list', '组织查询', 2, '1d2e3f4g5h6789012345678901234db6', '', '', '', 1, 1, '组织查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db8', NULL, 'system:org:create', '组织新增', 2, '1d2e3f4g5h6789012345678901234db6', '', '', '', 2, 1, '组织新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234db9', NULL, 'system:org:update', '组织修改', 2, '1d2e3f4g5h6789012345678901234db6', '', '', '', 3, 1, '组织修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dba', NULL, 'system:org:delete', '组织删除', 2, '1d2e3f4g5h6789012345678901234db6', '', '', '', 4, 1, '组织删除权限', 'system', NOW()),
-- 二级菜单：字典管理
('1d2e3f4g5h6789012345678901234dbb', NULL, 'system:dict', '字典管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/dict', 'system/dict/index', 'dict', 5, 1, '字典管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dbc', NULL, 'system:dict:list', '字典查询', 2, '1d2e3f4g5h6789012345678901234dbb', '', '', '', 1, 1, '字典查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dbd', NULL, 'system:dict:create', '字典新增', 2, '1d2e3f4g5h6789012345678901234dbb', '', '', '', 2, 1, '字典新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dbe', NULL, 'system:dict:update', '字典修改', 2, '1d2e3f4g5h6789012345678901234dbb', '', '', '', 3, 1, '字典修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dbf', NULL, 'system:dict:delete', '字典删除', 2, '1d2e3f4g5h6789012345678901234dbb', '', '', '', 4, 1, '字典删除权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc0', NULL, 'system:dict:export', '字典导出', 2, '1d2e3f4g5h6789012345678901234dbb', '', '', '', 5, 1, '字典导出权限', 'system', NOW()),
-- 二级菜单：配置管理
('1d2e3f4g5h6789012345678901234dc1', NULL, 'system:config', '配置管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/config', 'system/config/index', 'config', 6, 1, '配置管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc2', NULL, 'system:config:list', '配置查询', 2, '1d2e3f4g5h6789012345678901234dc1', '', '', '', 1, 1, '配置查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc3', NULL, 'system:config:create', '配置新增', 2, '1d2e3f4g5h6789012345678901234dc1', '', '', '', 2, 1, '配置新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc4', NULL, 'system:config:update', '配置修改', 2, '1d2e3f4g5h6789012345678901234dc1', '', '', '', 3, 1, '配置修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc5', NULL, 'system:config:delete', '配置删除', 2, '1d2e3f4g5h6789012345678901234dc1', '', '', '', 4, 1, '配置删除权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc6', NULL, 'system:config:export', '配置导出', 2, '1d2e3f4g5h6789012345678901234dc1', '', '', '', 5, 1, '配置导出权限', 'system', NOW()),
-- 二级菜单：租户管理
('1d2e3f4g5h6789012345678901234dc7', NULL, 'system:tenant', '租户管理', 1, '1d2e3f4g5h6789012345678901234da1', '/system/tenant', 'system/tenant/index', 'tenant', 7, 1, '租户管理菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc8', NULL, 'system:tenant:list', '租户查询', 2, '1d2e3f4g5h6789012345678901234dc7', '', '', '', 1, 1, '租户查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dc9', NULL, 'system:tenant:create', '租户新增', 2, '1d2e3f4g5h6789012345678901234dc7', '', '', '', 2, 1, '租户新增权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dca', NULL, 'system:tenant:update', '租户修改', 2, '1d2e3f4g5h6789012345678901234dc7', '', '', '', 3, 1, '租户修改权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dcb', NULL, 'system:tenant:delete', '租户删除', 2, '1d2e3f4g5h6789012345678901234dc7', '', '', '', 4, 1, '租户删除权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dcc', NULL, 'system:tenant:status', '租户状态', 2, '1d2e3f4g5h6789012345678901234dc7', '', '', '', 5, 1, '租户状态管理', 'system', NOW());

-- 5.2 系统监控权限模块
INSERT IGNORE INTO sys_permission (
    permission_id, tenant_id, permission_code, permission_name, permission_type, parent_id, path, 
    component, icon, sort_order, status, remark, created_by, created_time
) VALUES 
-- 一级菜单：系统监控
('1d2e3f4g5h6789012345678901234dcd', NULL, 'monitor', '系统监控', 1, '0', '/monitor', 'Layout', 'monitor', 2, 1, '系统监控根菜单', 'system', NOW()),
-- 二级菜单：操作日志
('1d2e3f4g5h6789012345678901234dce', NULL, 'monitor:log', '操作日志', 1, '1d2e3f4g5h6789012345678901234dcd', '/monitor/log', 'monitor/log/index', 'log', 1, 1, '操作日志菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dcf', NULL, 'monitor:log:list', '日志查询', 2, '1d2e3f4g5h6789012345678901234dce', '', '', '', 1, 1, '日志查询权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dch', NULL, 'monitor:log:export', '日志导出', 2, '1d2e3f4g5h6789012345678901234dce', '', '', '', 2, 1, '日志导出权限', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dci', NULL, 'monitor:log:clear', '日志清理', 2, '1d2e3f4g5h6789012345678901234dce', '', '', '', 3, 1, '日志清理权限', 'system', NOW()),
-- 二级菜单：在线用户
('1d2e3f4g5h6789012345678901234dcj', NULL, 'monitor:online', '在线用户', 1, '1d2e3f4g5h6789012345678901234dcd', '/monitor/online', 'monitor/online/index', 'online', 2, 1, '在线用户菜单', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dck', NULL, 'monitor:online:list', '在线查询', 2, '1d2e3f4g5h6789012345678901234dcj', '', '', '', 1, 1, '在线用户查询', 'system', NOW()),
('1d2e3f4g5h6789012345678901234dcl', NULL, 'monitor:online:kick', '强制下线', 2, '1d2e3f4g5h6789012345678901234dcj', '', '', '', 2, 1, '强制下线权限', 'system', NOW()); 

-- =============================================
-- 6. 系统配置数据初始化
-- =============================================

-- 6.1 安全配置
INSERT IGNORE INTO sys_config (
    config_id, tenant_id, config_key, config_name, config_value, config_type, remark,
    created_by, created_time
) VALUES 
('1f2g3h4i5j6789012345678901234fa1', NULL, 'security.password.min.length', '密码最小长度', '8', 'number', '用户密码最小长度限制', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa2', NULL, 'security.password.max.length', '密码最大长度', '30', 'number', '用户密码最大长度限制', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa3', NULL, 'security.password.complexity', '密码复杂度要求', 'true', 'boolean', '是否需要密码复杂度验证', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa4', NULL, 'security.login.max.attempts', '登录最大尝试次数', '5', 'number', '登录失败最大尝试次数', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa5', NULL, 'security.login.lock.duration', '账户锁定时长', '1800', 'number', '账户锁定时长（秒）', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa6', NULL, 'security.token.expire.time', 'Token过期时间', '7200', 'number', '用户Token过期时间（秒）', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa7', NULL, 'security.token.refresh.enabled', '刷新Token启用', 'true', 'boolean', '是否启用Token自动刷新', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa8', NULL, 'security.2fa.enabled', '二次验证启用', 'false', 'boolean', '是否启用二次验证', 'system', NOW()),
('1f2g3h4i5j6789012345678901234fa9', NULL, 'security.api.rate.limit', 'API访问频率限制', '1000', 'number', '每分钟API访问次数限制', 'system', NOW()),
('1f2g3h4i5j6789012345678901234faa', NULL, 'security.ip.whitelist.enabled', 'IP白名单启用', 'false', 'boolean', '是否启用IP白名单', 'system', NOW());

-- 6.2 业务配置
INSERT IGNORE INTO sys_config (
    config_id, tenant_id, config_key, config_name, config_value, config_type, remark,
    created_by, created_time
) VALUES 
('2f2g3h4i5j6789012345678901234fb1', NULL, 'business.system.name', '系统名称', 'Slavopolis', 'string', '系统显示名称', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb2', NULL, 'business.system.version', '系统版本', '1.0.0', 'string', '当前系统版本号', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb3', NULL, 'business.system.logo', '系统LOGO', '/static/images/logo.png', 'string', '系统LOGO地址', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb4', NULL, 'business.file.max.size', '文件最大大小', '10485760', 'number', '文件上传最大大小（字节）', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb5', NULL, 'business.file.allowed.types', '允许文件类型', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'string', '允许上传的文件类型', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb6', NULL, 'business.notification.enabled', '通知功能启用', 'true', 'boolean', '是否启用系统通知', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb7', NULL, 'business.audit.enabled', '审计功能启用', 'true', 'boolean', '是否启用操作审计', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb8', NULL, 'business.audit.level', '审计级别', '2', 'number', '审计记录级别', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fb9', NULL, 'business.backup.enabled', '备份功能启用', 'true', 'boolean', '是否启用数据备份', 'system', NOW()),
('2f2g3h4i5j6789012345678901234fba', NULL, 'business.backup.schedule', '备份计划', '0 2 * * *', 'string', '数据备份计划Cron表达式', 'system', NOW());

-- 6.3 功能开关配置
INSERT IGNORE INTO sys_config (
    config_id, tenant_id, config_key, config_name, config_value, config_type, remark,
    created_by, created_time
) VALUES 
('1g2h3i4j5k6789012345678901234ga1', NULL, 'feature.register.enabled', '注册功能启用', 'true', 'boolean', '是否开放用户注册', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga2', NULL, 'feature.email.verify.enabled', '邮箱验证启用', 'true', 'boolean', '是否启用邮箱验证', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga3', NULL, 'feature.sms.enabled', '短信功能启用', 'false', 'boolean', '是否启用短信功能', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga4', NULL, 'feature.social.login.enabled', '社交登录启用', 'false', 'boolean', '是否启用社交登录', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga5', NULL, 'feature.api.doc.enabled', 'API文档启用', 'true', 'boolean', '是否启用API文档', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga6', NULL, 'feature.offline.mode.enabled', '离线模式启用', 'false', 'boolean', '是否启用离线模式', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga7', NULL, 'feature.data.import.enabled', '数据导入启用', 'true', 'boolean', '是否启用数据导入', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga8', NULL, 'feature.data.export.enabled', '数据导出启用', 'true', 'boolean', '是否启用数据导出', 'system', NOW()),
('1g2h3i4j5k6789012345678901234ga9', NULL, 'feature.maintenance.mode', '维护模式', 'false', 'boolean', '是否启用维护模式', 'system', NOW()),
('1g2h3i4j5k6789012345678901234gaa', NULL, 'feature.debug.logging', '调试日志启用', 'false', 'boolean', '是否启用调试日志', 'system', NOW());

-- 6.4 存储配置
INSERT IGNORE INTO sys_config (
    config_id, tenant_id, config_key, config_name, config_value, config_type, remark,
    created_by, created_time
) VALUES 
('1h2i3j4k5l6789012345678901234ha1', NULL, 'storage.type', '存储类型', 'OSS', 'string', '默认文件存储类型', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha2', NULL, 'storage.oss.endpoint', 'OSS终端地址', 'oss-cn-hangzhou.aliyuncs.com', 'string', '阿里云OSS终端地址', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha3', NULL, 'storage.oss.bucket', 'OSS存储桶', 'slavopolis-files', 'string', '阿里云OSS存储桶名称', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha4', NULL, 'storage.oss.access.key', 'OSS访问密钥', '', 'string', '阿里云OSS访问密钥', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha5', NULL, 'storage.oss.secret.key', 'OSS秘密密钥', '', 'string', '阿里云OSS秘密密钥', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha6', NULL, 'storage.local.path', '本地存储路径', '/data/uploads', 'string', '本地文件存储路径', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha7', NULL, 'storage.local.max.size', '本地存储限制', '1073741824', 'number', '本地存储空间限制（字节）', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha8', NULL, 'storage.database.enabled', '数据库存储启用', 'false', 'boolean', '是否启用数据库存储', 'system', NOW()),
('1h2i3j4k5l6789012345678901234ha9', NULL, 'storage.cleanup.enabled', '存储清理启用', 'true', 'boolean', '是否启用存储清理', 'system', NOW()),
('1h2i3j4k5l6789012345678901234haa', NULL, 'storage.cleanup.schedule', '存储清理计划', '0 3 * * 0', 'string', '存储清理计划Cron表达式', 'system', NOW());

-- =============================================
-- 7. 默认组织结构初始化
-- =============================================

-- 7.1 创建默认组织结构
INSERT IGNORE INTO sys_organization (
    org_id, tenant_id, org_code, org_name, org_type, parent_id, sort_order, status, remark,
    created_by, created_time
) VALUES 
('1e2f3g4h5i6789012345678901234ea1', 'a1b2c3d4e5f6789012345678901234ab', 'ROOT', 'Slavopolis集团', 1, '0', 1, 1, '集团总部', 'system', NOW()),
('1e2f3g4h5i6789012345678901234ea2', 'a1b2c3d4e5f6789012345678901234ab', 'TECH', '技术部', 2, '1e2f3g4h5i6789012345678901234ea1', 1, 1, '技术研发部门', 'system', NOW()),
('1e2f3g4h5i6789012345678901234ea3', 'a1b2c3d4e5f6789012345678901234ab', 'BUSINESS', '业务部', 2, '1e2f3g4h5i6789012345678901234ea1', 2, 1, '业务运营部门', 'system', NOW()),
('1e2f3g4h5i6789012345678901234ea4', 'a1b2c3d4e5f6789012345678901234ab', 'ADMIN', '行政部', 2, '1e2f3g4h5i6789012345678901234ea1', 3, 1, '行政管理部门', 'system', NOW());

-- =============================================
-- 8. 默认用户初始化
-- =============================================

-- 8.1 创建默认管理员用户
INSERT IGNORE INTO sys_user (
    user_id, tenant_id, username, password, nickname, email, phone, avatar, gender, status, 
    last_login_time, created_by, created_time
) VALUES 
('1d2e3f4g5h6789012345678901234da1', 'a1b2c3d4e5f6789012345678901234ab', 'admin', '$2a$10$7JB720yubVSOfvVPCk8VLe.T3VLO/dxHVSJrWYVXwSHfQTjR5dJ3C', '超级管理员', 'admin@slavopolis.club', '13800138000', '/static/avatars/admin.png', 1, 1, NOW(), 'system', NOW()),
('1d2e3f4g5h6789012345678901234da2', 'a1b2c3d4e5f6789012345678901234ab', 'operator', '$2a$10$7JB720yubVSOfvVPCk8VLe.T3VLO/dxHVSJrWYVXwSHfQTjR5dJ3C', '系统运维', 'operator@slavopolis.club', '13800138001', '/static/avatars/operator.png', 1, 1, NOW(), 'system', NOW()),
('1d2e3f4g5h6789012345678901234da3', 'a1b2c3d4e5f6789012345678901234ab', 'demo', '$2a$10$7JB720yubVSOfvVPCk8VLe.T3VLO/dxHVSJrWYVXwSHfQTjR5dJ3C', '演示用户', 'demo@slavopolis.club', '13800138002', '/static/avatars/demo.png', 2, 1, NOW(), 'system', NOW());

-- =============================================
-- 9. 用户角色分配
-- =============================================

-- 9.1 默认用户角色分配
INSERT IGNORE INTO sys_user_role (user_id, role_id, created_by, created_time) VALUES 
('1d2e3f4g5h6789012345678901234da1', '1c2d3e4f5g6789012345678901234ca1', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da1', '2c2d3e4f5g6789012345678901234cb1', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da2', '1c2d3e4f5g6789012345678901234ca2', 'system', NOW()),
('1d2e3f4g5h6789012345678901234da3', '2c2d3e4f5g6789012345678901234cb3', 'system', NOW());

-- =============================================
-- 10. 系统权限分配
-- =============================================

-- 10.1 超级管理员角色权限（系统级）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id, created_by, created_time)
SELECT '1c2d3e4f5g6789012345678901234ca1', permission_id, 'system', NOW() 
FROM sys_permission WHERE tenant_id IS NULL;

-- 10.2 租户管理员角色权限（业务功能）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id, created_by, created_time)
SELECT '2c2d3e4f5g6789012345678901234cb1', permission_id, 'system', NOW() 
FROM sys_permission 
WHERE tenant_id IS NULL 
AND permission_code NOT LIKE '%tenant%' 
AND permission_code NOT LIKE '%platform%';

-- 10.3 普通用户角色权限（基础查询）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id, created_by, created_time)
SELECT '2c2d3e4f5g6789012345678901234cb3', permission_id, 'system', NOW() 
FROM sys_permission 
WHERE tenant_id IS NULL 
AND permission_code IN ('system:user:list', 'system:org:list', 'monitor:online:list');

-- 10.4 只读用户角色权限（仅查询）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id, created_by, created_time)
SELECT '2c2d3e4f5g6789012345678901234cb4', permission_id, 'system', NOW() 
FROM sys_permission 
WHERE tenant_id IS NULL 
AND permission_code LIKE '%:list';

-- =============================================
-- 11. 事务提交和脚本结束
-- =============================================

-- 提交事务
COMMIT;

-- 重置外键检查
SET FOREIGN_KEY_CHECKS = 1;