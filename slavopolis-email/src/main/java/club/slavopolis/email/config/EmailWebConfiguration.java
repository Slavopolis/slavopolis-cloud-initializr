package club.slavopolis.email.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 邮件模块Web配置类 - 配置Web相关组件
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.config
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@ConditionalOnProperty(prefix = "slavopolis.email.web", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EmailWebConfiguration implements WebMvcConfigurer {
    // Web相关配置可以在这里添加
} 