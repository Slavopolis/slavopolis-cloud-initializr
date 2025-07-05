package club.slavopolis.file.api;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.domain.request.ChunkUploadRequest;
import club.slavopolis.file.domain.request.FileListRequest;
import club.slavopolis.file.domain.request.FileUploadRequest;
import club.slavopolis.file.domain.result.ChunkUploadResult;
import club.slavopolis.file.domain.result.FileUploadResult;

/**
 * 统一文件服务接口
 * <p>
 * 提供文件上传、下载、删除等基本操作，支持分片上传和断点续传
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public interface FileService {

    // ================================ 基础操作 ================================

    /**
     * 上传文件
     *
     * @param request 上传请求
     * @return 上传结果
     */
    FileUploadResult upload(FileUploadRequest request);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件输入流
     */
    InputStream download(String fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean delete(String fileId);

    // ================================ 分片操作 ================================

    /**
     * 初始化分片上传
     *
     * @param request 上传请求
     * @return 上传会话ID
     */
    String initializeMultipartUpload(FileUploadRequest request);

    /**
     * 上传文件分片
     *
     * @param request 分片上传请求
     * @return 分片上传结果
     */
    ChunkUploadResult uploadChunk(ChunkUploadRequest request);

    /**
     * 完成分片上传
     *
     * @param uploadId 上传会话ID
     * @return 文件上传结果
     */
    FileUploadResult completeMultipartUpload(String uploadId);

    /**
     * 取消分片上传
     *
     * @param uploadId 上传会话ID
     * @return 是否取消成功
     */
    boolean abortMultipartUpload(String uploadId);

    // ================================ 元数据操作 ================================

    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfo getFileInfo(String fileId);

    /**
     * 列出文件
     *
     * @param request 列表请求
     * @return 文件列表
     */
    List<FileInfo> listFiles(FileListRequest request);

    /**
     * 获取文件列表总数
     *
     * @param request 列表请求
     * @return 符合条件的文件总数
     */
    long countFiles(FileListRequest request);

    // ================================ 高级功能 ================================

    /**
     * 生成下载链接
     *
     * @param fileId 文件ID
     * @param expiry 过期时间
     * @return 下载链接
     */
    String generateDownloadUrl(String fileId, Duration expiry);

    /**
     * 检查文件是否存在（基于哈希值）
     *
     * @param fileHash 文件哈希值
     * @return 文件ID（如果存在），否则返回null
     */
    String checkFileExists(String fileHash);

    /**
     * 复制文件
     *
     * @param sourceFileId 源文件ID
     * @param targetPath   目标路径
     * @return 新文件ID
     */
    String copyFile(String sourceFileId, String targetPath);

    /**
     * 移动文件
     *
     * @param fileId     文件ID
     * @param targetPath 目标路径
     * @return 是否移动成功
     */
    boolean moveFile(String fileId, String targetPath);
} 