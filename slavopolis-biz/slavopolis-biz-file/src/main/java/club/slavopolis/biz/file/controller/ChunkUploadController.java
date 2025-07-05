package club.slavopolis.biz.file.controller;

import java.io.IOException;

import club.slavopolis.base.enums.BizErrorCode;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.biz.file.mapping.FileMapping;
import club.slavopolis.web.vo.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import club.slavopolis.biz.file.dto.ChunkUploadDTO;
import club.slavopolis.biz.file.dto.FileInfoDTO;
import club.slavopolis.biz.file.dto.MultipartUploadDTO;
import club.slavopolis.file.api.FileService;
import club.slavopolis.file.domain.request.ChunkUploadRequest;
import club.slavopolis.file.domain.request.FileUploadRequest;
import club.slavopolis.file.domain.result.ChunkUploadResult;
import club.slavopolis.file.domain.result.FileUploadResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 分片上传控制器
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/files/multipart")
@RequiredArgsConstructor
public class ChunkUploadController {

    private final FileService fileService;
    private final FileMapping fileMapping;
    private final CurrentSystemProperties systemProperties;

    /**
     * 初始化分片上传
     *
     * @param uploadDTO 上传信息
     * @return 初始化结果
     */
    @PostMapping("/init")
    public Result<String> initMultipartUpload(@Valid @RequestBody MultipartUploadDTO uploadDTO) {
        if (systemProperties.isDebug()) {
            log.info("初始化分片上传: 文件名={}, 大小={}, 创建者={}", uploadDTO.getFileName(), uploadDTO.getFileSize(), uploadDTO.getCreatedBy());
        }

        FileUploadRequest request = new FileUploadRequest();
        request.setOriginalName(uploadDTO.getFileName());
        request.setFileSize(uploadDTO.getFileSize());
        request.setContentType(uploadDTO.getContentType());
        request.setCreatedBy(uploadDTO.getCreatedBy());
        request.setTenantId(uploadDTO.getTenantId());
        request.setDescription(uploadDTO.getDescription());

        String uploadId = fileService.initializeMultipartUpload(request);
        
        if (systemProperties.isDebug()) {
            log.info("初始化分片上传完成: 上传ID={}, 文件名={}", uploadId, uploadDTO.getFileName());
        }
        
        return Result.success(uploadId);
    }

    /**
     * 上传分片
     *
     * @param file     分片文件
     * @param chunkDTO 分片信息
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<String> uploadChunk(@RequestParam("file") @NotNull MultipartFile file,
            @Valid ChunkUploadDTO chunkDTO) throws IOException {
        
        if (systemProperties.isDebug()) {
            log.info("上传分片: 上传ID={}, 分片索引={}, 大小={}",
                    chunkDTO.getUploadId(), chunkDTO.getChunkIndex(), file.getSize());
        }

        ChunkUploadRequest request = new ChunkUploadRequest();
        request.setUploadId(chunkDTO.getUploadId());
        request.setChunkIndex(chunkDTO.getChunkIndex());
        request.setChunkSize(Math.toIntExact(file.getSize()));
        request.setChunkStream(file.getInputStream());
        request.setChunkHash(chunkDTO.getChunkHash());

        ChunkUploadResult result = fileService.uploadChunk(request);
        
        if (systemProperties.isDebug()) {
            log.info("上传分片完成: 上传ID={}, 分片索引={}, 结果={}",
                    chunkDTO.getUploadId(), chunkDTO.getChunkIndex(), result.getChunkId());
        }
        
        return Result.success(result.getChunkId());
    }

    /**
     * 完成分片上传
     *
     * @param uploadId 上传ID
     * @return 完成结果
     */
    @PostMapping("/complete/{uploadId}")
    public Result<FileInfoDTO> completeMultipartUpload(@PathVariable @NotBlank String uploadId) {
        if (systemProperties.isDebug()) {
            log.info("完成分片上传: 上传ID={}", uploadId);
        }
        
        try {
            FileUploadResult result = fileService.completeMultipartUpload(uploadId);
            FileInfoDTO fileInfoDTO = fileMapping.to(result.getFileInfo());
            
            if (systemProperties.isDebug()) {
                log.info("完成分片上传成功: 上传ID={}, 文件ID={}, 文件名={}",
                        uploadId, result.getFileId(), result.getFileInfo().getOriginalName());
            }
            
            return Result.success(fileInfoDTO);
        } catch (Exception e) {
            log.error("完成分片上传失败: 上传ID={}, 错误={}", uploadId, e.getMessage());
            return Result.error(BizErrorCode.FILE_MULTIPART_COMPLETE_FAILED.getCode(), "完成分片上传失败: " + e.getMessage());
        }
    }

    /**
     * 取消分片上传
     *
     * @param uploadId 上传ID
     * @return 取消结果
     */
    @DeleteMapping("/cancel/{uploadId}")
    public Result<Boolean> cancelMultipartUpload(@PathVariable @NotBlank String uploadId) {
        if (systemProperties.isDebug()) {
            log.info("取消分片上传: 上传ID={}", uploadId);
        }
        
        try {
            boolean cancelled = fileService.abortMultipartUpload(uploadId);
            if (systemProperties.isDebug()) {
                log.info("取消分片上传完成: 上传ID={}, 结果={}", uploadId, cancelled);
            }
            return Result.success(cancelled);
        } catch (Exception e) {
            log.error("取消分片上传失败: 上传ID={}, 错误={}", uploadId, e.getMessage());
            return Result.error(BizErrorCode.FILE_MULTIPART_ABORT_FAILED.getCode(), "取消分片上传失败: " + e.getMessage());
        }
    }
} 