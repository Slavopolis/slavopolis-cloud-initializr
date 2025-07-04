package club.slavopolis.file.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 文件操作常量类
 * <p>
 * 定义文件操作相关的常量和错误代码
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileConstants {

    // ================================ 错误代码 ================================

    /**
     * 文件未找到
     */
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";

    /**
     * 文件过大
     */
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";

    /**
     * 文件类型不允许
     */
    public static final String FILE_TYPE_NOT_ALLOWED = "FILE_TYPE_NOT_ALLOWED";

    /**
     * 存储配额超出
     */
    public static final String STORAGE_QUOTA_EXCEEDED = "STORAGE_QUOTA_EXCEEDED";

    /**
     * 上传失败
     */
    public static final String UPLOAD_FAILED = "UPLOAD_FAILED";

    /**
     * 下载失败
     */
    public static final String DOWNLOAD_FAILED = "DOWNLOAD_FAILED";

    /**
     * 删除失败
     */
    public static final String DELETE_FAILED = "DELETE_FAILED";

    /**
     * 权限拒绝
     */
    public static final String PERMISSION_DENIED = "PERMISSION_DENIED";

    /**
     * 文件哈希值无效
     */
    public static final String INVALID_FILE_HASH = "INVALID_FILE_HASH";

    /**
     * 分片上传失败
     */
    public static final String CHUNK_UPLOAD_FAILED = "CHUNK_UPLOAD_FAILED";

    /**
     * 分片上传失败
     */
    public static final String MULTIPART_UPLOAD_FAILED = "MULTIPART_UPLOAD_FAILED";

    /**
     * 存储服务不可用
     */
    public static final String STORAGE_SERVICE_UNAVAILABLE = "STORAGE_SERVICE_UNAVAILABLE";

    // ================================ 默认值常量 ================================

    /**
     * 默认分片大小（5MB）
     */
    public static final long DEFAULT_CHUNK_SIZE = 5 * 1024 * 1024L;

    /**
     * 最大分片大小（100MB）
     */
    public static final long MAX_CHUNK_SIZE = 100 * 1024 * 1024L;

    /**
     * 最小分片大小（1MB）
     */
    public static final long MIN_CHUNK_SIZE = 1024 * 1024L;

    /**
     * 默认并发上传数
     */
    public static final int DEFAULT_CONCURRENT_UPLOADS = 3;

    /**
     * 最大并发上传数
     */
    public static final int MAX_CONCURRENT_UPLOADS = 10;

    /**
     * 默认文件过期时间（24小时）
     */
    public static final long DEFAULT_SESSION_EXPIRY_HOURS = 24;

    /**
     * 默认最大重试次数
     */
    public static final int DEFAULT_MAX_RETRIES = 3;

    // ================================ 文件类型常量 ================================

    /**
     * 图片文件扩展名
     */
    protected static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"};

    /**
     * 文档文件扩展名
     */
    protected static final String[] DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf"};

    /**
     * 视频文件扩展名
     */
    protected static final String[] VIDEO_EXTENSIONS = {"mp4", "avi", "mov", "wmv", "flv", "webm", "mkv"};

    /**
     * 音频文件扩展名
     */
    protected static final String[] AUDIO_EXTENSIONS = {"mp3", "wav", "aac", "flac", "ogg", "wma"};

    /**
     * 压缩文件扩展名
     */
    protected static final String[] ARCHIVE_EXTENSIONS = {"zip", "rar", "7z", "tar", "gz", "bz2"};

    /**
     * 危险文件扩展名（禁止上传）
     */
    protected static final String[] DANGEROUS_EXTENSIONS = {"exe", "bat", "cmd", "sh", "com", "scr", "pif", "vbs", "js"};

    // ================================ 存储路径常量 ================================

    /**
     * 文件路径分隔符
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * 临时文件前缀
     */
    public static final String TEMP_FILE_PREFIX = "temp_";

    /**
     * 分片文件前缀
     */
    public static final String CHUNK_FILE_PREFIX = "chunk_";

    /**
     * 默认上传目录
     */
    public static final String DEFAULT_UPLOAD_DIR = "uploads";

    /**
     * 默认临时目录
     */
    public static final String DEFAULT_TEMP_DIR = "temp";

    // ================================ 其他常量 ================================

    /**
     * 默认页面大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 文件ID长度
     */
    public static final int FILE_ID_LENGTH = 32;

    /**
     * 上传会话ID长度
     */
    public static final int UPLOAD_SESSION_ID_LENGTH = 32;
} 