package club.slavopolis.excel.core.processor;

import club.slavopolis.excel.annotation.ExcelField;
import club.slavopolis.excel.annotation.ExcelSheet;
import club.slavopolis.excel.annotation.ExcelTemplate;
import club.slavopolis.excel.core.converter.ExcelDataConverter;
import club.slavopolis.excel.model.metadata.ExcelFieldMeta;
import club.slavopolis.excel.model.metadata.ExcelSheetMeta;
import club.slavopolis.excel.model.metadata.ExcelTemplateMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel注解处理器，解析类和字段上的Excel注解
 */
@Slf4j
@Component
public class AnnotationProcessor {

    /**
     * 类元数据缓存
     */
    private final Map<Class<?>, ExcelSheetMeta> sheetMetaCache = new ConcurrentHashMap<>();

    /**
     * 模板元数据缓存
     */
    private final Map<Class<?>, ExcelTemplateMeta> templateMetaCache = new ConcurrentHashMap<>();

    /**
     * 解析Excel Sheet元数据
     */
    public ExcelSheetMeta parseSheetMeta(Class<?> clazz) {
        return sheetMetaCache.computeIfAbsent(clazz, this::doParseSheetMeta);
    }

    /**
     * 解析Excel Template元数据
     */
    public ExcelTemplateMeta parseTemplateMeta(Class<?> clazz) {
        return templateMetaCache.computeIfAbsent(clazz, this::doParseTemplateMeta);
    }

    /**
     * 检查类是否有Excel注解
     */
    public boolean hasExcelAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(ExcelSheet.class) || 
               clazz.isAnnotationPresent(ExcelTemplate.class) ||
               hasExcelFieldAnnotation(clazz);
    }

    /**
     * 检查类是否有ExcelField注解的字段
     */
    public boolean hasExcelFieldAnnotation(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelField.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有Excel字段元数据
     */
    public List<ExcelFieldMeta> getExcelFields(Class<?> clazz) {
        List<ExcelFieldMeta> fieldMetas = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            ExcelField annotation = field.getAnnotation(ExcelField.class);
            if (annotation != null && !annotation.ignore()) {
                ExcelFieldMeta fieldMeta = buildFieldMeta(field, annotation);
                fieldMetas.add(fieldMeta);
            }
        }
        
        // 按order排序
        fieldMetas.sort(Comparator.comparing(ExcelFieldMeta::getOrder));
        
        log.debug("解析类[{}]的Excel字段，共{}个", clazz.getSimpleName(), fieldMetas.size());
        return fieldMetas;
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        sheetMetaCache.clear();
        templateMetaCache.clear();
        log.info("注解处理器缓存已清空");
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("sheetMetaCacheSize", sheetMetaCache.size());
        stats.put("templateMetaCacheSize", templateMetaCache.size());
        stats.put("cachedClasses", sheetMetaCache.keySet().stream()
                .map(Class::getSimpleName)
                .toList());
        return stats;
    }

    /**
     * 执行Sheet元数据解析
     */
    private ExcelSheetMeta doParseSheetMeta(Class<?> clazz) {
        ExcelSheet sheetAnnotation = clazz.getAnnotation(ExcelSheet.class);
        List<ExcelFieldMeta> fieldMetas = getExcelFields(clazz);
        
        ExcelSheetMeta.ExcelSheetMetaBuilder builder = ExcelSheetMeta.builder()
                .entityClass(clazz)
                .fields(fieldMetas);
        
        if (sheetAnnotation != null) {
            builder.sheetName(sheetAnnotation.value().isEmpty() ? clazz.getSimpleName() : sheetAnnotation.value())
                   .sheetIndex(sheetAnnotation.index())
                   .headerIndex(sheetAnnotation.headerIndex())
                   .dataStartIndex(sheetAnnotation.dataStartIndex())
                   .includeHeader(sheetAnnotation.includeHeader())
                   .ignoreEmptyRow(sheetAnnotation.ignoreEmptyRow())
                   .autoTrim(sheetAnnotation.autoTrim())
                   .maxRows(sheetAnnotation.maxRows())
                   .maxColumns(sheetAnnotation.maxColumns())
                   .password(sheetAnnotation.password())
                   .enableValidation(sheetAnnotation.enableValidation())
                   .failFast(sheetAnnotation.failFast())
                   .description(sheetAnnotation.description());
        } else {
            // 使用默认值
            builder.sheetName(clazz.getSimpleName())
                   .sheetIndex(0)
                   .headerIndex(0)
                   .dataStartIndex(1)
                   .includeHeader(true)
                   .ignoreEmptyRow(true)
                   .autoTrim(true)
                   .maxRows(0)
                   .maxColumns(0)
                   .password("")
                   .enableValidation(true)
                   .failFast(false)
                   .description("");
        }
        
        ExcelSheetMeta meta = builder.build();
        log.debug("解析Sheet元数据完成: {}", meta);
        return meta;
    }

    /**
     * 执行Template元数据解析
     */
    private ExcelTemplateMeta doParseTemplateMeta(Class<?> clazz) {
        ExcelTemplate templateAnnotation = clazz.getAnnotation(ExcelTemplate.class);
        
        if (templateAnnotation == null) {
            throw new IllegalArgumentException("类[" + clazz.getName() + "]没有@ExcelTemplate注解");
        }
        
        List<ExcelFieldMeta> fieldMetas = getExcelFields(clazz);
        
        ExcelTemplateMeta meta = ExcelTemplateMeta.builder()
                .entityClass(clazz)
                .templatePath(templateAnnotation.value())
                .templateName(templateAnnotation.name().isEmpty() ? clazz.getSimpleName() : templateAnnotation.name())
                .horizontal(templateAnnotation.horizontal())
                .startRow(templateAnnotation.startRow())
                .startColumn(templateAnnotation.startColumn())
                .forceNewSheet(templateAnnotation.forceNewSheet())
                .enableFormula(templateAnnotation.enableFormula())
                .autoRowHeight(templateAnnotation.autoRowHeight())
                .autoColumnWidth(templateAnnotation.autoColumnWidth())
                .cacheSeconds(templateAnnotation.cacheSeconds())
                .description(templateAnnotation.description())
                .fields(fieldMetas)
                .build();
        
        log.debug("解析Template元数据完成: {}", meta);
        return meta;
    }

    /**
     * 构建字段元数据
     */
    private ExcelFieldMeta buildFieldMeta(Field field, ExcelField annotation) {
        return ExcelFieldMeta.builder()
                .field(field)
                .fieldName(field.getName())
                .fieldType(field.getType())
                .columnTitle(annotation.value().isEmpty() ? field.getName() : annotation.value())
                .columnIndex(annotation.index())
                .required(annotation.required())
                .defaultValue(annotation.defaultValue())
                .converterClass(annotation.converter())
                .dateFormat(annotation.dateFormat())
                .numberFormat(annotation.numberFormat())
                .maxLength(annotation.maxLength())
                .minLength(annotation.minLength())
                .pattern(annotation.pattern())
                .message(annotation.message())
                .width(annotation.width())
                .cellStyle(annotation.cellStyle())
                .autoWidth(annotation.autoWidth())
                .order(annotation.order())
                .build();
    }
} 