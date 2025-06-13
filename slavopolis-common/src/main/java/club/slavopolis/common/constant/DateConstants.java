package club.slavopolis.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 日期时间常量定义
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateConstants {

    /**
     * 标准日期格式
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 标准时间格式
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 标准日期时间格式
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 带毫秒的日期时间格式
     */
    public static final String DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 紧凑型日期格式
     */
    public static final String DATE_COMPACT_PATTERN = "yyyyMMdd";

    /**
     * 紧凑型日期时间格式
     */
    public static final String DATETIME_COMPACT_PATTERN = "yyyyMMddHHmmss";

    /**
     * ISO日期时间格式
     */
    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * ISO日期时间格式（带时区）
     */
    public static final String ISO_DATETIME_TIMEZONE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

    // ==================== Excel 转换器专用格式 ====================

    /**
     * 斜杠分隔的日期时间格式
     */
    public static final String SLASH_DATETIME_PATTERN = "yyyy/MM/dd HH:mm:ss";

    /**
     * 斜杠分隔的日期时间格式（不含秒）
     */
    public static final String SLASH_DATETIME_NO_SEC_PATTERN = "yyyy/MM/dd HH:mm";

    /**
     * 标准日期时间格式（不含秒）
     */
    public static final String DATETIME_NO_SEC_PATTERN = "yyyy-MM-dd HH:mm";

    /**
     * 斜杠分隔的日期格式
     */
    public static final String SLASH_DATE_PATTERN = "yyyy/MM/dd";

    /**
     * 紧凑型日期时间格式（带空格）
     */
    public static final String COMPACT_DATETIME_SPACE_PATTERN = "yyyyMMdd HHmmss";

    /**
     * 默认时间补充格式
     */
    public static final String DEFAULT_TIME_SUFFIX = " 00:00:00";

    /**
     * 常用日期时间格式数组（用于多格式解析）
     */
    private static final String[] COMMON_DATETIME_PATTERNS = {
        DATETIME_PATTERN,
        SLASH_DATETIME_PATTERN,
        DATETIME_NO_SEC_PATTERN,
        SLASH_DATETIME_NO_SEC_PATTERN,
        DATE_PATTERN,
        SLASH_DATE_PATTERN,
        COMPACT_DATETIME_SPACE_PATTERN,
        DATETIME_COMPACT_PATTERN,
        ISO_DATETIME_PATTERN,
        DATETIME_MS_PATTERN
    };

    /**
     * 获取常用日期时间格式数组
     */
    public static String[] getCommonDateTimePatterns() {
        return COMMON_DATETIME_PATTERNS.clone();
    }
}
