package club.slavopolis.common.security.context;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 安全上下文
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityContext {

    /**
     * 线程本地存储
     */
    private static final ThreadLocal<SecurityContextHolder> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 安全上下文持有者
     */
    private static class SecurityContextHolder {
        private String userId;
        private String username;
        private String tenantId;
        private String sessionId;
        private String requestId;
        private String traceId;
        private Set<String> roles;
        private Set<String> permissions;
        private final Map<String, Object> attributes;
        private long loginTime;
        private String clientIp;
        private String userAgent;
        private boolean authenticated;

        public SecurityContextHolder() {
            this.roles = Collections.emptySet();
            this.permissions = Collections.emptySet();
            this.attributes = new ConcurrentHashMap<>();
            this.authenticated = false;
            this.loginTime = System.currentTimeMillis();
        }
    }

    // ==================== 用户信息管理 ====================

    /**
     * 设置当前用户ID
     * 
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        getOrCreateHolder().userId = userId;
    }

    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static String getUserId() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.userId : null;
    }

    /**
     * 设置当前用户名
     * 
     * @param username 用户名
     */
    public static void setUsername(String username) {
        getOrCreateHolder().username = username;
    }

    /**
     * 获取当前用户名
     * 
     * @return 用户名
     */
    public static String getUsername() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.username : null;
    }

    /**
     * 设置认证状态
     * 
     * @param authenticated 是否已认证
     */
    public static void setAuthenticated(boolean authenticated) {
        getOrCreateHolder().authenticated = authenticated;
    }

    /**
     * 判断是否已认证
     * 
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null && holder.authenticated;
    }

    // ==================== 租户信息管理 ====================

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
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.tenantId : null;
    }

    // ==================== 会话信息管理 ====================

    /**
     * 设置会话ID
     * 
     * @param sessionId 会话ID
     */
    public static void setSessionId(String sessionId) {
        getOrCreateHolder().sessionId = sessionId;
    }

    /**
     * 获取会话ID
     * 
     * @return 会话ID
     */
    public static String getSessionId() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.sessionId : null;
    }

    /**
     * 设置请求ID
     * 
     * @param requestId 请求ID
     */
    public static void setRequestId(String requestId) {
        getOrCreateHolder().requestId = requestId;
    }

    /**
     * 获取请求ID
     * 
     * @return 请求ID
     */
    public static String getRequestId() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.requestId : null;
    }

    /**
     * 设置追踪ID
     * 
     * @param traceId 追踪ID
     */
    public static void setTraceId(String traceId) {
        getOrCreateHolder().traceId = traceId;
    }

    /**
     * 获取追踪ID
     * 
     * @return 追踪ID
     */
    public static String getTraceId() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.traceId : null;
    }

    // ==================== 权限管理 ====================

    /**
     * 设置用户角色
     * 
     * @param roles 角色集合
     */
    public static void setRoles(Set<String> roles) {
        getOrCreateHolder().roles = roles != null ? roles : Collections.emptySet();
    }

    /**
     * 获取用户角色
     * 
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.roles : Collections.emptySet();
    }

    /**
     * 判断是否拥有指定角色
     * 
     * @param role 角色
     * @return 是否拥有角色
     */
    public static boolean hasRole(String role) {
        if (role == null) {
            return false;
        }
        return getRoles().contains(role);
    }

    /**
     * 判断是否拥有任意一个角色
     * 
     * @param roles 角色列表
     * @return 是否拥有任意一个角色
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        
        Set<String> userRoles = getRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否拥有所有角色
     * 
     * @param roles 角色列表
     * @return 是否拥有所有角色
     */
    public static boolean hasAllRoles(String... roles) {
        if (roles == null || roles.length == 0) {
            return true;
        }
        
        Set<String> userRoles = getRoles();
        for (String role : roles) {
            if (!userRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置用户权限
     * 
     * @param permissions 权限集合
     */
    public static void setPermissions(Set<String> permissions) {
        getOrCreateHolder().permissions = permissions != null ? permissions : Collections.emptySet();
    }

    /**
     * 获取用户权限
     * 
     * @return 权限集合
     */
    public static Set<String> getPermissions() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.permissions : Collections.emptySet();
    }

    /**
     * 判断是否拥有指定权限
     * 
     * @param permission 权限
     * @return 是否拥有权限
     */
    public static boolean hasPermission(String permission) {
        if (permission == null) {
            return false;
        }
        return getPermissions().contains(permission);
    }

    /**
     * 判断是否拥有任意一个权限
     * 
     * @param permissions 权限列表
     * @return 是否拥有任意一个权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        
        Set<String> userPermissions = getPermissions();
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否拥有所有权限
     * 
     * @param permissions 权限列表
     * @return 是否拥有所有权限
     */
    public static boolean hasAllPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        
        Set<String> userPermissions = getPermissions();
        for (String permission : permissions) {
            if (!userPermissions.contains(permission)) {
                return false;
            }
        }
        return true;
    }

    // ==================== 客户端信息管理 ====================

    /**
     * 设置客户端IP
     * 
     * @param clientIp 客户端IP
     */
    public static void setClientIp(String clientIp) {
        getOrCreateHolder().clientIp = clientIp;
    }

    /**
     * 获取客户端IP
     * 
     * @return 客户端IP
     */
    public static String getClientIp() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.clientIp : null;
    }

    /**
     * 设置用户代理
     * 
     * @param userAgent 用户代理
     */
    public static void setUserAgent(String userAgent) {
        getOrCreateHolder().userAgent = userAgent;
    }

    /**
     * 获取用户代理
     * 
     * @return 用户代理
     */
    public static String getUserAgent() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.userAgent : null;
    }

    /**
     * 设置登录时间
     * 
     * @param loginTime 登录时间
     */
    public static void setLoginTime(long loginTime) {
        getOrCreateHolder().loginTime = loginTime;
    }

    /**
     * 获取登录时间
     * 
     * @return 登录时间
     */
    public static long getLoginTime() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.loginTime : 0L;
    }

    // ==================== 自定义属性管理 ====================

    /**
     * 设置自定义属性
     * 
     * @param key 属性键
     * @param value 属性值
     */
    public static void setAttribute(String key, Object value) {
        if (key != null) {
            getOrCreateHolder().attributes.put(key, value);
        }
    }

    /**
     * 获取自定义属性
     * 
     * @param key 属性键
     * @return 属性值
     */
    public static Object getAttribute(String key) {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.attributes.get(key) : null;
    }

    /**
     * 获取自定义属性（指定类型）
     * 
     * @param key 属性键
     * @param type 属性类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(String key, Class<T> type) {
        Object value = getAttribute(key);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 移除自定义属性
     * 
     * @param key 属性键
     * @return 被移除的属性值
     */
    public static Object removeAttribute(String key) {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? holder.attributes.remove(key) : null;
    }

    /**
     * 获取所有自定义属性
     * 
     * @return 属性映射
     */
    public static Map<String, Object> getAttributes() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        return holder != null ? Collections.unmodifiableMap(holder.attributes) : Collections.emptyMap();
    }

    // ==================== 上下文管理 ====================

    /**
     * 清空当前线程的安全上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 判断当前线程是否有安全上下文
     * 
     * @return 是否有安全上下文
     */
    public static boolean hasContext() {
        return CONTEXT_HOLDER.get() != null;
    }

    /**
     * 获取或创建上下文持有者
     * 
     * @return 上下文持有者
     */
    private static SecurityContextHolder getOrCreateHolder() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        if (holder == null) {
            holder = new SecurityContextHolder();
            CONTEXT_HOLDER.set(holder);
        }
        return holder;
    }

    // ==================== 便捷方法 ====================

    /**
     * 初始化安全上下文
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param tenantId 租户ID
     * @param roles 角色集合
     * @param permissions 权限集合
     */
    public static void initialize(String userId, String username, String tenantId, 
                                Set<String> roles, Set<String> permissions) {
        clear();
        setUserId(userId);
        setUsername(username);
        setTenantId(tenantId);
        setRoles(roles);
        setPermissions(permissions);
        setAuthenticated(true);
    }

    /**
     * 获取当前用户的简要信息
     * 
     * @return 用户信息字符串
     */
    public static String getCurrentUserInfo() {
        SecurityContextHolder holder = CONTEXT_HOLDER.get();
        if (holder == null || !holder.authenticated) {
            return "Anonymous";
        }
        
        return String.format("User{id=%s, username=%s, tenant=%s, roles=%s}", 
            holder.userId, holder.username, holder.tenantId, holder.roles);
    }

    /**
     * 判断是否为超级管理员
     * 
     * @return 是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN") || hasRole("SYSTEM_ADMIN");
    }

    /**
     * 判断是否为租户管理员
     * 
     * @return 是否为租户管理员
     */
    public static boolean isTenantAdmin() {
        return hasRole("TENANT_ADMIN");
    }

    /**
     * 判断是否为普通用户
     * 
     * @return 是否为普通用户
     */
    public static boolean isNormalUser() {
        return hasRole("USER") && !isSuperAdmin() && !isTenantAdmin();
    }
} 