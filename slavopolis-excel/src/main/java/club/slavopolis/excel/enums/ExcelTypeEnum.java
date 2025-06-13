package club.slavopolis.excel.enums;

import club.slavopolis.common.constant.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: Excel文件类型枚举
 */
@Getter
@AllArgsConstructor
public enum ExcelTypeEnum {

    /**
     * Excel 2007及以上版本 (.xlsx)
     */
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    /**
     * Excel 97-2003版本 (.xls)
     */
    XLS("xls", "application/vnd.ms-excel");

    /**
     * 文件扩展名
     */
    private final String extension;

    /**
     * MIME类型
     */
    private final String mimeType;

    /**
     * 根据扩展名获取枚举
     */
    public static ExcelTypeEnum fromExtension(String extension) {
        if (extension == null) {
            // 默认返回XLSX
            return XLSX;
        }
        
        String normalizedExt = extension.toLowerCase().replace(".", "");
        for (ExcelTypeEnum type : values()) {
            if (type.getExtension().equals(normalizedExt)) {
                return type;
            }
        }
        // 默认返回XLSX
        return XLSX;
    }

    /**
     * 根据文件名获取枚举
     */
    public static ExcelTypeEnum fromFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return XLSX;
        }
        
        // 提取文件扩展名
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return XLSX;
        }
        
        String extension = fileName.substring(lastDotIndex + 1);
        return fromExtension(extension);
    }

    /**
     * 根据MIME类型获取枚举
     */
    public static ExcelTypeEnum fromMimeType(String mimeType) {
        if (mimeType == null) {
            return XLSX;
        }
        
        for (ExcelTypeEnum type : values()) {
            if (type.getMimeType().equals(mimeType)) {
                return type;
            }
        }
        return XLSX;
    }

    /**
     * 检查是否为支持的Excel文件类型
     */
    public static boolean isSupported(String extension) {
        return fromExtension(extension) != null;
    }

    /**
     * 获取完整的文件扩展名（包含点号）
     */
    public String getFullExtension() {
        return CommonConstants.DOT + extension;
    }
} 