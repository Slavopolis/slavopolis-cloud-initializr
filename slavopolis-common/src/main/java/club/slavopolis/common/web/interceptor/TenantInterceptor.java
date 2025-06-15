package club.slavopolis.common.web.interceptor;

import club.slavopolis.common.core.constants.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import club.slavopolis.common.security.context.TenantContext;
import club.slavopolis.common.web.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 租户拦截器
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@Getter
@Setter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 租户ID请求头名称
     */
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /**
     * 租户ID参数名称
     */
    private static final String PARAM_TENANT_ID = "tenantId";

    /**
     * 租户编码请求头名称
     */
    private static final String HEADER_TENANT_CODE = "X-Tenant-Code";

    /**
     * 租户编码参数名称
     */
    private static final String PARAM_TENANT_CODE = "tenantCode";

    /**
     * 是否启用严格模式（必须提供租户信息）
     */
    private boolean strictMode = false;

    /**
     * 默认租户ID
     */
    private String defaultTenantId = TenantContext.DEFAULT_TENANT_ID;

    /**
     * 构造函数
     * 
     * @param strictMode 是否启用严格模式
     */
    public TenantInterceptor(boolean strictMode) {
        this.strictMode = strictMode;
    }

    /**
     * 构造函数
     * 
     * @param strictMode 是否启用严格模式
     * @param defaultTenantId 默认租户ID
     */
    public TenantInterceptor(boolean strictMode, String defaultTenantId) {
        this.strictMode = strictMode;
        this.defaultTenantId = defaultTenantId;
    }

    /**
     * 前置处理：初始化租户上下文
     *
     * @param request HttpServletRequest 请求
     * @param response HttpServletResponse 响应
     * @param handler Object 处理程序
     * @return boolean 是否继续处理
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        try {
            // 清空当前租户上下文
            TenantContext.clear();

            // 从请求中提取租户信息
            String tenantId = extractTenantId(request);
            String tenantCode = extractTenantCode(request);

            // 验证租户信息（严格模式下必须提供租户ID信息，非严格模式下如果没有提供则使用默认租户ID)
            if (strictMode && (tenantId == null || tenantId.isEmpty())) {
                log.warn("已启用严格模式但未提供请求的租户 ID: {}", RequestUtil.getRequestUri(request));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"code\":400,\"message\":\"Tenant ID is required\"}");
                return false;
            }

            // 设置租户ID为默认租户ID（如果没有提供）, 并设置到线程局部变量中
            if (tenantId == null || tenantId.isEmpty()) {
                tenantId = defaultTenantId;
            }
            TenantContext.setTenantId(tenantId);

            // 设置租户编码, 如果提供的话
            if (tenantCode != null && !tenantCode.isEmpty()) {
                TenantContext.setTenantCode(tenantCode);
            }

            // 记录租户信息
            if (log.isDebugEnabled()) {
                log.debug("租户上下文已初始化: tenantId={}, tenantCode={}, uri={}",
                    tenantId, tenantCode, RequestUtil.getRequestUri(request));
            }

            return true;
        } catch (Exception e) {
            log.error("无法初始化租户上下文", e);
            // 清除线程局部变量
            TenantContext.clear();

            // 如果启用严格模式，则返回错误响应
            if (strictMode) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"code\":500,\"message\":\"Failed to initialize tenant context\"}");
                return false;
            }
            
            // 非严格模式下，设置默认租户并继续处理
            TenantContext.setTenantId(defaultTenantId);
            return true;
        }
    }

    /**
     * 后置处理：在视图渲染前进行一些处理
     *
     * @param request HttpServletRequest 请求
     * @param response HttpServletResponse 响应
     * @param handler Object 处理程序
     * @param modelAndView ModelAndView 模型和视图
     * @throws Exception 异常
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {
        // 在视图渲染前可以进行一些处理
        if (log.isDebugEnabled()) {
            log.debug("Post handle for tenant: {}, uri: {}", 
                TenantContext.getTenantId(), RequestUtil.getRequestUri(request));
        }
    }

    /**
     * 完成处理：在请求完成后进行一些清理工作
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理程序
     * @param ex 异常
     * @throws Exception 异常
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        try {
            // 记录请求完成信息
            if (log.isDebugEnabled()) {
                log.debug("Request completed for tenant: {}, uri: {}, status: {}", 
                    TenantContext.getTenantId(), RequestUtil.getRequestUri(request), response.getStatus());
            }

            // 如果有异常，记录错误日志
            if (ex != null) {
                log.error("Request failed for tenant: {}, uri: {}", 
                    TenantContext.getTenantId(), RequestUtil.getRequestUri(request), ex);
            }
        } finally {
            // 清空租户上下文
            TenantContext.clear();
        }
    }

    /**
     * 从请求中提取租户ID
     * 
     * @param request HTTP请求
     * @return 租户ID
     */
    private String extractTenantId(HttpServletRequest request) {
        // 1. 优先从请求头获取
        String tenantId = RequestUtil.getHeader(request, HEADER_TENANT_ID);
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId.trim();
        }

        // 2. 从请求参数获取
        tenantId = RequestUtil.getParameter(request, PARAM_TENANT_ID);
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId.trim();
        }

        // 3. 从路径变量中提取（如果URL包含租户信息）
        tenantId = extractTenantFromPath(request);
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId.trim();
        }

        // 4. 从子域名中提取（如果使用子域名区分租户）
        tenantId = extractTenantFromSubdomain(request);
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId.trim();
        }

        return null;
    }

    /**
     * 从请求中提取租户编码
     * 
     * @param request HTTP请求
     * @return 租户编码
     */
    private String extractTenantCode(HttpServletRequest request) {
        // 1. 优先从请求头获取
        String tenantCode = RequestUtil.getHeader(request, HEADER_TENANT_CODE);
        if (tenantCode != null && !tenantCode.isEmpty()) {
            return tenantCode.trim();
        }

        // 2. 从请求参数获取
        tenantCode = RequestUtil.getParameter(request, PARAM_TENANT_CODE);
        if (tenantCode != null && !tenantCode.isEmpty()) {
            return tenantCode.trim();
        }

        return null;
    }

    /**
     * 从请求路径中提取租户ID
     * 
     * @param request HTTP请求
     * @return 租户ID
     */
    private String extractTenantFromPath(HttpServletRequest request) {
        String uri = RequestUtil.getRequestUri(request);
        if (uri == null) {
            return null;
        }

        // 匹配路径模式：/tenant/{tenantId}/...
        if (uri.startsWith("/tenant/")) {
            String[] parts = uri.split(CommonConstants.SLASH);
            if (parts.length >= 3) {
                return parts[2];
            }
        }

        // 匹配路径模式：/api/{tenantId}/...
        if (uri.startsWith("/api/")) {
            String[] parts = uri.split(CommonConstants.SLASH);
            if (parts.length >= 3 && !"v1".equals(parts[2]) && !"v2".equals(parts[2])) {
                // 如果第三部分不是版本号，则可能是租户ID
                return parts[2];
            }
        }

        return null;
    }

    /**
     * 从子域名中提取租户ID
     * 
     * @param request HTTP请求
     * @return 租户ID
     */
    private String extractTenantFromSubdomain(HttpServletRequest request) {
        String serverName = RequestUtil.getServerName(request);
        if (serverName == null) {
            return null;
        }

        // 匹配子域名模式：{tenantId}.domain.com
        String[] parts = serverName.split("\\.");
        if (parts.length >= 3) {
            String subdomain = parts[0];
            // 排除常见的非租户子域名
            if (!isCommonSubdomain(subdomain)) {
                return subdomain;
            }
        }

        return null;
    }

    /**
     * 判断是否为常见的非租户子域名
     * 
     * @param subdomain 子域名
     * @return 是否为常见子域名
     */
    private boolean isCommonSubdomain(String subdomain) {
        if (subdomain == null) {
            return true;
        }

        String lower = subdomain.toLowerCase();
        return "www".equals(lower) || "api".equals(lower) || "admin".equals(lower) 
            || "app".equals(lower) || "web".equals(lower) || "mobile".equals(lower)
            || "static".equals(lower) || "cdn".equals(lower) || "img".equals(lower);
    }
} 