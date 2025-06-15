package club.slavopolis.infrastructure.integration.excel.model.metadata;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel工作表元数据
 */
@Data
@Builder
public class ExcelSheetMeta {

    /**
     * 实体类
     */
    private Class<?> entityClass;

    /**
     * Sheet名称
     */
    private String sheetName;

    /**
     * Sheet索引
     */
    private int sheetIndex;

    /**
     * 标题行索引
     */
    private int headerIndex;

    /**
     * 数据开始行索引
     */
    private int dataStartIndex;

    /**
     * 是否包含标题行
     */
    private boolean includeHeader;

    /**
     * 是否忽略空行
     */
    private boolean ignoreEmptyRow;

    /**
     * 是否自动去除空格
     */
    private boolean autoTrim;

    /**
     * 最大读取行数
     */
    private int maxRows;

    /**
     * 最大读取列数
     */
    private int maxColumns;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否启用数据验证
     */
    private boolean enableValidation;

    /**
     * 是否快速失败
     */
    private boolean failFast;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 字段元数据列表
     */
    private List<ExcelFieldMeta> fields;

    /**
     * 是否有密码保护
     */
    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }

    /**
     * 是否限制行数
     */
    public boolean hasMaxRows() {
        return maxRows > 0;
    }

    /**
     * 是否限制列数
     */
    public boolean hasMaxColumns() {
        return maxColumns > 0;
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
     * 根据列索引获取字段元数据
     */
    public ExcelFieldMeta getFieldByColumnIndex(int columnIndex) {
        if (fields == null) {
            return null;
        }
        return fields.stream()
                .filter(field -> field.getColumnIndex() == columnIndex)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取必填字段列表
     */
    public List<ExcelFieldMeta> getRequiredFields() {
        if (fields == null) {
            return List.of();
        }
        return fields.stream()
                .filter(ExcelFieldMeta::isRequired)
                .toList();
    }

    /**
     * 获取有验证规则的字段列表
     */
    public List<ExcelFieldMeta> getValidationFields() {
        if (fields == null) {
            return List.of();
        }
        return fields.stream()
                .filter(ExcelFieldMeta::hasValidation)
                .toList();
    }

    @Override
    public String toString() {
        return String.format("ExcelSheetMeta{entityClass=%s, sheetName='%s', fieldCount=%d}", 
            entityClass.getSimpleName(), sheetName, getFieldCount());
    }
} 