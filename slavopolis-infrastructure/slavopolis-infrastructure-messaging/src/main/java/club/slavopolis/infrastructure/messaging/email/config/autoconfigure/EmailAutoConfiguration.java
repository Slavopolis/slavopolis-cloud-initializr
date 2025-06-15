package club.slavopolis.infrastructure.messaging.email.config.autoconfigure;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.cache.redis.service.CacheService;
import club.slavopolis.infrastructure.cache.redis.service.RateLimitService;
import club.slavopolis.infrastructure.messaging.email.config.EmailWebConfiguration;
import club.slavopolis.infrastructure.messaging.email.config.properties.EmailProperties;
import club.slavopolis.infrastructure.messaging.email.service.EmailService;
import club.slavopolis.infrastructure.messaging.email.service.impl.EmailServiceImpl;
import club.slavopolis.infrastructure.messaging.email.template.EmailTemplateEngine;
import club.slavopolis.infrastructure.messaging.email.template.impl.FreemarkerEmailTemplateEngine;
import club.slavopolis.infrastructure.messaging.email.template.impl.ThymeleafEmailTemplateEngine;
import club.slavopolis.infrastructure.messaging.email.util.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 邮件模块自动配置类
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.config.autoconfigure
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Slf4j
@AutoConfiguration
@EnableAsync
@EnableConfigurationProperties(EmailProperties.class)
@ConditionalOnProperty(prefix = "slavopolis.email", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({EmailWebConfiguration.class})
public class EmailAutoConfiguration {

    /**
     * 配置JavaMailSender
     */
    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender(EmailProperties emailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        EmailProperties.SmtpConfig smtp = emailProperties.getSmtp();
        mailSender.setHost(smtp.getHost());
        mailSender.setPort(smtp.getPort());
        mailSender.setUsername(smtp.getUsername());
        mailSender.setPassword(smtp.getPassword());
        mailSender.setDefaultEncoding(CommonConstants.CHARSET_UTF8);

        // 配置SMTP属性
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtp.isAuth());
        props.put("mail.smtp.starttls.enable", smtp.isStarttls());
        props.put("mail.smtp.ssl.enable", smtp.isSsl());
        props.put("mail.smtp.connectiontimeout", smtp.getConnectionTimeout());
        props.put("mail.smtp.timeout", smtp.getReadTimeout());
        props.put("mail.smtp.writetimeout", smtp.getWriteTimeout());

        // 添加自定义SMTP属性
        if (smtp.getProperties() != null) {
            props.putAll(smtp.getProperties());
        }

        log.info("JavaMailSender configured with host: {}, port: {}", smtp.getHost(), smtp.getPort());
        return mailSender;
    }

    /**
     * 配置邮件服务
     */
    @Bean
    @ConditionalOnMissingBean
    public EmailService emailService(JavaMailSender javaMailSender, 
                                   EmailTemplateEngine templateEngine,
                                   EmailProperties emailProperties,
                                   Executor emailTaskExecutor,
                                   @Autowired(required = false) RateLimitService rateLimitService,
                                   @Autowired(required = false) CacheService cacheService) {
        return new EmailServiceImpl(javaMailSender, templateEngine, emailProperties, 
                                   emailTaskExecutor, rateLimitService, cacheService);
    }

    /**
     * 配置FreeMarker模板引擎
     */
    @Bean
    @ConditionalOnClass(name = "freemarker.template.Configuration")
    @ConditionalOnProperty(prefix = "slavopolis.email.template", name = "engine", havingValue = "FREEMARKER", matchIfMissing = true)
    @ConditionalOnMissingBean
    public EmailTemplateEngine freemarkerEmailTemplateEngine(EmailProperties emailProperties) {
        log.info("Configuring FreeMarker email template engine");
        return new FreemarkerEmailTemplateEngine(emailProperties.getTemplate());
    }

    /**
     * 配置Thymeleaf模板引擎
     */
    @Bean
    @ConditionalOnClass(name = "org.thymeleaf.TemplateEngine")
    @ConditionalOnProperty(prefix = "slavopolis.email.template", name = "engine", havingValue = "THYMELEAF")
    @ConditionalOnMissingBean
    public EmailTemplateEngine thymeleafEmailTemplateEngine(EmailProperties emailProperties) {
        log.info("Configuring Thymeleaf email template engine");
        return new ThymeleafEmailTemplateEngine(emailProperties.getTemplate());
    }

    /**
     * 配置邮件异步执行器
     */
    @Bean("emailTaskExecutor")
    @ConditionalOnMissingBean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor(EmailProperties emailProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        EmailProperties.SendStrategy sendStrategy = emailProperties.getSendStrategy();
        executor.setCorePoolSize(sendStrategy.getAsyncPoolSize());
        executor.setMaxPoolSize(sendStrategy.getAsyncPoolSize() * 2);
        executor.setQueueCapacity(sendStrategy.getAsyncQueueSize());
        executor.setThreadNamePrefix("email-async-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        
        log.info("Email task executor configured with core pool size: {}, queue capacity: {}", 
                sendStrategy.getAsyncPoolSize(), sendStrategy.getAsyncQueueSize());
        return executor;
    }

    /**
     * 配置邮件地址验证器
     */
    @Bean
    @ConditionalOnMissingBean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }
} 