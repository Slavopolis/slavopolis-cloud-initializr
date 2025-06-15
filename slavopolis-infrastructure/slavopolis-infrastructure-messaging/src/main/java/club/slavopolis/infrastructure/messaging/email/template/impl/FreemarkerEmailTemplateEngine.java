package club.slavopolis.infrastructure.messaging.email.template.impl;

import club.slavopolis.common.core.util.DateTimeUtil;
import club.slavopolis.infrastructure.messaging.email.config.properties.EmailProperties;
import club.slavopolis.infrastructure.messaging.email.exception.EmailTemplateException;
import club.slavopolis.infrastructure.messaging.email.enums.EmailErrorCode;
import club.slavopolis.infrastructure.messaging.email.template.EmailTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FreeMarker邮件模板引擎实现类
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
public class FreemarkerEmailTemplateEngine implements EmailTemplateEngine {

    private final Configuration freemarkerConfig;
    private final EmailProperties.TemplateConfig templateConfig;
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * 构造函数
     *
     * @param templateConfig 模板配置
     */
    public FreemarkerEmailTemplateEngine(EmailProperties.TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.freemarkerConfig = createFreemarkerConfiguration();
    }

    /**
     * 创建FreeMarker配置
     *
     * @return FreeMarker配置
     */
    private Configuration createFreemarkerConfiguration() {
        Configuration config = new Configuration(Configuration.VERSION_2_3_32);
        
        try {
            // 设置模板加载器
            if (templateConfig.getTemplatePath().startsWith("classpath:")) {
                config.setClassForTemplateLoading(this.getClass(), templateConfig.getTemplatePath().substring("classpath:".length()));
            } else {
                config.setDirectoryForTemplateLoading(new java.io.File(templateConfig.getTemplatePath()));
            }
            
            // 设置编码
            config.setDefaultEncoding(templateConfig.getEncoding());
            
            // 设置错误处理
            config.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
            
            // 设置日期格式
            config.setDateFormat(DateTimeUtil.DATE_PATTERN);
            config.setTimeFormat(DateTimeUtil.TIME_PATTERN);
            config.setDateTimeFormat(DateTimeUtil.DATETIME_PATTERN);
            
            // 设置数字格式
            config.setNumberFormat("0.######");
            
            // 设置布尔格式
            config.setBooleanFormat("true,false");
            
            // 开启自动转义
            config.setAutoEscapingPolicy(Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY);
            
            // 缓存配置
            if (templateConfig.isCacheEnabled()) {
                config.setTemplateUpdateDelayMilliseconds(templateConfig.getCacheUpdateDelay() * 1000L);
                int cacheSize = templateConfig.getCacheSize();
                config.setCacheStorage(new freemarker.cache.MruCacheStorage(cacheSize, cacheSize * 2));
            } else {
                config.setTemplateUpdateDelayMilliseconds(0);
            }
            
            log.info("FreeMarker配置初始化成功");
            
        } catch (IOException e) {
            log.error("FreeMarker配置初始化失败", e);
            throw new EmailTemplateException(EmailErrorCode.TEMPLATE_ENGINE_ERROR, "FreeMarker配置初始化失败", e);
        }
        
        return config;
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
            // 添加文件后缀
            String fullTemplateName = templateName;
            if (!templateName.endsWith(templateConfig.getTemplateSuffix())) {
                fullTemplateName = templateName + templateConfig.getTemplateSuffix();
            }
            
            Template template = freemarkerConfig.getTemplate(fullTemplateName);
            
            StringWriter writer = new StringWriter();
            template.process(variables, writer);
            
            String result = writer.toString();
            log.debug("模板 '{}' 渲染成功", templateName);
            return result;
            
        } catch (IOException e) {
            log.error("模板 '{}' 未找到", templateName, e);
            throw new EmailTemplateException(EmailErrorCode.TEMPLATE_NOT_FOUND, "模板 '" + templateName + "' 未找到", e);
        } catch (TemplateException e) {
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
            Template template = new Template("string-template", new StringReader(templateContent), freemarkerConfig);
            
            StringWriter writer = new StringWriter();
            template.process(variables, writer);
            
            String result = writer.toString();
            log.debug("字符串模板渲染成功");
            return result;
            
        } catch (IOException e) {
            log.error("字符串模板解析失败", e);
            throw new EmailTemplateException(EmailErrorCode.TEMPLATE_PARSE_ERROR, "字符串模板解析失败", e);
        } catch (TemplateException e) {
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
            String fullTemplateName = templateName;
            if (!templateName.endsWith(templateConfig.getTemplateSuffix())) {
                fullTemplateName = templateName + templateConfig.getTemplateSuffix();
            }
            
            freemarkerConfig.getTemplate(fullTemplateName);
            return true;
        } catch (IOException e) {
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
            new Template("validation-template", new StringReader(templateContent), freemarkerConfig);
            return TemplateValidationResult.success();
        } catch (IOException e) {
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
        return "FreeMarker";
    }

    /**
     * 获取可用的模板列表
     */
    public List<String> getAvailableTemplates() {
        List<String> templates = new ArrayList<>();
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
        freemarkerConfig.clearTemplateCache();
        log.info("FreeMarker模板缓存已清除");
    }

    /**
     * 预热模板缓存
     */
    public void warmUpCache() {
        List<String> templates = getAvailableTemplates();
        
        for (String templateName : templates) {
            try {
                freemarkerConfig.getTemplate(templateName + templateConfig.getTemplateSuffix());
                log.debug("模板 '{}' 预加载完成", templateName);
            } catch (IOException e) {
                log.warn("预加载模板 '{}' 失败", templateName, e);
            }
        }
        
        log.info("FreeMarker模板缓存预热完成，共 {} 个模板", templates.size());
    }
} 