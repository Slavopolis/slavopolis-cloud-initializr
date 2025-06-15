package club.slavopolis.infrastructure.integration.excel.core.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.integration.excel.enums.ExcelTypeEnum;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import org.springframework.stereotype.Service;

import club.slavopolis.infrastructure.integration.excel.enums.ExcelErrorCode;
import club.slavopolis.infrastructure.integration.excel.exception.ExcelException;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelFillRequest;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelFillResult;
import club.slavopolis.infrastructure.integration.excel.util.ExcelErrorCollector;
import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel填充服务实现，封装EasyExcel填充功能
 */
@Slf4j
@Service
public class ExcelFillerService {

    /**
     * 模板缓存
     */
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    /**
     * 基于模板填充Excel文件
     */
    public ExcelFillResult fill(ExcelFillRequest request) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(false);
        
        try {
            // 执行填充
            String outputPath = executeFill(request, errorCollector);
            
            if (errorCollector.hasFatalErrors()) {
                return ExcelFillResult.failure(errorCollector.getErrorSummary())
                        .withTimeInfo(startTime, LocalDateTime.now())
                        .withRequestId(request.getRequestId());
            }
            
            // 获取文件信息
            long fileSize = getFileSize(outputPath);
            int variableCount = countVariables(request.getFillData());
            
            // 实际统计填充信息
            FillStats fillStats = calculateFillStats(request.getFillData());
            
            return ExcelFillResult.success(variableCount, outputPath, fileSize)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId())
                    .withTemplateInfo(getTemplatePath(request))
                    .withFillStats(fillStats.successCount(), fillStats.failureCount(), fillStats.skippedCount());
                    
        } catch (Exception e) {
            log.error("Excel填充失败: {}", e.getMessage(), e);
            return ExcelFillResult.failure(e.getMessage())
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 异步填充Excel文件
     */
    public CompletableFuture<ExcelFillResult> fillAsync(ExcelFillRequest request) {
        return CompletableFuture.supplyAsync(() -> fill(request));
    }

    /**
     * 基于已注册的模板进行填充
     */
    public ExcelFillResult fillWithTemplate(String templateName, Object data) {
        String templatePath = templateCache.get(templateName);
        if (templatePath == null) {
            throw new ExcelException(ExcelErrorCode.TEMPLATE_NOT_FOUND, 
                "模板未找到: " + templateName);
        }
        
        // 生成临时输出路径
        String outputPath = generateTempOutputPath(templateName);
        
        ExcelFillRequest request = ExcelFillRequest.ofFile(templatePath, outputPath, data);
        return fill(request);
    }

    /**
     * 注册模板
     */
    public void registerTemplate(String templateName, String templatePath) {
        // 验证模板文件是否存在
        if (!Files.exists(Paths.get(templatePath))) {
            throw new ExcelException(ExcelErrorCode.TEMPLATE_NOT_FOUND, 
                "模板文件不存在: " + templatePath);
        }
        
        templateCache.put(templateName, templatePath);
        log.info("模板已注册: {} -> {}", templateName, templatePath);
    }

    /**
     * 移除模板
     */
    public void unregisterTemplate(String templateName) {
        String removed = templateCache.remove(templateName);
        if (removed != null) {
            log.info("模板已移除: {} -> {}", templateName, removed);
        }
    }

    /**
     * 获取已注册的模板列表
     */
    public Map<String, String> getRegisteredTemplates() {
        return new ConcurrentHashMap<>(templateCache);
    }

    /**
     * 执行填充操作
     */
    private String executeFill(ExcelFillRequest request, ExcelErrorCollector errorCollector) {
        try {
            if (request.getFillData() instanceof Map) {
                return executeMapFill(request, errorCollector);
            } else {
                return executeObjectFill(request, errorCollector);
            }
            
        } catch (Exception e) {
            log.error("执行填充失败: {}", e.getMessage(), e);
            errorCollector.addFatalError(ExcelErrorCode.FILL_ERROR, e.getMessage(), null);
            throw new ExcelException(ExcelErrorCode.FILL_ERROR, "填充执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行Map数据填充
     */
    @SuppressWarnings("unchecked")
    private String executeMapFill(ExcelFillRequest request, ExcelErrorCollector errorCollector) {
        Map<String, Object> fillData = (Map<String, Object>) request.getFillData();
        var config = request.getConfigOrDefault();
        
        try (ExcelWriter excelWriter = createExcelWriter(request)) {
            WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
            
            // 配置填充方向
            FillConfig fillConfig = FillConfig.builder()
                    .direction(Boolean.TRUE.equals(config.getHorizontalFill()) ? WriteDirectionEnum.HORIZONTAL : WriteDirectionEnum.VERTICAL)
                    .forceNewRow(Boolean.TRUE.equals(config.getForceNewSheet()))
                    .build();
            
            excelWriter.fill(fillData, fillConfig, writeSheet);
            
            log.info("Map填充完成，共填充{}个变量", fillData.size());
            return request.getOutputPath();
            
        } catch (Exception e) {
            log.error("Map填充失败: {}", e.getMessage(), e);
            errorCollector.addSystemError(ExcelErrorCode.FILL_ERROR, "Map填充失败: " + e.getMessage());
            throw new ExcelException(ExcelErrorCode.FILL_ERROR, "Map填充失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行对象数据填充
     */
    private String executeObjectFill(ExcelFillRequest request, ExcelErrorCollector errorCollector) {
        Object fillData = request.getFillData();
        var config = request.getConfigOrDefault();
        
        try (ExcelWriter excelWriter = createExcelWriter(request)) {
            WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
            
            // 配置填充方向
            FillConfig fillConfig = FillConfig.builder()
                    .direction(Boolean.TRUE.equals(config.getHorizontalFill()) ? WriteDirectionEnum.HORIZONTAL : WriteDirectionEnum.VERTICAL)
                    .forceNewRow(Boolean.TRUE.equals(config.getForceNewSheet()))
                    .build();
            
            excelWriter.fill(fillData, fillConfig, writeSheet);
            
            log.info("对象填充完成，类型: {}", fillData.getClass().getSimpleName());
            return request.getOutputPath();
            
        } catch (Exception e) {
            log.error("对象填充失败: {}", e.getMessage(), e);
            errorCollector.addSystemError(ExcelErrorCode.FILL_ERROR, "对象填充失败: " + e.getMessage());
            throw new ExcelException(ExcelErrorCode.FILL_ERROR, "对象填充失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建ExcelWriter
     */
    private ExcelWriter createExcelWriter(ExcelFillRequest request) {
        if (request.getTemplateInputStream() != null) {
            return request.getOutputStream() != null
                ? EasyExcelFactory.write(request.getOutputStream()).withTemplate(request.getTemplateInputStream()).build()
                : EasyExcelFactory.write(request.getOutputPath()).withTemplate(request.getTemplateInputStream()).build();
        } else {
            return request.getOutputStream() != null
                ? EasyExcelFactory.write(request.getOutputStream()).withTemplate(request.getTemplatePath()).build()
                : EasyExcelFactory.write(request.getOutputPath()).withTemplate(request.getTemplatePath()).build();
        }
    }

    /**
     * 获取文件大小
     */
    private long getFileSize(String filePath) {
        if (filePath == null) {
            return 0; // 对于OutputStream，无法准确获取大小
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
     * 统计变量数量
     */
    private int countVariables(Object fillData) {
        if (fillData == null) {
            return 0;
        }
        
        return switch (fillData) {
            case Map<?, ?> map -> map.size();
            case java.util.Collection<?> collection -> collection.size();
            default -> countObjectFields(fillData);
        };
    }

    /**
     * 统计对象字段数量
     */
    private int countObjectFields(Object obj) {
        if (obj == null) {
            return 0;
        }
        
        try {
            int count = 0;
            Field[] fields = obj.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                count += countFieldValue(field, obj);
            }
            
            return count;
            
        } catch (Exception e) {
            log.warn("统计对象字段失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 统计单个字段值
     */
    private int countFieldValue(Field field, Object obj) {
        try {
            Object value = field.get(obj);
            return value != null ? 1 : 0;
        } catch (IllegalAccessException e) {
            log.warn("无法访问字段 {}: {}", field.getName(), e.getMessage());
            return 0;
        }
    }

    /**
     * 计算填充统计信息
     */
    private FillStats calculateFillStats(Object fillData) {
        if (fillData == null) {
            return new FillStats(0, 0, 0);
        }
        
        int totalCount = countVariables(fillData);
        // 简单实现：假设所有变量都成功填充
        // 实际项目中可以根据具体需求实现更复杂的统计逻辑
        return new FillStats(totalCount, 0, 0);
    }

    /**
     * 获取模板路径
     */
    private String getTemplatePath(ExcelFillRequest request) {
        if (request.getTemplatePath() != null) {
            return request.getTemplatePath();
        }
        return "inputStream";
    }

    /**
     * 生成临时输出路径
     */
    private String generateTempOutputPath(String templateName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return System.getProperty("java.io.tmpdir") +
                CommonConstants.SLASH + templateName +
                CommonConstants.UNDERSCORE + timestamp +
                CommonConstants.DOT +
                ExcelTypeEnum.XLSX.getExtension();
    }

    /**
     * 高级填充功能（支持复杂模板）
     */
    public ExcelFillResult advancedFill(ExcelFillRequest request, Map<String, Object> extraData) {
        request.validate();
        
        LocalDateTime startTime = LocalDateTime.now();
        ExcelErrorCollector errorCollector = ExcelErrorCollector.create(false);
        
        try {
            String outputPath = executeAdvancedFill(request, extraData, errorCollector);
            
            if (errorCollector.hasFatalErrors()) {
                return ExcelFillResult.failure(errorCollector.getErrorSummary())
                        .withTimeInfo(startTime, LocalDateTime.now())
                        .withRequestId(request.getRequestId());
            }
            
            long fileSize = getFileSize(outputPath);
            int variableCount = countVariables(request.getFillData()) + extraData.size();
            
            return ExcelFillResult.success(variableCount, outputPath, fileSize)
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId())
                    .withTemplateInfo(getTemplatePath(request));
                    
        } catch (Exception e) {
            log.error("高级填充失败: {}", e.getMessage(), e);
            return ExcelFillResult.failure(e.getMessage())
                    .withTimeInfo(startTime, LocalDateTime.now())
                    .withRequestId(request.getRequestId());
        }
    }

    /**
     * 执行高级填充
     */
    @SuppressWarnings("unchecked")
    private String executeAdvancedFill(ExcelFillRequest request, Map<String, Object> extraData, ExcelErrorCollector errorCollector) {
        // 合并填充数据
        Map<String, Object> mergedData = new java.util.HashMap<>();
        
        // 添加主要填充数据
        if (request.getFillData() instanceof Map) {
            Map<String, Object> originalData = (Map<String, Object>) request.getFillData();
            mergedData.putAll(originalData);
        }
        
        // 添加额外数据
        mergedData.putAll(extraData);
        
        // 执行填充
        return executeMapFill(
            ExcelFillRequest.builder()
                .templateInputStream(request.getTemplateInputStream())
                .templatePath(request.getTemplatePath())
                .outputStream(request.getOutputStream())
                .outputPath(request.getOutputPath())
                .fillData(mergedData)
                .config(request.getConfig())
                .build(),
            errorCollector
        );
    }

    /**
     * 批量填充（多个模板）
     */
    public Map<String, ExcelFillResult> batchFill(Map<String, ExcelFillRequest> requests) {
        Map<String, ExcelFillResult> results = new ConcurrentHashMap<>();
        
        requests.entrySet().parallelStream().forEach(entry -> {
            String key = entry.getKey();
            ExcelFillRequest request = entry.getValue();
            
            try {
                ExcelFillResult result = fill(request);
                results.put(key, result);
                
            } catch (Exception e) {
                log.error("批量填充[{}]失败: {}", key, e.getMessage(), e);
                results.put(key, ExcelFillResult.failure("批量填充失败: " + e.getMessage()));
            }
        });
        
        log.info("批量填充完成，总数: {}，成功: {}", 
            requests.size(), 
            results.values().stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum());
        
        return results;
    }

    /**
     * 填充统计信息记录类
     */
    public record FillStats(int successCount, int failureCount, int skippedCount) {
    }
} 