package club.slavopolis.file.domain.result;

import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.enums.UploadMethod;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文件上传结果
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Data
@Accessors(chain = true)
public class FileUploadResult {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件信息
     */
    private FileInfo fileInfo;

    /**
     * 上传方式
     */
    private UploadMethod uploadMethod;

    /**
     * 是否为秒传
     */
    private Boolean isInstantUpload = false;

    /**
     * 上传耗时（毫秒）
     */
    private Long uploadDuration;

    /**
     * 上传开始时间
     */
    private LocalDateTime uploadStartTime;

    /**
     * 上传完成时间
     */
    private LocalDateTime uploadCompleteTime;

    /**
     * 上传速度（字节/秒）
     */
    private Long uploadSpeed;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 访问URL（如果可以直接访问）
     */
    private String accessUrl;

    /**
     * 下载URL（临时链接）
     */
    private String downloadUrl;

    /**
     * 扩展信息
     */
    private Map<String, Object> extensionInfo;

    /**
     * 创建成功结果
     *
     * @param fileId   文件ID
     * @param fileInfo 文件信息
     * @return 上传结果
     */
    public static FileUploadResult success(String fileId, FileInfo fileInfo) {
        return new FileUploadResult()
                .setSuccess(true)
                .setFileId(fileId)
                .setFileInfo(fileInfo)
                .setUploadCompleteTime(LocalDateTime.now());
    }

    /**
     * 创建失败结果
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误信息
     * @return 上传结果
     */
    public static FileUploadResult failure(String errorCode, String errorMessage) {
        return new FileUploadResult()
                .setSuccess(false)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage);
    }

    /**
     * 创建秒传结果
     *
     * @param fileId   文件ID
     * @param fileInfo 文件信息
     * @return 上传结果
     */
    public static FileUploadResult instantUpload(String fileId, FileInfo fileInfo) {
        return new FileUploadResult()
                .setSuccess(true)
                .setFileId(fileId)
                .setFileInfo(fileInfo)
                .setUploadMethod(UploadMethod.INSTANT)
                .setIsInstantUpload(true)
                .setUploadCompleteTime(LocalDateTime.now());
    }
} 