package club.slavopolis.email.model;

import club.slavopolis.email.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * 邮件内嵌资源模型类 - 封装邮件中的内嵌资源（如图片）
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
public class EmailInlineResource implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资源ID（在HTML中引用时使用，如：cid:imageId）
     */
    @NotBlank(message = "资源ID不能为空")
    private String resourceId;

    /**
     * 资源名称
     */
    @NotBlank(message = "资源名称不能为空")
    private String resourceName;

    /**
     * 资源大小（字节）
     */
    private Long resourceSize;

    /**
     * 内容类型（MIME类型）
     */
    private String contentType;

    /**
     * 资源内容（字节数组）
     */
    private byte[] content;

    /**
     * 资源输入流
     */
    private transient InputStream inputStream;

    /**
     * 文件路径（用于文件系统中的文件）
     */
    private String filePath;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 资源来源类型
     */
    private SourceType sourceType = SourceType.BYTES;

    /**
     * 便捷创建方法 - 字节数组资源
     */
    public static EmailInlineResource fromBytes(String resourceId, String resourceName, byte[] content, String contentType) {
        return EmailInlineResource.builder()
                .resourceId(resourceId)
                .resourceName(resourceName)
                .content(content)
                .contentType(contentType)
                .resourceSize((long) content.length)
                .sourceType(SourceType.BYTES)
                .build();
    }

    /**
     * 便捷创建方法 - 输入流资源
     */
    public static EmailInlineResource fromInputStream(String resourceId, String resourceName, InputStream inputStream, String contentType) {
        return EmailInlineResource.builder()
                .resourceId(resourceId)
                .resourceName(resourceName)
                .inputStream(inputStream)
                .contentType(contentType)
                .sourceType(SourceType.INPUT_STREAM)
                .build();
    }

    /**
     * 便捷创建方法 - 文件路径资源
     */
    public static EmailInlineResource fromFilePath(String resourceId, String resourceName, String filePath, String contentType) {
        return EmailInlineResource.builder()
                .resourceId(resourceId)
                .resourceName(resourceName)
                .filePath(filePath)
                .contentType(contentType)
                .sourceType(SourceType.FILE_PATH)
                .build();
    }

    /**
     * 便捷创建方法 - 图片资源
     */
    public static EmailInlineResource image(String resourceId, String imageName, byte[] imageData) {
        String contentType = determineImageContentType(imageName);
        return fromBytes(resourceId, imageName, imageData, contentType);
    }

    /**
     * 根据文件名确定图片内容类型
     */
    private static String determineImageContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String lowerCaseName = fileName.toLowerCase();
        if (lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCaseName.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerCaseName.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerCaseName.endsWith(".svg")) {
            return "image/svg+xml";
        }
        
        return "application/octet-stream";
    }
} 