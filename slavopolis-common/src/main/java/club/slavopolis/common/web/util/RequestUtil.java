package club.slavopolis.common.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import club.slavopolis.common.core.constants.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestUtil {

    /**
     * 未知IP标识
     */
    private static final String UNKNOWN = "unknown";

    /**
     * 本地IP地址
     */
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * IP地址分隔符
     */
    private static final String IP_SEPARATOR = ",";

    /**
     * 请求头名称
     */
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final String HEADER_HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String HEADER_HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_REFERER = "Referer";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    // ==================== IP地址获取 ====================

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = getIpFromHeader(request, HEADER_X_FORWARDED_FOR);
        if (isValidIp(ip)) {
            // 多级反向代理时，第一个IP为客户端真实IP
            return ip.split(IP_SEPARATOR)[0].trim();
        }

        ip = getIpFromHeader(request, HEADER_X_REAL_IP);
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, HEADER_PROXY_CLIENT_IP);
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, HEADER_WL_PROXY_CLIENT_IP);
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, HEADER_HTTP_CLIENT_IP);
        if (isValidIp(ip)) {
            return ip;
        }

        ip = getIpFromHeader(request, HEADER_HTTP_X_FORWARDED_FOR);
        if (isValidIp(ip)) {
            return ip;
        }

        // 最后尝试从RemoteAddr获取
        ip = request.getRemoteAddr();
        if (isLocalhost(ip)) {
            // 如果是本地地址，尝试获取本机真实IP
            try {
                InetAddress inet = InetAddress.getLocalHost();
                ip = inet.getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("Failed to get local host address", e);
            }
        }

        return ip;
    }

    /**
     * 从请求头获取IP地址
     * 
     * @param request HTTP请求
     * @param headerName 请求头名称
     * @return IP地址
     */
    private static String getIpFromHeader(HttpServletRequest request, String headerName) {
        String ip = request.getHeader(headerName);
        return isValidIp(ip) ? ip : null;
    }

    /**
     * 判断IP地址是否有效
     * 
     * @param ip IP地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 判断是否为本地地址
     * 
     * @param ip IP地址
     * @return 是否为本地地址
     */
    private static boolean isLocalhost(String ip) {
        return LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip);
    }

    // ==================== 请求头处理 ====================

    /**
     * 获取用户代理
     * 
     * @param request HTTP请求
     * @return 用户代理
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request != null ? request.getHeader(HEADER_USER_AGENT) : null;
    }

    /**
     * 获取引用页面
     * 
     * @param request HTTP请求
     * @return 引用页面
     */
    public static String getReferer(HttpServletRequest request) {
        return request != null ? request.getHeader(HEADER_REFERER) : null;
    }

    /**
     * 获取授权头
     * 
     * @param request HTTP请求
     * @return 授权头
     */
    public static String getAuthorization(HttpServletRequest request) {
        return request != null ? request.getHeader(HEADER_AUTHORIZATION) : null;
    }

    /**
     * 获取内容类型
     * 
     * @param request HTTP请求
     * @return 内容类型
     */
    public static String getContentType(HttpServletRequest request) {
        return request != null ? request.getContentType() : null;
    }

    /**
     * 获取接受类型
     * 
     * @param request HTTP请求
     * @return 接受类型
     */
    public static String getAccept(HttpServletRequest request) {
        return request != null ? request.getHeader(HEADER_ACCEPT) : null;
    }

    /**
     * 获取接受语言
     * 
     * @param request HTTP请求
     * @return 接受语言
     */
    public static String getAcceptLanguage(HttpServletRequest request) {
        return request != null ? request.getHeader(HEADER_ACCEPT_LANGUAGE) : null;
    }

    /**
     * 获取接受编码
     * 
     * @param request HTTP请求
     * @return 接受编码
     */
    public static String getAcceptEncoding(HttpServletRequest request) {
        return request != null ? request.getHeader(HEADER_ACCEPT_ENCODING) : null;
    }

    /**
     * 获取所有请求头
     * 
     * @param request HTTP请求
     * @return 请求头映射
     */
    public static Map<String, String> getAllHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        if (request != null) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
        }
        return headers;
    }

    /**
     * 获取指定请求头的值
     * 
     * @param request HTTP请求
     * @param headerName 请求头名称
     * @return 请求头值
     */
    public static String getHeader(HttpServletRequest request, String headerName) {
        return request != null ? request.getHeader(headerName) : null;
    }

    /**
     * 获取指定请求头的值（忽略大小写）
     * 
     * @param request HTTP请求
     * @param headerName 请求头名称
     * @return 请求头值
     */
    public static String getHeaderIgnoreCase(HttpServletRequest request, String headerName) {
        if (request == null || headerName == null) {
            return null;
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (headerName.equalsIgnoreCase(name)) {
                return request.getHeader(name);
            }
        }
        return null;
    }

    // ==================== 请求参数处理 ====================

    /**
     * 获取所有请求参数
     * 
     * @param request HTTP请求
     * @return 参数映射
     */
    public static Map<String, String[]> getAllParameters(HttpServletRequest request) {
        return request != null ? request.getParameterMap() : new HashMap<>();
    }

    /**
     * 获取单个参数值
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @return 参数值
     */
    public static String getParameter(HttpServletRequest request, String paramName) {
        return request != null ? request.getParameter(paramName) : null;
    }

    /**
     * 获取单个参数值（带默认值）
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static String getParameter(HttpServletRequest request, String paramName, String defaultValue) {
        String value = getParameter(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取多个参数值
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @return 参数值数组
     */
    public static String[] getParameterValues(HttpServletRequest request, String paramName) {
        return request != null ? request.getParameterValues(paramName) : null;
    }

    /**
     * 获取整数参数
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @return 整数值
     */
    public static Integer getIntParameter(HttpServletRequest request, String paramName) {
        String value = getParameter(request, paramName);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse int parameter: {} = {}", paramName, value);
            }
        }
        return null;
    }

    /**
     * 获取整数参数（带默认值）
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @param defaultValue 默认值
     * @return 整数值
     */
    public static int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        Integer value = getIntParameter(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取长整数参数
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @return 长整数值
     */
    public static Long getLongParameter(HttpServletRequest request, String paramName) {
        String value = getParameter(request, paramName);
        if (value != null && !value.isEmpty()) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse long parameter: {} = {}", paramName, value);
            }
        }
        return null;
    }

    /**
     * 获取长整数参数（带默认值）
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @param defaultValue 默认值
     * @return 长整数值
     */
    public static long getLongParameter(HttpServletRequest request, String paramName, long defaultValue) {
        Long value = getLongParameter(request, paramName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取布尔参数
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @return 布尔值
     */
    public static Boolean getBooleanParameter(HttpServletRequest request, String paramName) {
        String value = getParameter(request, paramName);
        if (value != null && !value.isEmpty()) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    /**
     * 获取布尔参数（带默认值）
     * 
     * @param request HTTP请求
     * @param paramName 参数名称
     * @param defaultValue 默认值
     * @return 布尔值
     */
    public static boolean getBooleanParameter(HttpServletRequest request, String paramName, boolean defaultValue) {
        Boolean value = getBooleanParameter(request, paramName);
        return value != null ? value : defaultValue;
    }

    // ==================== 请求体处理 ====================

    /**
     * 获取请求体内容
     * 
     * @param request HTTP请求
     * @return 请求体内容
     */
    public static String getRequestBody(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            log.error("Failed to read request body", e);
            return null;
        }

        return body.toString();
    }

    /**
     * 判断是否为JSON请求
     * 
     * @param request HTTP请求
     * @return 是否为JSON请求
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        String contentType = getContentType(request);
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    /**
     * 判断是否为XML请求
     * 
     * @param request HTTP请求
     * @return 是否为XML请求
     */
    public static boolean isXmlRequest(HttpServletRequest request) {
        String contentType = getContentType(request);
        return contentType != null && (contentType.toLowerCase().contains("application/xml") 
            || contentType.toLowerCase().contains("text/xml"));
    }

    /**
     * 判断是否为表单请求
     * 
     * @param request HTTP请求
     * @return 是否为表单请求
     */
    public static boolean isFormRequest(HttpServletRequest request) {
        String contentType = getContentType(request);
        return contentType != null && contentType.toLowerCase().contains("application/x-www-form-urlencoded");
    }

    /**
     * 判断是否为文件上传请求
     * 
     * @param request HTTP请求
     * @return 是否为文件上传请求
     */
    public static boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = getContentType(request);
        return contentType != null && contentType.toLowerCase().contains("multipart/form-data");
    }

    // ==================== AJAX请求判断 ====================

    /**
     * 判断是否为AJAX请求
     * 
     * @param request HTTP请求
     * @return 是否为AJAX请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }

    /**
     * 判断是否为移动端请求
     * 
     * @param request HTTP请求
     * @return 是否为移动端请求
     */
    public static boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        if (userAgent == null) {
            return false;
        }

        String lowerUserAgent = userAgent.toLowerCase();
        return lowerUserAgent.contains("mobile") 
            || lowerUserAgent.contains("android")
            || lowerUserAgent.contains("iphone")
            || lowerUserAgent.contains("ipad")
            || lowerUserAgent.contains("windows phone");
    }

    // ==================== 请求信息获取 ====================

    /**
     * 获取请求方法
     * 
     * @param request HTTP请求
     * @return 请求方法
     */
    public static String getMethod(HttpServletRequest request) {
        return request != null ? request.getMethod() : null;
    }

    /**
     * 获取请求URI
     * 
     * @param request HTTP请求
     * @return 请求URI
     */
    public static String getRequestUri(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 获取请求URL
     * 
     * @param request HTTP请求
     * @return 请求URL
     */
    public static String getRequestUrl(HttpServletRequest request) {
        return request != null ? request.getRequestURL().toString() : null;
    }

    /**
     * 获取查询字符串
     * 
     * @param request HTTP请求
     * @return 查询字符串
     */
    public static String getQueryString(HttpServletRequest request) {
        return request != null ? request.getQueryString() : null;
    }

    /**
     * 获取完整的请求URL（包含查询参数）
     * 
     * @param request HTTP请求
     * @return 完整的请求URL
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String url = getRequestUrl(request);
        String queryString = getQueryString(request);
        
        if (queryString != null && !queryString.isEmpty()) {
            url += CommonConstants.QUESTION_MARK + queryString;
        }
        
        return url;
    }

    /**
     * 获取服务器名称
     * 
     * @param request HTTP请求
     * @return 服务器名称
     */
    public static String getServerName(HttpServletRequest request) {
        return request != null ? request.getServerName() : null;
    }

    /**
     * 获取服务器端口
     * 
     * @param request HTTP请求
     * @return 服务器端口
     */
    public static int getServerPort(HttpServletRequest request) {
        return request != null ? request.getServerPort() : -1;
    }

    /**
     * 获取协议
     * 
     * @param request HTTP请求
     * @return 协议
     */
    public static String getScheme(HttpServletRequest request) {
        return request != null ? request.getScheme() : null;
    }

    /**
     * 判断是否为HTTPS请求
     * 
     * @param request HTTP请求
     * @return 是否为HTTPS请求
     */
    public static boolean isHttps(HttpServletRequest request) {
        return "https".equalsIgnoreCase(getScheme(request));
    }

    // ==================== 工具方法 ====================

    /**
     * 生成请求ID
     * 
     * @return 请求ID
     */
    public static String generateRequestId() {
        return UUID.randomUUID().toString().replace(CommonConstants.HYPHEN, CommonConstants.EMPTY);
    }

    /**
     * 生成追踪ID
     * 
     * @return 追踪ID
     */
    public static String generateTraceId() {
        return System.currentTimeMillis() + CommonConstants.HYPHEN + generateRequestId().substring(0, 8);
    }

    /**
     * 获取请求的基本信息
     * 
     * @param request HTTP请求
     * @return 请求信息映射
     */
    public static Map<String, Object> getRequestInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        if (request != null) {
            info.put("method", getMethod(request));
            info.put("uri", getRequestUri(request));
            info.put("url", getRequestUrl(request));
            info.put("queryString", getQueryString(request));
            info.put("clientIp", getClientIp(request));
            info.put("userAgent", getUserAgent(request));
            info.put("referer", getReferer(request));
            info.put("contentType", getContentType(request));
            info.put("isAjax", isAjaxRequest(request));
            info.put("isMobile", isMobileRequest(request));
            info.put("isHttps", isHttps(request));
        }
        return info;
    }

    /**
     * 打印请求信息（用于调试）
     * 
     * @param request HTTP请求
     */
    public static void printRequestInfo(HttpServletRequest request) {
        if (request == null) {
            log.info("Request is null");
            return;
        }

        log.info("=== Request Information ===");
        log.info("Method: {}", getMethod(request));
        log.info("URI: {}", getRequestUri(request));
        log.info("URL: {}", getRequestUrl(request));
        log.info("Query String: {}", getQueryString(request));
        log.info("Client IP: {}", getClientIp(request));
        log.info("User Agent: {}", getUserAgent(request));
        log.info("Referer: {}", getReferer(request));
        log.info("Content Type: {}", getContentType(request));
        log.info("Is AJAX: {}", isAjaxRequest(request));
        log.info("Is Mobile: {}", isMobileRequest(request));
        log.info("Is HTTPS: {}", isHttps(request));
        log.info("=== End Request Information ===");
    }
} 