package club.slavopolis.excel.core.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import club.slavopolis.excel.core.processor.AnnotationProcessor;
import club.slavopolis.excel.enums.ExcelErrorCode;
import club.slavopolis.excel.exception.ExcelException;
import club.slavopolis.excel.model.config.ExcelReadConfig;
import club.slavopolis.excel.model.config.ExcelWriteConfig;
import club.slavopolis.excel.model.metadata.ExcelSheetMeta;
import club.slavopolis.excel.model.metadata.ExcelTemplateMeta;
import club.slavopolis.excel.model.request.ExcelFillRequest;
import club.slavopolis.excel.model.request.ExcelReadRequest;
import club.slavopolis.excel.model.request.ExcelWriteRequest;
import club.slavopolis.excel.model.response.ExcelFillResult;
import club.slavopolis.excel.model.response.ExcelReadResult;
import club.slavopolis.excel.model.response.ExcelWriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 基于注解的 Excel 服务，提供更高级的注解驱动功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnotationExcelService {

    private final ExcelServiceImpl excelService;
    private final AnnotationProcessor annotationProcessor;

    // ==================== 注解驱动的读取操作 ====================

    /**
     * 基于注解读取 Excel 文件
     *
     * @param filePath 文件路径
     * @param clazz 实体类
     * @return 读取结果
     * @param <T> 实体类类型
     */
    public <T> ExcelReadResult<T> read(String filePath, Class<T> clazz) {
        validateExcelAnnotation(clazz);
        
        ExcelSheetMeta sheetMeta = annotationProcessor.parseSheetMeta(clazz);
        ExcelReadRequest<T> request = buildReadRequest(filePath, clazz, sheetMeta);
        
        log.info("基于注解读取Excel文件: {}, 类型: {}", filePath, clazz.getSimpleName());
        return excelService.read(request);
    }

    /**
     * 基于注解读取 Excel 文件（InputStream）
     *
     * @param inputStream 输入流
     * @param clazz 实体类
     * @return 读取结果
     * @param <T> 实体类类型
     */
    public <T> ExcelReadResult<T> read(InputStream inputStream, Class<T> clazz) {
        validateExcelAnnotation(clazz);
        
        ExcelSheetMeta sheetMeta = annotationProcessor.parseSheetMeta(clazz);
        ExcelReadRequest<T> request = buildReadRequest(inputStream, clazz, sheetMeta);
        
        log.info("基于注解读取Excel文件流, 类型: {}", clazz.getSimpleName());
        return excelService.read(request);
    }

    /**
     * 基于注解异步读取 Excel 文件
     *
     * @param filePath 文件路径
     * @param clazz 实体类
     * @return 异步读取结果
     * @param <T> 实体类类型
     */
    public <T> CompletableFuture<ExcelReadResult<T>> readAsync(String filePath, Class<T> clazz) {
        validateExcelAnnotation(clazz);
        
        ExcelSheetMeta sheetMeta = annotationProcessor.parseSheetMeta(clazz);
        ExcelReadRequest<T> request = buildReadRequest(filePath, clazz, sheetMeta);
        
        log.info("基于注解异步读取Excel文件: {}, 类型: {}", filePath, clazz.getSimpleName());
        return excelService.readAsync(request);
    }

    // ==================== 注解驱动的写入操作 ====================

    /**
     * 基于注解写入 Excel 文件
     *
     * @param filePath 文件路径
     * @param data 数据列表
     * @param clazz 实体类
     * @return 写入结果
     * @param <T> 实体类类型
     */
    public <T> ExcelWriteResult write(String filePath, List<T> data, Class<T> clazz) {
        validateExcelAnnotation(clazz);
        
        ExcelSheetMeta sheetMeta = annotationProcessor.parseSheetMeta(clazz);
        ExcelWriteRequest request = buildWriteRequest(filePath, data, clazz, sheetMeta);
        
        log.info("基于注解写入Excel文件: {}, 类型: {}, 数据量: {}", 
            filePath, clazz.getSimpleName(), data.size());
        return excelService.write(request);
    }

    /**
     * 基于注解写入 Excel 文件（OutputStream）
     *
     * @param outputStream  输出流
     * @param data 数据列表
     * @param clazz 实体类
     * @return 写入结果
     * @param <T> 实体类类型
     */
    public <T> ExcelWriteResult write(OutputStream outputStream, List<T> data, Class<T> clazz) {
        validateExcelAnnotation(clazz);
        
        ExcelSheetMeta sheetMeta = annotationProcessor.parseSheetMeta(clazz);
        ExcelWriteRequest request = buildWriteRequest(outputStream, data, clazz, sheetMeta);
        
        log.info("基于注解写入Excel文件流, 类型: {}, 数据量: {}", 
            clazz.getSimpleName(), data.size());
        return excelService.write(request);
    }

    /**
     * 基于注解异步写入 Excel 文件
     *
     * @param filePath 文件路径
     * @param data 数据列表
     * @param clazz 实体类
     * @return 异步写入结果
     * @param <T> 实体类类型
     */
    public <T> CompletableFuture<ExcelWriteResult> writeAsync(String filePath, List<T> data, Class<T> clazz) {
        validateExcelAnnotation(clazz);
        
        ExcelSheetMeta sheetMeta = annotationProcessor.parseSheetMeta(clazz);
        ExcelWriteRequest request = buildWriteRequest(filePath, data, clazz, sheetMeta);
        
        log.info("基于注解异步写入Excel文件: {}, 类型: {}, 数据量: {}", 
            filePath, clazz.getSimpleName(), data.size());
        return excelService.writeAsync(request);
    }

    // ==================== 注解驱动的模板填充操作 ====================

    /**
     * 基于注解进行模板填充
     *
     * @param outputPath 输出路径
     * @param data 数据对象
     * @param clazz 实体类
     * @return 填充结果
     * @param <T> 实体类类型
     */
    public <T> ExcelFillResult fill(String outputPath, T data, Class<T> clazz) {
        validateTemplateAnnotation(clazz);
        
        ExcelTemplateMeta templateMeta = annotationProcessor.parseTemplateMeta(clazz);
        ExcelFillRequest request = buildFillRequest(outputPath, data, templateMeta);
        
        log.info("基于注解进行模板填充: {}, 类型: {}", outputPath, clazz.getSimpleName());
        return excelService.fill(request);
    }

    /**
     * 基于注解进行模板填充（OutputStream）
     *
     * @param outputStream 输出流
     * @param data 数据对象
     * @param clazz 实体类
     * @return 填充结果
     * @param <T> 实体类类型
     */
    public <T> ExcelFillResult fill(OutputStream outputStream, T data, Class<T> clazz) {
        validateTemplateAnnotation(clazz);
        
        ExcelTemplateMeta templateMeta = annotationProcessor.parseTemplateMeta(clazz);
        ExcelFillRequest request = buildFillRequest(outputStream, data, templateMeta);
        
        log.info("基于注解进行模板填充到流, 类型: {}", clazz.getSimpleName());
        return excelService.fill(request);
    }

    /**
     * 基于注解异步进行模板填充
     *
     * @param outputPath 输出路径
     * @param data 数据对象
     * @param clazz 实体类
     * @return 异步填充结果
     * @param <T> 实体类类型
     */
    public <T> CompletableFuture<ExcelFillResult> fillAsync(String outputPath, T data, Class<T> clazz) {
        validateTemplateAnnotation(clazz);
        
        ExcelTemplateMeta templateMeta = annotationProcessor.parseTemplateMeta(clazz);
        ExcelFillRequest request = buildFillRequest(outputPath, data, templateMeta);
        
        log.info("基于注解异步进行模板填充: {}, 类型: {}", outputPath, clazz.getSimpleName());
        return excelService.fillAsync(request);
    }

    // ==================== 工具方法 ====================

    /**
     * 验证类是否有 Excel 注解
     *
     * @param clazz 实体类
     */
    private void validateExcelAnnotation(Class<?> clazz) {
        if (!annotationProcessor.hasExcelAnnotation(clazz)) {
            throw new ExcelException(ExcelErrorCode.ANNOTATION_NOT_FOUND,
                "类[" + clazz.getName() + "]缺少Excel相关注解");
        }
    }

    /**
     * 验证类是否有 Template 注解
     *
     * @param clazz 实体类
     */
    private void validateTemplateAnnotation(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(club.slavopolis.excel.annotation.ExcelTemplate.class)) {
            throw new ExcelException(ExcelErrorCode.ANNOTATION_NOT_FOUND,
                "类[" + clazz.getName() + "]缺少@ExcelTemplate注解");
        }
    }

    /**
     * 构建读取请求
     *
     * @param filePath 文件路径
     * @param clazz 实体类
     * @param sheetMeta Sheet元数据
     * @return 读取请求
     * @param <T> 实体类类型
     */
    private <T> ExcelReadRequest<T> buildReadRequest(String filePath, Class<T> clazz, ExcelSheetMeta sheetMeta) {
        return ExcelReadRequest.<T>builder()
                .filePath(filePath)
                .dataClass(clazz)
                .config(ExcelReadConfig.builder()
                        .sheetNo(sheetMeta.getSheetIndex())
                        .headerRowNumber(sheetMeta.getHeaderIndex())
                        .ignoreEmptyRow(sheetMeta.isIgnoreEmptyRow())
                        .autoTrim(sheetMeta.isAutoTrim())
                        .failFast(sheetMeta.isFailFast())
                        .maxRows(sheetMeta.hasMaxRows() ? sheetMeta.getMaxRows() : Integer.MAX_VALUE)
                        .password(sheetMeta.hasPassword() ? sheetMeta.getPassword() : null)
                        .build())
                .build();
    }

    /**
     * 构建读取请求（InputStream）
     *
     * @param inputStream 输入流
     * @param clazz 实体类
     * @param sheetMeta Sheet元数据
     * @return 读取请求
     * @param <T> 实体类类型
     */
    private <T> ExcelReadRequest<T> buildReadRequest(InputStream inputStream, Class<T> clazz, ExcelSheetMeta sheetMeta) {
        return ExcelReadRequest.<T>builder()
                .inputStream(inputStream)
                .dataClass(clazz)
                .config(ExcelReadConfig.builder()
                        .sheetNo(sheetMeta.getSheetIndex())
                        .headerRowNumber(sheetMeta.getHeaderIndex())
                        .ignoreEmptyRow(sheetMeta.isIgnoreEmptyRow())
                        .autoTrim(sheetMeta.isAutoTrim())
                        .failFast(sheetMeta.isFailFast())
                        .maxRows(sheetMeta.hasMaxRows() ? sheetMeta.getMaxRows() : Integer.MAX_VALUE)
                        .password(sheetMeta.hasPassword() ? sheetMeta.getPassword() : null)
                        .build())
                .build();
    }

    /**
     * 构建写入请求
     *
     * @param filePath 文件路径
     * @param data 数据列表
     * @param clazz 实体类
     * @param sheetMeta Sheet元数据
     * @return 写入请求
     * @param <T> 实体类类型
     */
    private <T> ExcelWriteRequest buildWriteRequest(String filePath, List<T> data, Class<T> clazz, ExcelSheetMeta sheetMeta) {
        return ExcelWriteRequest.builder()
                .filePath(filePath)
                .data(data)
                .dataClass(clazz)
                .config(ExcelWriteConfig.builder()
                        .sheetName(sheetMeta.getSheetName())
                        .includeHeader(sheetMeta.isIncludeHeader())
                        .build())
                .build();
    }

    /**
     * 构建写入请求（OutputStream）
     *
     * @param outputStream 输出流
     * @param data 数据列表
     * @param clazz 实体类
     * @param sheetMeta Sheet元数据
     * @return 写入请求
     * @param <T> 实体类类型
     */
    private <T> ExcelWriteRequest buildWriteRequest(OutputStream outputStream, List<T> data, Class<T> clazz, ExcelSheetMeta sheetMeta) {
        return ExcelWriteRequest.builder()
                .outputStream(outputStream)
                .data(data)
                .dataClass(clazz)
                .config(ExcelWriteConfig.builder()
                        .sheetName(sheetMeta.getSheetName())
                        .includeHeader(sheetMeta.isIncludeHeader())
                        .build())
                .build();
    }

    /**
     * 构建填充请求
     *
     * @param outputPath 输出路径
     * @param data 数据对象
     * @param templateMeta 模板元数据
     * @return 填充请求
     * @param <T> 数据对象类型
     */
    private <T> ExcelFillRequest buildFillRequest(String outputPath, T data, ExcelTemplateMeta templateMeta) {
        return ExcelFillRequest.builder()
                .templatePath(templateMeta.getTemplatePath())
                .outputPath(outputPath)
                .fillData(data)
                .config(ExcelFillRequest.FillConfig.builder()
                        .horizontalFill(templateMeta.isHorizontal())
                        .startRow(templateMeta.getStartRow())
                        .startColumn(templateMeta.getStartColumn())
                        .forceNewSheet(templateMeta.isForceNewSheet())
                        .autoCalculateFormula(templateMeta.isEnableFormula())
                        .build())
                .build();
    }

    /**
     * 构建填充请求（OutputStream）
     *
     * @param outputStream 输出流
     * @param data 数据对象
     * @param templateMeta 模板元数据
     * @return 填充请求
     * @param <T> 数据对象类型
     */
    private <T> ExcelFillRequest buildFillRequest(OutputStream outputStream, T data, ExcelTemplateMeta templateMeta) {
        return ExcelFillRequest.builder()
                .templatePath(templateMeta.getTemplatePath())
                .outputStream(outputStream)
                .fillData(data)
                .config(ExcelFillRequest.FillConfig.builder()
                        .horizontalFill(templateMeta.isHorizontal())
                        .startRow(templateMeta.getStartRow())
                        .startColumn(templateMeta.getStartColumn())
                        .forceNewSheet(templateMeta.isForceNewSheet())
                        .autoCalculateFormula(templateMeta.isEnableFormula())
                        .build())
                .build();
    }

    /**
     * 获取注解处理器统计信息
     */
    public java.util.Map<String, Object> getAnnotationStatistics() {
        return annotationProcessor.getCacheStatistics();
    }

    /**
     * 清空注解处理器缓存
     */
    public void clearAnnotationCache() {
        annotationProcessor.clearCache();
        log.info("注解处理器缓存已清空");
    }
} 