package club.slavopolis.common.log.filter;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.core.constants.HttpConstants;
import club.slavopolis.common.core.result.ResultCode;
import club.slavopolis.common.log.config.properties.LogProperties;
import club.slavopolis.common.log.model.AccessLog;
import club.slavopolis.common.security.util.SecurityUtil;
import club.slavopolis.common.util.JsonUtils;
import club.slavopolis.common.web.util.RequestUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 访问日志过滤器
 *
 * <p>
 * 该过滤器记录所有 HTTP 请求的详细信息，包括请求参数、请求体、响应状态和响应时间。
 * 日志以结构化的格式输出，便于后续的日志分析和审计。支持敏感信息脱敏和大数据截断。
 * </p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class AccessLogFilter extends OncePerRequestFilter {

    private final LogProperties logProperties;

    private static final Logger accessLogger = LoggerFactory.getLogger("club.slavopolis.common.log.access");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 检查是否需要忽略
        if (SecurityUtil.shouldIgnore(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 包装请求和响应，以便读取内容
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // 构建访问日志对象
        AccessLog accessLog = new AccessLog();
        accessLog.setTraceId(MDC.get(CommonConstants.TRACE_ID));
        accessLog.setRequestTime(LocalDateTime.now());
        accessLog.setMethod(request.getMethod());
        accessLog.setUri(request.getRequestURI());
        accessLog.setQueryString(request.getQueryString());
        accessLog.setClientIp(RequestUtil.getClientIp(request));
        accessLog.setUserAgent(request.getHeader(HttpConstants.HEADER_USER_AGENT));
        accessLog.setHeaders(RequestUtil.getAllHeaders(request));

        try {
            // 执行请求
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            // 记录请求体
            String requestBody = RequestUtil.getRequestBody(wrappedRequest);
            if (requestBody != null && !requestBody.isEmpty()) {
                accessLog.setRequestBody(SecurityUtil.maskSensitiveData(requestBody));
            }

            // 记录响应信息
            accessLog.setStatus(wrappedResponse.getStatus());

            // 复制响应内容到原始响应
            wrappedResponse.copyBodyToResponse();
        } catch (Exception e) {
            accessLog.setStatus(ResultCode.INTERNAL_SERVER_ERROR.getCode());
            accessLog.setError(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long duration = System.currentTimeMillis() - startTime;
            accessLog.setDuration(duration);
            accessLog.setResponseTime(LocalDateTime.now());

            // 记录访问日志
            logAccess(accessLog);
        }
    }

    /**
     * 记录访问日志
     */
    private void logAccess(AccessLog accessLog) {
        // 根据响应状态判断日志级别
        if (accessLog.getStatus() >= ResultCode.INTERNAL_SERVER_ERROR.getCode()) {
            accessLogger.error("服务端错误: {}", JsonUtils.toJson(accessLog));
        } else if (accessLog.getStatus() >= ResultCode.BAD_REQUEST.getCode()) {
            accessLogger.warn("客户端错误: {}", JsonUtils.toJson(accessLog));
        } else if (accessLog.getDuration() > logProperties.getAccess().getSlowRequestThreshold()) {
            accessLogger.warn("慢速请求: {}", JsonUtils.toJson(accessLog));
        } else {
            accessLogger.info("访问日志: {}", JsonUtils.toJson(accessLog));
        }
    }
}
