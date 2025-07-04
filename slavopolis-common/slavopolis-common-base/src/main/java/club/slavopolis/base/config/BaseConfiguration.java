package club.slavopolis.base.config;

import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.base.utils.SpringContextHolder;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 通用模块配置类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@EnableConfigurationProperties(CurrentSystemProperties.class)
@SpringBootConfiguration
public class BaseConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
