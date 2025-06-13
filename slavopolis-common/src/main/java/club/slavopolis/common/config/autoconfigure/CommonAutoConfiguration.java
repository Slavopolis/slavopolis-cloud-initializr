package club.slavopolis.common.config.autoconfigure;

import club.slavopolis.common.config.JacksonConfig;
import club.slavopolis.common.handler.GlobalExceptionHandler;
import club.slavopolis.common.handler.ResponseAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Common 模块自动配置类
 */
@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ComponentScan(basePackages = "club.slavopolis.common")
@Import({JacksonConfig.class})
public class CommonAutoConfiguration {

    /**
     * 全局异常处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        log.info("Initializing GlobalExceptionHandler");
        return new GlobalExceptionHandler();
    }

    /**
     * 响应增强处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseAdvice responseAdvice(ObjectMapper objectMapper) {
        log.info("Initializing ResponseAdvice");
        return new ResponseAdvice(objectMapper);
    }
}
