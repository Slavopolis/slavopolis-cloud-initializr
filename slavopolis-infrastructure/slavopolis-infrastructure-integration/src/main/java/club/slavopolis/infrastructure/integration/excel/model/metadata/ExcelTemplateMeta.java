package club.slavopolis.infrastructure.integration.excel.model.metadata;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel模板元数据
 */
@Data
@Builder
public class ExcelTemplateMeta {

    /**
     * 实体类
     */
    private Class<?> entityClass;

    /**
     * 模板路径
     */
    private String templatePath;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 填充方向
     */
    private boolean horizontal;

    /**
     * 起始填充行
     */
    private int startRow;

    /**
     * 起始填充列
     */
    private int startColumn;

    /**
     * 是否强制新建Sheet
     */
    private boolean forceNewSheet;

    /**
     * 是否启用公式计算
     */
    private boolean enableFormula;

    /**
     * 是否自动调整行高
     */
    private boolean autoRowHeight;

    /**
     * 是否自动调整列宽
     */
    private boolean autoColumnWidth;

    /**
     * 模板缓存时间（秒）
     */
    private int cacheSeconds;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 字段元数据列表
     */
    private List<ExcelFieldMeta> fields;

    /**
     * 是否启用模板缓存
     */
    public boolean isCacheEnabled() {
        return cacheSeconds > 0;
    }

    /**
     * 获取字段数量
     */
    public int getFieldCount() {
        return fields != null ? fields.size() : 0;
    }

    /**
     * 根据字段名获取字段元数据
     */
    public ExcelFieldMeta getFieldByName(String fieldName) {
        if (fields == null || fieldName == null) {
            return null;
        }
        return fields.stream()
                .filter(field -> fieldName.equals(field.getFieldName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取有效的模板名称
     */
    public String getEffectiveTemplateName() {
        return templateName != null && !templateName.isEmpty() ? templateName : entityClass.getSimpleName();
    }

    /**
     * 是否有自定义起始位置
     */
    public boolean hasCustomStartPosition() {
        return startRow > 0 || startColumn > 0;
    }

    /**
     * 获取缓存键
     */
    public String getCacheKey() {
        return String.format("template:%s:%s", entityClass.getName(), templatePath);
    }

    @Override
    public String toString() {
        return String.format("ExcelTemplateMeta{entityClass=%s, templateName='%s', templatePath='%s'}", 
            entityClass.getSimpleName(), templateName, templatePath);
    }
} 