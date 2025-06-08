package club.slavopolis.common.log.config;

import club.slavopolis.common.log.config.properties.LogProperties;
import club.slavopolis.common.log.filter.AccessLogFilter;
import club.slavopolis.common.log.filter.MdcFilter;
import club.slavopolis.common.log.interceptor.TraceInterceptor;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 日志模块自动配置类
 *
 * <p>
 * 该配置类负责自动装配日志模块的各个组件，包括链路追踪拦截器、
 * 访问日志过滤器、性能监控切面等。通过 Spring Boot 的自动配置机制，
 * 使用方只需引入依赖即可自动启用完整的日志功能。
 * </p>
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(LogProperties.class)
@ComponentScan(basePackages = "club.slavopolis.common.log")
@ConditionalOnProperty(prefix = "common.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration implements WebMvcConfigurer {

    private final LogProperties logProperties;
    private final TraceInterceptor traceInterceptor;

    /**
     * 注册链路追踪拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册链路追踪拦截器");
        registry.addInterceptor(traceInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(logProperties.getExcludePatterns())
                .order(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 注册访问日志过滤器
     *
     * @param accessLogFilter 访问日志过滤器
     * @return 过滤器注册 Bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "common.log.access", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistration(AccessLogFilter accessLogFilter) {
        log.info("注册访问日志过滤器");
        FilterRegistrationBean<AccessLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(accessLogFilter);
        registration.addUrlPatterns("/*");
        registration.setName("accessLogFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    /**
     * MDC 过滤器
     * 用于在异步线程中传递 MDC 上下文
     *
     * @return MDC 过滤器
     */
    @Bean
    public Filter mdcFilter() {
        return new MdcFilter();
    }
}
