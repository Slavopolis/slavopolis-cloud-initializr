package club.slavopolis.common.web.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应工具类
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
public final class ResponseUtil {

    /**
     * JSON对象映射器
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 内容类型常量
     */
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_JAVASCRIPT = "application/javascript";
    public static final String CONTENT_TYPE_CSS = "text/css";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    public static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";

    /**
     * 字符编码
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 缓存控制头
     */
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_EXPIRES = "Expires";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_PRAGMA = "Pragma";

    /**
     * 跨域相关头
     */
    public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    /**
     * 其他常用头
     */
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";
    public static final String HEADER_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    public static final String HEADER_X_XSS_PROTECTION = "X-XSS-Protection";

    // ==================== JSON响应 ====================

    /**
     * 输出JSON响应
     * 
     * @param response HTTP响应
     * @param data 数据对象
     */
    public static void writeJson(HttpServletResponse response, Object data) {
        writeJson(response, data, HttpServletResponse.SC_OK);
    }

    /**
     * 输出JSON响应（指定状态码）
     * 
     * @param response HTTP响应
     * @param data 数据对象
     * @param statusCode 状态码
     */
    public static void writeJson(HttpServletResponse response, Object data, int statusCode) {
        if (response == null) {
            return;
        }

        try {
            response.setStatus(statusCode);
            response.setContentType(CONTENT_TYPE_JSON);
            response.setCharacterEncoding(CHARSET_UTF8);

            String json = OBJECT_MAPPER.writeValueAsString(data);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write JSON response", e);
        }
    }

    /**
     * 输出成功结果
     * 
     * @param response HTTP响应
     * @param data 数据
     */
    public static void writeSuccess(HttpServletResponse response, Object data) {
        writeJson(response, Result.success(data));
    }

    /**
     * 输出成功结果（无数据）
     * 
     * @param response HTTP响应
     */
    public static void writeSuccess(HttpServletResponse response) {
        writeJson(response, Result.success());
    }

    /**
     * 输出成功结果（带消息）
     * 
     * @param response HTTP响应
     * @param message 消息
     */
    public static void writeSuccess(HttpServletResponse response, String message) {
        writeJson(response, Result.success(message));
    }

    /**
     * 输出成功结果（带数据和消息）
     * 
     * @param response HTTP响应
     * @param data 数据
     * @param message 消息
     */
    public static void writeSuccess(HttpServletResponse response, Object data, String message) {
        writeJson(response, Result.success(message, data));
    }

    /**
     * 输出失败结果
     * 
     * @param response HTTP响应
     * @param message 错误消息
     */
    public static void writeError(HttpServletResponse response, String message) {
        writeJson(response, Result.failed(message), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * 输出失败结果（指定错误码）
     * 
     * @param response HTTP响应
     * @param code 错误码
     * @param message 错误消息
     */
    public static void writeError(HttpServletResponse response, int code, String message) {
        writeJson(response, Result.failed(code, message), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * 输出失败结果（指定HTTP状态码）
     * 
     * @param response HTTP响应
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     */
    public static void writeError(HttpServletResponse response, String message, int httpStatus) {
        writeJson(response, Result.failed(message), httpStatus);
    }

    /**
     * 输出失败结果（完整参数）
     * 
     * @param response HTTP响应
     * @param code 错误码
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     */
    public static void writeError(HttpServletResponse response, int code, String message, int httpStatus) {
        writeJson(response, Result.failed(code, message), httpStatus);
    }

    // ==================== 特定错误响应 ====================

    /**
     * 输出未认证错误
     * 
     * @param response HTTP响应
     */
    public static void writeUnauthorized(HttpServletResponse response) {
        writeError(response, "未认证", HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * 输出未认证错误（带消息）
     * 
     * @param response HTTP响应
     * @param message 错误消息
     */
    public static void writeUnauthorized(HttpServletResponse response, String message) {
        writeError(response, message, HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * 输出无权限错误
     * 
     * @param response HTTP响应
     */
    public static void writeForbidden(HttpServletResponse response) {
        writeError(response, "无权限", HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * 输出无权限错误（带消息）
     * 
     * @param response HTTP响应
     * @param message 错误消息
     */
    public static void writeForbidden(HttpServletResponse response, String message) {
        writeError(response, message, HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * 输出资源未找到错误
     * 
     * @param response HTTP响应
     */
    public static void writeNotFound(HttpServletResponse response) {
        writeError(response, "资源未找到", HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * 输出资源未找到错误（带消息）
     * 
     * @param response HTTP响应
     * @param message 错误消息
     */
    public static void writeNotFound(HttpServletResponse response, String message) {
        writeError(response, message, HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * 输出请求方法不支持错误
     * 
     * @param response HTTP响应
     */
    public static void writeMethodNotAllowed(HttpServletResponse response) {
        writeError(response, "请求方法不支持", HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * 输出请求参数错误
     * 
     * @param response HTTP响应
     */
    public static void writeBadRequest(HttpServletResponse response) {
        writeError(response, "请求参数错误", HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * 输出请求参数错误（带消息）
     * 
     * @param response HTTP响应
     * @param message 错误消息
     */
    public static void writeBadRequest(HttpServletResponse response, String message) {
        writeError(response, message, HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * 输出请求超时错误
     * 
     * @param response HTTP响应
     */
    public static void writeRequestTimeout(HttpServletResponse response) {
        writeError(response, "请求超时", HttpServletResponse.SC_REQUEST_TIMEOUT);
    }

    /**
     * 输出请求频率过高错误
     * 
     * @param response HTTP响应
     */
    public static void writeTooManyRequests(HttpServletResponse response) {
        // HTTP 429 Too Many Requests
        writeError(response, "请求频率过高", 429);
    }

    /**
     * 输出服务不可用错误
     * 
     * @param response HTTP响应
     */
    public static void writeServiceUnavailable(HttpServletResponse response) {
        writeError(response, "服务不可用", HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }

    // ==================== 其他格式响应 ====================

    /**
     * 输出文本响应
     * 
     * @param response HTTP响应
     * @param text 文本内容
     */
    public static void writeText(HttpServletResponse response, String text) {
        writeText(response, text, HttpServletResponse.SC_OK);
    }

    /**
     * 输出文本响应（指定状态码）
     * 
     * @param response HTTP响应
     * @param text 文本内容
     * @param statusCode 状态码
     */
    public static void writeText(HttpServletResponse response, String text, int statusCode) {
        if (response == null) {
            return;
        }

        try {
            response.setStatus(statusCode);
            response.setContentType(CONTENT_TYPE_TEXT);
            response.setCharacterEncoding(CHARSET_UTF8);

            PrintWriter writer = response.getWriter();
            writer.write(text != null ? text : CommonConstants.EMPTY);
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write text response", e);
        }
    }

    /**
     * 输出HTML响应
     * 
     * @param response HTTP响应
     * @param html HTML内容
     */
    public static void writeHtml(HttpServletResponse response, String html) {
        writeHtml(response, html, HttpServletResponse.SC_OK);
    }

    /**
     * 输出HTML响应（指定状态码）
     * 
     * @param response HTTP响应
     * @param html HTML内容
     * @param statusCode 状态码
     */
    public static void writeHtml(HttpServletResponse response, String html, int statusCode) {
        if (response == null) {
            return;
        }

        try {
            response.setStatus(statusCode);
            response.setContentType(CONTENT_TYPE_HTML);
            response.setCharacterEncoding(CHARSET_UTF8);

            PrintWriter writer = response.getWriter();
            writer.write(html != null ? html : CommonConstants.EMPTY);
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write HTML response", e);
        }
    }

    /**
     * 输出XML响应
     * 
     * @param response HTTP响应
     * @param xml XML内容
     */
    public static void writeXml(HttpServletResponse response, String xml) {
        writeXml(response, xml, HttpServletResponse.SC_OK);
    }

    /**
     * 输出XML响应（指定状态码）
     * 
     * @param response HTTP响应
     * @param xml XML内容
     * @param statusCode 状态码
     */
    public static void writeXml(HttpServletResponse response, String xml, int statusCode) {
        if (response == null) {
            return;
        }

        try {
            response.setStatus(statusCode);
            response.setContentType(CONTENT_TYPE_XML);
            response.setCharacterEncoding(CHARSET_UTF8);

            PrintWriter writer = response.getWriter();
            writer.write(xml != null ? xml : CommonConstants.EMPTY);
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write XML response", e);
        }
    }

    // ==================== 响应头设置 ====================

    /**
     * 设置响应头
     * 
     * @param response HTTP响应
     * @param name 头名称
     * @param value 头值
     */
    public static void setHeader(HttpServletResponse response, String name, String value) {
        if (response != null && name != null) {
            response.setHeader(name, value);
        }
    }

    /**
     * 添加响应头
     * 
     * @param response HTTP响应
     * @param name 头名称
     * @param value 头值
     */
    public static void addHeader(HttpServletResponse response, String name, String value) {
        if (response != null && name != null) {
            response.addHeader(name, value);
        }
    }

    /**
     * 设置多个响应头
     * 
     * @param response HTTP响应
     * @param headers 头映射
     */
    public static void setHeaders(HttpServletResponse response, Map<String, String> headers) {
        if (response != null && headers != null) {
            headers.forEach(response::setHeader);
        }
    }

    /**
     * 设置缓存控制头
     * 
     * @param response HTTP响应
     * @param cacheControl 缓存控制值
     */
    public static void setCacheControl(HttpServletResponse response, String cacheControl) {
        setHeader(response, HEADER_CACHE_CONTROL, cacheControl);
    }

    /**
     * 设置不缓存
     * 
     * @param response HTTP响应
     */
    public static void setNoCache(HttpServletResponse response) {
        if (response != null) {
            response.setHeader(HEADER_CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            response.setHeader(HEADER_PRAGMA, "no-cache");
            response.setDateHeader(HEADER_EXPIRES, 0);
        }
    }

    /**
     * 设置缓存时间（秒）
     * 
     * @param response HTTP响应
     * @param seconds 缓存秒数
     */
    public static void setCache(HttpServletResponse response, int seconds) {
        if (response != null) {
            response.setHeader(HEADER_CACHE_CONTROL, "max-age=" + seconds);
            response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + seconds * 1000L);
        }
    }

    // ==================== 跨域设置 ====================

    /**
     * 设置跨域头
     * 
     * @param response HTTP响应
     * @param origin 允许的源
     */
    public static void setCors(HttpServletResponse response, String origin) {
        if (response != null) {
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin != null ? origin : CommonConstants.ASTERISK);
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization, X-Requested-With");
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.setHeader(HEADER_ACCESS_CONTROL_MAX_AGE, "3600");
        }
    }

    /**
     * 设置跨域头（允许所有源）
     * 
     * @param response HTTP响应
     */
    public static void setCorsAll(HttpServletResponse response) {
        setCors(response, CommonConstants.ASTERISK);
    }

    /**
     * 设置跨域头（详细配置）
     * 
     * @param response HTTP响应
     * @param origin 允许的源
     * @param methods 允许的方法
     * @param headers 允许的头
     * @param credentials 是否允许凭证
     * @param maxAge 预检请求缓存时间
     */
    public static void setCors(HttpServletResponse response, String origin, String methods, 
                              String headers, boolean credentials, int maxAge) {
        if (response != null) {
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin != null ? origin : CommonConstants.ASTERISK);
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, methods != null ? methods : "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, headers != null ? headers : "Content-Type, Authorization, X-Requested-With");
            response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(credentials));
            response.setHeader(HEADER_ACCESS_CONTROL_MAX_AGE, String.valueOf(maxAge));
        }
    }

    // ==================== 安全头设置 ====================

    /**
     * 设置安全头
     * 
     * @param response HTTP响应
     */
    public static void setSecurityHeaders(HttpServletResponse response) {
        if (response != null) {
            response.setHeader(HEADER_X_FRAME_OPTIONS, "DENY");
            response.setHeader(HEADER_X_CONTENT_TYPE_OPTIONS, "nosniff");
            response.setHeader(HEADER_X_XSS_PROTECTION, "1; mode=block");
        }
    }

    /**
     * 设置内容安全策略
     * 
     * @param response HTTP响应
     * @param policy CSP策略
     */
    public static void setContentSecurityPolicy(HttpServletResponse response, String policy) {
        setHeader(response, "Content-Security-Policy", policy);
    }

    // ==================== 文件下载 ====================

    /**
     * 设置文件下载头
     * 
     * @param response HTTP响应
     * @param filename 文件名
     */
    public static void setDownload(HttpServletResponse response, String filename) {
        if (response != null && filename != null) {
            response.setContentType(CONTENT_TYPE_OCTET_STREAM);
            response.setHeader(HEADER_CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        }
    }

    /**
     * 设置文件下载头（UTF-8编码文件名）
     * 
     * @param response HTTP响应
     * @param filename 文件名
     */
    public static void setDownloadUtf8(HttpServletResponse response, String filename) {
        if (response != null && filename != null) {
            try {
                String encodedFilename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                response.setContentType(CONTENT_TYPE_OCTET_STREAM);
                response.setHeader(HEADER_CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"");
            } catch (Exception e) {
                log.error("Failed to encode filename: {}", filename, e);
                setDownload(response, filename);
            }
        }
    }

    // ==================== 重定向 ====================

    /**
     * 重定向到指定URL
     * 
     * @param response HTTP响应
     * @param url 目标URL
     */
    public static void redirect(HttpServletResponse response, String url) {
        if (response != null && url != null) {
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                log.error("Failed to redirect to: {}", url, e);
            }
        }
    }

    /**
     * 永久重定向
     * 
     * @param response HTTP响应
     * @param url 目标URL
     */
    public static void redirectPermanent(HttpServletResponse response, String url) {
        if (response != null && url != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader(HEADER_LOCATION, url);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 判断响应是否已提交
     * 
     * @param response HTTP响应
     * @return 是否已提交
     */
    public static boolean isCommitted(HttpServletResponse response) {
        return response != null && response.isCommitted();
    }

    /**
     * 重置响应
     * 
     * @param response HTTP响应
     */
    public static void reset(HttpServletResponse response) {
        if (response != null && !response.isCommitted()) {
            response.reset();
        }
    }

    /**
     * 刷新响应缓冲区
     * 
     * @param response HTTP响应
     */
    public static void flush(HttpServletResponse response) {
        if (response != null) {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                log.error("Failed to flush response buffer", e);
            }
        }
    }

    /**
     * 创建简单的错误响应映射
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return 错误响应映射
     */
    public static Map<String, Object> createErrorResponse(int code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }

    /**
     * 创建简单的成功响应映射
     * 
     * @param data 数据
     * @return 成功响应映射
     */
    public static Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> success = new HashMap<>();
        success.put("code", 200);
        success.put("message", "success");
        success.put("data", data);
        success.put("timestamp", System.currentTimeMillis());
        return success;
    }
} 