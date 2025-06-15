package club.slavopolis.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Slavopolis Gateway 网关服务启动类
 * 
 * <p>
 * 基于Spring Cloud Gateway的统一API网关
 * 提供路由转发、统一鉴权、流量控制和熔断降级服务
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/1/20
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {
        "club.slavopolis.gateway",
        "club.slavopolis.common",
        "club.slavopolis.cache"
})
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
} 