package club.slavopolis.file.util;

import club.slavopolis.base.utils.DateUtil;
import club.slavopolis.file.constant.FileConstants;
import club.slavopolis.file.exception.FileOperationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件工具类
 * <p>
 * 提供文件操作相关的通用工具方法
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.COMPACT_DATE_SLASH);
    private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("#,##0.#");

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名（小写，不包含点）
     */
    public static String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalName 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalName) {
        String extension = getFileExtension(originalName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        return StringUtils.hasText(extension) ? uuid + "." + extension : uuid;
    }

    /**
     * 生成基于日期的存储路径
     *
     * @param fileName 文件名
     * @return 存储路径
     */
    public static String generateDateBasedPath(String fileName) {
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        return datePath + FileConstants.PATH_SEPARATOR + fileName;
    }

    /**
     * 生成基于哈希的存储路径
     *
     * @param fileHash 文件哈希值
     * @param fileName 文件名
     * @return 存储路径
     */
    public static String generateHashBasedPath(String fileHash, String fileName) {
        if (!StringUtils.hasText(fileHash) || fileHash.length() < 4) {
            return generateDateBasedPath(fileName);
        }
        
        // 使用哈希的前两位和第3-4位创建两级目录
        String level1 = fileHash.substring(0, 2);
        String level2 = fileHash.substring(2, 4);
        
        return level1 + FileConstants.PATH_SEPARATOR + level2 + FileConstants.PATH_SEPARATOR + fileName;
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的大小字符串
     */
    public static String formatFileSize(long size) {
        if (size < 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        int unitIndex = 0;
        double fileSize = size;
        
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        return SIZE_FORMAT.format(fileSize) + " " + units[unitIndex];
    }

    /**
     * 检查文件类型是否为图片
     *
     * @param extension 文件扩展名
     * @return 是否为图片类型
     */
    public static boolean isImageFile(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        return Arrays.stream(FileConstants.IMAGE_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 检查文件类型是否为文档
     *
     * @param extension 文件扩展名
     * @return 是否为文档类型
     */
    public static boolean isDocumentFile(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        return Arrays.stream(FileConstants.DOCUMENT_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 检查文件类型是否为视频
     *
     * @param extension 文件扩展名
     * @return 是否为视频类型
     */
    public static boolean isVideoFile(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        return Arrays.stream(FileConstants.VIDEO_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 检查文件类型是否为音频
     *
     * @param extension 文件扩展名
     * @return 是否为音频类型
     */
    public static boolean isAudioFile(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        return Arrays.stream(FileConstants.AUDIO_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 检查文件类型是否为压缩文件
     *
     * @param extension 文件扩展名
     * @return 是否为压缩文件类型
     */
    public static boolean isArchiveFile(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        return Arrays.stream(FileConstants.ARCHIVE_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 检查文件类型是否为危险文件
     *
     * @param extension 文件扩展名
     * @return 是否为危险文件类型
     */
    public static boolean isDangerousFile(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        return Arrays.stream(FileConstants.DANGEROUS_EXTENSIONS)
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }

    /**
     * 计算文件MD5哈希值
     *
     * @param inputStream 文件输入流
     * @return MD5哈希值
     * @throws FileOperationException 计算哈希值失败时抛出
     */
    public static String calculateMd5(InputStream inputStream) {
        return calculateHash(inputStream, "MD5");
    }

    /**
     * 计算文件SHA256哈希值
     *
     * @param inputStream 文件输入流
     * @return SHA256哈希值
     * @throws FileOperationException 计算哈希值失败时抛出
     */
    public static String calculateSha256(InputStream inputStream) {
        return calculateHash(inputStream, "SHA-256");
    }

    /**
     * 计算文件哈希值的通用方法
     *
     * @param inputStream 文件输入流
     * @param algorithm   哈希算法
     * @return 哈希值
     * @throws FileOperationException 计算哈希值失败时抛出
     */
    private static String calculateHash(InputStream inputStream, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            log.error("计算文件{}失败", algorithm, e);
            throw new FileOperationException(
                FileConstants.STORAGE_SERVICE_UNAVAILABLE,
                "计算文件" + algorithm + "失败: " + e.getMessage(), 
                e
            );
        }
    }

    /**
     * 验证文件名是否合法
     *
     * @param fileName 文件名
     * @return 是否合法
     */
    public static boolean isValidFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        
        // 检查文件名长度
        if (fileName.length() > 255) {
            return false;
        }
        
        // 检查非法字符
        char[] illegalChars = {'/', '\\', ':', '*', '?', '"', '<', '>', '|'};
        for (char illegalChar : illegalChars) {
            if (fileName.indexOf(illegalChar) != -1) {
                return false;
            }
        }
        
        // 检查是否以点开头或结尾
        return !fileName.startsWith(".") && !fileName.endsWith(".");
    }

    /**
     * 清理文件名
     *
     * @param fileName 原始文件名
     * @return 清理后的文件名
     */
    public static String sanitizeFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "unnamed";
        }
        
        // 替换非法字符
        String sanitized = fileName.replaceAll("[/\\\\:*?\"<>|]", "_");
        
        // 去除开头和结尾的点
        sanitized = sanitized.replaceAll("(^\\.*|\\.*$)", "");
        
        // 如果清理后为空，使用默认名称
        if (!StringUtils.hasText(sanitized)) {
            sanitized = "unnamed";
        }
        
        // 限制长度
        if (sanitized.length() > 255) {
            String extension = getFileExtension(sanitized);
            String nameWithoutExt = getFileNameWithoutExtension(sanitized);
            
            int maxNameLength = 255 - (StringUtils.hasText(extension) ? extension.length() + 1 : 0);
            nameWithoutExt = nameWithoutExt.substring(0, Math.min(nameWithoutExt.length(), maxNameLength));
            
            sanitized = StringUtils.hasText(extension) ? nameWithoutExt + "." + extension : nameWithoutExt;
        }
        
        return sanitized;
    }

    /**
     * 复制输入流到字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     */
    public static byte[] copyToByteArray(InputStream inputStream) throws IOException {
        return inputStream.readAllBytes();
    }

    /**
     * 获取文件类型分类
     *
     * @param extension 文件扩展名
     * @return 文件类型分类
     */
    public static String getFileCategory(String extension) {
        if (isImageFile(extension)) {
            return "image";
        } else if (isDocumentFile(extension)) {
            return "document";
        } else if (isVideoFile(extension)) {
            return "video";
        } else if (isAudioFile(extension)) {
            return "audio";
        } else if (isArchiveFile(extension)) {
            return "archive";
        } else {
            return "other";
        }
    }
} 