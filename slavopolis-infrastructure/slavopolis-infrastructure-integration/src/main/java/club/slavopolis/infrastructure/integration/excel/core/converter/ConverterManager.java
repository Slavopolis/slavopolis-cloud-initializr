package club.slavopolis.infrastructure.integration.excel.core.converter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.excel.converters.Converter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 转换器管理器，统一管理所有数据转换器
 */
@Slf4j
@Component
public class ConverterManager {

    /**
     * 转换器缓存
     */
    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, ExcelDataConverter> converters = new ConcurrentHashMap<>();

    /**
     * 初始化默认转换器
     */
    public ConverterManager() {
        initDefaultConverters();
    }

    /**
     * 初始化默认转换器
     */
    private void initDefaultConverters() {
        // 注册默认转换器
        registerConverter(LocalDateTime.class, new LocalDateTimeConverter());
        log.info("默认转换器初始化完成，共注册{}个转换器", converters.size());
    }

    /**
     * 注册转换器
     */
    @SuppressWarnings("rawtypes")
    public <T> void registerConverter(Class<T> type, ExcelDataConverter<T> converter) {
        converters.put(type, converter);
        log.info("转换器已注册: {} -> {}", type.getSimpleName(), converter.getConverterName());
    }

    /**
     * 移除转换器
     */
    public void removeConverter(Class<?> type) {
        @SuppressWarnings("rawtypes")
        ExcelDataConverter removed = converters.remove(type);
        if (removed != null) {
            log.info("转换器已移除: {} -> {}", type.getSimpleName(), removed.getConverterName());
        }
    }

    /**
     * 获取转换器
     */
    @SuppressWarnings("unchecked")
    public <T> ExcelDataConverter<T> getConverter(Class<T> type) {
        return converters.get(type);
    }

    /**
     * 检查是否存在转换器
     */
    public boolean hasConverter(Class<?> type) {
        return converters.containsKey(type);
    }

    /**
     * 获取所有已注册的转换器
     */
    @SuppressWarnings("unchecked")
    public Map<Class<?>, ExcelDataConverter<Object>> getAllConverters() {
        Map<Class<?>, ExcelDataConverter<Object>> result = new ConcurrentHashMap<>();
        converters.forEach(result::put);
        return result;
    }

    /**
     * 获取EasyExcel兼容的转换器列表
     */
    @SuppressWarnings("unchecked")
    public List<Converter<Object>> getEasyExcelConverters() {
        return converters.values().stream()
                .map(converter -> (Converter<Object>) converter)
                .toList();
    }

    /**
     * 批量注册转换器
     */
    @SuppressWarnings("rawtypes")
    public void registerConverters(Map<Class<?>, ExcelDataConverter<Object>> converterMap) {
        converterMap.forEach((type, converter) -> {
            converters.put(type, converter);
            log.info("批量注册转换器: {} -> {}", type.getSimpleName(), converter.getConverterName());
        });
        
        log.info("批量注册完成，共注册{}个转换器", converterMap.size());
    }

    /**
     * 清空所有转换器
     */
    public void clearAllConverters() {
        int count = converters.size();
        converters.clear();
        log.info("已清空所有转换器，共清空{}个", count);
    }

    /**
     * 重新初始化
     */
    public void reinitialize() {
        clearAllConverters();
        initDefaultConverters();
        log.info("转换器管理器重新初始化完成");
    }

    /**
     * 获取转换器统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalConverters", converters.size());
        stats.put("converterTypes", converters.keySet().stream()
                .map(Class::getSimpleName)
                .toList());
        
        Map<String, String> converterDetails = new java.util.HashMap<>();
        converters.forEach((type, converter) -> converterDetails.put(type.getSimpleName(),
            converter.getConverterName() + " v" + converter.getVersion()));
        stats.put("converterDetails", converterDetails);
        
        return stats;
    }
} 