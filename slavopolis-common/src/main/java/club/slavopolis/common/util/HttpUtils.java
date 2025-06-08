package club.slavopolis.common.util;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.constant.HttpConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: HTTP 请求工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

    /**
     * 需要忽略的 URL 模式
     */
    public static final Set<String> IGNORE_PATTERNS = Set.of(
            "/actuator/",
            "/swagger",
            "/api-docs",
            "/webjars/",
            "/favicon.ico",
            "/static/",
            "/public/"
    );

    /**
     * 敏感字段列表
     */
    public static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "pwd", "secret", "token", "key", "credential",
            "cardNumber", "cvv", "pin", "ssn", "idCard"
    );

    /**
     * 最大请求体记录大小（2KB）
     */
    public static final int MAX_PAYLOAD_LENGTH = 2048;

    /**
     * 获取请求头信息
     *
     * @param request HttpServletRequest 对象
     * @return 请求头信息的 Map
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        // 获取请求头信息
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        // 遍历请求头，排除敏感信息
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // 忽略敏感请求头，如 Authorization、Cookie
            if (!name.equalsIgnoreCase(HttpConstants.HEADER_AUTHORIZATION)
                    && !name.equalsIgnoreCase(HttpConstants.COOKIE_PREFIX)) {
                headers.put(name, request.getHeader(name));
            }
        }

        return headers;
    }

    /**
     * 获取请求体内容
     *
     * @param request HttpServletRequest 对象
     * @return 请求体内容
     */
    public static String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            // 获取请求体内容字节数组
            byte[] content = request.getContentAsByteArray();
            // 如果请求体不为空，则转换为字符串
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                // 限制记录大小
                if (body.length() > MAX_PAYLOAD_LENGTH) {
                    return body.substring(0, MAX_PAYLOAD_LENGTH) + "... [truncated]";
                }
                return body;
            }
        } catch (Exception e) {
            log.error("读取请求体时出错", e);
        }
        return null;
    }

    /**
     * 脱敏处理
     *
     * @param data 原始数据
     * @return 脱敏后的数据
     */
    public static String maskSensitiveData(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }

        try {
            // 尝试解析为 JSON 进行脱敏
            if (data.trim().startsWith(CommonConstants.LEFT_BRACE) || data.trim().startsWith(CommonConstants.LEFT_BRACKET)) {
                Map<String, Object> jsonData = JsonUtils.toMap(data);
                maskSensitiveFields(jsonData);
                return JsonUtils.toJson(jsonData);
            }
        } catch (Exception e) {
            // 如果不是 JSON，返回原始数据
        }

        return data;
    }

    /**
     * 递归脱敏 JSON 中的敏感字段
     */
    @SuppressWarnings("unchecked")
    public static void maskSensitiveFields(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 检查是否为敏感字段
            if (isSensitiveField(key)) {
                data.put(key, "***MASKED***");
            } else if (value instanceof Map) {
                maskSensitiveFields((Map<String, Object>) value);
            } else if (value instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map) {
                        maskSensitiveFields((Map<String, Object>) item);
                    }
                }
            }
        }
    }

    /**
     * 判断是否为敏感字段
     *
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    public static boolean isSensitiveField(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            return false;
        }

        String lowerFieldName = fieldName.toLowerCase();
        return SENSITIVE_FIELDS.stream().anyMatch(lowerFieldName::contains);
    }

    /**
     * 判断是否应该忽略该请求
     *
     * @param request HttpServletRequest 对象
     * @return 是否忽略该请求
     */
    public static boolean shouldIgnore(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return IGNORE_PATTERNS.stream().anyMatch(uri::contains);
    }

    /**
     * 获取客户端 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String[] headers = {
                HttpConstants.HEADER_CLIENT_IP,
                HttpConstants.HEADER_FORWARDED_FOR,
                HttpConstants.HEADER_PROXY_CLIENT_IP,
                HttpConstants.HEADER_WL_PROXY_CLIENT_IP,
                HttpConstants.HEADER_HTTP_CLIENT_IP,
                HttpConstants.HEADER_HTTP_X_FORWARDED_FOR
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
                // 多级代理的情况下，取第一个 IP
                if (ip.contains(CommonConstants.COMMA)) {
                    return ip.split(CommonConstants.COMMA)[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 获取异常堆栈信息
     */
    public static String getStackTrace(Exception e) {
        if (Objects.isNull(e)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getName())
                .append(CommonConstants.CACHE_KEY_SEPARATOR)
                .append(e.getMessage())
                .append(CommonConstants.NEW_LINE);

        StackTraceElement[] stackTrace = e.getStackTrace();
        // 只记录前10行堆栈信息
        int limit = Math.min(stackTrace.length, 10);
        for (int i = 0; i < limit; i++) {
            sb.append(CommonConstants.TAB)
                    .append("at ").append(stackTrace[i])
                    .append(CommonConstants.NEW_LINE);
        }

        if (stackTrace.length > 10) {
            sb.append(CommonConstants.TAB)
                    .append("... ")
                    .append(stackTrace.length - 10)
                    .append(" more")
                    .append(CommonConstants.NEW_LINE);
        }

        return sb.toString();
    }

    /**
     * 生成 traceId
     * <p>
     * 生成一个唯一的追踪 ID，用于标识整个请求链路。
     * 格式：32位的 UUID（去除横线）
     * </p>
     *
     * @return traceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace(CommonConstants.HYPHEN, CommonConstants.EMPTY);
    }

    /**
     * 生成 requestId
     * <p>
     * 生成一个唯一的请求 ID，用于标识单次请求。
     * 格式：时间戳 + 4位随机数
     * </p>
     *
     * @return requestId
     */
    public static String generateRequestId() {
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return System.currentTimeMillis() + String.format("%04d", randomNum);
    }
}
