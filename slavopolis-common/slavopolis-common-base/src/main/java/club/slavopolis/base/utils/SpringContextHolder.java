package club.slavopolis.base.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Spring 上下文工具类：在 Spring 应用中提供全局访问 Spring 应用上下文（ApplicationContext）的能力。在项目的任何地方获取 Spring 容器
 * 管理的 Bean，而无需通过依赖注入的方式。
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/20
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static final Object LOCK = new Object();
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        // 双重检查锁定机制保证线程安全
        if (Objects.isNull(SpringContextHolder.applicationContext)) {
            synchronized (LOCK) {
                if (Objects.isNull(SpringContextHolder.applicationContext)) {
                    SpringContextHolder.applicationContext = applicationContext;
                }
            }
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        if (Objects.isNull(applicationContext)) {
            throw new IllegalStateException("ApplicationContext 未初始化");
        }
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        if (Objects.isNull(applicationContext)) {
            throw new IllegalStateException("ApplicationContext 未初始化");
        }
        return applicationContext.getBean(name, clazz);
    }
}
