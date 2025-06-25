package club.slavopolis.base.exception;

import club.slavopolis.base.enums.ErrorCode;
import org.springframework.http.HttpStatusCode;

/**
 * HTTP 业务异常
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-25
 * <p>
 * Copyright (c) 2025 Slavopolis Cloud Platform
 * All rights reserved.
 */
public class HttpBusinessException extends HttpClientException {

    /**
     * 构造函数
     *
     * @param message        异常消息
     * @param cause          原始异常
     * @param errorCode      错误码
     * @param httpStatusCode HTTP状态码
     * @param requestUrl     请求URL
     * @param httpMethod     HTTP方法
     */
    public HttpBusinessException(String message, Throwable cause, ErrorCode errorCode, 
                                Integer httpStatusCode, String requestUrl, String httpMethod) {
        super(message, cause, errorCode, httpStatusCode, requestUrl, httpMethod);
    }

    /**
     * 构造函数 - 带Spring HttpStatusCode
     *
     * @param message        异常消息
     * @param errorCode      错误码
     * @param httpStatusCode Spring HTTP状态码
     * @param requestUrl     请求URL
     * @param httpMethod     HTTP方法
     */
    public HttpBusinessException(String message, ErrorCode errorCode, HttpStatusCode httpStatusCode, 
                                String requestUrl, String httpMethod) {
        super(message, null, errorCode, httpStatusCode.value(), requestUrl, httpMethod);
    }

    /**
     * 构造函数
     *
     * @param message   异常消息
     * @param cause     原始异常
     * @param errorCode 错误码
     */
    public HttpBusinessException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
} 