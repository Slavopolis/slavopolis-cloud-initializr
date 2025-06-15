package club.slavopolis.common.security.context;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import club.slavopolis.common.core.constants.CommonConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 租户上下文
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantContext {

    /**
     * 线程本地存储
     */
    private static final ThreadLocal<TenantContextHolder> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 默认租户ID
     */
    public static final String DEFAULT_TENANT_ID = "default";

    /**
     * 系统租户ID
     */
    public static final String SYSTEM_TENANT_ID = "system";

    /**
     * 租户上下文持有者
     */
    private static class TenantContextHolder {
        private String tenantId;
        private String tenantName;
        private String tenantCode;
        private String tenantType;
        private String dataSourceKey;
        private String schemaName;
        private String tablePrefix;
        private final Map<String, Object> tenantConfig;
        private boolean enabled;
        private long createTime;

        public TenantContextHolder() {
            this.tenantConfig = new ConcurrentHashMap<>();
            this.enabled = true;
            this.createTime = System.currentTimeMillis();
        }
    }

    // ==================== 基础租户信息管理 ====================

    /**
     * 设置当前租户ID
     * 
     * @param tenantId 租户ID
     */
    public static void setTenantId(String tenantId) {
        getOrCreateHolder().tenantId = tenantId;
    }

    /**
     * 获取当前租户ID
     * 
     * @return 租户ID
     */
    public static String getTenantId() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantId : DEFAULT_TENANT_ID;
    }

    /**
     * 设置租户名称
     * 
     * @param tenantName 租户名称
     */
    public static void setTenantName(String tenantName) {
        getOrCreateHolder().tenantName = tenantName;
    }

    /**
     * 获取租户名称
     * 
     * @return 租户名称
     */
    public static String getTenantName() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantName : null;
    }

    /**
     * 设置租户编码
     * 
     * @param tenantCode 租户编码
     */
    public static void setTenantCode(String tenantCode) {
        getOrCreateHolder().tenantCode = tenantCode;
    }

    /**
     * 获取租户编码
     * 
     * @return 租户编码
     */
    public static String getTenantCode() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantCode : null;
    }

    /**
     * 设置租户类型
     * 
     * @param tenantType 租户类型
     */
    public static void setTenantType(String tenantType) {
        getOrCreateHolder().tenantType = tenantType;
    }

    /**
     * 获取租户类型
     * 
     * @return 租户类型
     */
    public static String getTenantType() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantType : null;
    }

    // ==================== 数据源管理 ====================

    /**
     * 设置数据源键
     * 
     * @param dataSourceKey 数据源键
     */
    public static void setDataSourceKey(String dataSourceKey) {
        getOrCreateHolder().dataSourceKey = dataSourceKey;
    }

    /**
     * 获取数据源键
     * 
     * @return 数据源键
     */
    public static String getDataSourceKey() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.dataSourceKey : null;
    }

    /**
     * 设置模式名称
     * 
     * @param schemaName 模式名称
     */
    public static void setSchemaName(String schemaName) {
        getOrCreateHolder().schemaName = schemaName;
    }

    /**
     * 获取模式名称
     * 
     * @return 模式名称
     */
    public static String getSchemaName() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.schemaName : null;
    }

    /**
     * 设置表前缀
     * 
     * @param tablePrefix 表前缀
     */
    public static void setTablePrefix(String tablePrefix) {
        getOrCreateHolder().tablePrefix = tablePrefix;
    }

    /**
     * 获取表前缀
     * 
     * @return 表前缀
     */
    public static String getTablePrefix() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tablePrefix : null;
    }

    // ==================== 租户状态管理 ====================

    /**
     * 设置租户启用状态
     * 
     * @param enabled 是否启用
     */
    public static void setEnabled(boolean enabled) {
        getOrCreateHolder().enabled = enabled;
    }

    /**
     * 判断租户是否启用
     * 
     * @return 是否启用
     */
    public static boolean isEnabled() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null && holder.enabled;
    }

    /**
     * 设置创建时间
     * 
     * @param createTime 创建时间
     */
    public static void setCreateTime(long createTime) {
        getOrCreateHolder().createTime = createTime;
    }

    /**
     * 获取创建时间
     * 
     * @return 创建时间
     */
    public static long getCreateTime() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.createTime : 0L;
    }

    // ==================== 租户配置管理 ====================

    /**
     * 设置租户配置
     * 
     * @param key 配置键
     * @param value 配置值
     */
    public static void setConfig(String key, Object value) {
        if (key != null) {
            getOrCreateHolder().tenantConfig.put(key, value);
        }
    }

    /**
     * 获取租户配置
     * 
     * @param key 配置键
     * @return 配置值
     */
    public static Object getConfig(String key) {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantConfig.get(key) : null;
    }

    /**
     * 获取租户配置（指定类型）
     * 
     * @param key 配置键
     * @param type 配置类型
     * @return 配置值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getConfig(String key, Class<T> type) {
        Object value = getConfig(key);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 获取租户配置（带默认值）
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getConfig(String key, T defaultValue) {
        Object value = getConfig(key);
        if (defaultValue != null && defaultValue.getClass().isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }

    /**
     * 移除租户配置
     * 
     * @param key 配置键
     * @return 被移除的配置值
     */
    public static Object removeConfig(String key) {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantConfig.remove(key) : null;
    }

    /**
     * 获取所有租户配置
     * 
     * @return 配置映射
     */
    public static Map<String, Object> getAllConfig() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? Collections.unmodifiableMap(holder.tenantConfig) : Collections.emptyMap();
    }

    /**
     * 清空租户配置
     */
    public static void clearConfig() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        if (holder != null) {
            holder.tenantConfig.clear();
        }
    }

    // ==================== 上下文管理 ====================

    /**
     * 清空当前线程的租户上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 判断当前线程是否有租户上下文
     * 
     * @return 是否有租户上下文
     */
    public static boolean hasContext() {
        return CONTEXT_HOLDER.get() != null;
    }

    /**
     * 获取或创建上下文持有者
     * 
     * @return 上下文持有者
     */
    private static TenantContextHolder getOrCreateHolder() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        if (holder == null) {
            holder = new TenantContextHolder();
            CONTEXT_HOLDER.set(holder);
        }
        return holder;
    }

    // ==================== 便捷方法 ====================

    /**
     * 初始化租户上下文
     * 
     * @param tenantId 租户ID
     * @param tenantName 租户名称
     * @param tenantCode 租户编码
     * @param dataSourceKey 数据源键
     */
    public static void initialize(String tenantId, String tenantName, String tenantCode, String dataSourceKey) {
        clear();
        setTenantId(tenantId);
        setTenantName(tenantName);
        setTenantCode(tenantCode);
        setDataSourceKey(dataSourceKey);
        setEnabled(true);
    }

    /**
     * 初始化租户上下文（完整版）
     * 
     * @param tenantId 租户ID
     * @param tenantName 租户名称
     * @param tenantCode 租户编码
     * @param tenantType 租户类型
     * @param dataSourceKey 数据源键
     * @param schemaName 模式名称
     * @param tablePrefix 表前缀
     */
    public static void initialize(String tenantId, String tenantName, String tenantCode, 
                                String tenantType, String dataSourceKey, String schemaName, String tablePrefix) {
        clear();
        setTenantId(tenantId);
        setTenantName(tenantName);
        setTenantCode(tenantCode);
        setTenantType(tenantType);
        setDataSourceKey(dataSourceKey);
        setSchemaName(schemaName);
        setTablePrefix(tablePrefix);
        setEnabled(true);
    }

    /**
     * 获取当前租户的简要信息
     * 
     * @return 租户信息字符串
     */
    public static String getCurrentTenantInfo() {
        TenantContextHolder holder = CONTEXT_HOLDER.get();
        if (holder == null) {
            return "Tenant{id=" + DEFAULT_TENANT_ID + "}";
        }
        
        return String.format("Tenant{id=%s, name=%s, code=%s, type=%s, enabled=%s}", 
            holder.tenantId, holder.tenantName, holder.tenantCode, holder.tenantType, holder.enabled);
    }

    // ==================== 租户类型判断 ====================

    /**
     * 判断是否为默认租户
     * 
     * @return 是否为默认租户
     */
    public static boolean isDefaultTenant() {
        return DEFAULT_TENANT_ID.equals(getTenantId());
    }

    /**
     * 判断是否为系统租户
     * 
     * @return 是否为系统租户
     */
    public static boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(getTenantId());
    }

    /**
     * 判断是否为企业租户
     * 
     * @return 是否为企业租户
     */
    public static boolean isEnterpriseTenant() {
        return "ENTERPRISE".equals(getTenantType());
    }

    /**
     * 判断是否为个人租户
     * 
     * @return 是否为个人租户
     */
    public static boolean isPersonalTenant() {
        return "PERSONAL".equals(getTenantType());
    }

    /**
     * 判断是否为试用租户
     * 
     * @return 是否为试用租户
     */
    public static boolean isTrialTenant() {
        return "TRIAL".equals(getTenantType());
    }

    // ==================== 数据隔离相关 ====================

    /**
     * 获取完整的表名（带前缀）
     * 
     * @param tableName 原始表名
     * @return 完整表名
     */
    public static String getFullTableName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return tableName;
        }
        
        String prefix = getTablePrefix();
        if (prefix != null && !prefix.isEmpty()) {
            return prefix + CommonConstants.UNDERSCORE + tableName;
        }
        
        return tableName;
    }

    /**
     * 获取完整的模式表名
     * 
     * @param tableName 表名
     * @return 完整的模式表名
     */
    public static String getFullSchemaTableName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return tableName;
        }
        
        String schema = getSchemaName();
        String fullTableName = getFullTableName(tableName);
        
        if (schema != null && !schema.isEmpty()) {
            return schema + CommonConstants.DOT + fullTableName;
        }
        
        return fullTableName;
    }

    /**
     * 生成租户特定的缓存键
     * 
     * @param key 原始键
     * @return 租户特定的缓存键
     */
    public static String getTenantCacheKey(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        
        String tenantId = getTenantId();
        return tenantId + CommonConstants.COLON + key;
    }

    /**
     * 生成租户特定的消息主题
     * 
     * @param topic 原始主题
     * @return 租户特定的消息主题
     */
    public static String getTenantTopic(String topic) {
        if (topic == null || topic.isEmpty()) {
            return topic;
        }
        
        String tenantId = getTenantId();
        return tenantId + CommonConstants.DOT + topic;
    }

    // ==================== 租户切换 ====================

    /**
     * 临时切换租户执行操作
     * 
     * @param tenantId 临时租户ID
     * @param action 要执行的操作
     */
    public static void runWithTenant(String tenantId, Runnable action) {
        String originalTenantId = getTenantId();
        try {
            setTenantId(tenantId);
            action.run();
        } finally {
            setTenantId(originalTenantId);
        }
    }

    /**
     * 临时切换租户执行操作（带返回值）
     * 
     * @param tenantId 临时租户ID
     * @param supplier 要执行的操作
     * @return 操作结果
     */
    public static <T> T callWithTenant(String tenantId, java.util.function.Supplier<T> supplier) {
        String originalTenantId = getTenantId();
        try {
            setTenantId(tenantId);
            return supplier.get();
        } finally {
            setTenantId(originalTenantId);
        }
    }

    /**
     * 以系统租户身份执行操作
     * 
     * @param action 要执行的操作
     */
    public static void runAsSystem(Runnable action) {
        runWithTenant(SYSTEM_TENANT_ID, action);
    }

    /**
     * 以系统租户身份执行操作（带返回值）
     * 
     * @param supplier 要执行的操作
     * @return 操作结果
     */
    public static <T> T callAsSystem(java.util.function.Supplier<T> supplier) {
        return callWithTenant(SYSTEM_TENANT_ID, supplier);
    }
} 