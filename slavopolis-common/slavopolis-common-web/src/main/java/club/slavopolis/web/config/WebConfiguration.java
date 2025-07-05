package club.slavopolis.web.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.web.filter.TokenFilter;
import club.slavopolis.web.handler.GlobalWebExceptionHandler;

/**
 * Web 配置类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(CurrentSystemProperties.class)
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public GlobalWebExceptionHandler globalWebExceptionHandler() {
        return new GlobalWebExceptionHandler();
    }

    @Bean
    @ConditionalOnProperty(prefix = "biz.web.token-filter", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<TokenFilter> tokenFilter(RedissonClient redissonClient, CurrentSystemProperties systemProperties) {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter(redissonClient, systemProperties));

        CurrentSystemProperties.TokenFilterConfig tokenFilterConfig = systemProperties.getWeb().getTokenFilter();
        String[] urlPatterns = tokenFilterConfig.getUrlPatterns().toArray(new String[0]);
        registrationBean.addUrlPatterns(urlPatterns);
        
        registrationBean.setOrder(tokenFilterConfig.getOrder());
        return registrationBean;
    }
}
