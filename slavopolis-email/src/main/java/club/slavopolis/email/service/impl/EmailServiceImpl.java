package club.slavopolis.email.service.impl;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.constant.HttpConstants;
import club.slavopolis.email.config.properties.EmailProperties;
import club.slavopolis.email.enums.ContentType;
import club.slavopolis.email.enums.EmailErrorCode;
import club.slavopolis.email.enums.Priority;
import club.slavopolis.email.enums.SendStatus;
import club.slavopolis.email.exception.EmailSendException;
import club.slavopolis.email.exception.EmailTemplateException;
import club.slavopolis.email.model.*;
import club.slavopolis.email.service.EmailService;
import club.slavopolis.email.template.EmailTemplateEngine;
import club.slavopolis.email.util.EmailValidator;
import club.slavopolis.cache.service.RateLimitService;
import club.slavopolis.cache.model.RateLimitResult;
import club.slavopolis.cache.service.CacheService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 邮件服务核心实现类 - 提供完整的邮件发送和管理功能
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.service.impl
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailTemplateEngine templateEngine;
    private final EmailProperties emailProperties;
    private final Executor emailTaskExecutor;
    private final EmailValidator emailValidator;
    private final RateLimitService rateLimitService;
    private final CacheService cacheService;

    // 邮件发送结果缓存（支持内存或Redis）
    private final Map<String, EmailSendResult> sendResultCache;
    
    // 启动时间
    private final long startTime = System.currentTimeMillis();

    public EmailServiceImpl(JavaMailSender javaMailSender, 
                           EmailTemplateEngine templateEngine,
                           EmailProperties emailProperties,
                           Executor emailTaskExecutor,
                           RateLimitService rateLimitService,
                           @Autowired(required = false) CacheService cacheService) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.emailProperties = emailProperties;
        this.emailTaskExecutor = emailTaskExecutor;
        this.emailValidator = new EmailValidator();
        this.rateLimitService = rateLimitService;
        this.cacheService = cacheService;
        
        // 根据配置选择缓存实现
        if (cacheService != null && emailProperties.isUseRedisCacheForResults()) {
            // 使用Redis缓存
            this.sendResultCache = null;
            log.info("使用Redis缓存存储邮件发送结果");
        } else {
            this.sendResultCache = new ConcurrentHashMap<>();
            log.info("使用内存缓存存储邮件发送结果");
        }
        
        log.info("使用模板引擎初始化的 EmailService: {}, 限流服务: {}",
                templateEngine != null ? templateEngine.getEngineType() : "None",
                rateLimitService != null ? "启用" : "禁用");
    }

    // ==================== 基础发送功能 ====================

    @Override
    public EmailSendResult sendSimpleText(String to, String subject, String text) {
        EmailMessage emailMessage = EmailMessage.simpleText(to, subject, text);
        return sendEmail(emailMessage);
    }

    @Override
    public EmailSendResult sendHtmlEmail(String to, String subject, String html) {
        EmailMessage emailMessage = EmailMessage.htmlEmail(to, subject, html);
        return sendEmail(emailMessage);
    }

    @Override
    public EmailSendResult sendEmail(EmailMessage emailMessage) {
        log.debug("发送电子邮件: {}", emailMessage.getMessageId());
        
        try {
            // 参数验证
            validateEmailMessage(emailMessage);
            
            // 设置默认发送方信息
            setupDefaultSenderInfo(emailMessage);
            
            // 处理模板渲染
            processTemplate(emailMessage);
            
            // 限流检查
            checkRateLimit(emailMessage);
            
            // 创建发送结果
            EmailSendResult result = EmailSendResult.sending(emailMessage.getMessageId());
            result.setSendStartTime(LocalDateTime.now());
            storeEmailResult(emailMessage.getMessageId(), result);
            
            // 执行发送
            return doSendEmail(emailMessage, result);
            
        } catch (Exception e) {
            log.error("发送电子邮件失败: {}", emailMessage.getMessageId(), e);
            EmailSendResult failureResult = createFailureResult(emailMessage, e);
            storeEmailResult(emailMessage.getMessageId(), failureResult);
            return failureResult;
        }
    }

    // ==================== 异步发送功能 ====================

    @Override
    public CompletableFuture<EmailSendResult> sendSimpleTextAsync(String to, String subject, String text) {
        return CompletableFuture.supplyAsync(() -> sendSimpleText(to, subject, text), emailTaskExecutor);
    }

    @Override
    public CompletableFuture<EmailSendResult> sendHtmlEmailAsync(String to, String subject, String html) {
        return CompletableFuture.supplyAsync(() -> sendHtmlEmail(to, subject, html), emailTaskExecutor);
    }

    @Override
    public CompletableFuture<EmailSendResult> sendEmailAsync(EmailMessage emailMessage) {
        return CompletableFuture.supplyAsync(() -> sendEmail(emailMessage), emailTaskExecutor);
    }

    // ==================== 批量发送功能 ====================

    @Override
    public List<EmailSendResult> sendBatchEmails(List<EmailMessage> emailMessages) {
        if (CollectionUtils.isEmpty(emailMessages)) {
            return new ArrayList<>();
        }
        
        log.info("发送批量邮件，计数: {}", emailMessages.size());
        List<EmailSendResult> results = new ArrayList<>();
        
        for (EmailMessage message : emailMessages) {
            try {
                EmailSendResult result = sendEmail(message);
                results.add(result);
                
                // 批量发送间隔
                if (!handleBatchInterval(message.getMessageId())) {
                    break; // 中断时退出循环
                }
            } catch (Exception e) {
                log.error("消息批量发送邮件失败: {}", message.getMessageId(), e);
                results.add(createFailureResult(message, e));
            }
        }
        
        return results;
    }

    @Override
    public CompletableFuture<List<EmailSendResult>> sendBatchEmailsAsync(List<EmailMessage> emailMessages) {
        return CompletableFuture.supplyAsync(() -> sendBatchEmails(emailMessages), emailTaskExecutor);
    }

    // ==================== 模板邮件功能 ====================

    @Override
    public EmailSendResult sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> params) {
        EmailMessage emailMessage = EmailMessage.templateEmail(to, subject, templateName, params);
        return sendEmail(emailMessage);
    }

    @Override
    public CompletableFuture<EmailSendResult> sendTemplateEmailAsync(String to, String subject, String templateName, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> sendTemplateEmail(to, subject, templateName, params), emailTaskExecutor);
    }

    @Override
    public List<EmailSendResult> sendBatchTemplateEmails(Map<String, Map<String, Object>> recipients, String subject, String templateName) {
        List<EmailSendResult> results = new ArrayList<>();
        
        for (Map.Entry<String, Map<String, Object>> entry : recipients.entrySet()) {
            try {
                EmailSendResult result = sendTemplateEmail(entry.getKey(), subject, templateName, entry.getValue());
                results.add(result);
            } catch (Exception e) {
                log.error("收件人的模板电子邮件发送失败: {}", entry.getKey(), e);
                EmailMessage dummyMessage = EmailMessage.builder()
                    .messageId("batch_temp_" + System.currentTimeMillis())
                    .to(List.of(entry.getKey()))
                    .subject(subject)
                    .templateName(templateName)
                    .build();
                results.add(createFailureResult(dummyMessage, e));
            }
        }
        
        return results;
    }

    // ==================== 状态查询功能 ====================

    @Override
    public EmailSendResult getEmailStatus(String messageId) {
        return retrieveEmailResult(messageId);
    }

    @Override
    public List<EmailSendResult> getBatchEmailStatus(List<String> messageIds) {
        return messageIds.stream()
                .map(this::getEmailStatus)
                .filter(Objects::nonNull)
                .toList();
    }


    // ==================== 配置和管理功能 ====================

    @Override
    public boolean testConnection() {
        try {
            javaMailSender.createMimeMessage();
            return true;
        } catch (Exception e) {
            log.error("电子邮件连接测试失败", e);
            return false;
        }
    }

    @Override
    public boolean validateEmail(String email) {
        return emailValidator.isValid(email);
    }

    @Override
    public EmailServiceStatus getServiceStatus() {
        return new EmailServiceStatus(
            emailProperties.isEnabled(),
            testConnection(),
            emailProperties.getSmtp().getHost() + CommonConstants.CACHE_KEY_SEPARATOR + emailProperties.getSmtp().getPort(),
            System.currentTimeMillis() - startTime,
            "1.0.0"
        );
    }

    // ==================== 私有辅助方法 ====================

    private void validateEmailMessage(EmailMessage emailMessage) {
        if (emailMessage == null) {
            throw new EmailSendException(EmailErrorCode.PARAMETER_ERROR, EmailErrorCode.PARAMETER_ERROR.getMessage());
        }
        
        if (CollectionUtils.isEmpty(emailMessage.getTo())) {
            throw new EmailSendException(EmailErrorCode.RECIPIENT_INVALID, EmailErrorCode.RECIPIENT_INVALID.getMessage());
        }
        
        if (!StringUtils.hasText(emailMessage.getSubject())) {
            throw new EmailSendException(EmailErrorCode.SUBJECT_EMPTY, EmailErrorCode.SUBJECT_EMPTY.getMessage());
        }
        
        // 验证收件人地址
        for (String recipient : emailMessage.getTo()) {
            if (!emailValidator.isValid(recipient)) {
                throw new EmailSendException(EmailErrorCode.RECIPIENT_INVALID, 
                    EmailErrorCode.RECIPIENT_INVALID.getMessage()
                            + CommonConstants.CACHE_KEY_SEPARATOR
                            + CommonConstants.SPACE + recipient);
            }
        }
        
        // 验证内容
        if (!StringUtils.hasText(emailMessage.getText()) && 
            !StringUtils.hasText(emailMessage.getHtml()) && 
            !StringUtils.hasText(emailMessage.getTemplateName())) {
            throw new EmailSendException(EmailErrorCode.CONTENT_EMPTY, EmailErrorCode.CONTENT_EMPTY.getMessage());
        }
    }

    private void setupDefaultSenderInfo(EmailMessage emailMessage) {
        if (!StringUtils.hasText(emailMessage.getFrom())) {
            emailMessage.setFrom(emailProperties.getDefaultSender().getFrom());
        }
        
        if (!StringUtils.hasText(emailMessage.getFromName())) {
            emailMessage.setFromName(emailProperties.getDefaultSender().getFromName());
        }
        
        if (!StringUtils.hasText(emailMessage.getReplyTo())) {
            emailMessage.setReplyTo(emailProperties.getDefaultSender().getReplyTo());
        }
    }

    private void processTemplate(EmailMessage emailMessage) {
        if (StringUtils.hasText(emailMessage.getTemplateName()) && templateEngine != null) {
            try {
                String renderedContent = templateEngine.render(
                    emailMessage.getTemplateName(), 
                    emailMessage.getTemplateParams()
                );
                emailMessage.setHtml(renderedContent);
                emailMessage.setContentType(ContentType.HTML);
            } catch (Exception e) {
                throw new EmailTemplateException(EmailErrorCode.TEMPLATE_RENDER_ERROR, 
                    EmailErrorCode.TEMPLATE_RENDER_ERROR.getMessage()
                            + CommonConstants.CACHE_KEY_SEPARATOR
                            + CommonConstants.SPACE
                            + emailMessage.getTemplateName(), e);
            }
        }
    }

    private void checkRateLimit(EmailMessage emailMessage) {
        if (!emailProperties.getRateLimit().isEnabled() || rateLimitService == null) {
            return;
        }
        
        try {
            EmailProperties.RateLimit rateLimitConfig = emailProperties.getRateLimit();
            
            // 构建限流键（可以根据收件人、发送方、业务标签等维度）
            String rateLimitKey = buildRateLimitKey(emailMessage);
            
            // 执行限流检查
            RateLimitResult result = rateLimitService.slidingWindowLimit(
                rateLimitKey, 
                rateLimitConfig.getWindow(),
                rateLimitConfig.getMaxRequests()
            );
            
            if (!result.isAllowed()) {
                String errorMsg = String.format("邮件发送被限流: key=%s, 剩余配额=%d, 重置时间=%s, 原因=%s",
                    rateLimitKey, 
                    result.getRemainingQuota(),
                    result.getResetTime(),
                    result.getReason());
                
                log.warn(errorMsg);
                throw new EmailSendException(EmailErrorCode.RATE_LIMIT_EXCEEDED, errorMsg);
            }
            
            log.debug("限流检查通过: key={}, 剩余配额={}", rateLimitKey, result.getRemainingQuota());
            
        } catch (EmailSendException e) {
            // 重新抛出邮件发送异常
            throw e;
        } catch (Exception e) {
            log.error("限流检查失败: {}", emailMessage.getMessageId(), e);
            // 限流检查失败时根据配置决定是否继续发送
            if (emailProperties.getRateLimit().isFailOnLimitError()) {
                throw new EmailSendException(EmailErrorCode.RATE_LIMIT_ERROR, 
                    "限流检查失败: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * 构建限流键
     * 支持多维度限流：全局、发送方、收件人、业务标签等
     */
    private String buildRateLimitKey(EmailMessage emailMessage) {
        List<String> keyParts = new ArrayList<>();
        
        // 全局限流
        keyParts.add("email");
        
        // 根据限流策略添加不同维度
        EmailProperties.RateLimit rateLimitConfig = emailProperties.getRateLimit();
        
        if (rateLimitConfig.isPerSender() && StringUtils.hasText(emailMessage.getFrom())) {
            keyParts.add("sender:" + emailMessage.getFrom());
        }
        
        if (rateLimitConfig.isPerRecipient() && !CollectionUtils.isEmpty(emailMessage.getTo())) {
            // 对于多收件人，可以选择用第一个收件人或计算hash
            keyParts.add("recipient:" + emailMessage.getTo().getFirst());
        }
        
        if (rateLimitConfig.isPerBusinessTag() && StringUtils.hasText(emailMessage.getBusinessTag())) {
            keyParts.add("tag:" + emailMessage.getBusinessTag());
        }
        
        return String.join(CommonConstants.CACHE_KEY_SEPARATOR, keyParts);
    }

    /**
     * 处理批量发送间隔
     * 
     * @param messageId 当前邮件ID
     * @return true=继续处理, false=中断处理
     */
    private boolean handleBatchInterval(String messageId) {
        if (emailProperties.getSendStrategy().getBatchInterval() <= 0) {
            // 无需间隔，继续处理
            return true;
        }
        
        try {
            Thread.sleep(emailProperties.getSendStrategy().getBatchInterval());
            // 间隔成功，继续处理
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("批量电子邮件发送因消息中断: {}", messageId);
            // 中断发生，停止处理
            return false;
        }
    }

    /**
     * 存储邮件发送结果
     */
    private void storeEmailResult(String messageId, EmailSendResult result) {
        if (cacheService != null && emailProperties.isUseRedisCacheForResults()) {
            // 使用Redis缓存，设置1小时过期时间
            String cacheKey = "email:result:" + messageId;
            cacheService.set(cacheKey, result, Duration.ofHours(1));
        } else {
            // 使用内存缓存
            sendResultCache.put(messageId, result);
        }
    }

    /**
     * 获取邮件发送结果
     */
    private EmailSendResult retrieveEmailResult(String messageId) {
        if (cacheService != null && emailProperties.isUseRedisCacheForResults()) {
            // 从Redis缓存获取
            String cacheKey = "email:result:" + messageId;
            return cacheService.get(cacheKey, EmailSendResult.class);
        } else {
            // 从内存缓存获取
            return sendResultCache.get(messageId);
        }
    }

    private EmailSendResult doSendEmail(EmailMessage emailMessage, EmailSendResult result) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, emailMessage.getEncoding());
            
            // 设置基本信息
            helper.setFrom(emailMessage.getFrom(), emailMessage.getFromName());
            helper.setTo(emailMessage.getTo().toArray(new String[0]));
            helper.setSubject(emailMessage.getSubject());
            
            // 设置抄送和密送
            if (!CollectionUtils.isEmpty(emailMessage.getCc())) {
                helper.setCc(emailMessage.getCc().toArray(new String[0]));
            }
            if (!CollectionUtils.isEmpty(emailMessage.getBcc())) {
                helper.setBcc(emailMessage.getBcc().toArray(new String[0]));
            }
            
            // 设置回复地址
            if (StringUtils.hasText(emailMessage.getReplyTo())) {
                helper.setReplyTo(emailMessage.getReplyTo());
            }
            
            // 设置内容
            if (emailMessage.getContentType() == ContentType.HTML && StringUtils.hasText(emailMessage.getHtml())) {
                helper.setText(emailMessage.getText(), emailMessage.getHtml());
            } else if (StringUtils.hasText(emailMessage.getText())) {
                helper.setText(emailMessage.getText(), false);
            }
            
            // 设置优先级
            if (emailMessage.getPriority() != Priority.NORMAL) {
                mimeMessage.setHeader(HttpConstants.HEADER_X_PRIORITY, String.valueOf(emailMessage.getPriority().getValue()));
            }
            
            // 添加自定义头部
            if (!CollectionUtils.isEmpty(emailMessage.getHeaders())) {
                for (Map.Entry<String, String> header : emailMessage.getHeaders().entrySet()) {
                    mimeMessage.setHeader(header.getKey(), header.getValue());
                }
            }
            
            // 添加附件
            addAttachments(helper, emailMessage);
            
            // 添加内嵌资源
            addInlineResources(helper, emailMessage);
            
            // 发送邮件
            javaMailSender.send(mimeMessage);
            
            // 更新结果
            result.setStatus(SendStatus.SUCCESS);
            result.setSuccess(true);
            result.setSendEndTime(LocalDateTime.now());
            result.calculateDuration();
            result.setRecipientCount(emailMessage.getTo().size());
            result.setSuccessRecipientCount(emailMessage.getTo().size());
            result.setFailedRecipientCount(0);
            
            // 更新缓存中的结果
            storeEmailResult(emailMessage.getMessageId(), result);
            
            log.info("邮件发送成功: {}", emailMessage.getMessageId());
            return result;
            
        } catch (MailException e) {
            log.error("邮件发送失败: {}", emailMessage.getMessageId(), e);
            
            result.setStatus(SendStatus.FAILED);
            result.setSuccess(false);
            result.setErrorCode(EmailErrorCode.SEND_FAILED.getCode());
            result.setErrorMessage(e.getMessage());
            result.setSendEndTime(LocalDateTime.now());
            result.calculateDuration();
            
            // 更新缓存中的结果
            storeEmailResult(emailMessage.getMessageId(), result);
            
            return result;
        } catch (Exception e) {
            log.error("电子邮件发送过程中出现意外错误: {}", emailMessage.getMessageId(), e);
            throw new EmailSendException(EmailErrorCode.SEND_FAILED, 
                EmailErrorCode.SEND_FAILED.getMessage() + CommonConstants.CACHE_KEY_SEPARATOR + e.getMessage(), e);
        }
    }

    private void addAttachments(MimeMessageHelper helper, EmailMessage emailMessage) throws Exception {
        if (CollectionUtils.isEmpty(emailMessage.getAttachments())) {
            return;
        }
        
        for (EmailAttachment attachment : emailMessage.getAttachments()) {
            switch (attachment.getSourceType()) {
                case BYTES:
                    helper.addAttachment(attachment.getFileName(), 
                        new ByteArrayResource(attachment.getContent()));
                    break;
                case INPUT_STREAM:
                    helper.addAttachment(attachment.getFileName(),
                        new InputStreamResource(attachment.getInputStream()));
                    break;
                case FILE_PATH:
                    helper.addAttachment(attachment.getFileName(), 
                        new FileSystemResource(attachment.getFilePath()));
                    break;
                default:
                    log.warn("不支持的附件源类型: {}", attachment.getSourceType());
            }
        }
    }

    private void addInlineResources(MimeMessageHelper helper, EmailMessage emailMessage) throws Exception {
        if (CollectionUtils.isEmpty(emailMessage.getInlineResources())) {
            return;
        }
        
        for (EmailInlineResource resource : emailMessage.getInlineResources()) {
            switch (resource.getSourceType()) {
                case BYTES:
                    helper.addInline(resource.getResourceId(),
                        new ByteArrayResource(resource.getContent()));
                    break;
                case INPUT_STREAM:
                    helper.addInline(resource.getResourceId(),
                        new InputStreamResource(resource.getInputStream()));
                    break;
                case FILE_PATH:
                    helper.addInline(resource.getResourceId(), 
                        new FileSystemResource(resource.getFilePath()));
                    break;
                default:
                    log.warn("不支持的内联资源源类型: {}", resource.getSourceType());
            }
        }
    }

    private EmailSendResult createFailureResult(EmailMessage emailMessage, Exception e) {
        EmailErrorCode errorCode = EmailErrorCode.SEND_FAILED;
        if (e instanceof EmailSendException ese) {
            errorCode = EmailErrorCode.fromCode(ese.getCode());
        } else if (e instanceof EmailTemplateException) {
            errorCode = EmailErrorCode.TEMPLATE_RENDER_ERROR;
        }
        
        return EmailSendResult.failure(emailMessage.getMessageId(), errorCode.getCode(), e.getMessage(), e);
    }
} 