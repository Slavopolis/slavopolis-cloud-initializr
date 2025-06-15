package club.slavopolis.infrastructure.messaging.email.model;

import club.slavopolis.infrastructure.messaging.email.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * 邮件附件模型类 - 封装邮件附件信息
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
public class EmailAttachment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 附件名称
     */
    @NotBlank(message = "附件名称不能为空")
    private String fileName;

    /**
     * 附件大小（字节）
     */
    private Long fileSize;

    /**
     * 内容类型（MIME类型）
     */
    private String contentType;

    /**
     * 附件内容（字节数组）
     */
    private byte[] content;

    /**
     * 附件输入流
     */
    private transient InputStream inputStream;

    /**
     * 文件路径（用于文件系统中的文件）
     */
    private String filePath;

    /**
     * 附件描述
     */
    private String description;

    /**
     * 是否为内联附件
     */
    private boolean inline = false;

    /**
     * 内联附件的Content-ID
     */
    private String contentId;

    /**
     * 附件来源类型
     */
    private SourceType sourceType = SourceType.BYTES;

    /**
     * 便捷创建方法 - 字节数组附件
     */
    public static EmailAttachment fromBytes(String fileName, byte[] content, String contentType) {
        return EmailAttachment.builder()
                .fileName(fileName)
                .content(content)
                .contentType(contentType)
                .fileSize((long) content.length)
                .sourceType(SourceType.BYTES)
                .build();
    }

    /**
     * 便捷创建方法 - 输入流附件
     */
    public static EmailAttachment fromInputStream(String fileName, InputStream inputStream, String contentType) {
        return EmailAttachment.builder()
                .fileName(fileName)
                .inputStream(inputStream)
                .contentType(contentType)
                .sourceType(SourceType.INPUT_STREAM)
                .build();
    }

    /**
     * 便捷创建方法 - 文件路径附件
     */
    public static EmailAttachment fromFilePath(String fileName, String filePath, String contentType) {
        return EmailAttachment.builder()
                .fileName(fileName)
                .filePath(filePath)
                .contentType(contentType)
                .sourceType(SourceType.FILE_PATH)
                .build();
    }

    /**
     * 便捷创建方法 - 内联附件
     */
    public static EmailAttachment inline(String fileName, byte[] content, String contentType, String contentId) {
        return EmailAttachment.builder()
                .fileName(fileName)
                .content(content)
                .contentType(contentType)
                .fileSize((long) content.length)
                .inline(true)
                .contentId(contentId)
                .sourceType(SourceType.BYTES)
                .build();
    }
} 