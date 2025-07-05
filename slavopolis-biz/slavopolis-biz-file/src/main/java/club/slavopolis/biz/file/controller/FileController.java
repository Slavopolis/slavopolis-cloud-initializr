package club.slavopolis.biz.file.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import club.slavopolis.base.enums.BizErrorCode;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.biz.file.dto.FileInfoDTO;
import club.slavopolis.biz.file.dto.FileListQueryDTO;
import club.slavopolis.biz.file.dto.FileUploadDTO;
import club.slavopolis.biz.file.mapping.FileMapping;
import club.slavopolis.file.api.FileService;
import club.slavopolis.file.domain.FileInfo;
import club.slavopolis.file.domain.request.FileListRequest;
import club.slavopolis.file.domain.request.FileUploadRequest;
import club.slavopolis.file.domain.result.FileUploadResult;
import club.slavopolis.web.vo.MultiResult;
import club.slavopolis.web.vo.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件服务控制器
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
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileMapping fileMapping;
    private final CurrentSystemProperties systemProperties;

    /**
     * 上传文件
     *
     * @param file      文件
     * @param uploadDTO 上传信息
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<FileInfoDTO> uploadFile(@RequestParam("file") @NotNull MultipartFile file,
                                          @Valid FileUploadDTO uploadDTO) throws IOException {
        if (systemProperties.isDebug()) {
            log.info("上传文件开始: 文件名={}, 大小={}, 类型={}", file.getOriginalFilename(), file.getSize(), file.getContentType());
        }

        FileUploadRequest request = new FileUploadRequest();
        request.setOriginalName(file.getOriginalFilename());
        request.setFileSize(file.getSize());
        request.setContentType(file.getContentType());
        request.setInputStream(file.getInputStream());
        request.setCreatedBy(uploadDTO.getCreatedBy());
        request.setTenantId(uploadDTO.getTenantId());
        request.setDescription(uploadDTO.getDescription());

        FileUploadResult response = fileService.upload(request);
        FileInfoDTO fileInfoDTO = fileMapping.to(response.getFileInfo());
        
        if (systemProperties.isDebug()) {
            log.info("上传文件完成: 文件ID={}, 文件名={}", response.getFileId(), response.getFileInfo().getOriginalName());
        }

        return Result.success(fileInfoDTO);
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件流
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable @NotBlank String fileId) {
        if (systemProperties.isDebug()) {
            log.info("下载文件开始: 文件ID={}", fileId);
        }
        
        try {
            InputStream content = fileService.download(fileId);
            FileInfo fileInfo = fileService.getFileInfo(fileId);
            if (Objects.isNull(fileInfo)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = content.readAllBytes();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileInfo.getContentType()));
            headers.setContentDispositionFormData("attachment", fileInfo.getOriginalName());
            headers.setContentLength(fileContent.length);
            
            if (systemProperties.isDebug()) {
                log.info("下载文件完成: 文件ID={}, 文件名={}, 大小={}", fileId, fileInfo.getOriginalName(), fileContent.length);
            }
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("下载文件失败: 文件ID={}, 错误={}", fileId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{fileId}")
    public Result<Boolean> deleteFile(@PathVariable @NotBlank String fileId) {
        if (systemProperties.isDebug()) {
            log.info("删除文件开始: 文件ID={}", fileId);
        }
        
        try {
            boolean deleted = fileService.delete(fileId);
            if (systemProperties.isDebug()) {
                log.info("删除文件完成: 文件ID={}, 结果={}", fileId, deleted);
            }
            return Result.success(deleted);
        } catch (Exception e) {
            log.error("删除文件失败: 文件ID={}, 错误={}", fileId, e.getMessage());
            return Result.error(BizErrorCode.FILE_DELETE_FAILED.getCode(), "删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    @GetMapping("/{fileId}")
    public Result<FileInfoDTO> getFileInfo(@PathVariable @NotBlank String fileId) {
        if (systemProperties.isDebug()) {
            log.info("获取文件信息: 文件ID={}", fileId);
        }
        
        try {
            FileInfo fileInfo = fileService.getFileInfo(fileId);
            if (fileInfo == null) {
                return Result.error(BizErrorCode.FILE_NOT_FOUND.getCode(), "文件不存在");
            }
            
            FileInfoDTO fileInfoDTO = fileMapping.to(fileInfo);
            return Result.success(fileInfoDTO);
        } catch (Exception e) {
            log.error("获取文件信息失败: 文件ID={}, 错误={}", fileId, e.getMessage());
            return Result.error(BizErrorCode.FILE_INFO_ERROR.getCode(), "获取文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 查询文件列表
     *
     * @param queryDTO 查询条件
     * @return 文件列表
     */
    @GetMapping("/list")
    public MultiResult<FileInfoDTO> getFileList(@Valid FileListQueryDTO queryDTO) {
        if (systemProperties.isDebug()) {
            log.info("查询文件列表: 条件={}", queryDTO);
        }
        
        try {
            FileListRequest request = new FileListRequest();
            request.setCreatedBy(queryDTO.getCreatedBy());
            request.setTenantId(queryDTO.getTenantId());
            request.setFileName(queryDTO.getFileName());
            request.setPageNumber(queryDTO.getPageNumber());
            request.setPageSize(queryDTO.getPageSize());

            List<FileInfo> fileInfoList = fileService.listFiles(request);
            long totalCount = fileService.countFiles(request);
            
            List<FileInfoDTO> fileInfos = fileInfoList.stream()
                    .map(fileMapping::to)
                    .toList();
            
            return MultiResult.successMulti(fileInfos, totalCount, queryDTO.getPageNumber(), queryDTO.getPageSize());
        } catch (Exception e) {
            log.error("查询文件列表失败: 条件={}, 错误={}", queryDTO, e.getMessage());
            return MultiResult.errorMulti(BizErrorCode.FILE_LIST_ERROR.getCode(), "查询文件列表失败: " + e.getMessage());
        }
    }
} 