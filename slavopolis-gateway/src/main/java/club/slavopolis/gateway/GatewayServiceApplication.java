package club.slavopolis.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务启动类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/15
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@SpringBootApplication(scanBasePackages = "club.slavopolis.gateway")
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
