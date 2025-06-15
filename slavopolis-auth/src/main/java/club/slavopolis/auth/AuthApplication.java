package club.slavopolis.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Slavopolis Auth 认证授权服务启动类
 * 
 * <p>
 * 基于Spring Security + OAuth2的企业级认证中心
 * 提供统一的用户认证、权限管理和OAuth2授权服务
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/1/20
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {
        "club.slavopolis.auth",
        "club.slavopolis.common",
        "club.slavopolis.cache",
        "club.slavopolis.email",
        "club.slavopolis.jdbc"
})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
} 