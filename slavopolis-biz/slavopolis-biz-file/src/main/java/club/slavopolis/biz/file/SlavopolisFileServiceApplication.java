package club.slavopolis.biz.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件服务启动类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@SpringBootApplication(scanBasePackages = "club.slavopolis")
public class SlavopolisFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlavopolisFileServiceApplication.class, args);
    }
} 