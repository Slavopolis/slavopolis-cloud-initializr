package club.slavopolis.infrastructure.integration.excel.core.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import org.springframework.stereotype.Service;

import club.slavopolis.infrastructure.integration.excel.core.processor.DataProcessor;
import club.slavopolis.infrastructure.integration.excel.core.processor.ProcessContext;
import club.slavopolis.infrastructure.integration.excel.core.processor.ProcessResult;
import club.slavopolis.infrastructure.integration.excel.enums.ExcelErrorCode;
import club.slavopolis.infrastructure.integration.excel.exception.ExcelException;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelMultiSheetWriteRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelWriteRequest;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelWriteResult;
import club.slavopolis.infrastructure.integration.excel.util.ExcelErrorCollector;
import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel写入服务实现，封装EasyExcel写入功能
 */
@Slf4j
@Service
public class ExcelWriterService {

    /**
     * 文件名属性常量
     */
    private static final String ATTR_FILE_NAME = "fileName";

    /**
     * 写入Excel文件（单Sheet）
     */
    public ExcelWriteResult write(ExcelWriteRequest request) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(false);
        
        try {
            // 创建处理上下文
            ProcessContext context = ProcessContext.create(request.getRequestId())
                    .setAttribute(ATTR_FILE_NAME, getFileName(request))
                    .initStats();
            
            // 预处理数据
            List<?> processedData = preprocessData(request, context, errorCollector);
            
            if (errorCollector.hasFatalErrors()) {
                return ExcelWriteResult.failure(errorCollector.getErrorSummary())
                        .withTimeInfo(startTime, LocalDateTime.now())
                        .withRequestId(request.getRequestId());
            }
            
            // 执行写入
            String outputPath = executeWrite(request, processedData);
            
            // 获取文件信息
            long fileSize = getFileSize(outputPath);
            
            return ExcelWriteResult.success(processedData.size(), outputPath, fileSize)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId())
                    .withSheetInfo(1, getMaxColumns(processedData));
                    
        } catch (Exception e) {
            log.error("Excel写入失败: {}", e.getMessage(), e);
            return ExcelWriteResult.failure(e.getMessage())
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 异步写入Excel文件
     */
    public CompletableFuture<ExcelWriteResult> writeAsync(ExcelWriteRequest request) {
        return CompletableFuture.supplyAsync(() -> write(request));
    }

    /**
     * 写入多个Sheet
     */
    public ExcelWriteResult writeMultiSheet(ExcelMultiSheetWriteRequest request) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(false);
        
        try {
            ProcessContext context = ProcessContext.create(request.getRequestId())
                    .setAttribute(ATTR_FILE_NAME, getMultiSheetFileName(request))
                    .initStats();
            
            String outputPath = executeMultiSheetWrite(request, context, errorCollector);
            
            if (errorCollector.hasFatalErrors()) {
                return ExcelWriteResult.failure(errorCollector.getErrorSummary())
                        .withTimeInfo(startTime, LocalDateTime.now())
                        .withRequestId(request.getRequestId());
            }
            
            long fileSize = getFileSize(outputPath);
            int totalCount = request.getTotalDataSize();
            int maxColumns = request.getSheetDataList().stream()
                    .mapToInt(sheet -> getMaxColumns(sheet.getData()))
                    .max()
                    .orElse(0);
            
            return ExcelWriteResult.success(totalCount, outputPath, fileSize)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId())
                    .withSheetInfo(request.getSheetCount(), maxColumns);
                    
        } catch (Exception e) {
            log.error("Excel多Sheet写入失败: {}", e.getMessage(), e);
            return ExcelWriteResult.failure(e.getMessage())
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 流式写入Excel文件（大数据量）
     */
    public ExcelWriteResult writeStream(ExcelWriteRequest request) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(false);
        
        try {
            ProcessContext context = ProcessContext.create(request.getRequestId())
                    .setAttribute(ATTR_FILE_NAME, getFileName(request))
                    .setAsync()
                    .initStats();
            
            String outputPath = executeStreamWrite(request, context);
            
            if (errorCollector.hasFatalErrors()) {
                return ExcelWriteResult.failure(errorCollector.getErrorSummary())
                        .withTimeInfo(startTime, LocalDateTime.now())
                        .withRequestId(request.getRequestId());
            }
            
            long fileSize = getFileSize(outputPath);
            
            return ExcelWriteResult.success(request.getDataSize(), outputPath, fileSize)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId())
                    .withSheetInfo(1, getMaxColumns(request.getData()));
                    
        } catch (Exception e) {
            log.error("Excel流式写入失败: {}", e.getMessage(), e);
            return ExcelWriteResult.failure(e.getMessage())
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 预处理数据
     */
    @SuppressWarnings("unchecked")
    private List<?> preprocessData(ExcelWriteRequest request, ProcessContext context, ExcelErrorCollector errorCollector) {
        if (!request.hasProcessor()) {
            return request.getData();
        }
        
        try {
            DataProcessor<Object> processor = (DataProcessor<Object>) request.getProcessor();
            processor.beforeProcess(context);
            
            ProcessResult<Object> result = processor.process(
                (List<Object>) request.getData(), 
                context, 
                errorCollector
            );
            
            if (result.isSuccess() && result.hasData()) {
                return result.getProcessedData();
            } else {
                errorCollector.addSystemError(ExcelErrorCode.DATA_PROCESS_ERROR, 
                    result.getMessage() != null ? result.getMessage() : "数据预处理失败");
                return request.getData();
            }
            
        } catch (Exception e) {
            log.error("数据预处理失败: {}", e.getMessage(), e);
            errorCollector.addSystemError(ExcelErrorCode.DATA_PROCESS_ERROR, e.getMessage());
            return request.getData();
        }
    }

    /**
     * 执行单Sheet写入
     */
    private String executeWrite(ExcelWriteRequest request, List<?> data) {
        var config = request.getConfigOrDefault();
        
        if (request.isTemplateMode()) {
            return executeTemplateWrite(request, data);
        }
        
        var writerBuilder = request.getOutputStream() != null
            ? EasyExcelFactory.write(request.getOutputStream(), request.getDataClass())
            : EasyExcelFactory.write(request.getFilePath(), request.getDataClass());
        
        writerBuilder
            .sheet(config.getSheetName())
            .needHead(config.getIncludeHeader())
            .doWrite(data);
        
        return request.getFilePath();
    }

    /**
     * 执行基于模板的写入
     */
    private String executeTemplateWrite(ExcelWriteRequest request, List<?> data) {
        // 模板写入逻辑
        try {
            var config = request.getConfigOrDefault();
            
            var writerBuilder = request.getOutputStream() != null
                ? EasyExcelFactory.write(request.getOutputStream(), request.getDataClass())
                : EasyExcelFactory.write(request.getFilePath(), request.getDataClass());
            
            // 如果有模板路径，可以在这里添加模板读取逻辑
            writerBuilder
                .sheet(config.getSheetName())
                .needHead(config.getIncludeHeader())
                .doWrite(data);
            
            return request.getFilePath();
            
        } catch (Exception e) {
            log.error("模板写入失败: {}", e.getMessage(), e);
            throw new ExcelException(ExcelErrorCode.WRITE_FILE_ERROR, "模板写入失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行多Sheet写入
     */
    private String executeMultiSheetWrite(ExcelMultiSheetWriteRequest request, ProcessContext context, ExcelErrorCollector errorCollector) {
        String outputPath = request.getFilePath();
        
        try (ExcelWriter excelWriter = createExcelWriter(request)) {
            
            for (int i = 0; i < request.getSheetDataList().size(); i++) {
                var sheetData = request.getSheetDataList().get(i);
                context.updateSheet(i, sheetData.getSheetName());
                
                writeSheetData(excelWriter, sheetData, request, i, errorCollector);
            }
            
        } catch (Exception e) {
            log.error("多Sheet写入失败: {}", e.getMessage(), e);
            throw new ExcelException(ExcelErrorCode.WRITE_FILE_ERROR, "多Sheet写入失败: " + e.getMessage(), e);
        }
        
        return outputPath;
    }

    /**
     * 写入单个Sheet数据
     */
    private void writeSheetData(ExcelWriter excelWriter, ExcelMultiSheetWriteRequest.SheetData sheetData, 
                               ExcelMultiSheetWriteRequest request, int sheetIndex, ExcelErrorCollector errorCollector) {
        try {
            // 获取有效配置
            var effectiveConfig = sheetData.getEffectiveConfig(request.getGlobalConfigOrDefault());
            
            // 创建Sheet
            WriteSheet writeSheet = EasyExcelFactory.writerSheet(sheetIndex, sheetData.getSheetName())
                    .head(sheetData.getDataClass())
                    .needHead(effectiveConfig.getIncludeHeader())
                    .build();
            
            // 写入数据
            excelWriter.write(sheetData.getData(), writeSheet);
            
            log.info("Sheet[{}]写入完成，共{}行数据", sheetData.getSheetName(), sheetData.getDataSize());
            
        } catch (Exception e) {
            log.error("Sheet[{}]写入失败: {}", sheetData.getSheetName(), e.getMessage(), e);
            errorCollector.addSystemError(ExcelErrorCode.WRITE_FILE_ERROR, 
                String.format("Sheet[%s]写入失败: %s", sheetData.getSheetName(), e.getMessage()));
        }
    }

    /**
     * 执行流式写入
     */
    private String executeStreamWrite(ExcelWriteRequest request, ProcessContext context) {
        var config = request.getConfigOrDefault();
        List<?> data = request.getData();
        
        try {
            // 分批写入
            int batchSize = config.getBatchSize();
            int totalBatches = (data.size() + batchSize - 1) / batchSize;
            context.setTotalBatches(totalBatches);
            
            var writerBuilder = request.getOutputStream() != null
                ? EasyExcelFactory.write(request.getOutputStream(), request.getDataClass())
                : EasyExcelFactory.write(request.getFilePath(), request.getDataClass());
            
            try (ExcelWriter excelWriter = writerBuilder.build()) {
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(config.getSheetName())
                        .head(request.getDataClass())
                        .needHead(config.getIncludeHeader())
                        .build();
                
                for (int i = 0; i < totalBatches; i++) {
                    context.updateBatch(i + 1);
                    
                    int startIndex = i * batchSize;
                    int endIndex = Math.min(startIndex + batchSize, data.size());
                    List<?> batchData = data.subList(startIndex, endIndex);
                    
                    excelWriter.write(batchData, writeSheet);
                    
                    // 进度回调
                    if (request.hasProgressCallback()) {
                        double percent = (double) (i + 1) / totalBatches * 100;
                        request.getProgressCallback().onProgress(endIndex, data.size(), percent);
                    }
                    
                    log.debug("流式写入批次{}/{}完成，已写入{}行", i + 1, totalBatches, endIndex);
                }
            }
            
            return request.getFilePath();
            
        } catch (Exception e) {
            log.error("流式写入失败: {}", e.getMessage(), e);
            throw new ExcelException(ExcelErrorCode.WRITE_FILE_ERROR, "流式写入失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建ExcelWriter
     */
    private ExcelWriter createExcelWriter(ExcelMultiSheetWriteRequest request) {
        return request.getOutputStream() != null
            ? EasyExcelFactory.write(request.getOutputStream()).build()
            : EasyExcelFactory.write(request.getFilePath()).build();
    }

    /**
     * 获取文件大小
     */
    private long getFileSize(String filePath) {
        if (filePath == null) {
            // 对于OutputStream，无法准确获取大小
            return 0;
        }
        
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) ? Files.size(path) : 0;
        } catch (IOException e) {
            log.warn("获取文件大小失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取最大列数
     */
    private int getMaxColumns(List<?> data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        
        // 简单估算，可以根据实际需要优化
        Object firstItem = data.getFirst();
        if (firstItem == null) {
            return 0;
        }
        
        // 通过反射获取字段数量作为列数估算
        return firstItem.getClass().getDeclaredFields().length;
    }

    /**
     * 获取文件名
     */
    private String getFileName(ExcelWriteRequest request) {
        if (request.getFilePath() != null) {
            return Paths.get(request.getFilePath()).getFileName().toString();
        }
        return "outputStream";
    }

    /**
     * 获取多Sheet文件名
     */
    private String getMultiSheetFileName(ExcelMultiSheetWriteRequest request) {
        if (request.getFilePath() != null) {
            return Paths.get(request.getFilePath()).getFileName().toString();
        }
        return "multiSheet";
    }
} 