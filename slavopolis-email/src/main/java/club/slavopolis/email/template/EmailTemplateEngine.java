package club.slavopolis.email.template;

import java.util.Map;

/**
 * 邮件模板引擎接口 - 定义模板处理的标准方法
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.template
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface EmailTemplateEngine {

    /**
     * 渲染模板
     *
     * @param templateName 模板名称
     * @param variables    模板变量
     * @return 渲染后的内容
     */
    String render(String templateName, Map<String, Object> variables);

    /**
     * 渲染模板字符串
     *
     * @param templateContent 模板内容
     * @param variables       模板变量
     * @return 渲染后的内容
     */
    String renderString(String templateContent, Map<String, Object> variables);

    /**
     * 检查模板是否存在
     *
     * @param templateName 模板名称
     * @return 是否存在
     */
    boolean templateExists(String templateName);

    /**
     * 验证模板语法
     *
     * @param templateContent 模板内容
     * @return 验证结果
     */
    TemplateValidationResult validateTemplate(String templateContent);

    /**
     * 获取模板引擎类型
     *
     * @return 引擎类型
     */
    String getEngineType();

    /**
     * 模板验证结果
     */
    record TemplateValidationResult(
            boolean valid,
            String errorMessage,
            int errorLine,
            int errorColumn
    ) {
        public static TemplateValidationResult success() {
            return new TemplateValidationResult(true, null, -1, -1);
        }

        public static TemplateValidationResult failure(String errorMessage) {
            return new TemplateValidationResult(false, errorMessage, -1, -1);
        }

        public static TemplateValidationResult failure(String errorMessage, int line, int column) {
            return new TemplateValidationResult(false, errorMessage, line, column);
        }
    }
} 