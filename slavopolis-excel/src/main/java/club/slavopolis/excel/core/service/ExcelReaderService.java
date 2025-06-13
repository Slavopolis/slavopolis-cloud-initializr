package club.slavopolis.excel.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;

import org.springframework.stereotype.Service;

import club.slavopolis.excel.core.processor.DataProcessor;
import club.slavopolis.excel.core.processor.ProcessContext;
import club.slavopolis.excel.enums.ExcelErrorCode;
import club.slavopolis.excel.exception.ExcelException;
import club.slavopolis.excel.model.request.ExcelReadRequest;
import club.slavopolis.excel.model.response.ExcelReadResult;
import club.slavopolis.excel.util.ExcelErrorCollector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel读取服务实现，封装EasyExcel读取功能
 */
@Slf4j
@Service
public class ExcelReaderService {

    /**
     * 文件名属性常量
     */
    private static final String ATTR_FILE_NAME = "fileName";

    /**
     * 同步读取Excel文件
     */
    public <T> ExcelReadResult<T> read(ExcelReadRequest<T> request) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(
            request.getConfigOrDefault().getFailFast()
        );
        
        try {
            // 创建处理上下文
            ProcessContext context = ProcessContext.create(request.getRequestId())
                    .setAttribute(ATTR_FILE_NAME, getFileName(request))
                    .initStats();
            
            // 创建数据收集器
            DataCollectListener<T> listener = new DataCollectListener<>(
                request.getProcessor(),
                context,
                errorCollector,
                request.getProgressCallback()
            );
            
            // 构建EasyExcel读取器
            var readerBuilder = createReaderBuilder(request, listener);
            
            // 执行读取
            readerBuilder.doReadAll();
            
            // 构建结果
            return buildReadResult(request, listener.getData(), errorCollector, startTime);
            
        } catch (Exception e) {
            log.error("Excel读取失败: {}", e.getMessage(), e);
            errorCollector.addFatalError(ExcelErrorCode.READ_FILE_ERROR, e.getMessage(), null);
            return ExcelReadResult.<T>failure(errorCollector)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 异步读取Excel文件
     */
    public <T> CompletableFuture<ExcelReadResult<T>> readAsync(ExcelReadRequest<T> request) {
        return CompletableFuture.supplyAsync(() -> read(request));
    }

    /**
     * 流式读取Excel文件
     */
    public <T> Stream<T> readAsStream(ExcelReadRequest<T> request) {
        request.validate();
        
        try {
            ExcelErrorCollector errorCollector = ExcelErrorCollector.create(false);
            ProcessContext context = ProcessContext.create(request.getRequestId())
                    .setAttribute(ATTR_FILE_NAME, getFileName(request))
                    .setSync()
                    .initStats();
            
            StreamDataListener<T> listener = new StreamDataListener<>(
                request.getProcessor(),
                context,
                errorCollector
            );
            
            var readerBuilder = createReaderBuilder(request, listener);
            readerBuilder.doReadAll();
            
            return listener.getDataStream();
            
        } catch (Exception e) {
            log.error("Excel流式读取失败: {}", e.getMessage(), e);
            throw new ExcelException(ExcelErrorCode.READ_FILE_ERROR, e.getMessage(), e);
        }
    }

    /**
     * 读取多个Sheet
     */
    public <T> ExcelReadResult<T> readMultiSheet(ExcelReadRequest<T> request) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(
            request.getConfigOrDefault().getFailFast()
        );
        
        try {
            ProcessContext context = ProcessContext.create(request.getRequestId())
                    .setAttribute(ATTR_FILE_NAME, getFileName(request))
                    .initStats();
            
            MultiSheetDataListener<T> listener = new MultiSheetDataListener<>(
                request.getProcessor(),
                context,
                errorCollector,
                request.getProgressCallback()
            );
            
            var readerBuilder = createReaderBuilder(request, listener);
            readerBuilder.doReadAll();
            
            return buildMultiSheetResult(request, listener, errorCollector, startTime);
            
        } catch (Exception e) {
            log.error("Excel多Sheet读取失败: {}", e.getMessage(), e);
            errorCollector.addFatalError(ExcelErrorCode.READ_FILE_ERROR, e.getMessage(), null);
            return ExcelReadResult.<T>failure(errorCollector)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 创建EasyExcel读取器建造器
     */
    private <T> com.alibaba.excel.read.builder.ExcelReaderBuilder createReaderBuilder(
            ExcelReadRequest<T> request, ReadListener<T> listener) {
        
        var config = request.getConfigOrDefault();
        var builder = request.getInputStream() != null 
            ? EasyExcelFactory.read(request.getInputStream(), request.getDataClass(), listener)
            : EasyExcelFactory.read(request.getFilePath(), request.getDataClass(), listener);
            
        return builder
                .headRowNumber(config.getHeaderRowNumber())
                .ignoreEmptyRow(config.getIgnoreEmptyRow())
                .autoTrim(config.getAutoTrim());
    }

    /**
     * 构建读取结果
     */
    private <T> ExcelReadResult<T> buildReadResult(
            ExcelReadRequest<T> request, 
            List<T> data,
            ExcelErrorCollector errorCollector,
            LocalDateTime startTime) {
        
        LocalDateTime endTime = LocalDateTime.now();
        
        if (errorCollector.hasFatalErrors()) {
            return ExcelReadResult.<T>failure(errorCollector)
                    .withTimeInfo(startTime, endTime)
                    .withRequestId(request.getRequestId());
        } else if (errorCollector.hasErrors()) {
            return ExcelReadResult.partialSuccess(data, errorCollector)
                    .withTimeInfo(startTime, endTime)
                    .withRequestId(request.getRequestId());
        } else {
            return ExcelReadResult.success(data)
                    .withTimeInfo(startTime, endTime)
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 构建多Sheet读取结果
     */
    private <T> ExcelReadResult<T> buildMultiSheetResult(
            ExcelReadRequest<T> request,
            MultiSheetDataListener<T> listener,
            ExcelErrorCollector errorCollector,
            LocalDateTime startTime) {
        
        var result = buildReadResult(request, listener.getAllData(), errorCollector, startTime);
        result.setSheetInfos(listener.getSheetInfos());
        return result;
    }

    /**
     * 获取文件名
     */
    private String getFileName(ExcelReadRequest<?> request) {
        if (request.getFilePath() != null) {
            return java.nio.file.Paths.get(request.getFilePath()).getFileName().toString();
        }
        return "inputStream";
    }

    /**
     * 数据收集监听器 - 用于普通读取
     */
    @Getter
    private static class DataCollectListener<T> implements ReadListener<T> {
        private final DataProcessor<T> processor;
        private final ProcessContext context;
        private final ExcelErrorCollector errorCollector;
        private final ExcelReadRequest.ProgressCallback progressCallback;
        private final List<T> data = new ArrayList<>();
        private final AtomicInteger rowIndex = new AtomicInteger(0);

        public DataCollectListener(DataProcessor<T> processor,
                                 ProcessContext context,
                                 ExcelErrorCollector errorCollector,
                                 ExcelReadRequest.ProgressCallback progressCallback) {
            this.processor = processor;
            this.context = context;
            this.errorCollector = errorCollector;
            this.progressCallback = progressCallback;
        }

        @Override
        public void invoke(T data, AnalysisContext context) {
            int currentRow = rowIndex.incrementAndGet();
            this.context.updatePosition(currentRow);
            
            try {
                // 数据验证
                if (processor != null && !processor.validate(data, currentRow, this.context, errorCollector)) {
                    this.context.getStats().incrementFailed();
                    return;
                }
                
                // 数据处理
                T processedData = processor != null 
                    ? processor.processOne(data, currentRow, this.context, errorCollector)
                    : data;
                
                if (processedData != null) {
                    this.data.add(processedData);
                    this.context.getStats().incrementSuccess();
                } else {
                    this.context.getStats().incrementSkipped();
                }
                
                // 进度回调
                if (progressCallback != null) {
                    progressCallback.onProgress(currentRow, -1, 0);
                }
                
            } catch (Exception e) {
                log.error("处理第{}行数据时发生错误: {}", currentRow, e.getMessage(), e);
                errorCollector.addDataError(ExcelErrorCode.DATA_PROCESS_ERROR, 
                    e.getMessage(), currentRow, 0);
                this.context.getStats().incrementFailed();
            }
            
            this.context.getStats().incrementProcessed();
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (processor != null) {
                try {
                    processor.afterProcess(this.context, data, errorCollector);
                } catch (Exception e) {
                    log.error("后处理阶段发生错误: {}", e.getMessage(), e);
                    errorCollector.addSystemError(ExcelErrorCode.POST_PROCESS_ERROR, e.getMessage());
                }
            }
            
            log.info("Excel读取完成，共处理{}行数据，成功{}行，失败{}行", 
                rowIndex.get(), data.size(), 
                rowIndex.get() - data.size());
        }
    }

    /**
     * 流式数据监听器 - 用于流式读取
     */
    private static class StreamDataListener<T> implements ReadListener<T> {
        private final DataProcessor<T> processor;
        private final ProcessContext context;
        private final ExcelErrorCollector errorCollector;
        private final List<T> dataBuffer = new ArrayList<>();
        private final AtomicInteger rowIndex = new AtomicInteger(0);

        public StreamDataListener(DataProcessor<T> processor,
                                ProcessContext context,
                                ExcelErrorCollector errorCollector) {
            this.processor = processor;
            this.context = context;
            this.errorCollector = errorCollector;
        }

        @Override
        public void invoke(T data, AnalysisContext context) {
            int currentRow = rowIndex.incrementAndGet();
            this.context.updatePosition(currentRow);
            
            try {
                if (processor != null && !processor.validate(data, currentRow, this.context, errorCollector)) {
                    return;
                }
                
                T processedData = processor != null 
                    ? processor.processOne(data, currentRow, this.context, errorCollector)
                    : data;
                
                if (processedData != null) {
                    dataBuffer.add(processedData);
                }
                
            } catch (Exception e) {
                log.error("流式处理第{}行数据时发生错误: {}", currentRow, e.getMessage(), e);
                errorCollector.addDataError(ExcelErrorCode.DATA_PROCESS_ERROR, 
                    e.getMessage(), currentRow, 0);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("流式读取完成，共缓存{}行数据", dataBuffer.size());
        }

        public Stream<T> getDataStream() {
            return dataBuffer.stream();
        }
    }

    /**
     * 多Sheet数据监听器
     */
    @Getter
    private static class MultiSheetDataListener<T> implements ReadListener<T> {
        private final DataProcessor<T> processor;
        private final ProcessContext context;
        private final ExcelErrorCollector errorCollector;
        private final ExcelReadRequest.ProgressCallback progressCallback;
        private final List<T> allData = new ArrayList<>();
        private final List<ExcelReadResult.SheetInfo> sheetInfos = new ArrayList<>();
        private final AtomicInteger totalRowIndex = new AtomicInteger(0);
        private ExcelReadResult.SheetInfo currentSheetInfo;

        public MultiSheetDataListener(DataProcessor<T> processor,
                                    ProcessContext context,
                                    ExcelErrorCollector errorCollector,
                                    ExcelReadRequest.ProgressCallback progressCallback) {
            this.processor = processor;
            this.context = context;
            this.errorCollector = errorCollector;
            this.progressCallback = progressCallback;
        }

        @Override
        public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
            // 新Sheet开始
            if (currentSheetInfo != null) {
                sheetInfos.add(currentSheetInfo);
            }
            
            currentSheetInfo = ExcelReadResult.SheetInfo.builder()
                    .sheetIndex(context.readSheetHolder().getSheetNo())
                    .sheetName(context.readSheetHolder().getSheetName())
                    .columnCount(headMap.size())
                    .successRows(0)
                    .failedRows(0)
                    .errors(new ArrayList<>())
                    .build();
                    
            this.context.updateSheet(currentSheetInfo.getSheetIndex(), currentSheetInfo.getSheetName());
        }

        @Override
        public void invoke(T data, AnalysisContext context) {
            int currentRow = totalRowIndex.incrementAndGet();
            this.context.updatePosition(currentRow);
            
            try {
                if (processor != null && !processor.validate(data, currentRow, this.context, errorCollector)) {
                    currentSheetInfo.setFailedRows(currentSheetInfo.getFailedRows() + 1);
                    return;
                }
                
                T processedData = processor != null 
                    ? processor.processOne(data, currentRow, this.context, errorCollector)
                    : data;
                
                if (processedData != null) {
                    allData.add(processedData);
                    currentSheetInfo.setSuccessRows(currentSheetInfo.getSuccessRows() + 1);
                } else {
                    currentSheetInfo.setFailedRows(currentSheetInfo.getFailedRows() + 1);
                }
                
                currentSheetInfo.setRowCount(currentSheetInfo.getRowCount() + 1);
                
                if (progressCallback != null) {
                    progressCallback.onProgress(currentRow, -1, 0);
                }
                
            } catch (Exception e) {
                log.error("处理Sheet[{}]第{}行数据时发生错误: {}", 
                    currentSheetInfo.getSheetName(), currentRow, e.getMessage(), e);
                errorCollector.addDataError(ExcelErrorCode.DATA_PROCESS_ERROR, 
                    e.getMessage(), currentRow, 0);
                currentSheetInfo.setFailedRows(currentSheetInfo.getFailedRows() + 1);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (currentSheetInfo != null) {
                sheetInfos.add(currentSheetInfo);
            }
            
            if (processor != null) {
                try {
                    processor.afterProcess(this.context, allData, errorCollector);
                } catch (Exception e) {
                    log.error("多Sheet后处理阶段发生错误: {}", e.getMessage(), e);
                    errorCollector.addSystemError(ExcelErrorCode.POST_PROCESS_ERROR, e.getMessage());
                }
            }
            
            log.info("多Sheet读取完成，共处理{}个Sheet，{}行数据", 
                sheetInfos.size(), totalRowIndex.get());
        }
    }
} 