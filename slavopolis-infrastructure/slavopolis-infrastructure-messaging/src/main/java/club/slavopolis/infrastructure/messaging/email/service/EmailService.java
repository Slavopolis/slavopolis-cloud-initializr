package club.slavopolis.infrastructure.messaging.email.service;

import club.slavopolis.infrastructure.messaging.email.model.EmailMessage;
import club.slavopolis.infrastructure.messaging.email.model.EmailSendResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 邮件服务核心接口 - 定义邮件发送、管理和监控的所有功能
 * <p>
 * <ul>
 *   <li>支持基础发送功能</li>
 *   <li>支持异步发送功能</li>
 *   <li>支持批量发送功能</li>
 *   <li>支持模板邮件功能</li>
 *   <li>支持发送状态查询</li>
 * </ul>
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.service
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface EmailService {

    // ==================== 基础发送功能 ====================
    
    /**
     * 发送简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param text    内容
     * @return 发送结果
     */
    EmailSendResult sendSimpleText(String to, String subject, String text);

    /**
     * 发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param html    HTML内容
     * @return 发送结果
     */
    EmailSendResult sendHtmlEmail(String to, String subject, String html);

    /**
     * 发送邮件（完整配置）
     *
     * @param emailMessage 邮件消息
     * @return 发送结果
     */
    EmailSendResult sendEmail(EmailMessage emailMessage);

    // ==================== 异步发送功能 ====================

    /**
     * 异步发送简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param text    内容
     * @return 异步发送结果
     */
    CompletableFuture<EmailSendResult> sendSimpleTextAsync(String to, String subject, String text);

    /**
     * 异步发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param html    HTML内容
     * @return 异步发送结果
     */
    CompletableFuture<EmailSendResult> sendHtmlEmailAsync(String to, String subject, String html);

    /**
     * 异步发送邮件
     *
     * @param emailMessage 邮件消息
     * @return 异步发送结果
     */
    CompletableFuture<EmailSendResult> sendEmailAsync(EmailMessage emailMessage);

    // ==================== 批量发送功能 ====================

    /**
     * 批量发送邮件
     *
     * @param emailMessages 邮件消息列表
     * @return 批量发送结果
     */
    List<EmailSendResult> sendBatchEmails(List<EmailMessage> emailMessages);

    /**
     * 异步批量发送邮件
     *
     * @param emailMessages 邮件消息列表
     * @return 异步批量发送结果
     */
    CompletableFuture<List<EmailSendResult>> sendBatchEmailsAsync(List<EmailMessage> emailMessages);

    // ==================== 模板邮件功能 ====================

    /**
     * 发送模板邮件
     *
     * @param to           收件人
     * @param subject      主题
     * @param templateName 模板名称
     * @param params       模板参数
     * @return 发送结果
     */
    EmailSendResult sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> params);

    /**
     * 异步发送模板邮件
     *
     * @param to           收件人
     * @param subject      主题
     * @param templateName 模板名称
     * @param params       模板参数
     * @return 异步发送结果
     */
    CompletableFuture<EmailSendResult> sendTemplateEmailAsync(String to, String subject, String templateName, Map<String, Object> params);

    /**
     * 批量发送模板邮件（相同模板，不同参数）
     *
     * @param recipients   收件人和参数映射
     * @param subject      主题
     * @param templateName 模板名称
     * @return 批量发送结果
     */
    List<EmailSendResult> sendBatchTemplateEmails(Map<String, Map<String, Object>> recipients, String subject, String templateName);

    // ==================== 状态查询功能 ====================

    /**
     * 查询邮件发送状态
     *
     * @param messageId 邮件ID
     * @return 发送结果
     */
    EmailSendResult getEmailStatus(String messageId);

    /**
     * 查询多个邮件发送状态
     *
     * @param messageIds 邮件ID列表
     * @return 发送结果列表
     */
    List<EmailSendResult> getBatchEmailStatus(List<String> messageIds);



    // ==================== 配置和管理功能 ====================

    /**
     * 测试邮件服务连接
     *
     * @return 是否连接成功
     */
    boolean testConnection();

    /**
     * 验证邮件地址格式
     *
     * @param email 邮件地址
     * @return 是否有效
     */
    boolean validateEmail(String email);

    /**
     * 获取服务状态
     *
     * @return 服务状态信息
     */
    EmailServiceStatus getServiceStatus();

    /**
     * 服务状态信息
     */
    record EmailServiceStatus(
            boolean isEnabled,      // 是否启用
            boolean isConnected,    // 是否连接
            String serverInfo,      // 服务器信息
            long uptime,            // 运行时间（毫秒）
            String version          // 版本信息
    ) {}
} 