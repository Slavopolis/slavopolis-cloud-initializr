package club.slavopolis.auth.controller;

import club.slavopolis.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * <p>
 * 提供服务健康状态检查接口
 * 用于负载均衡器和监控系统检查服务可用性
 * </p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/1/20
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 基础健康检查
     * 
     * @return 健康状态信息
     */
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "slavopolis-auth");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("version", "1.0.0");
        
        log.debug("健康检查请求 - 服务状态正常");
        return Result.success(healthInfo);
    }

    /**
     * 详细健康检查
     * 
     * @return 详细健康状态信息
     */
    @GetMapping("/details")
    public Result<Map<String, Object>> healthDetails() {
        Map<String, Object> details = new HashMap<>();
        
        // 基础信息
        details.put("status", "UP");
        details.put("service", "slavopolis-auth");
        details.put("timestamp", LocalDateTime.now());
        details.put("version", "1.0.0");
        
        // 运行时信息
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> runtime_info = new HashMap<>();
        runtime_info.put("processors", runtime.availableProcessors());
        runtime_info.put("freeMemory", runtime.freeMemory());
        runtime_info.put("totalMemory", runtime.totalMemory());
        runtime_info.put("maxMemory", runtime.maxMemory());
        details.put("runtime", runtime_info);
        
        // JVM信息
        Map<String, Object> jvm_info = new HashMap<>();
        jvm_info.put("version", System.getProperty("java.version"));
        jvm_info.put("vendor", System.getProperty("java.vendor"));
        jvm_info.put("home", System.getProperty("java.home"));
        details.put("jvm", jvm_info);
        
        log.debug("详细健康检查请求");
        return Result.success(details);
    }
} 