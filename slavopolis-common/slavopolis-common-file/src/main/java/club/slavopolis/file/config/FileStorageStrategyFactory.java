package club.slavopolis.file.config;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.file.api.FileStorageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件存储策略工厂
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageStrategyFactory {

    private final List<FileStorageStrategy> strategies;

    /**
     * 获取存储策略映射
     *
     * @return 存储类型到策略实现的映射
     */
    public Map<StorageType, FileStorageStrategy> getStorageStrategies() {
        Map<StorageType, FileStorageStrategy> strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                    FileStorageStrategy::getStorageType,
                    strategy -> strategy,
                    (existing, replacement) -> {
                        log.warn("发现重复的存储策略: {}, 使用: {}", 
                            existing.getClass().getSimpleName(), replacement.getClass().getSimpleName());
                        return replacement;
                    }
                ));

        log.info("已加载存储策略: {}", strategyMap.keySet());
        return strategyMap;
    }

    /**
     * 根据存储类型获取策略实现
     *
     * @param storageType 存储类型
     * @return 策略实现
     */
    public FileStorageStrategy getStrategy(StorageType storageType) {
        return getStorageStrategies().get(storageType);
    }

    /**
     * 检查是否支持指定的存储类型
     *
     * @param storageType 存储类型
     * @return 是否支持
     */
    public boolean isSupported(StorageType storageType) {
        return getStorageStrategies().containsKey(storageType);
    }

    /**
     * 获取所有支持的存储类型
     *
     * @return 支持的存储类型列表
     */
    public List<StorageType> getSupportedStorageTypes() {
        return getStorageStrategies().keySet().stream().toList();
    }
} 