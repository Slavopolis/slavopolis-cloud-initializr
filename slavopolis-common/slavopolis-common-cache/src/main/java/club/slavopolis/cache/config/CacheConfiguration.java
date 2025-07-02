package club.slavopolis.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringBootConfiguration;

/**
 * 通用缓存配置
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@SpringBootConfiguration
@EnableMethodCache(basePackages = "club.slavopolis") // 启用方法缓存
public class CacheConfiguration {

}
