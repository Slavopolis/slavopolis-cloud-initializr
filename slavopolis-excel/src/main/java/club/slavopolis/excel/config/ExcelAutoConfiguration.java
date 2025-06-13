package club.slavopolis.excel.config;

import club.slavopolis.excel.core.ExcelService;
import club.slavopolis.excel.core.converter.ConverterManager;
import club.slavopolis.excel.core.processor.AnnotationProcessor;
import club.slavopolis.excel.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel自动配置类
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({com.alibaba.excel.EasyExcel.class})
@EnableConfigurationProperties(ExcelProperties.class)
@ComponentScan(basePackages = "club.slavopolis.excel")
@ConditionalOnProperty(prefix = "slavopolis.excel", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExcelAutoConfiguration {

    /**
     * Excel属性配置
     */
    private final ExcelProperties excelProperties;

    public ExcelAutoConfiguration(ExcelProperties excelProperties) {
        this.excelProperties = excelProperties;
        log.info("Excel自动配置已启用，配置: {}", excelProperties);
    }

    /**
     * 转换器管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ConverterManager converterManager() {
        log.info("注册ConverterManager Bean");
        return new ConverterManager();
    }

    /**
     * 注解处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public AnnotationProcessor annotationProcessor() {
        log.info("注册AnnotationProcessor Bean");
        return new AnnotationProcessor();
    }

    /**
     * Excel读取服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelReaderService excelReaderService() {
        log.info("注册ExcelReaderService Bean");
        return new ExcelReaderService();
    }

    /**
     * Excel写入服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelWriterService excelWriterService() {
        log.info("注册ExcelWriterService Bean");
        return new ExcelWriterService();
    }

    /**
     * Excel填充服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelFillerService excelFillerService() {
        log.info("注册ExcelFillerService Bean");
        return new ExcelFillerService();
    }

    /**
     * Excel统一服务实现
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelServiceImpl excelServiceImpl(
            ExcelReaderService readerService,
            ExcelWriterService writerService,
            ExcelFillerService fillerService) {
        
        log.info("注册ExcelServiceImpl Bean");
        return new ExcelServiceImpl(readerService, writerService, fillerService, excelProperties);
    }

    /**
     * Excel服务接口
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcelService excelService(ExcelServiceImpl excelServiceImpl) {
        log.info("注册ExcelService Bean");
        return excelServiceImpl;
    }

    /**
     * 基于注解的Excel服务
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "slavopolis.excel.annotation", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AnnotationExcelService annotationExcelService(
            ExcelServiceImpl excelService,
            AnnotationProcessor annotationProcessor) {
        
        log.info("注册AnnotationExcelService Bean");
        return new AnnotationExcelService(excelService, annotationProcessor);
    }
} 