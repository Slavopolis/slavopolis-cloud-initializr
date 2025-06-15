package club.slavopolis.infrastructure.messaging.email.model;

import club.slavopolis.infrastructure.messaging.email.enums.SendStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 邮件发送结果模型类 - 封装邮件发送的状态和结果信息
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
public class EmailSendResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邮件唯一标识
     */
    private String messageId;

    /**
     * 发送状态
     */
    private SendStatus status;

    /**
     * 是否发送成功
     */
    private boolean success;

    /**
     * 错误码（失败时）
     */
    private Integer errorCode;

    /**
     * 错误信息（失败时）
     */
    private String errorMessage;

    /**
     * 详细错误信息
     */
    private String detailErrorMessage;

    /**
     * 发送开始时间
     */
    private LocalDateTime sendStartTime;

    /**
     * 发送完成时间
     */
    private LocalDateTime sendEndTime;

    /**
     * 发送耗时（毫秒）
     */
    private Long sendDuration;

    /**
     * 收件人数量
     */
    private Integer recipientCount;

    /**
     * 成功发送的收件人数量
     */
    private Integer successRecipientCount;

    /**
     * 失败的收件人数量
     */
    private Integer failedRecipientCount;

    /**
     * 失败的收件人列表
     */
    private java.util.List<String> failedRecipients;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * 服务器响应信息
     */
    private String serverResponse;

    /**
     * 邮件大小（字节）
     */
    private Long emailSize;

    /**
     * 业务标识
     */
    private String businessTag;

    /**
     * 追踪标识
     */
    private String trackingId;

    /**
     * 扩展属性
     */
    private transient Map<String, Object> extraProperties;

    /**
     * 创建成功结果
     */
    public static EmailSendResult success(String messageId) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.SUCCESS)
                .success(true)
                .sendEndTime(LocalDateTime.now())
                .recipientCount(1)
                .successRecipientCount(1)
                .failedRecipientCount(0)
                .retryCount(0)
                .build();
    }

    /**
     * 创建成功结果（带收件人数量）
     */
    public static EmailSendResult success(String messageId, int recipientCount) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.SUCCESS)
                .success(true)
                .sendEndTime(LocalDateTime.now())
                .recipientCount(recipientCount)
                .successRecipientCount(recipientCount)
                .failedRecipientCount(0)
                .retryCount(0)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static EmailSendResult failure(String messageId, int errorCode, String errorMessage) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.FAILED)
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .sendEndTime(LocalDateTime.now())
                .recipientCount(1)
                .successRecipientCount(0)
                .failedRecipientCount(1)
                .retryCount(0)
                .build();
    }

    /**
     * 创建失败结果（带异常）
     */
    public static EmailSendResult failure(String messageId, int errorCode, String errorMessage, Throwable cause) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.FAILED)
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .detailErrorMessage(cause != null ? cause.getMessage() : null)
                .sendEndTime(LocalDateTime.now())
                .recipientCount(1)
                .successRecipientCount(0)
                .failedRecipientCount(1)
                .retryCount(0)
                .build();
    }

    /**
     * 创建部分成功结果
     */
    public static EmailSendResult partialSuccess(String messageId, int totalRecipients, int successCount, java.util.List<String> failedRecipients) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.PARTIAL_SUCCESS)
                .success(successCount > 0)
                .sendEndTime(LocalDateTime.now())
                .recipientCount(totalRecipients)
                .successRecipientCount(successCount)
                .failedRecipientCount(totalRecipients - successCount)
                .failedRecipients(failedRecipients)
                .retryCount(0)
                .build();
    }

    /**
     * 创建待发送结果
     */
    public static EmailSendResult pending(String messageId) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.PENDING)
                .success(false)
                .sendStartTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }

    /**
     * 创建发送中结果
     */
    public static EmailSendResult sending(String messageId) {
        return EmailSendResult.builder()
                .messageId(messageId)
                .status(SendStatus.SENDING)
                .success(false)
                .sendStartTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }

    /**
     * 计算发送耗时
     */
    public void calculateDuration() {
        if (sendStartTime != null && sendEndTime != null) {
            this.sendDuration = java.time.Duration.between(sendStartTime, sendEndTime).toMillis();
        }
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null) ? 1 : this.retryCount + 1;
    }

    /**
     * 判断是否可以重试
     */
    public boolean canRetry() {
        if (maxRetries == null || retryCount == null) {
            return false;
        }
        return retryCount < maxRetries && (status == SendStatus.FAILED || status == SendStatus.TIMEOUT);
    }

    /**
     * 判断是否最终失败（无法重试）
     */
    public boolean isFinalFailure() {
        return !success && !canRetry() && status != SendStatus.PENDING && status != SendStatus.SENDING && status != SendStatus.RETRY;
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (recipientCount == null || recipientCount == 0) {
            return 0.0;
        }
        int successCount = successRecipientCount != null ? successRecipientCount : 0;
        return (double) successCount / recipientCount * 100.0;
    }

    /**
     * 设置扩展属性
     */
    public EmailSendResult setExtraProperty(String key, Object value) {
        if (this.extraProperties == null) {
            this.extraProperties = new java.util.HashMap<>();
        }
        this.extraProperties.put(key, value);
        return this;
    }

    /**
     * 获取扩展属性
     */
    public Object getExtraProperty(String key) {
        return extraProperties != null ? extraProperties.get(key) : null;
    }
} 