package club.slavopolis.common.log.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: MDC 过滤器
 *
 * <p>
 * 该过滤器用于在异步线程池中传递 MDC 上下文信息，
 * 确保在异步执行的代码中也能获取到追踪 ID 等信息。
 * </p>
 */
@Component
public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            // 在请求开始时保存 MDC 上下文
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            try {
                // 继续处理请求
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                // 恢复 MDC 上下文
                if (Objects.nonNull(contextMap)) {
                    MDC.setContextMap(contextMap);
                }
            }
        } else {
            // 非 HTTP 请求，直接处理
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
