package club.slavopolis.web.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.slavopolis.base.properties.CurrentSystemProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

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

    // ================================ 错误消息常量 ================================

    /**
     * Token缺失 - 日志消息
     */
    private static final String LOG_NO_TOKEN_FOUND = "请求头中未找到token，请检查认证信息";

    /**
     * Token验证失败 - 日志消息
     */
    private static final String LOG_TOKEN_VALIDATE_FAILED = "token验证失败，请重新登录";

    /**
     * Token缺失 - 响应消息
     */
    private static final String RESPONSE_NO_TOKEN_FOUND = "缺少认证信息";

    /**
     * Token无效 - 响应消息
     */
    private static final String RESPONSE_INVALID_TOKEN = "认证信息无效或已过期";

    // ================================ 成员变量 ================================

    /**
     * 线程本地变量: Token
     */
    public static final ThreadLocal<String> TOKEN_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 线程本地变量: 压测
     */
    public static final ThreadLocal<Boolean> STRESS_THREAD_LOCAL = new ThreadLocal<>();

    private final RedissonClient redissonClient;

    private final CurrentSystemProperties systemProperties;

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
                httpResponse.getWriter().write(RESPONSE_NO_TOKEN_FOUND);
                logger.error(LOG_NO_TOKEN_FOUND);
                return;
            }

            boolean isValid = checkTokenValidity(token, isStress);
            if (!isValid) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write(RESPONSE_INVALID_TOKEN);
                logger.error(LOG_TOKEN_VALIDATE_FAILED);
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            TOKEN_THREAD_LOCAL.remove();
            STRESS_THREAD_LOCAL.remove();
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

        String result = redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.STATUS,
                Collections.singletonList(token)
        );

        if (Boolean.TRUE.equals(isStress) && systemProperties.getWeb().getTokenFilter().isStressTestEnabled()) {
            // 压测状态，随机生成模拟 Token
            result = UUID.randomUUID().toString();
            STRESS_THREAD_LOCAL.set(true);
        }

        TOKEN_THREAD_LOCAL.set(result);
        return result != null;
    }
}

