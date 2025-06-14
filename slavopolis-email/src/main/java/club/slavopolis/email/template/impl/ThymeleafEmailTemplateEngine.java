package club.slavopolis.email.template.impl;

import club.slavopolis.email.config.properties.EmailProperties;
import club.slavopolis.email.exception.EmailTemplateException;
import club.slavopolis.email.enums.EmailErrorCode;
import club.slavopolis.email.template.EmailTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Thymeleaf邮件模板引擎实现类
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.template.impl
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Slf4j
public class ThymeleafEmailTemplateEngine implements EmailTemplateEngine {

    private final TemplateEngine thymeleafEngine;
    private final EmailProperties.TemplateConfig templateConfig;
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * 构造函数
     *
     * @param templateConfig 模板配置
     */
    public ThymeleafEmailTemplateEngine(EmailProperties.TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.thymeleafEngine = createThymeleafEngine();
    }

    /**
     * 创建Thymeleaf引擎
     *
     * @return Thymeleaf引擎
     */
    private TemplateEngine createThymeleafEngine() {
        TemplateEngine engine = new TemplateEngine();
        
        try {
            // 设置模板解析器
            if (templateConfig.getTemplatePath().startsWith("classpath:")) {
                ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
                resolver.setPrefix(templateConfig.getTemplatePath().substring("classpath:".length()));
                resolver.setSuffix(templateConfig.getTemplateSuffix());
                resolver.setTemplateMode(TemplateMode.HTML);
                resolver.setCharacterEncoding(templateConfig.getEncoding());
                resolver.setCacheable(templateConfig.isCacheEnabled());
                if (templateConfig.isCacheEnabled()) {
                    resolver.setCacheTTLMs((long) templateConfig.getCacheUpdateDelay() * 1000);
                }
                engine.setTemplateResolver(resolver);
            } else {
                FileTemplateResolver resolver = new FileTemplateResolver();
                resolver.setPrefix(templateConfig.getTemplatePath());
                resolver.setSuffix(templateConfig.getTemplateSuffix());
                resolver.setTemplateMode(TemplateMode.HTML);
                resolver.setCharacterEncoding(templateConfig.getEncoding());
                resolver.setCacheable(templateConfig.isCacheEnabled());
                if (templateConfig.isCacheEnabled()) {
                    resolver.setCacheTTLMs((long) templateConfig.getCacheUpdateDelay() * 1000);
                }
                engine.setTemplateResolver(resolver);
            }
            
            log.info("Thymeleaf配置初始化成功");
        } catch (Exception e) {
            log.error("Thymeleaf配置初始化失败", e);
            throw new EmailTemplateException(EmailErrorCode.TEMPLATE_ENGINE_ERROR, "Thymeleaf配置初始化失败", e);
        }
        
        return engine;
    }

    /**
     * 渲染模板
     *
     * @param templateName 模板名称
     * @param variables    模板变量
     * @return 渲染后的内容
     */
    @Override
    public String render(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context(Locale.getDefault(), variables);
            
            String result = thymeleafEngine.process(templateName, context);
            log.debug("模板 '{}' 渲染成功", templateName);
            return result;
            
        } catch (Exception e) {
            log.error("模板 '{}' 渲染失败", templateName, e);
            throw new EmailTemplateException(EmailErrorCode.TEMPLATE_RENDER_ERROR, "模板 '" + templateName + "' 渲染失败: " + e.getMessage(), e);
        }
    }

    /**
     * 渲染模板字符串
     *
     * @param templateContent 模板内容
     * @param variables       模板变量
     * @return 渲染后的内容
     */
    @Override
    public String renderString(String templateContent, Map<String, Object> variables) {
        try {
            // 创建字符串模板解析器
            StringTemplateResolver stringResolver = new StringTemplateResolver();
            stringResolver.setTemplateMode(TemplateMode.HTML);
            stringResolver.setCacheable(false);
            
            TemplateEngine stringEngine = new TemplateEngine();
            stringEngine.setTemplateResolver(stringResolver);
            
            Context context = new Context(Locale.getDefault(), variables);
            
            String result = stringEngine.process(templateContent, context);
            log.debug("字符串模板渲染成功");
            return result;
            
        } catch (Exception e) {
            log.error("字符串模板渲染失败", e);
            throw new EmailTemplateException(EmailErrorCode.TEMPLATE_RENDER_ERROR, "字符串模板渲染失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查模板是否存在
     *
     * @param templateName 模板名称
     * @return 是否存在
     */
    @Override
    public boolean templateExists(String templateName) {
        try {
            Context context = new Context();
            thymeleafEngine.process(templateName, context);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证模板语法
     *
     * @param templateContent 模板内容
     * @return 验证结果
     */
    @Override
    public TemplateValidationResult validateTemplate(String templateContent) {
        try {
            StringTemplateResolver stringResolver = new StringTemplateResolver();
            stringResolver.setTemplateMode(TemplateMode.HTML);
            stringResolver.setCacheable(false);
            
            TemplateEngine validationEngine = new TemplateEngine();
            validationEngine.setTemplateResolver(stringResolver);
            
            Context context = new Context();
            validationEngine.process(templateContent, context);
            
            return TemplateValidationResult.success();
        } catch (Exception e) {
            return TemplateValidationResult.failure("模板解析失败: " + e.getMessage());
        }
    }

    /**
     * 获取模板引擎类型
     *
     * @return 引擎类型
     */
    @Override
    public String getEngineType() {
        return "Thymeleaf";
    }

    /**
     * 获取可用的模板列表
     */
    public java.util.List<String> getAvailableTemplates() {
        java.util.List<String> templates = new java.util.ArrayList<>();
        return getResultTemplates(templates, templateConfig, resourcePatternResolver, log);
    }

    /**
     * 获取模板列表
     *
     * @param templates 模板列表
     * @param templateConfig 模板配置
     * @param resourcePatternResolver 资源解析器
     * @param log 日志记录器
     * @return 模板列表
     */
    private List<String> getResultTemplates(List<String> templates, EmailProperties.TemplateConfig templateConfig, ResourcePatternResolver resourcePatternResolver, Logger log) {
        try {
            String pattern = templateConfig.getTemplatePath() + "/**/*" + templateConfig.getTemplateSuffix();
            Resource[] resources = resourcePatternResolver.getResources(pattern);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    String filename = resource.getFilename();
                    if (filename != null) {
                        // 移除后缀
                        String templateName = filename.substring(0, filename.length() - templateConfig.getTemplateSuffix().length());
                        templates.add(templateName);
                    }
                }
            }
        } catch (IOException e) {
            log.warn("扫描可用模板失败", e);
        }

        return templates;
    }

    /**
     * 清除模板缓存
     */
    public void clearCache() {
        thymeleafEngine.clearTemplateCache();
        log.info("Thymeleaf模板缓存已清除");
    }
} 