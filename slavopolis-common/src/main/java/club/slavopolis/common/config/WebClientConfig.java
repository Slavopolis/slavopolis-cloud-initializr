package club.slavopolis.common.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import club.slavopolis.common.config.properties.WebClientProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import club.slavopolis.common.constant.HttpConstants;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/13
 * @description: WebClient配置类，提供企业级的HTTP客户端配置
 */
@Slf4j
@Configuration
@ConditionalOnClass(WebClient.class)
@EnableConfigurationProperties(WebClientProperties.class)
public class WebClientConfig {

    /**
     * 创建默认的WebClient实例
     *
     * @param properties WebClient配置属性
     * @return WebClient实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(WebClientProperties properties) {
        return createWebClient(properties);
    }

    /**
     * 创建WebClient实例
     *
     * @param properties 配置属性
     * @return WebClient实例
     */
    public static WebClient createWebClient(WebClientProperties properties) {
        // 创建连接池配置
        ConnectionProvider connectionProvider = ConnectionProvider.builder("slavopolis-http-pool")
                .maxConnections(properties.getMaxConnections())
                .maxIdleTime(Duration.ofSeconds(properties.getMaxIdleTime()))
                .maxLifeTime(Duration.ofSeconds(properties.getMaxLifeTime()))
                .pendingAcquireTimeout(Duration.ofSeconds(properties.getPendingAcquireTimeout()))
                .evictInBackground(Duration.ofSeconds(properties.getEvictInBackground()))
                .build();

        // 创建HttpClient
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(properties.getResponseTimeout()))
                .doOnConnected(conn -> {
                    // 添加读写超时处理器
                    conn.addHandlerLast(new ReadTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS));
                    conn.addHandlerLast(new WriteTimeoutHandler(properties.getWriteTimeout(), TimeUnit.MILLISECONDS));
                })
                // 启用压缩
                .compress(properties.isCompressionEnabled())
                // 启用重定向
                .followRedirect(properties.isFollowRedirect());

        // 创建WebClient
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, properties.getUserAgent())
                // 设置内存缓冲区大小限制（默认256KB）
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(properties.getMaxInMemorySize()))
                // 添加请求日志过滤器
                .filter(logRequest())
                // 添加响应日志过滤器
                .filter(logResponse())
                .build();
    }

    /**
     * 请求日志过滤器
     */
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                log.debug("HTTP请求: {} {}", clientRequest.method(), clientRequest.url());
                clientRequest.headers().forEach((name, values) -> {
                    if (!HttpConstants.HEADER_AUTHORIZATION.equalsIgnoreCase(name)) {
                        log.debug("请求头: {} = {}", name, values);
                    }
                });
            }
            return Mono.just(clientRequest);
        });
    }

    /**
     * 响应日志过滤器
     */
    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                log.debug("HTTP响应: 状态码={}", clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }
} 