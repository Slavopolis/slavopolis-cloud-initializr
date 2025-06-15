package club.slavopolis.infrastructure.integration.excel.core.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

import org.springframework.stereotype.Service;

import club.slavopolis.infrastructure.integration.excel.config.properties.ExcelProperties;
import club.slavopolis.infrastructure.integration.excel.core.ExcelService;
import club.slavopolis.infrastructure.integration.excel.enums.ExcelErrorCode;
import club.slavopolis.infrastructure.integration.excel.enums.ExcelTypeEnum;
import club.slavopolis.infrastructure.integration.excel.exception.ExcelException;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelFillRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelMultiSheetWriteRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelReadRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelWriteRequest;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelFillResult;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelReadResult;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelWriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel服务统一实现，整合读取、写入、填充功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final ExcelReaderService readerService;
    private final ExcelWriterService writerService;
    private final ExcelFillerService fillerService;
    private final ExcelProperties excelProperties;

    // 常量定义
    private static final String OUTPUT_STREAM = "OutputStream";
    private static final String INPUT_STREAM = "InputStream";
    private static final String DEFAULT_SHEET_NAME = "Sheet1";

    // ==================== 读取操作 ====================

    @Override
    public <T> ExcelReadResult<T> read(ExcelReadRequest<T> request) {
        log.info("开始读取Excel文件: {}", getRequestInfo(request));
        return readerService.read(request);
    }

    @Override
    public <T> CompletableFuture<ExcelReadResult<T>> readAsync(ExcelReadRequest<T> request) {
        log.info("开始异步读取Excel文件: {}", getRequestInfo(request));
        return readerService.readAsync(request);
    }

    @Override
    public <T> Stream<T> readAsStream(ExcelReadRequest<T> request) {
        log.info("开始流式读取Excel文件: {}", getRequestInfo(request));
        return readerService.readAsStream(request);
    }

    @Override
    public <T> ExcelReadResult<T> readMultiSheet(ExcelReadRequest<T> request) {
        log.info("开始读取多Sheet Excel文件: {}", getRequestInfo(request));
        return readerService.readMultiSheet(request);
    }

    // ==================== 写入操作 ====================

    @Override
    public ExcelWriteResult write(ExcelWriteRequest request) {
        log.info("开始写入Excel文件: {}", getRequestInfo(request));
        return writerService.write(request);
    }

    @Override
    public CompletableFuture<ExcelWriteResult> writeAsync(ExcelWriteRequest request) {
        log.info("开始异步写入Excel文件: {}", getRequestInfo(request));
        return writerService.writeAsync(request);
    }

    @Override
    public ExcelWriteResult writeMultiSheet(ExcelMultiSheetWriteRequest request) {
        log.info("开始写入多Sheet Excel文件: {}", getRequestInfo(request));
        return writerService.writeMultiSheet(request);
    }

    @Override
    public ExcelWriteResult writeStream(ExcelWriteRequest request) {
        log.info("开始流式写入Excel文件: {}", getRequestInfo(request));
        return writerService.writeStream(request);
    }

    // ==================== 填充操作 ====================

    @Override
    public ExcelFillResult fill(ExcelFillRequest request) {
        log.info("开始填充Excel文件: {}", getRequestInfo(request));
        return fillerService.fill(request);
    }

    @Override
    public CompletableFuture<ExcelFillResult> fillAsync(ExcelFillRequest request) {
        log.info("开始异步填充Excel文件: {}", getRequestInfo(request));
        return fillerService.fillAsync(request);
    }

    @Override
    public ExcelFillResult fillWithTemplate(String templateName, Object data) {
        log.info("开始基于模板[{}]填充Excel文件", templateName);
        return fillerService.fillWithTemplate(templateName, data);
    }

    // ==================== 工具方法 ====================

    @Override
    public boolean isValidExcelFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        
        try {
            ExcelTypeEnum.fromFileName(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean needAsyncProcess(long fileSize) {
        // 根据配置决定是否需要异步处理
        return excelProperties.needAsyncProcess(fileSize);
    }

    @Override
    public ExcelFileInfo getFileInfo(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new ExcelException(ExcelErrorCode.READ_FILE_ERROR, "文件不存在: " + filePath);
            }
            
            return new ExcelFileInfoImpl(path);
            
        } catch (ExcelException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取Excel文件信息失败: {}", e.getMessage(), e);
            throw new ExcelException(ExcelErrorCode.UNKNOWN_ERROR, "获取文件信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取请求信息字符串
     */
    private String getRequestInfo(Object request) {
        return switch (request) {
            case ExcelReadRequest<?> req -> String.format("请求ID=%s, 文件=%s, 类型=%s", 
                req.getRequestId(), 
                req.getFilePath() != null ? req.getFilePath() : INPUT_STREAM,
                req.getDataClass().getSimpleName());
            case ExcelWriteRequest req -> String.format("请求ID=%s, 文件=%s, 数据量=%d", 
                req.getRequestId(),
                req.getFilePath() != null ? req.getFilePath() : OUTPUT_STREAM, 
                req.getDataSize());
            case ExcelMultiSheetWriteRequest req -> String.format("请求ID=%s, 文件=%s, Sheet数=%d, 总数据量=%d", 
                req.getRequestId(),
                req.getFilePath() != null ? req.getFilePath() : OUTPUT_STREAM,
                req.getSheetCount(), req.getTotalDataSize());
            case ExcelFillRequest req -> String.format("请求ID=%s, 模板=%s, 输出=%s", 
                req.getRequestId(),
                req.getTemplatePath() != null ? req.getTemplatePath() : INPUT_STREAM,
                req.getOutputPath() != null ? req.getOutputPath() : OUTPUT_STREAM);
            default -> request.toString();
        };
    }

    /**
     * Excel文件信息实现
     */
    private static class ExcelFileInfoImpl implements ExcelFileInfo {
        private final long fileSize;
        private final ExcelTypeEnum excelType;
        private final String fileName;

        public ExcelFileInfoImpl(Path filePath) throws IOException {
            this.fileName = filePath.getFileName().toString();
            this.fileSize = Files.size(filePath);
            this.excelType = ExcelTypeEnum.fromFileName(fileName);
        }

        @Override
        public long getFileSize() {
            return fileSize;
        }

        @Override
        public int getSheetCount() {
            try (ExcelReader excelReader = EasyExcelFactory.read(fileName).build()) {
                // 使用EasyExcel获取Sheet数量
                List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();
                return readSheets.size();
            } catch (Exception e) {
                log.warn("获取Sheet数量失败，返回默认值1: {}", e.getMessage());
                return 1;
            }
        }

        @Override
        public List<String> getSheetNames() {
            try (ExcelReader excelReader = EasyExcelFactory.read(fileName).build()) {
                // 使用EasyExcel获取Sheet名称列表
                List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();
                
                return readSheets.stream()
                        .map(ReadSheet::getSheetName)
                        .toList();
            } catch (Exception e) {
                log.warn("获取Sheet名称列表失败，返回默认值: {}", e.getMessage());
                return List.of(DEFAULT_SHEET_NAME);
            }
        }

        @Override
        public ExcelTypeEnum getExcelType() {
            return excelType;
        }

        @Override
        public boolean isEmpty() {
            return fileSize == 0;
        }
    }

    /**
     * 注册模板
     */
    public void registerTemplate(String templateName, String templatePath) {
        fillerService.registerTemplate(templateName, templatePath);
        log.info("模板已注册到Excel服务: {} -> {}", templateName, templatePath);
    }

    /**
     * 移除模板
     */
    public void unregisterTemplate(String templateName) {
        fillerService.unregisterTemplate(templateName);
        log.info("模板已从Excel服务移除: {}", templateName);
    }

    /**
     * 获取已注册的模板列表
     */
    public java.util.Map<String, String> getRegisteredTemplates() {
        return fillerService.getRegisteredTemplates();
    }

    /**
     * 批量操作支持
     */
    public CompletableFuture<List<ExcelReadResult<Object>>> batchRead(
            List<ExcelReadRequest<Object>> requests) {
        
        return CompletableFuture.supplyAsync(() -> 
            requests.parallelStream()
                    .map(this::read)
                    .toList()
        );
    }

    /**
     * 批量写入支持
     */
    public CompletableFuture<List<ExcelWriteResult>> batchWrite(
            List<ExcelWriteRequest> requests) {
        
        return CompletableFuture.supplyAsync(() -> 
            requests.parallelStream()
                    .map(this::write)
                    .toList()
        );
    }

    /**
     * 批量填充支持
     */
    public java.util.Map<String, ExcelFillResult> batchFill(
            java.util.Map<String, ExcelFillRequest> requests) {
        
        return fillerService.batchFill(requests);
    }

    /**
     * 高级填充功能
     */
    public ExcelFillResult advancedFill(ExcelFillRequest request, java.util.Map<String, Object> extraData) {
        log.info("开始高级填充Excel文件: {}", getRequestInfo(request));
        return fillerService.advancedFill(request, extraData);
    }

    /**
     * 健康检查
     */
    public boolean healthCheck() {
        try {
            // 检查各个服务组件状态
            boolean readerOk = readerService != null;
            boolean writerOk = writerService != null;
            boolean fillerOk = fillerService != null;
            boolean propertiesOk = excelProperties != null;
            
            boolean healthy = readerOk && writerOk && fillerOk && propertiesOk;
            
            log.info("Excel服务健康检查: 读取器={}, 写入器={}, 填充器={}, 配置={}, 整体={}", 
                readerOk, writerOk, fillerOk, propertiesOk, healthy);
            
            return healthy;
            
        } catch (Exception e) {
            log.error("Excel服务健康检查失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取服务统计信息
     */
    public java.util.Map<String, Object> getStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 基本信息
        stats.put("version", "1.0.0");
        stats.put("healthCheck", healthCheck());
        stats.put("registeredTemplates", fillerService.getRegisteredTemplates().size());
        
        // 配置信息
        if (excelProperties != null) {
            stats.put("config", java.util.Map.of(
                "maxFileSize", excelProperties.getMaxFileSize(),
                "asyncThreshold", excelProperties.getAsyncThreshold(),
                "defaultBatchSize", excelProperties.getDefaultBatchSize(),
                "enableAsync", excelProperties.isEnableAsync(),
                "enableErrorCollection", excelProperties.isEnableErrorCollection()
            ));
        }
        
        // JVM信息
        Runtime runtime = Runtime.getRuntime();
        stats.put("jvm", java.util.Map.of(
            "maxMemory", runtime.maxMemory(),
            "totalMemory", runtime.totalMemory(),
            "freeMemory", runtime.freeMemory(),
            "processors", runtime.availableProcessors()
        ));
        
        return stats;
    }
} 