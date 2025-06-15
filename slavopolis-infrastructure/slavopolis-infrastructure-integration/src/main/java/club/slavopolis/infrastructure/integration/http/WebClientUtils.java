package club.slavopolis.infrastructure.integration.http;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.core.constants.HttpConstants;
import club.slavopolis.common.core.exception.ThirdPartyException;
import club.slavopolis.common.util.StringUtils;
import club.slavopolis.common.web.util.RequestUtil;
import club.slavopolis.infrastructure.integration.http.config.WebClientConfig;
import club.slavopolis.infrastructure.integration.http.config.properties.WebClientProperties;
import club.slavopolis.infrastructure.integration.http.model.HttpRequest;
import club.slavopolis.infrastructure.integration.http.model.HttpResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/13
 * @description: WebClient HTTP工具类
 */
@Slf4j
public class WebClientUtils {

    /**
     * 默认WebClient实例持有者（线程安全的单例模式）
     */
    private static class WebClientHolder {
        private static final WebClient INSTANCE = WebClientConfig.createWebClient(DEFAULT_PROPERTIES);
    }

    /**
     * WebClient 实例缓存
     */
    private static final Map<String, WebClient> WEB_CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认配置属性
     */
    private static final WebClientProperties DEFAULT_PROPERTIES = new WebClientProperties();

    /**
     * 私有构造函数
     */
    private WebClientUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取默认 WebClient 实例
     */
    private static WebClient getDefaultWebClient() {
        return WebClientHolder.INSTANCE;
    }

    /**
     * 创建自定义 WebClient 实例
     *
     * @param baseUrl 基础URL
     * @param customizer 自定义配置
     * @return WebClient实例
     */
    public static WebClient createWebClient(String baseUrl, Consumer<WebClient.Builder> customizer) {
        return WEB_CLIENT_CACHE.computeIfAbsent(baseUrl, url -> {
            WebClient.Builder builder = WebClient.builder().baseUrl(url);
            if (customizer != null) {
                customizer.accept(builder);
            }
            return builder.build();
        });
    }

    // ==================== GET请求 ====================

    /**
     * 发送GET请求（同步）
     *
     * @param url 请求URL
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T get(String url, Class<T> responseType) {
        return get(url, null, null, responseType);
    }

    /**
     * 发送GET请求（同步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T get(String url, Map<String, String> headers, Class<T> responseType) {
        return get(url, headers, null, responseType);
    }

    /**
     * 发送GET请求（同步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param queryParams 查询参数
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T get(String url, Map<String, String> headers, MultiValueMap<String, String> queryParams, Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.GET)
                .headers(headers)
                .queryParams(queryParams)
                .build();
        
        HttpResponse<T> response = execute(request, responseType);
        return response.getBody();
    }

    /**
     * 发送GET请求（异步）
     *
     * @param url 请求URL
     * @param responseType 响应类型
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> getAsync(String url, Class<T> responseType) {
        return getAsync(url, null, null, responseType);
    }

    /**
     * 发送GET请求（异步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param queryParams 查询参数
     * @param responseType 响应类型
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> getAsync(String url, Map<String, String> headers,
                                                    MultiValueMap<String, String> queryParams, 
                                                    Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.GET)
                .headers(headers)
                .queryParams(queryParams)
                .build();
        
        return executeAsync(request, responseType)
                .thenApply(HttpResponse::getBody);
    }

    // ==================== POST请求 ====================

    /**
     * 发送POST请求（同步）
     *
     * @param url 请求URL
     * @param body 请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T post(String url, Object body, Class<T> responseType) {
        return post(url, null, body, responseType);
    }

    /**
     * 发送POST请求（同步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param body 请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T post(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.POST)
                .headers(headers)
                .body(body)
                .build();
        
        HttpResponse<T> response = execute(request, responseType);
        return response.getBody();
    }

    /**
     * 发送POST请求（异步）
     *
     * @param url 请求URL
     * @param body 请求体
     * @param responseType 响应类型
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> postAsync(String url, Object body, Class<T> responseType) {
        return postAsync(url, null, body, responseType);
    }

    /**
     * 发送POST请求（异步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param body 请求体
     * @param responseType 响应类型
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> postAsync(String url, Map<String, String> headers, 
                                                     Object body, Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.POST)
                .headers(headers)
                .body(body)
                .build();
        
        return executeAsync(request, responseType)
                .thenApply(HttpResponse::getBody);
    }

    // ==================== PUT请求 ====================

    /**
     * 发送PUT请求（同步）
     *
     * @param url 请求URL
     * @param body 请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T put(String url, Object body, Class<T> responseType) {
        return put(url, null, body, responseType);
    }

    /**
     * 发送PUT请求（同步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param body 请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T put(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.PUT)
                .headers(headers)
                .body(body)
                .build();
        
        HttpResponse<T> response = execute(request, responseType);
        return response.getBody();
    }

    // ==================== DELETE请求 ====================

    /**
     * 发送DELETE请求（同步）
     *
     * @param url 请求URL
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T delete(String url, Class<T> responseType) {
        return delete(url, null, responseType);
    }

    /**
     * 发送DELETE请求（同步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T delete(String url, Map<String, String> headers, Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.DELETE)
                .headers(headers)
                .build();
        
        HttpResponse<T> response = execute(request, responseType);
        return response.getBody();
    }

    // ==================== 表单请求 ====================

    /**
     * 发送表单请求（同步）
     *
     * @param url 请求URL
     * @param formData 表单数据
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T postForm(String url, MultiValueMap<String, String> formData, Class<T> responseType) {
        return postForm(url, null, formData, responseType);
    }

    /**
     * 发送表单请求（同步）
     *
     * @param url 请求URL
     * @param headers 请求头
     * @param formData 表单数据
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> T postForm(String url, Map<String, String> headers, 
                                MultiValueMap<String, String> formData, Class<T> responseType) {
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.POST)
                .headers(headers)
                .body(formData)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .build();
        
        HttpResponse<T> response = execute(request, responseType);
        return response.getBody();
    }

    // ==================== 核心执行方法 ====================

    /**
     * 执行HTTP请求（同步）
     *
     * @param request 请求对象
     * @param responseType 响应类型
     * @return 响应结果
     */
    public static <T> HttpResponse<T> execute(HttpRequest request, Class<T> responseType) {
        try {
            return executeAsync(request, responseType).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThirdPartyException("HTTP", "请求被中断", e);
        } catch (Exception e) {
            throw new ThirdPartyException("HTTP", "请求执行失败", e);
        }
    }

    /**
     * 执行HTTP请求（同步）- 支持泛型
     *
     * @param request 请求对象
     * @param responseType 响应类型引用
     * @return 响应结果
     */
    public static <T> HttpResponse<T> execute(HttpRequest request, ParameterizedTypeReference<T> responseType) {
        try {
            return executeAsync(request, responseType).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThirdPartyException("HTTP", "请求被中断", e);
        } catch (Exception e) {
            throw new ThirdPartyException("HTTP", "请求执行失败", e);
        }
    }

    /**
     * 执行HTTP请求（异步）
     *
     * @param request 请求对象
     * @param responseType 响应类型
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<HttpResponse<T>> executeAsync(HttpRequest request, Class<T> responseType) {
        return executeMono(request, responseType).toFuture();
    }

    /**
     * 执行HTTP请求（异步）- 支持泛型
     *
     * @param request 请求对象
     * @param responseType 响应类型引用
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<HttpResponse<T>> executeAsync(HttpRequest request, ParameterizedTypeReference<T> responseType) {
        return executeMono(request, responseType).toFuture();
    }

    /**
     * 执行HTTP请求（响应式）
     *
     * @param request 请求对象
     * @param responseType 响应类型
     * @return Mono
     */
    public static <T> Mono<HttpResponse<T>> executeMono(HttpRequest request, Class<T> responseType) {
        return doExecute(request, bodySpec -> bodySpec.bodyToMono(responseType));
    }

    /**
     * 执行HTTP请求（响应式）- 支持泛型
     *
     * @param request 请求对象
     * @param responseType 响应类型引用
     * @return Mono
     */
    public static <T> Mono<HttpResponse<T>> executeMono(HttpRequest request, ParameterizedTypeReference<T> responseType) {
        return doExecute(request, bodySpec -> bodySpec.bodyToMono(responseType));
    }

    /**
     * 执行HTTP请求（响应式）- 返回Flux
     *
     * @param request 请求对象
     * @param responseType 响应类型
     * @return Flux
     */
    public static <T> Flux<T> executeFlux(HttpRequest request, Class<T> responseType) {
        WebClient webClient = getDefaultWebClient();
        
        return webClient.method(request.getMethod())
                .uri(buildUri(request))
                .headers(headers -> addHeaders(headers, request))
                .bodyValue(request.getBody() != null ? request.getBody() : CommonConstants.EMPTY)
                .retrieve()
                .bodyToFlux(responseType)
                .retryWhen(createRetrySpec(request))
                .doOnError(error -> handleError(request, error));
    }

    /**
     * 执行HTTP请求（带回调）
     *
     * @param request 请求对象
     * @param responseType 响应类型
     * @param callback 回调接口
     */
    public static <T> void executeWithCallback(HttpRequest request, Class<T> responseType, HttpCallback<T> callback) {
        executeMono(request, responseType)
                .subscribe(
                        response -> {
                            callback.onSuccess(response);
                            callback.onComplete();
                        },
                        error -> {
                            callback.onError(error);
                            callback.onComplete();
                        }
                );
    }

    /**
     * 核心执行逻辑
     */
    private static <T> Mono<HttpResponse<T>> doExecute(HttpRequest request, Function<WebClient.ResponseSpec, Mono<T>> bodyHandler) {
        WebClient webClient = getDefaultWebClient();
        Instant startTime = Instant.now();
        
        // 生成请求ID和追踪ID
        String requestId = StringUtils.isBlank(request.getRequestId())
                ? RequestUtil.generateRequestId() : request.getRequestId();
        String traceId = StringUtils.isBlank(request.getTraceId()) 
                ? RequestUtil.generateTraceId() : request.getTraceId();
        
        // 记录请求日志
        if (request.isLogRequest()) {
            log.info("HTTP请求开始 - RequestId: {}, TraceId: {}, Method: {}, URL: {}", 
                    requestId, traceId, request.getMethod(), request.getUrl());
        }
        
        return webClient.method(request.getMethod())
                .uri(buildUri(request))
                .headers(headers -> {
                    addHeaders(headers, request);
                    headers.add(HttpConstants.HEADER_REQUEST_ID, requestId);
                    headers.add(HttpConstants.HEADER_TRACE_ID, traceId);
                })
                .bodyValue(request.getBody() != null ? request.getBody() : CommonConstants.EMPTY)
                .exchangeToMono(response -> handleResponse(response, bodyHandler, request, startTime, requestId, traceId))
                .retryWhen(createRetrySpec(request))
                .doOnError(error -> handleError(request, error))
                .timeout(getTimeout(request));
    }



    /**
     * 构建请求URI
     * 
     * @param request 请求对象
     * @return URI构建函数
     */
    private static Function<UriBuilder, URI> buildUri(HttpRequest request) {
        return uriBuilder -> {
            uriBuilder.path(request.getUrl());
            if (request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
                uriBuilder.queryParams(request.getQueryParams());
            }
            if (request.getPathVariables() != null && !request.getPathVariables().isEmpty()) {
                Object[] values = request.getPathVariables().values().toArray();
                return uriBuilder.build(values);
            }
            return uriBuilder.build();
        };
    }

    /**
     * 添加请求头
     */
    private static void addHeaders(HttpHeaders headers, HttpRequest request) {
        // 设置内容类型
        if (request.getContentType() != null) {
            headers.setContentType(request.getContentType());
        }
        
        // 设置接受类型
        if (request.getAcceptType() != null) {
            headers.setAccept(List.of(request.getAcceptType()));
        }
        
        // 添加自定义请求头
        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            request.getHeaders().forEach(headers::add);
        }
    }



    /**
     * 处理响应
     */
    private static <T> Mono<HttpResponse<T>> handleResponse(ClientResponse response, 
                                                           Function<WebClient.ResponseSpec, Mono<T>> bodyHandler,
                                                           HttpRequest request,
                                                           Instant startTime,
                                                           String requestId,
                                                           String traceId) {
        HttpStatus status = HttpStatus.valueOf(response.statusCode().value());
        HttpHeaders headers = response.headers().asHttpHeaders();
        
        // 处理成功响应
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class)
                    .defaultIfEmpty(CommonConstants.EMPTY)
                    .flatMap(rawBody -> {
                        // 记录响应日志
                        if (request.isLogResponse()) {
                            Duration duration = Duration.between(startTime, Instant.now());
                            log.info("HTTP响应成功 - RequestId: {}, TraceId: {}, Status: {}, Duration: {}ms", 
                                    requestId, traceId, status.value(), duration.toMillis());
                        }
                        
                        // 转换响应体
                        return bodyHandler.apply(WebClient.create().get().retrieve())
                                .map(body -> HttpResponse.<T>builder()
                                        .status(status)
                                        .statusCode(status.value())
                                        .headers(headers)
                                        .body(body)
                                        .rawBody(rawBody)
                                        .requestTime(startTime)
                                        .responseTime(Instant.now())
                                        .duration(Duration.between(startTime, Instant.now()))
                                        .requestId(requestId)
                                        .traceId(traceId)
                                        .build());
                    });
        }
        
        // 处理错误响应
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(errorBody -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    log.error("HTTP响应失败 - RequestId: {}, TraceId: {}, Status: {}, Duration: {}ms, Error: {}", 
                            requestId, traceId, status.value(), duration.toMillis(), errorBody);
                    
                    return Mono.error(new ThirdPartyException("HTTP", 
                            String.format("请求失败，状态码: %d, 错误信息: %s", status.value(), errorBody)));
                });
    }

    /**
     * 创建重试策略
     */
    private static Retry createRetrySpec(HttpRequest request) {
        WebClientProperties properties = DEFAULT_PROPERTIES;
        int maxRetries = request.getMaxRetries() != null ? request.getMaxRetries() : properties.getMaxRetries();
        
        return Retry.backoff(maxRetries, Duration.ofMillis(properties.getRetryInterval()))
                .maxBackoff(Duration.ofMillis(properties.getMaxRetryInterval()))
                .filter(throwable -> {
                    // 只对特定异常进行重试
                    if (throwable instanceof WebClientResponseException responseException) {
                        // 5xx错误和特定的4xx错误进行重试
                        return responseException.getStatusCode().is5xxServerError() ||
                               responseException.getStatusCode().value() == 429; // Too Many Requests
                    }
                    // 网络异常等进行重试
                    return throwable instanceof java.io.IOException;
                })
                .doBeforeRetry(retrySignal -> log.warn("HTTP请求重试 - 第{}次重试, 错误: {}", retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()));
    }

    /**
     * 处理错误
     */
    private static void handleError(HttpRequest request, Throwable error) {
        if (error instanceof WebClientResponseException responseException) {
            log.error("HTTP请求失败 - URL: {}, Status: {}, Error: {}", 
                    request.getUrl(), responseException.getStatusCode(), responseException.getResponseBodyAsString());
        } else {
            log.error("HTTP请求异常 - URL: {}, Error: {}", request.getUrl(), error.getMessage(), error);
        }
    }

    /**
     * 获取超时时间
     */
    private static Duration getTimeout(HttpRequest request) {
        if (request.getTimeout() != null) {
            return request.getTimeout();
        }
        return Duration.ofMillis(DEFAULT_PROPERTIES.getResponseTimeout());
    }

    // ==================== 便捷方法 ====================

    /**
     * 下载文件
     *
     * @param url 文件URL
     * @return 文件字节数组
     */
    public static byte[] download(String url) {
        return get(url, byte[].class);
    }

    /**
     * 下载文件（异步）
     *
     * @param url 文件URL
     * @return CompletableFuture
     */
    public static CompletableFuture<byte[]> downloadAsync(String url) {
        return getAsync(url, byte[].class);
    }

    /**
     * 上传文件
     *
     * @param url 上传URL
     * @param fileData 文件数据
     * @param fileName 文件名
     * @return 响应结果
     */
    public static String upload(String url, byte[] fileData, String fileName) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", new org.springframework.core.io.ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        
        HttpRequest request = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.POST)
                .body(formData)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .build();
        
        return execute(request, String.class).getBody();
    }

    /**
     * 批量请求
     *
     * @param requests 请求列表
     * @param responseType 响应类型
     * @return 响应结果列表
     */
    public static <T> List<HttpResponse<T>> batchExecute(List<HttpRequest> requests, Class<T> responseType) {
        return Flux.fromIterable(requests)
                .flatMap(request -> executeMono(request, responseType))
                .collectList()
                .block();
    }

    /**
     * 并发请求（限制并发数）
     *
     * @param requests 请求列表
     * @param responseType 响应类型
     * @param concurrency 并发数
     * @return 响应结果列表
     */
    public static <T> List<HttpResponse<T>> concurrentExecute(List<HttpRequest> requests, 
                                                             Class<T> responseType, 
                                                             int concurrency) {
        return Flux.fromIterable(requests)
                .flatMap(request -> executeMono(request, responseType), concurrency)
                .collectList()
                .block();
    }

    /**
     * 健康检查
     *
     * @param url 健康检查URL
     * @return 是否健康
     */
    public static boolean healthCheck(String url) {
        try {
            HttpResponse<String> response = execute(
                    HttpRequest.builder()
                            .url(url)
                            .method(HttpMethod.GET)
                            .timeout(Duration.ofSeconds(5))
                            .maxRetries(0)
                            .build(),
                    String.class
            );
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 清理WebClient缓存
     */
    public static void clearCache() {
        WEB_CLIENT_CACHE.clear();
    }
} 