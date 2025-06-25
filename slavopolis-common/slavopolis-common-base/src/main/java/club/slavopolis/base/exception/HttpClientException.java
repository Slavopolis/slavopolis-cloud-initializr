package club.slavopolis.base.exception;

import club.slavopolis.base.enums.ErrorCode;
import lombok.Getter;

/**
 * HTTP 客户端基础异常
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025-06-25
 * <p>
 * Copyright (c) 2025 Slavopolis Cloud Platform
 * All rights reserved.
 */
@Getter
public class HttpClientException extends SystemException {

    /**
     * HTTP状态码
     */
    private final Integer httpStatusCode;
    
    /**
     * 请求URL
     */
    private final String requestUrl;
    
    /**
     * 请求方法
     */
    private final String httpMethod;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public HttpClientException(ErrorCode errorCode) {
        super(errorCode);
        this.httpStatusCode = null;
        this.requestUrl = null;
        this.httpMethod = null;
    }

    /**
     * 构造函数
     *
     * @param message   异常消息
     * @param errorCode 错误码
     */
    public HttpClientException(String message, ErrorCode errorCode) {
        super(message, errorCode);
        this.httpStatusCode = null;
        this.requestUrl = null;
        this.httpMethod = null;
    }

    /**
     * 构造函数
     *
     * @param message   异常消息
     * @param cause     原始异常
     * @param errorCode 错误码
     */
    public HttpClientException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
        this.httpStatusCode = null;
        this.requestUrl = null;
        this.httpMethod = null;
    }

    /**
     * 带HTTP上下文的构造函数
     *
     * @param message        异常消息
     * @param cause          原始异常
     * @param errorCode      错误码
     * @param httpStatusCode HTTP状态码
     * @param requestUrl     请求URL
     * @param httpMethod     HTTP方法
     */
    public HttpClientException(String message, Throwable cause, ErrorCode errorCode, 
                             Integer httpStatusCode, String requestUrl, String httpMethod) {
        super(message, cause, errorCode);
        this.httpStatusCode = httpStatusCode;
        this.requestUrl = requestUrl;
        this.httpMethod = httpMethod;
    }

    /**
     * 获取完整的错误描述
     *
     * @return 错误描述
     */
    public String getFullErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP客户端异常");
        
        if (httpMethod != null && requestUrl != null) {
            sb.append(" [").append(httpMethod).append(" ").append(requestUrl).append("]");
        }
        
        if (httpStatusCode != null) {
            sb.append(" [状态码: ").append(httpStatusCode).append("]");
        }
        
        sb.append(": ").append(getMessage());
        
        return sb.toString();
    }
} 