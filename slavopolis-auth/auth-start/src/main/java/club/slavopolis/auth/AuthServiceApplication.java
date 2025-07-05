package club.slavopolis.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 鉴权服务启动类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/15
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
