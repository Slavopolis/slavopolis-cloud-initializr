package club.slavopolis.common.log.interceptor;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.constant.HttpConstants;
import club.slavopolis.common.util.HttpUtils;
import club.slavopolis.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/8
 * @description: 链路追踪拦截器
 *
 * <p>
 * 该拦截器负责在请求处理链中注入链路追踪信息，包括 traceId、requestId 等。
 * 这些信息会被添加到 MDC（Mapped Diagnostic Context）中，自动出现在日志中，
 * 便于跨服务、跨系统的日志追踪和问题定位。
 * </p>
 */
@Slf4j
@Component
public class TraceInterceptor implements HandlerInterceptor {

    /**
     * 请求处理前执行
     * <p>
     * 在请求处理之前生成或提取链路追踪信息，并设置到 MDC 中。
     * 如果请求头中已经包含 traceId，则使用该值；否则生成新的 traceId。
     * </p>
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler 处理器对象
     * @return true 继续执行后续流程
     */
    @Override
    public boolean preHandle(HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) {
        // 获取或生成 traceId
        String traceId = request.getHeader(HttpConstants.HEADER_TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = HttpUtils.generateTraceId();
        }

        // 获取或生成 requestId
        String requestId = request.getHeader(HttpConstants.HEADER_REQUEST_ID);
        if (StringUtils.isBlank(requestId)) {
            requestId = HttpUtils.generateRequestId();
        }

        // 获取用户信息（如果有）
        String userId = request.getHeader(CommonConstants.USER_ID);
        String tenantId = request.getHeader(CommonConstants.TENANT_ID);

        // 设置到 MDC
        MDC.put(CommonConstants.TRACE_ID, traceId);
        MDC.put(CommonConstants.REQUEST_ID, requestId);

        if (StringUtils.isNotBlank(userId)) {
            MDC.put(CommonConstants.USER_ID, userId);
        }

        if (StringUtils.isNotBlank(tenantId)) {
            MDC.put(CommonConstants.TENANT_ID, tenantId);
        }

        // 记录请求信息
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        MDC.put("clientIp", HttpUtils.getClientIp(request));

        // 将 traceId 设置到响应头，便于客户端追踪
        response.setHeader(HttpConstants.HEADER_TRACE_ID, traceId);
        response.setHeader(HttpConstants.HEADER_REQUEST_ID, requestId);

        // 记录请求开始时间
        request.setAttribute("startTime", System.currentTimeMillis());

        log.info("Request started - method: {}, uri: {}, clientIp: {}",
                request.getMethod(), request.getRequestURI(), HttpUtils.getClientIp(request));

        return true;
    }

    /**
     * 请求处理后执行
     * <p>
     * 在请求处理完成后，记录请求的处理结果。
     * </p>
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler 处理器对象
     * @param modelAndView 模型和视图对象
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, HttpServletResponse response,@NonNull Object handler, ModelAndView modelAndView) {
        // 记录响应状态
        MDC.put("status", String.valueOf(response.getStatus()));
    }

    /**
     * 请求完成后执行
     * <p>
     * 在整个请求完成后（包括视图渲染），清理 MDC 中的数据，
     * 避免线程池复用时的数据污染。
     * </p>
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler 处理器对象
     * @param ex 异常对象（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull Object handler, Exception ex) {
        // 计算请求处理时间
        Object startTimeObj = request.getAttribute("startTime");
        long duration = 0;
        if (startTimeObj instanceof Long startTime) {
            duration = System.currentTimeMillis() - startTime;
        }

        // 记录请求完成日志
        if (ex != null) {
            log.error("Request completed with error - duration: {}ms, status: {}, error: {}",
                    duration, response.getStatus(), ex.getMessage(), ex);
        } else {
            log.info("Request completed - duration: {}ms, status: {}",
                    duration, response.getStatus());
        }

        // 清理 MDC
        MDC.clear();
    }
}
