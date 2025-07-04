package club.slavopolis.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Token 过滤器
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@RequiredArgsConstructor
public class TokenFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    /**
     * 线程本地变量: Token
     */
    public static final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

    /**
     * 线程本地变量: 压测
     */
    public static final ThreadLocal<Boolean> stressThreadLocal = new ThreadLocal<>();

    private final RedissonClient redissonClient;

    /**
     * 初始化
     *
     * @param filterConfig 过滤器配置
     * @throws ServletException 抛出Servlet异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 过滤器初始化，可选实现
        Filter.super.init(filterConfig);
    }

    /**
     * 过滤
     *
     * @param servletRequest  Servlet请求
     * @param servletResponse Servlet响应
     * @param filterChain     过滤器链
     * @throws IOException      抛出IOException异常
     * @throws ServletException 抛出Servlet异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

            String token = httpRequest.getHeader("Authorization");
            Boolean isStress = BooleanUtils.toBoolean(httpRequest.getHeader("isStress"));

            if (token == null || "null".equals(token) || "undefined".equals(token)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("No Token Found ...");
                logger.error("no token found in header, pls check!");
                return;
            }

            boolean isValid = checkTokenValidity(token, isStress);
            if (!isValid) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid or expired token");
                logger.error("token validate failed, pls check!");
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            tokenThreadLocal.remove();
            stressThreadLocal.remove();
        }
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        // 过滤器销毁，可选实现
        Filter.super.destroy();
    }

    /**
     * 检查Token是否有效
     *
     * @param token    Token
     * @param isStress 是否压测
     * @return 是否有效
     */
    private boolean checkTokenValidity(String token, Boolean isStress) {
        String luaScript = """
                local value = redis.call('GET', KEYS[1])
                redis.call('DEL', KEYS[1])
                return value""";

        // 6.2.3 以上可以直接使用 GETDEL 命令
        // String value = (String) redisTemplate.opsForValue().getAndDelete(token);

        String result = (String) redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.STATUS,
                Arrays.asList(token)
        );

        if (isStress) {
            // 压测状态，随机生成模拟 Token
            result = UUID.randomUUID().toString();
            stressThreadLocal.set(isStress);
        }

        tokenThreadLocal.set(result);
        return result != null;
    }
}
