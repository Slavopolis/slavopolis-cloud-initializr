package club.slavopolis.base.utils;

import club.slavopolis.base.enums.BizErrorCode;
import club.slavopolis.base.exception.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * HTTP 客户端工具，基于 Spring 6 RestClient 封装
 *
 * @author slavopolis
 * @version 2.0.0
 * @since 2025-06-25
 * <p>
 * Copyright (c) 2025 Slavopolis Cloud Platform
 * All rights reserved.
 */
@Slf4j
public final class RestUtil implements AutoCloseable {

    // ================================ 常量定义 ================================

    /**
     * 默认用户代理字符串
     */
    private static final String DEFAULT_USER_AGENT = "Slavopolis-RestClient/1.0";

    /**
     * 默认连接超时时间（10秒）
     */
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * 默认读取超时时间（30秒）
     */
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);

    /**
     * 默认最大重试次数（3次）
     */
    private static final int DEFAULT_MAX_RETRIES = 3;

    /**
     * 默认重试间隔时间（1秒）
     */
    private static final Duration DEFAULT_RETRY_DELAY = Duration.ofMillis(1000);

    /**
     * 默认最大内存限制（10MB）
     * 单位：字节
     */
    private static final long DEFAULT_MAX_MEMORY_SIZE = 10 * 1024 * 1024L;

    /**
     * 默认缓冲区大小（8KB）
     */
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    // ================================ 核心组件 ================================

    /**
     * Spring RestClient 实例，用于执行实际的HTTP请求
     */
    private final RestClient restClient;

    /**
     * 客户端配置信息，包含超时、重试、缓冲区等配置
     */
    private final ClientConfiguration configuration;

    /**
     * 请求拦截器列表，线程安全集合
     */
    private final List<RequestInterceptor> requestInterceptors = new CopyOnWriteArrayList<>();

    /**
     * 响应拦截器列表，线程安全集合
     */
    private final List<ResponseInterceptor> responseInterceptors = new CopyOnWriteArrayList<>();

    /**
     * 异步执行器，用于异步请求处理
     */
    private final Executor asyncExecutor;

    /**
     * 客户端关闭状态标识，使用volatile保证可见性
     */
    private volatile boolean closed = false;

    // ================================ 配置类 ================================

    /**
     * 客户端配置
     * <p>
     * 包含HTTP客户端的基础配置参数：
     * <ul>
     *   <li>连接超时设置</li>
     *   <li>读取超时设置</li>
     *   <li>重试策略配置</li>
     *   <li>内存限制配置</li>
     *   <li>用户代理设置</li>
     *   <li>缓冲区大小</li>
     * </ul>
     *
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读取超时时间
     * @param retryConfig    重试配置
     * @param maxMemorySize  最大内存大小
     * @param userAgent      用户代理字符串
     * @param bufferSize     缓冲区大小
     */
    public record ClientConfiguration(
            Duration connectTimeout,
            Duration readTimeout,
            RetryConfiguration retryConfig,
            long maxMemorySize,
            String userAgent,
            int bufferSize
    ) {

        /**
         * 创建默认配置
         *
         * @return 默认客户端配置
         */
        public static ClientConfiguration defaultConfig() {
            return new ClientConfiguration(
                    DEFAULT_CONNECT_TIMEOUT,
                    DEFAULT_READ_TIMEOUT,
                    RetryConfiguration.defaultConfig(),
                    DEFAULT_MAX_MEMORY_SIZE,
                    DEFAULT_USER_AGENT,
                    DEFAULT_BUFFER_SIZE
            );
        }

        /**
         * 创建配置构建器
         *
         * @return 配置构建器实例
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * 客户端配置构建器
         */
        public static class Builder {
            /**
             * 连接超时时间
             */
            private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;

            /**
             * 读取超时时间
             */
            private Duration readTimeout = DEFAULT_READ_TIMEOUT;

            /**
             * 重试配置
             */
            private RetryConfiguration retryConfig = RetryConfiguration.defaultConfig();

            /**
             * 最大内存大小
             */
            private long maxMemorySize = DEFAULT_MAX_MEMORY_SIZE;

            /**
             * 用户代理字符串
             */
            private String userAgent = DEFAULT_USER_AGENT;

            /**
             * 缓冲区大小
             */
            private int bufferSize = DEFAULT_BUFFER_SIZE;

            /**
             * 设置连接超时时间
             *
             * @param timeout 超时时间
             * @return 构建器实例
             */
            public Builder connectTimeout(Duration timeout) {
                this.connectTimeout = timeout;
                return this;
            }

            /**
             * 设置读取超时时间
             *
             * @param timeout 超时时间
             * @return 构建器实例
             */
            public Builder readTimeout(Duration timeout) {
                this.readTimeout = timeout;
                return this;
            }

            /**
             * 设置重试配置
             *
             * @param config 重试配置
             * @return 构建器实例
             */
            public Builder retryConfig(RetryConfiguration config) {
                this.retryConfig = config;
                return this;
            }

            /**
             * 设置最大内存大小
             *
             * @param maxSize 最大内存大小（字节）
             * @return 构建器实例
             */
            public Builder maxMemorySize(long maxSize) {
                this.maxMemorySize = maxSize;
                return this;
            }

            /**
             * 设置用户代理字符串
             *
             * @param userAgent 用户代理
             * @return 构建器实例
             */
            public Builder userAgent(String userAgent) {
                this.userAgent = userAgent;
                return this;
            }

            /**
             * 设置缓冲区大小
             *
             * @param bufferSize 缓冲区大小（字节）
             * @return 构建器实例
             */
            public Builder bufferSize(int bufferSize) {
                this.bufferSize = bufferSize;
                return this;
            }

            /**
             * 构建客户端配置
             *
             * @return 客户端配置实例
             */
            public ClientConfiguration build() {
                return new ClientConfiguration(
                        connectTimeout, readTimeout, retryConfig,
                        maxMemorySize, userAgent, bufferSize
                );
            }
        }
    }

    /**
     * 重试配置
     * <p>
     * 定义HTTP请求失败时的重试行为：
     * <ul>
     *   <li>最大重试次数</li>
     *   <li>初始延迟时间</li>
     *   <li>最大延迟时间</li>
     *   <li>退避倍数</li>
     *   <li>重试条件判断</li>
     * </ul>
     *
     * @param maxRetries        最大重试次数
     * @param initialDelay      初始延迟时间
     * @param maxDelay          最大延迟时间
     * @param backoffMultiplier 退避倍数
     * @param retryPredicate    重试条件判断函数
     */
    public record RetryConfiguration(
            int maxRetries,
            Duration initialDelay,
            Duration maxDelay,
            double backoffMultiplier,
            Function<Throwable, Boolean> retryPredicate
    ) {

        public static RetryConfiguration defaultConfig() {
            return new RetryConfiguration(
                    DEFAULT_MAX_RETRIES,
                    DEFAULT_RETRY_DELAY,
                    Duration.ofSeconds(30),
                    2.0,
                    RestUtil::shouldRetry
            );
        }

        public static RetryConfiguration noRetry() {
            return new RetryConfiguration(0, Duration.ZERO, Duration.ZERO, 1.0, ex -> false);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int maxRetries = DEFAULT_MAX_RETRIES;
            private Duration initialDelay = DEFAULT_RETRY_DELAY;
            private Duration maxDelay = Duration.ofSeconds(30);
            private double backoffMultiplier = 2.0;
            private Function<Throwable, Boolean> retryPredicate = RestUtil::shouldRetry;

            public Builder maxRetries(int maxRetries) {
                this.maxRetries = maxRetries;
                return this;
            }

            public Builder initialDelay(Duration delay) {
                this.initialDelay = delay;
                return this;
            }

            public Builder maxDelay(Duration delay) {
                this.maxDelay = delay;
                return this;
            }

            public Builder backoffMultiplier(double multiplier) {
                this.backoffMultiplier = multiplier;
                return this;
            }

            public Builder retryPredicate(Function<Throwable, Boolean> predicate) {
                this.retryPredicate = predicate;
                return this;
            }

            public RetryConfiguration build() {
                return new RetryConfiguration(
                        maxRetries, initialDelay, maxDelay, backoffMultiplier, retryPredicate
                );
            }
        }
    }

    // ================================ 拦截器接口 ================================

    /**
     * 请求拦截器
     */
    @FunctionalInterface
    public interface RequestInterceptor {
        void intercept(RequestContext context) throws HttpClientException;
    }

    /**
     * 响应拦截器
     */
    @FunctionalInterface
    public interface ResponseInterceptor {
        void intercept(ResponseContext context) throws HttpClientException;
    }

    /**
     * 请求上下文
     */
    @Setter
    @Getter
    public static class RequestContext {

        /**
         * 请求方法
         */
        private final String method;

        /**
         * 请求URL
         */
        private final String url;

        /**
         * 请求头
         */
        private final HttpHeaders headers;

        /**
         * 请求体
         */
        private Object body;

        /**
         * 请求属性
         */
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        /**
         * 构造函数
         *
         * @param method 请求方法
         * @param url    请求URL
         * @param headers 请求头
         */
        public RequestContext(String method, String url, HttpHeaders headers) {
            this.method = method;
            this.url = url;
            this.headers = new HttpHeaders();
            this.headers.addAll(headers);
        }

    }

    /**
     * 响应上下文记录
     */
    public record ResponseContext(HttpStatusCode statusCode, HttpHeaders headers, Object body, Duration duration) {
    }

    // ================================ 构造函数 ================================

    /**
     * 默认构造函数
     */
    public RestUtil() {
        this(ClientConfiguration.defaultConfig());
    }

    /**
     * 带配置的构造函数
     *
     * @param configuration 客户端配置
     */
    public RestUtil(ClientConfiguration configuration) {
        this.configuration = configuration;
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, configuration.userAgent())
                .build();
        this.asyncExecutor = ForkJoinPool.commonPool();
    }

    /**
     * 自定义 RestClient 构造函数
     *
     * @param restClient    Spring RestClient实例
     * @param configuration 客户端配置
     */
    public RestUtil(RestClient restClient, ClientConfiguration configuration) {
        this.restClient = restClient;
        this.configuration = configuration;
        this.asyncExecutor = ForkJoinPool.commonPool();
    }

    // ================================ 核心 HTTP 方法 ================================

    /**
     * GET 请求 - 基础版本
     *
     * @param url          请求URL
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T get(String url, Class<T> responseType) {
        return execute(HttpMethod.GET, url, null, null, responseType, configuration.retryConfig());
    }

    /**
     * GET 请求 - 带URI变量
     *
     * @param url          请求URL
     * @param uriVariables URI变量
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T get(String url, Map<String, Object> uriVariables, Class<T> responseType) {
        return execute(HttpMethod.GET, url, null, uriVariables, responseType, configuration.retryConfig());
    }

    /**
     * GET 请求 - 参数化类型
     *
     * @param url          请求URL
     * @param responseType 参数化响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T get(String url, ParameterizedTypeReference<T> responseType) {
        return execute(HttpMethod.GET, url, null, null, responseType, configuration.retryConfig());
    }

    /**
     * POST 请求 - 基础版本
     *
     * @param url          请求URL
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T post(String url, Object requestBody, Class<T> responseType) {
        return execute(HttpMethod.POST, url, requestBody, null, responseType, configuration.retryConfig());
    }

    /**
     * POST 请求 - 参数化类型
     *
     * @param url          请求URL
     * @param requestBody  请求体
     * @param responseType 参数化响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T post(String url, Object requestBody, ParameterizedTypeReference<T> responseType) {
        return execute(HttpMethod.POST, url, requestBody, null, responseType, configuration.retryConfig());
    }

    /**
     * PUT 请求 - 有返回值
     *
     * @param url          请求URL
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T put(String url, Object requestBody, Class<T> responseType) {
        return execute(HttpMethod.PUT, url, requestBody, null, responseType, configuration.retryConfig());
    }

    /**
     * PUT 请求 - 无返回值
     *
     * @param url         请求URL
     * @param requestBody 请求体
     * @throws HttpClientException HTTP客户端异常
     */
    public void put(String url, Object requestBody) {
        execute(HttpMethod.PUT, url, requestBody, null, Void.class, configuration.retryConfig());
    }

    /**
     * DELETE 请求 - 无返回值
     *
     * @param url 请求URL
     * @throws HttpClientException HTTP客户端异常
     */
    public void delete(String url) {
        execute(HttpMethod.DELETE, url, null, null, Void.class, configuration.retryConfig());
    }

    /**
     * DELETE 请求 - 有返回值
     *
     * @param url          请求URL
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T delete(String url, Class<T> responseType) {
        return execute(HttpMethod.DELETE, url, null, null, responseType, configuration.retryConfig());
    }

    /**
     * PATCH 请求
     *
     * @param url          请求URL
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T patch(String url, Object requestBody, Class<T> responseType) {
        return execute(HttpMethod.PATCH, url, requestBody, null, responseType, configuration.retryConfig());
    }

    // ================================ multipart/form-data 请求支持 ================================

    /**
     * 发送 multipart/form-data 请求
     *
     * @param url          请求URL
     * @param parts        表单数据部分
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T postMultipart(String url, MultiValueMap<String, Object> parts, Class<T> responseType) {
        return execute(HttpMethod.POST, url, parts, null, responseType, configuration.retryConfig());
    }

    /**
     * 发送 multipart/form-data 请求 - 参数化类型
     *
     * @param url          请求URL
     * @param parts        表单数据部分
     * @param responseType 参数化响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> T postMultipart(String url, MultiValueMap<String, Object> parts, ParameterizedTypeReference<T> responseType) {
        return execute(HttpMethod.POST, url, parts, null, responseType, configuration.retryConfig());
    }

    // ================================ 异步方法 ================================

    /**
     * 异步 GET 请求
     *
     * @param url          请求URL
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 异步Future
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> CompletableFuture<T> getAsync(String url, Class<T> responseType) {
        return executeAsync(HttpMethod.GET, url, null, responseType, configuration.retryConfig());
    }

    /**
     * 异步 POST 请求
     *
     * @param url          请求URL
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 异步Future
     * @throws HttpClientException HTTP客户端异常
     */
    public <T> CompletableFuture<T> postAsync(String url, Object requestBody, Class<T> responseType) {
        return executeAsync(HttpMethod.POST, url, requestBody, responseType, configuration.retryConfig());
    }

    // ================================ 拦截器管理 ================================

    /**
     * 添加请求拦截器
     *
     * @param interceptor 请求拦截器
     * @return 当前实例，支持链式调用
     */
    public RestUtil addRequestInterceptor(RequestInterceptor interceptor) {
        this.requestInterceptors.add(interceptor);
        return this;
    }

    /**
     * 添加响应拦截器
     *
     * @param interceptor 响应拦截器
     * @return 当前实例，支持链式调用
     */
    public RestUtil addResponseInterceptor(ResponseInterceptor interceptor) {
        this.responseInterceptors.add(interceptor);
        return this;
    }

    // ================================ 私有核心方法 ================================

    /**
     * 核心执行方法 - Class类型重载
     *
     * @param method       HTTP方法
     * @param url          请求URL
     * @param body         请求体
     * @param uriVariables URI变量
     * @param responseType 响应类型
     * @param retryConfig  重试配置
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    private <T> T execute(HttpMethod method, String url, Object body, Map<String, Object> uriVariables,
                          Class<T> responseType, RetryConfiguration retryConfig) {
        return execute(method, url, body, uriVariables, ParameterizedTypeReference.forType(responseType), retryConfig);
    }

    /**
     * 核心执行方法 - 参数化类型
     *
     * @param method       HTTP方法
     * @param url          请求URL
     * @param body         请求体
     * @param uriVariables URI变量
     * @param responseType 参数化响应类型
     * @param retryConfig  重试配置
     * @param <T>          响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    private <T> T execute(HttpMethod method, String url, Object body, Map<String, Object> uriVariables,
                          ParameterizedTypeReference<T> responseType, RetryConfiguration retryConfig) {
        checkClosed();

        RequestContext requestContext = createRequestContext(method, url, body);
        executeRequestInterceptors(requestContext);

        return performWithRetry(() -> executeRequest(requestContext, uriVariables, responseType), retryConfig);
    }

    /**
     * 创建请求上下文
     *
     * @param method HTTP方法
     * @param url    请求URL
     * @param body   请求体
     * @return 请求上下文
     */
    private RequestContext createRequestContext(HttpMethod method, String url, Object body) {
        RequestContext requestContext = new RequestContext(method.name(), url, new HttpHeaders());
        requestContext.setBody(body);
        return requestContext;
    }

    /**
     * 执行请求拦截器
     *
     * @param requestContext 请求上下文
     * @throws HttpClientException 拦截器执行异常
     */
    private void executeRequestInterceptors(RequestContext requestContext) {
        for (RequestInterceptor interceptor : requestInterceptors) {
            interceptor.intercept(requestContext);
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param requestContext 请求上下文
     * @param uriVariables   URI变量
     * @param responseType   响应类型
     * @param <T>            响应数据类型
     * @return 响应数据
     * @throws HttpClientException HTTP客户端异常
     */
    private <T> T executeRequest(RequestContext requestContext, Map<String, Object> uriVariables,
                                 ParameterizedTypeReference<T> responseType) {
        Instant start = Instant.now();
        try {
            org.springframework.web.client.RestClient.RequestHeadersSpec<?> requestSpec = buildRequestSpec(requestContext, uriVariables);

            T result = retrieveResponse(requestSpec, responseType);

            Duration duration = Duration.between(start, Instant.now());
            executeResponseInterceptors(result, duration);

            return result;

        } catch (RestClientResponseException ex) {
            throw createBusinessException(ex, requestContext);
        } catch (Exception ex) {
            throw mapException(ex, requestContext);
        }
    }

    /**
     * 构建请求规范
     *
     * @param requestContext 请求上下文
     * @param uriVariables   URI变量
     * @return 请求规范
     */
    private org.springframework.web.client.RestClient.RequestHeadersSpec<?> buildRequestSpec(RequestContext requestContext, Map<String, Object> uriVariables) {
        HttpMethod method = HttpMethod.valueOf(requestContext.getMethod());
        org.springframework.web.client.RestClient.RequestBodyUriSpec requestSpec = restClient.method(method);

        org.springframework.web.client.RestClient.RequestBodySpec bodySpec = (uriVariables != null && !uriVariables.isEmpty())
                ? requestSpec.uri(requestContext.getUrl(), uriVariables)
                : requestSpec.uri(requestContext.getUrl());

        if (!requestContext.getHeaders().isEmpty()) {
            bodySpec = bodySpec.headers(h -> h.addAll(requestContext.getHeaders()));
        }

        return (requestContext.getBody() != null)
                ? bodySpec.body(requestContext.getBody())
                : bodySpec;
    }

    /**
     * 获取响应数据
     *
     * @param requestSpec  请求规范
     * @param responseType 响应类型
     * @param <T>          响应数据类型
     * @return 响应数据
     */
    private <T> T retrieveResponse(org.springframework.web.client.RestClient.RequestHeadersSpec<?> requestSpec, ParameterizedTypeReference<T> responseType) {
        if (responseType.getType().equals(Void.class)) {
            requestSpec.retrieve().toBodilessEntity();
            return null;
        } else {
            return requestSpec.retrieve().body(responseType);
        }
    }

    /**
     * 执行响应拦截器
     *
     * @param result   响应结果
     * @param duration 请求耗时
     * @param <T>      响应数据类型
     * @throws HttpClientException 拦截器执行异常
     */
    private <T> void executeResponseInterceptors(T result, Duration duration) {
        ResponseContext responseContext = new ResponseContext(HttpStatus.OK, new HttpHeaders(), result, duration);
        for (ResponseInterceptor interceptor : responseInterceptors) {
            interceptor.intercept(responseContext);
        }
    }


    /**
     * 创建业务异常
     *
     * @param ex             REST客户端响应异常
     * @param requestContext 请求上下文
     * @return HTTP业务异常
     */
    private HttpBusinessException createBusinessException(RestClientResponseException ex, RequestContext requestContext) {
        // 根据HTTP状态码选择合适的错误码
        BizErrorCode errorCode = ex.getStatusCode().is4xxClientError()
                ? BizErrorCode.HTTP_CLIENT_ERROR
                : BizErrorCode.HTTP_SERVER_ERROR;

        return new HttpBusinessException(
                "HTTP 请求失败: " + ex.getMessage(),
                errorCode,
                ex.getStatusCode(),
                requestContext.getUrl(),
                requestContext.getMethod()
        );
    }


    /**
     * 异步执行方法（简化版，移除固定参数）
     *
     * @param method       HTTP方法
     * @param url          请求URL
     * @param body         请求体
     * @param responseType 响应类型
     * @param retryConfig  重试配置
     * @param <T>          响应数据类型
     * @return 异步Future
     */
    private <T> CompletableFuture<T> executeAsync(HttpMethod method, String url, Object body,
                                                  Class<T> responseType, RetryConfiguration retryConfig) {
        return CompletableFuture.supplyAsync(() ->
                execute(method, url, body, null, responseType, retryConfig), asyncExecutor);
    }

    /**
     * 重试执行逻辑
     *
     * @param operation   可重试操作
     * @param retryConfig 重试配置
     * @param <T>         返回类型
     * @return 执行结果
     * @throws HttpClientException 执行失败异常
     */
    private <T> T performWithRetry(RetryableOperation<T> operation, RetryConfiguration retryConfig) {
        int attempt = 0;
        Duration currentDelay = retryConfig.initialDelay();
        HttpClientException lastException = null;

        while (attempt <= retryConfig.maxRetries()) {
            try {
                return operation.execute();
            } catch (HttpClientException ex) {
                lastException = ex;
                attempt++;

                boolean shouldRetry = attempt <= retryConfig.maxRetries() &&
                        Boolean.TRUE.equals(retryConfig.retryPredicate().apply(ex));

                if (shouldRetry) {
                    sleepWithBackoff(currentDelay);
                    currentDelay = calculateNextDelay(currentDelay, retryConfig);
                } else {
                    break;
                }
            }
        }

        throw new HttpClientException("请求失败，已重试 " + retryConfig.maxRetries() + " 次", lastException,
                BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED);
    }

    /**
     * 等待指定时间（处理中断异常）
     *
     * @param delay 等待时间
     * @throws HttpClientException 线程中断异常
     */
    private void sleepWithBackoff(Duration delay) {
        try {
            TimeUnit.MILLISECONDS.sleep(delay.toMillis());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new HttpClientException("请求被中断", ie, BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED);
        }
    }

    /**
     * 计算下次重试延迟时间
     *
     * @param currentDelay 当前延迟
     * @param retryConfig  重试配置
     * @return 下次延迟时间
     */
    private Duration calculateNextDelay(Duration currentDelay, RetryConfiguration retryConfig) {
        return Duration.ofMillis(Math.min(
                (long) (currentDelay.toMillis() * retryConfig.backoffMultiplier()),
                retryConfig.maxDelay().toMillis()
        ));
    }

    /**
     * 异常映射（带请求上下文）
     *
     * @param ex             原始异常
     * @param requestContext 请求上下文
     * @return HTTP客户端异常
     */
    private HttpClientException mapException(Throwable ex, RequestContext requestContext) {
        return switch (ex) {
            case SocketTimeoutException socketTimeoutException ->
                    new HttpTimeoutException("请求超时", ex, BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED, null, requestContext.getUrl(), requestContext.getMethod());
            case ConnectException connectException ->
                    new HttpNetworkException("网络连接失败", ex, BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED, null, requestContext.getUrl(), requestContext.getMethod());
            case IOException ioException ->
                    new HttpNetworkException("网络IO异常", ex, BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED, null, requestContext.getUrl(), requestContext.getMethod());
            case null, default -> new HttpClientException("未知异常", ex, BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED, null, requestContext.getUrl(), requestContext.getMethod());
        };
    }

    /**
     * 判断是否应该重试
     */
    private static boolean shouldRetry(Throwable ex) {
        if (ex instanceof RestClientResponseException responseEx) {
            HttpStatusCode statusCode = responseEx.getStatusCode();
            return statusCode.is5xxServerError() ||
                    statusCode.value() == HttpStatus.REQUEST_TIMEOUT.value() ||
                    statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value();
        }

        return ex instanceof java.io.IOException;
    }

    /**
     * 检查客户端是否已关闭
     */
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("RestClient 已关闭");
        }
    }

    // ================================ 可重试操作接口 ================================

    @FunctionalInterface
    private interface RetryableOperation<T> {
        T execute() throws HttpClientException;
    }

    // ================================ 资源管理 ================================


    /**
     * 资源清理，关闭客户端并清理相关资源
     */
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            requestInterceptors.clear();
            responseInterceptors.clear();
            log.info("RestClient 已关闭");
        }
    }

    // ================================ 静态工厂方法 ================================

    /**
     * 创建默认配置的HTTP客户端
     *
     * @return RestClient实例
     */
    public static RestUtil create() {
        return new RestUtil();
    }

    /**
     * 创建带自定义配置的HTTP客户端
     *
     * @param configuration 客户端配置
     * @return RestClient实例
     */
    public static RestUtil create(ClientConfiguration configuration) {
        return new RestUtil(configuration);
    }

    /**
     * 创建带Bearer认证的HTTP客户端
     *
     * @param baseUrl 基础URL
     * @param token   Bearer令牌
     * @return RestClient实例
     */
    public static RestUtil createWithAuth(String baseUrl, String token) {
        org.springframework.web.client.RestClient restClient = org.springframework.web.client.RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        return new RestUtil(restClient, ClientConfiguration.defaultConfig());
    }
} 