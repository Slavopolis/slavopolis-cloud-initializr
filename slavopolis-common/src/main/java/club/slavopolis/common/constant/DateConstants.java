package club.slavopolis.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
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
}
