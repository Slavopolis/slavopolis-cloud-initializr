package club.slavopolis.common.web.interceptor;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import club.slavopolis.common.security.context.SecurityContext;
import club.slavopolis.common.security.context.TenantContext;
import club.slavopolis.common.web.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志拦截器
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
public class LoggingInterceptor implements HandlerInterceptor {

    /**
     * 请求开始时间属性名
     */
    private static final String REQUEST_START_TIME = "REQUEST_START_TIME";

    /**
     * 请求ID属性名
     */
    private static final String REQUEST_ID = "REQUEST_ID";

    /**
     * 追踪ID属性名
     */
    private static final String TRACE_ID = "TRACE_ID";

    /**
     * 是否记录请求头
     */
    private boolean logHeaders = true;

    /**
     * 是否记录请求参数
     */
    private boolean logParameters = true;

    /**
     * 是否记录请求体
     */
    private boolean logRequestBody = false;

    /**
     * 是否记录响应体
     */
    private boolean logResponseBody = false;

    /**
     * 是否记录性能信息
     */
    private boolean logPerformance = true;

    /**
     * 慢请求阈值（毫秒）
     */
    private long slowRequestThreshold = 1000L;

    /**
     * 敏感参数名称（不记录值）
     */
    private String[] sensitiveParams = {"password", "pwd", "token", "secret", "key", "authorization"};

    /**
     * 敏感请求头名称（不记录值）
     */
    private String[] sensitiveHeaders = {"authorization", "cookie", "x-auth-token", "x-api-key"};

    /**
     * 构造函数
     * 
     * @param logHeaders 是否记录请求头
     * @param logParameters 是否记录请求参数
     */
    public LoggingInterceptor(boolean logHeaders, boolean logParameters) {
        this.logHeaders = logHeaders;
        this.logParameters = logParameters;
    }

    /**
     * 前置处理：记录请求信息
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理程序
     * @return true 继续处理，false 中断处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(REQUEST_START_TIME, startTime);

        // 生成请求ID和追踪ID
        String requestId = generateRequestId();
        String traceId = generateTraceId();
        request.setAttribute(REQUEST_ID, requestId);
        request.setAttribute(TRACE_ID, traceId);

        // 设置到安全上下文
        SecurityContext.setRequestId(requestId);
        SecurityContext.setTraceId(traceId);

        // 记录基本请求信息
        logRequestStart(request, requestId, traceId);

        // 记录详细请求信息
        if (log.isDebugEnabled()) {
            logRequestDetails(request);
        }

        return true;
    }

    /**
     * 后置处理：在视图渲染前进行一些处理
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理程序
     * @param modelAndView 模型和视图
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        // 记录响应信息
        if (log.isDebugEnabled()) {
            logResponseDetails(request, response, modelAndView);
        }
    }

    /**
     * 完成处理：记录请求完成信息
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理程序
     * @param ex 异常（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // 计算请求处理时间
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        long endTime = System.currentTimeMillis();
        long duration = startTime != null ? endTime - startTime : 0;

        // 记录请求完成信息
        logRequestCompletion(request, response, duration, ex);

        // 记录性能信息
        if (logPerformance) {
            logPerformanceInfo(request, response, duration);
        }
    }

    /**
     * 记录请求开始信息
     * 
     * @param request HTTP请求
     * @param requestId 请求ID
     * @param traceId 追踪ID
     */
    private void logRequestStart(HttpServletRequest request, String requestId, String traceId) {
        String method = RequestUtil.getMethod(request);
        String uri = RequestUtil.getRequestUri(request);
        String clientIp = RequestUtil.getClientIp(request);
        String userAgent = RequestUtil.getUserAgent(request);
        String tenantId = TenantContext.getTenantId();
        String userId = SecurityContext.getUserId();

        log.info("Request started - Method: {}, URI: {}, RequestId: {}, TraceId: {}, ClientIP: {}, TenantId: {}, UserId: {}, UserAgent: {}", 
            method, uri, requestId, traceId, clientIp, tenantId, userId, maskUserAgent(userAgent));
    }

    /**
     * 记录详细请求信息
     * 
     * @param request HTTP请求
     */
    private void logRequestDetails(HttpServletRequest request) {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        
        log.debug("Request details [{}]:", requestId);
        log.debug("  URL: {}", RequestUtil.getFullRequestUrl(request));
        log.debug("  Protocol: {}", request.getProtocol());
        log.debug("  Scheme: {}", RequestUtil.getScheme(request));
        log.debug("  Server: {}:{}", RequestUtil.getServerName(request), RequestUtil.getServerPort(request));
        log.debug("  Content-Type: {}", RequestUtil.getContentType(request));
        log.debug("  Content-Length: {}", request.getContentLength());
        log.debug("  Character-Encoding: {}", request.getCharacterEncoding());
        log.debug("  Locale: {}", request.getLocale());
        log.debug("  Is-AJAX: {}", RequestUtil.isAjaxRequest(request));
        log.debug("  Is-Mobile: {}", RequestUtil.isMobileRequest(request));
        log.debug("  Is-HTTPS: {}", RequestUtil.isHttps(request));

        // 记录请求头
        if (logHeaders) {
            logRequestHeaders(request, requestId);
        }

        // 记录请求参数
        if (logParameters) {
            logRequestParameters(request, requestId);
        }

        // 记录请求体
        if (logRequestBody && shouldLogRequestBody(request)) {
            logRequestBody(request, requestId);
        }
    }

    /**
     * 记录请求头
     * 
     * @param request HTTP请求
     * @param requestId 请求ID
     */
    private void logRequestHeaders(HttpServletRequest request, String requestId) {
        log.debug("Request headers [{}]:", requestId);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            if (isSensitiveHeader(headerName)) {
                headerValue = "***MASKED***";
            }
            
            log.debug("  {}: {}", headerName, headerValue);
        }
    }

    /**
     * 记录请求参数
     * 
     * @param request HTTP请求
     * @param requestId 请求ID
     */
    private void logRequestParameters(HttpServletRequest request, String requestId) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.isEmpty()) {
            log.debug("Request parameters [{}]:", requestId);
            parameterMap.forEach((name, values) -> {
                String valueStr;
                if (isSensitiveParameter(name)) {
                    valueStr = "***MASKED***";
                } else {
                    valueStr = values.length == 1 ? values[0] : Arrays.toString(values);
                }
                log.debug("  {}: {}", name, valueStr);
            });
        }
    }

    /**
     * 记录请求体
     * 
     * @param request HTTP请求
     * @param requestId 请求ID
     */
    private void logRequestBody(HttpServletRequest request, String requestId) {
        try {
            String body = RequestUtil.getRequestBody(request);
            if (body != null && !body.isEmpty()) {
                log.debug("Request body [{}]: {}", requestId, maskSensitiveData(body));
            }
        } catch (Exception e) {
            log.warn("Failed to read request body for request [{}]", requestId, e);
        }
    }

    /**
     * 记录响应详情
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param modelAndView 模型视图
     */
    private void logResponseDetails(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        
        log.debug("Response details [{}]:", requestId);
        log.debug("  Status: {}", response.getStatus());
        log.debug("  Content-Type: {}", response.getContentType());
        log.debug("  Character-Encoding: {}", response.getCharacterEncoding());
        log.debug("  Buffer-Size: {}", response.getBufferSize());
        log.debug("  Is-Committed: {}", response.isCommitted());

        // 记录响应头
        if (logHeaders) {
            logResponseHeaders(response, requestId);
        }

        // 记录模型视图信息
        if (modelAndView != null) {
            log.debug("ModelAndView [{}]:", requestId);
            log.debug("  View-Name: {}", modelAndView.getViewName());
            modelAndView.getModel();
            if (!modelAndView.getModel().isEmpty()) {
                log.debug("  Model: {}", modelAndView.getModel().keySet());
            }
        }
    }

    /**
     * 记录响应头
     * 
     * @param response HTTP响应
     * @param requestId 请求ID
     */
    private void logResponseHeaders(HttpServletResponse response, String requestId) {
        log.debug("Response headers [{}]:", requestId);
        response.getHeaderNames().forEach(headerName -> {
            String headerValue = response.getHeader(headerName);
            if (isSensitiveHeader(headerName)) {
                headerValue = "***MASKED***";
            }
            log.debug("  {}: {}", headerName, headerValue);
        });
    }

    /**
     * 记录请求完成信息
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param duration 处理时间
     * @param ex 异常
     */
    private void logRequestCompletion(HttpServletRequest request, HttpServletResponse response, long duration, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        String traceId = (String) request.getAttribute(TRACE_ID);
        String method = RequestUtil.getMethod(request);
        String uri = RequestUtil.getRequestUri(request);
        int status = response.getStatus();
        String tenantId = TenantContext.getTenantId();
        String userId = SecurityContext.getUserId();

        if (ex != null) {
            log.error("Request failed - Method: {}, URI: {}, RequestId: {}, TraceId: {}, Status: {}, Duration: {}ms, TenantId: {}, UserId: {}, Error: {}", 
                method, uri, requestId, traceId, status, duration, tenantId, userId, ex.getMessage(), ex);
        } else {
            log.info("Request completed - Method: {}, URI: {}, RequestId: {}, TraceId: {}, Status: {}, Duration: {}ms, TenantId: {}, UserId: {}", 
                method, uri, requestId, traceId, status, duration, tenantId, userId);
        }
    }

    /**
     * 记录性能信息
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param duration 处理时间
     */
    private void logPerformanceInfo(HttpServletRequest request, HttpServletResponse response, long duration) {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        String method = RequestUtil.getMethod(request);
        String uri = RequestUtil.getRequestUri(request);

        // 记录慢请求
        if (duration > slowRequestThreshold) {
            log.warn("Slow request detected - Method: {}, URI: {}, RequestId: {}, Duration: {}ms (threshold: {}ms)", 
                method, uri, requestId, duration, slowRequestThreshold);
        }

        // 记录性能统计
        if (log.isDebugEnabled()) {
            log.debug("Performance info [{}] - Duration: {}ms, Memory: {}MB", 
                requestId, duration, getUsedMemoryMb());
        }
    }

    /**
     * 生成请求ID
     * 
     * @return 请求ID
     */
    private String generateRequestId() {
        return RequestUtil.generateRequestId();
    }

    /**
     * 生成追踪ID
     * 
     * @return 追踪ID
     */
    private String generateTraceId() {
        return RequestUtil.generateTraceId();
    }

    /**
     * 判断是否为敏感参数
     * 
     * @param paramName 参数名
     * @return 是否为敏感参数
     */
    private boolean isSensitiveParameter(String paramName) {
        if (paramName == null) {
            return false;
        }
        
        String lowerName = paramName.toLowerCase();
        for (String sensitive : sensitiveParams) {
            if (lowerName.contains(sensitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为敏感请求头
     * 
     * @param headerName 请求头名
     * @return 是否为敏感请求头
     */
    private boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        
        String lowerName = headerName.toLowerCase();
        for (String sensitive : sensitiveHeaders) {
            if (lowerName.contains(sensitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否应该记录请求体
     * 
     * @param request HTTP请求
     * @return 是否应该记录
     */
    private boolean shouldLogRequestBody(HttpServletRequest request) {
        String contentType = RequestUtil.getContentType(request);
        if (contentType == null) {
            return false;
        }
        
        String lowerContentType = contentType.toLowerCase();
        return lowerContentType.contains("application/json") 
            || lowerContentType.contains("application/xml")
            || lowerContentType.contains("text/");
    }

    /**
     * 脱敏敏感数据
     * 
     * @param data 原始数据
     * @return 脱敏后的数据
     */
    private String maskSensitiveData(String data) {
        if (data == null) {
            return null;
        }
        
        String result = data;
        for (String sensitive : sensitiveParams) {
            // 简单的正则替换，实际项目中可能需要更复杂的处理
            result = result.replaceAll("(?i)\"" + sensitive + "\"\\s*:\\s*\"[^\"]*\"", 
                "\"" + sensitive + "\":\"***MASKED***\"");
        }
        return result;
    }

    /**
     * 脱敏用户代理信息
     * 
     * @param userAgent 用户代理
     * @return 脱敏后的用户代理
     */
    private String maskUserAgent(String userAgent) {
        if (userAgent == null || userAgent.length() <= 50) {
            return userAgent;
        }
        return userAgent.substring(0, 50) + "...";
    }

    /**
     * 获取已使用内存（MB）
     * 
     * @return 已使用内存
     */
    private long getUsedMemoryMb() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
    }
} 