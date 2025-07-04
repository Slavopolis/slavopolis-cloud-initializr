package club.slavopolis.file.config;

import club.slavopolis.base.enums.StorageType;
import club.slavopolis.base.properties.CurrentSystemProperties;
import club.slavopolis.file.api.FileService;
import club.slavopolis.file.api.FileStorageStrategy;
import club.slavopolis.file.repository.FileContentRepository;
import club.slavopolis.file.repository.FileInfoRepository;
import club.slavopolis.file.repository.FileUploadSessionRepository;
import club.slavopolis.file.repository.impl.FileContentRepositoryImpl;
import club.slavopolis.file.repository.impl.FileInfoRepositoryImpl;
import club.slavopolis.file.repository.impl.FileUploadSessionRepositoryImpl;
import club.slavopolis.file.service.FileServiceImpl;
import club.slavopolis.file.service.MultipartUploadManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;

/**
 * 文件模块自动配置类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(CurrentSystemProperties.class)
@ComponentScan(basePackages = {
        "club.slavopolis.file.storage",
        "club.slavopolis.file.service",
        "club.slavopolis.file.config",
        "club.slavopolis.file.repository"
})
@ConditionalOnProperty(
        prefix = "biz.file",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class FileAutoConfiguration {

    private final CurrentSystemProperties systemProperties;

    /**
     * 事务定义Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public TransactionDefinition defaultTransactionDefinition() {
        return new DefaultTransactionDefinition();
    }

    /**
     * 文件信息Repository Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public FileInfoRepository fileInfoRepository() {
        return new FileInfoRepositoryImpl(systemProperties);
    }

    /**
     * 文件上传会话Repository Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public FileUploadSessionRepository fileUploadSessionRepository() {
        return new FileUploadSessionRepositoryImpl(systemProperties);
    }

    /**
     * 文件内容Repository Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public FileContentRepository fileContentRepository() {
        return new FileContentRepositoryImpl(systemProperties);
    }

    /**
     * 存储策略映射Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public Map<StorageType, FileStorageStrategy> storageStrategies(FileStorageStrategyFactory strategyFactory) {
        return strategyFactory.getStorageStrategies();
    }

    /**
     * 分片上传管理器Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public MultipartUploadManager multipartUploadManager(
            Map<StorageType, FileStorageStrategy> storageStrategies,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            DataSourceTransactionManager transactionManager,
            TransactionDefinition defaultTransactionDefinition,
            CurrentSystemProperties systemProperties,
            FileUploadSessionRepository fileUploadSessionRepository,
            FileInfoRepository fileInfoRepository) {
        return new MultipartUploadManager(
                storageStrategies,
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition,
                systemProperties,
                fileUploadSessionRepository,
                fileInfoRepository
        );
    }

    /**
     * 文件服务Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public FileService fileService(
            Map<StorageType, FileStorageStrategy> storageStrategies,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            DataSourceTransactionManager transactionManager,
            TransactionDefinition defaultTransactionDefinition,
            MultipartUploadManager multipartUploadManager,
            FileInfoRepository fileInfoRepository) {
        return new FileServiceImpl(
                storageStrategies,
                namedParameterJdbcTemplate,
                transactionManager,
                defaultTransactionDefinition,
                systemProperties,
                multipartUploadManager,
                fileInfoRepository
        );
    }
} 