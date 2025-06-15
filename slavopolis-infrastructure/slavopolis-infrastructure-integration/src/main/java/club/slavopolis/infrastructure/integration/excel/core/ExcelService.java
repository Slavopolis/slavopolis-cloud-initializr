package club.slavopolis.infrastructure.integration.excel.core;

import club.slavopolis.infrastructure.integration.excel.enums.ExcelTypeEnum;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelFillRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelMultiSheetWriteRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelReadRequest;
import club.slavopolis.infrastructure.integration.excel.model.request.ExcelWriteRequest;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelFillResult;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelReadResult;
import club.slavopolis.infrastructure.integration.excel.model.response.ExcelWriteResult;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel服务统一接口，提供读、写、填充的顶层抽象
 */
public interface ExcelService {

    // ==================== 读取操作 ====================

    /**
     * 同步读取Excel文件
     *
     * @param request 读取请求
     * @param <T>     数据类型
     * @return 读取结果
     */
    <T> ExcelReadResult<T> read(ExcelReadRequest<T> request);

    /**
     * 异步读取Excel文件
     *
     * @param request 读取请求
     * @param <T>     数据类型
     * @return 读取结果的Future
     */
    <T> CompletableFuture<ExcelReadResult<T>> readAsync(ExcelReadRequest<T> request);

    /**
     * 流式读取Excel文件（内存友好）
     *
     * @param request 读取请求
     * @param <T>     数据类型
     * @return 数据流
     */
    <T> Stream<T> readAsStream(ExcelReadRequest<T> request);

    /**
     * 读取多个Sheet
     *
     * @param request 读取请求
     * @param <T>     数据类型
     * @return 多Sheet读取结果
     */
    <T> ExcelReadResult<T> readMultiSheet(ExcelReadRequest<T> request);

    // ==================== 写入操作 ====================

    /**
     * 写入Excel文件（单Sheet）
     *
     * @param request 写入请求
     * @return 写入结果
     */
    ExcelWriteResult write(ExcelWriteRequest request);

    /**
     * 异步写入Excel文件
     *
     * @param request 写入请求
     * @return 写入结果的Future
     */
    CompletableFuture<ExcelWriteResult> writeAsync(ExcelWriteRequest request);

    /**
     * 写入多个Sheet
     *
     * @param request 多Sheet写入请求
     * @return 写入结果
     */
    ExcelWriteResult writeMultiSheet(ExcelMultiSheetWriteRequest request);

    /**
     * 流式写入Excel文件（大数据量）
     *
     * @param request 写入请求
     * @return 写入结果
     */
    ExcelWriteResult writeStream(ExcelWriteRequest request);

    // ==================== 填充操作 ====================

    /**
     * 基于模板填充Excel文件
     *
     * @param request 填充请求
     * @return 填充结果
     */
    ExcelFillResult fill(ExcelFillRequest request);

    /**
     * 异步填充Excel文件
     *
     * @param request 填充请求
     * @return 填充结果的Future
     */
    CompletableFuture<ExcelFillResult> fillAsync(ExcelFillRequest request);

    /**
     * 基于已注册的模板进行填充
     *
     * @param templateName 模板名称
     * @param data         填充数据
     * @return 填充结果
     */
    ExcelFillResult fillWithTemplate(String templateName, Object data);

    // ==================== 工具方法 ====================

    /**
     * 验证Excel文件格式
     *
     * @param fileName 文件名
     * @return 是否为有效的Excel文件
     */
    boolean isValidExcelFile(String fileName);

    /**
     * 检查文件大小是否需要异步处理
     *
     * @param fileSize 文件大小（字节）
     * @return 是否需要异步处理
     */
    boolean needAsyncProcess(long fileSize);

    /**
     * 获取Excel文件的基本信息
     *
     * @param filePath 文件路径
     * @return Excel文件信息
     */
    ExcelFileInfo getFileInfo(String filePath);

    /**
     * Excel文件信息
     */
    interface ExcelFileInfo {
        /**
         * 获取文件大小
         */
        long getFileSize();

        /**
         * 获取Sheet数量
         */
        int getSheetCount();

        /**
         * 获取Sheet名称列表
         */
        java.util.List<String> getSheetNames();

        /**
         * 获取Excel类型
         */
        ExcelTypeEnum getExcelType();

        /**
         * 是否为空文件
         */
        boolean isEmpty();
    }
} 