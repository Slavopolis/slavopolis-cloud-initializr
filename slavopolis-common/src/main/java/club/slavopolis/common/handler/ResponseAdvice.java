package club.slavopolis.common.handler;

import club.slavopolis.common.annotation.NoResponseWrap;
import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 统一响应处理, 自动包装Controller返回值为Result格式
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "club.slavopolis")
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * Jackson 对象映射器，用于处理 JSON 序列化
     */
    private final ObjectMapper objectMapper;

    /**
     * 判断是否需要对响应进行统一包装处理
     * <p>
     * 以下情况不进行包装：
     * 1. 类上标注了 @NoResponseWrap 注解
     * 2. 方法上标注了 @NoResponseWrap 注解
     * 3. 返回值已经是 Result 类型
     * </p>
     *
     * @param returnType     方法返回类型的元数据
     * @param converterType  使用的 HttpMessageConverter 类型
     * @return true 表示需要进行包装处理，false 表示不需要
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 检查类上是否有 @NoResponseWrap 注解
        if (returnType.getDeclaringClass().isAnnotationPresent(NoResponseWrap.class)) {
            return false;
        }

        // 检查方法上是否有 @NoResponseWrap 注解
        Method method = returnType.getMethod();
        if (method != null && method.isAnnotationPresent(NoResponseWrap.class)) {
            return false;
        }

        // 已经是 Result 类型的不再包装
        return !returnType.getParameterType().equals(Result.class);
    }

    /**
     * 在响应体写入之前进行处理，实现统一响应格式包装
     * <p>
     * 处理逻辑：
     * 1. 提取请求追踪 ID（优先从 MDC 获取，其次从请求头获取）
     * 2. 对不同类型的返回值进行相应处理：
     *    - null 值：包装为成功的空响应
     *    - String 类型：特殊处理以避免序列化问题
     *    - 其他类型：直接包装为 Result 对象
     * 3. 为所有响应添加追踪 ID
     * </p>
     *
     * @param body                   原始响应体，可能为 null
     * @param returnType             方法返回类型的元数据
     * @param selectedContentType    选定的内容类型
     * @param selectedConverterType  选定的消息转换器类型
     * @param request                服务器 HTTP 请求
     * @param response               服务器 HTTP 响应
     * @return 包装后的响应体
     */
    @Override
    @Nullable
    public Object beforeBodyWrite(
            @Nullable Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        // 获取追踪 ID，优先从 MDC 获取
        String traceId = MDC.get(CommonConstants.TRACE_ID);

        // 如果 MDC 中没有，尝试从请求头获取
        if (traceId == null && request instanceof ServletServerHttpRequest servletRequest) {
            traceId = servletRequest.getServletRequest().getHeader(CommonConstants.TRACE_ID);
        }

        // 处理返回值为 null 的情况
        if (body == null) {
            Result<Object> result = Result.success();
            if (traceId != null) {
                result.setTraceId(traceId);
            }
            return result;
        }

        // 处理返回值为 String 类型的特殊情况
        // String 类型需要特殊处理，因为 StringHttpMessageConverter 会直接处理字符串
        // 如果直接返回 Result 对象，会导致类型转换异常
        if (body instanceof String) {
            try {
                Result<Object> result = Result.success(body);
                if (traceId != null) {
                    result.setTraceId(traceId);
                }

                // 设置响应内容类型为 JSON
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                // 手动序列化为 JSON 字符串
                return objectMapper.writeValueAsString(result);
            } catch (Exception e) {
                log.error("Failed to wrap string response", e);
                // 序列化失败时返回原始字符串，避免接口异常
                return body;
            }
        }

        // 处理其他类型的返回值，统一包装为 Result
        Result<Object> result = Result.success(body);
        if (traceId != null) {
            result.setTraceId(traceId);
        }

        return result;
    }
}
