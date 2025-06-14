package club.slavopolis.email.model;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.email.enums.ContentType;
import club.slavopolis.email.enums.EmailType;
import club.slavopolis.email.enums.Priority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 邮件消息模型类 - 封装完整的邮件信息
 * <p>
 * 支持功能：
 * <ul>
 *   <li>基础邮件信息（发送方、接收方、主题、内容）</li>
 *   <li>附件和内嵌资源</li>
 *   <li>邮件优先级和类型</li>
 *   <li>发送配置和追踪信息</li>
 * </ul>
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.model
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邮件唯一标识
     */
    private String messageId;

    /**
     * 发送方邮箱地址
     */
    @NotBlank(message = "发送方邮箱地址不能为空")
    @Email(message = "发送方邮箱地址格式不正确")
    private String from;

    /**
     * 发送方显示名称
     */
    private String fromName;

    /**
     * 收件人列表
     */
    @NotNull(message = "收件人列表不能为空")
    private List<@Email(message = "收件人邮箱地址格式不正确") String> to;

    /**
     * 抄送人列表
     */
    private List<@Email(message = "抄送人邮箱地址格式不正确") String> cc;

    /**
     * 密送人列表
     */
    private List<@Email(message = "密送人邮箱地址格式不正确") String> bcc;

    /**
     * 回复地址
     */
    @Email(message = "回复地址格式不正确")
    private String replyTo;

    /**
     * 邮件主题
     */
    @NotBlank(message = "邮件主题不能为空")
    private String subject;

    /**
     * 邮件内容（纯文本）
     */
    private String text;

    /**
     * 邮件内容（HTML格式）
     */
    private String html;

    /**
     * 内容类型
     */
    private ContentType contentType = ContentType.TEXT;

    /**
     * 字符编码
     */
    private String encoding = CommonConstants.CHARSET_UTF8;

    /**
     * 邮件优先级
     */
    private Priority priority = Priority.NORMAL;

    /**
     * 邮件类型
     */
    private EmailType emailType = EmailType.NOTIFICATION;

    /**
     * 附件列表
     */
    private List<EmailAttachment> attachments;

    /**
     * 内嵌资源列表
     */
    private List<EmailInlineResource> inlineResources;

    /**
     * 模板名称（如果使用模板）
     */
    private String templateName;

    /**
     * 模板参数
     */
    private transient Map<String, Object> templateParams;

    /**
     * 自定义邮件头
     */
    private Map<String, String> headers;

    /**
     * 发送配置
     */
    private SendConfig sendConfig;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 计划发送时间
     */
    private LocalDateTime scheduledTime;

    /**
     * 业务标识（用于分类和统计）
     */
    private String businessTag;

    /**
     * 追踪标识（用于发送状态追踪）
     */
    private String trackingId;

    /**
     * 扩展属性
     */
    private transient Map<String, Object> extraProperties;

    /**
     * 发送配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendConfig implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 是否启用重试
         */
        private boolean retryEnabled = true;

        /**
         * 最大重试次数
         */
        private int maxRetries = 3;

        /**
         * 重试间隔（毫秒）
         */
        private long retryInterval = 1000;

        /**
         * 发送超时时间（毫秒）
         */
        private long sendTimeout = 30000;

        /**
         * 是否异步发送
         */
        private boolean async = true;

        /**
         * 是否需要发送回执
         */
        private boolean needReceipt = false;

        /**
         * 是否启用追踪
         */
        private boolean trackingEnabled = true;
    }

    /**
     * 便捷构造方法 - 简单文本邮件
     */
    public static EmailMessage simpleText(String to, String subject, String text) {
        return EmailMessage.builder()
                .to(List.of(to))
                .subject(subject)
                .text(text)
                .contentType(ContentType.TEXT)
                .messageId(generateMessageId())
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 便捷构造方法 - HTML邮件
     */
    public static EmailMessage htmlEmail(String to, String subject, String html) {
        return EmailMessage.builder()
                .to(List.of(to))
                .subject(subject)
                .html(html)
                .contentType(ContentType.HTML)
                .messageId(generateMessageId())
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 便捷构造方法 - 模板邮件
     */
    public static EmailMessage templateEmail(String to, String subject, String templateName, Map<String, Object> params) {
        return EmailMessage.builder()
                .to(List.of(to))
                .subject(subject)
                .templateName(templateName)
                .templateParams(params)
                .contentType(ContentType.HTML)
                .messageId(generateMessageId())
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 生成邮件唯一标识
     */
    private static String generateMessageId() {
        return "email_" + System.currentTimeMillis() + CommonConstants.UNDERSCORE + Thread.currentThread().threadId();
    }

    /**
     * 添加收件人
     */
    public EmailMessage addTo(String email) {
        if (this.to == null) {
            this.to = new java.util.ArrayList<>();
        }
        this.to.add(email);
        return this;
    }

    /**
     * 添加抄送人
     */
    public EmailMessage addCc(String email) {
        if (this.cc == null) {
            this.cc = new java.util.ArrayList<>();
        }
        this.cc.add(email);
        return this;
    }

    /**
     * 添加密送人
     */
    public EmailMessage addBcc(String email) {
        if (this.bcc == null) {
            this.bcc = new java.util.ArrayList<>();
        }
        this.bcc.add(email);
        return this;
    }

    /**
     * 添加附件
     */
    public EmailMessage addAttachment(EmailAttachment attachment) {
        if (this.attachments == null) {
            this.attachments = new java.util.ArrayList<>();
        }
        this.attachments.add(attachment);
        return this;
    }

    /**
     * 添加内嵌资源
     */
    public EmailMessage addInlineResource(EmailInlineResource resource) {
        if (this.inlineResources == null) {
            this.inlineResources = new java.util.ArrayList<>();
        }
        this.inlineResources.add(resource);
        return this;
    }

    /**
     * 设置模板参数
     */
    public EmailMessage setTemplateParam(String key, Object value) {
        if (this.templateParams == null) {
            this.templateParams = new java.util.HashMap<>();
        }
        this.templateParams.put(key, value);
        return this;
    }

    /**
     * 设置自定义头部
     */
    public EmailMessage setHeader(String name, String value) {
        if (this.headers == null) {
            this.headers = new java.util.HashMap<>();
        }
        this.headers.put(name, value);
        return this;
    }
} 