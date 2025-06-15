package club.slavopolis.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 统一认证网关过滤器工厂
 * 
 * <p>
 * 负责在网关层进行统一的用户认证和授权验证
 * 支持JWT令牌验证、白名单路径过滤等功能
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/1/20
 */
@Slf4j
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * 默认白名单路径
     */
    private static final List<String> DEFAULT_WHITELIST_PATHS = Arrays.asList(
            "/auth/oauth2/",
            "/auth/login/",
            "/auth/register/",
            "/auth/captcha/",
            "/actuator/"
    );

    public AuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            
            log.debug("认证过滤器处理请求: {}", path);
            
            // 检查是否为白名单路径
            if (isWhitelistPath(path, config.getWhitelistPaths())) {
                log.debug("路径 {} 在白名单中，跳过认证", path);
                return chain.filter(exchange);
            }
            
            // 提取Authorization头
            String authorizationHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
            
            if (!StringUtils.hasText(authorizationHeader)) {
                log.warn("请求 {} 缺少Authorization头", path);
                return handleUnauthorized(exchange, "缺少Authorization头");
            }
            
            if (!authorizationHeader.startsWith(BEARER_TOKEN_PREFIX)) {
                log.warn("请求 {} 的Authorization头格式无效", path);
                return handleUnauthorized(exchange, "Authorization头格式无效");
            }
            
            String token = authorizationHeader.substring(BEARER_TOKEN_PREFIX.length());
            
            // 验证JWT令牌
            return validateToken(token, exchange)
                    .flatMap(isValid -> {
                        if (isValid) {
                            log.debug("令牌验证成功，继续处理请求: {}", path);
                            return chain.filter(exchange);
                        } else {
                            log.warn("令牌验证失败，拒绝请求: {}", path);
                            return handleUnauthorized(exchange, "令牌验证失败");
                        }
                    })
                    .doOnError(error -> log.error("认证过程中发生错误", error))
                    .onErrorResume(error -> handleUnauthorized(exchange, "认证服务异常"));
        };
    }

    /**
     * 检查路径是否在白名单中
     * 
     * @param path 请求路径
     * @param whitelistPaths 白名单路径配置
     * @return 是否在白名单中
     */
    private boolean isWhitelistPath(String path, List<String> whitelistPaths) {
        List<String> allWhitelistPaths = whitelistPaths != null && !whitelistPaths.isEmpty() 
                ? whitelistPaths 
                : DEFAULT_WHITELIST_PATHS;
        
        return allWhitelistPaths.stream()
                .anyMatch(whitelist -> path.startsWith(whitelist));
    }

    /**
     * 验证JWT令牌
     * 
     * @param token JWT令牌
     * @param exchange 服务器交换对象
     * @return 验证结果
     */
    private Mono<Boolean> validateToken(String token, ServerWebExchange exchange) {
        // TODO: 实际实现中应该：
        // 1. 从Redis中检查令牌是否存在且未过期
        // 2. 验证JWT签名和有效性
        // 3. 提取用户信息并设置到请求头中
        
        return Mono.fromCallable(() -> {
            try {
                // 这里是临时实现，实际应该调用认证服务或直接验证JWT
                log.debug("验证令牌: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
                
                // 临时验证逻辑 - 检查令牌不为空
                boolean isValid = StringUtils.hasText(token) && token.length() > 10;
                
                if (isValid) {
                    // 将用户信息添加到请求头中，供下游服务使用
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", "1")
                            .header("X-User-Name", "admin")
                            .header("X-User-Roles", "ADMIN")
                            .build();
                    
                    exchange.mutate().request(mutatedRequest).build();
                }
                
                return isValid;
            } catch (Exception e) {
                log.error("令牌验证过程中发生异常", e);
                return false;
            }
        });
    }

    /**
     * 处理未授权请求
     * 
     * @param exchange 服务器交换对象
     * @param message 错误消息
     * @return Mono<Void>
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        String responseBody = String.format(
                "{\"code\":401,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message,
                System.currentTimeMillis()
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(buffer));
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("whitelist-paths");
    }

    /**
     * 过滤器配置类
     */
    @Data
    public static class Config {
        /**
         * 白名单路径列表
         */
        private List<String> whitelistPaths;
    }
} 