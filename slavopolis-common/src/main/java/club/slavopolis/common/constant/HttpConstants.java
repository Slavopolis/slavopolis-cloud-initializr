package club.slavopolis.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: HTTP相关常量定义
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpConstants {

    // ==================== Header常量 ====================

    /**
     * 认证请求头
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Bearer Token前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Cookie 前缀
     */
    public static final String COOKIE_PREFIX = "Cookie";

    /**
     * 内容类型
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * 用户代理
     */
    public static final String HEADER_USER_AGENT = "User-Agent";

    /**
     * 请求追踪ID
     */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";

    /**
     * 请求ID
     */
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    /**
     * 客户端IP
     */
    public static final String HEADER_CLIENT_IP = "X-Real-IP";

    /**
     * 转发IP
     */
    public static final String HEADER_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * 代理客户端IP
     */
    public static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";

    /**
     * WebLogic代理客户端IP
     */
    public static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    /**
     * HTTP客户端IP
     */
    public static final String HEADER_HTTP_CLIENT_IP = "HTTP_CLIENT_IP";

    /**
     * HTTP转发IP
     */
    public static final String HEADER_HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

    /**
     * 语言
     */
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    // ==================== Content-Type常量 ====================

    /**
     * JSON类型
     */
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    /**
     * 表单类型
     */
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    /**
     * 文件上传类型
     */
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";

    /**
     * 文本类型
     */
    public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";

    /**
     * XML类型
     */
    public static final String CONTENT_TYPE_XML = "application/xml;charset=UTF-8";

    // ==================== 请求方法常量 ====================

    /**
     * GET请求
     */
    public static final String METHOD_GET = "GET";

    /**
     * POST请求
     */
    public static final String METHOD_POST = "POST";

    /**
     * PUT请求
     */
    public static final String METHOD_PUT = "PUT";

    /**
     * DELETE请求
     */
    public static final String METHOD_DELETE = "DELETE";

    /**
     * PATCH请求
     */
    public static final String METHOD_PATCH = "PATCH";
}
