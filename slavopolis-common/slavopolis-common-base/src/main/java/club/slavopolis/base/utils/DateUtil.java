package club.slavopolis.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 日期时间工具类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    // ================================ 日期格式化常量 ================================

    /**
     * 标准日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String STANDARD_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 标准日期格式：yyyy-MM-dd
     */
    public static final String STANDARD_DATE = "yyyy-MM-dd";

    /**
     * 标准时间格式：HH:mm:ss
     */
    public static final String STANDARD_TIME = "HH:mm:ss";

    /**
     * 毫秒级日期时间格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String STANDARD_DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 紧凑日期时间格式：yyyyMMddHHmmss
     */
    public static final String COMPACT_DATETIME = "yyyyMMddHHmmss";

    /**
     * 紧凑日期时间格式：yyyy/MM/dd/HH/mm/ss
     */
    public static final String COMPACT_DATETIME_SLASH = "yyyy/MM/dd/HH/mm/ss";

    /**
     * 紧凑日期格式：yyyyMMdd
     */
    public static final String COMPACT_DATE = "yyyyMMdd";

    /**
     * 紧凑时间格式：yyyy/MM/dd
     */
    public static final String COMPACT_DATE_SLASH = "yyyy/MM/dd";

    /**
     * 紧凑时间格式：HHmmss
     */
    public static final String COMPACT_TIME = "HHmmss";

    /**
     * 紧凑时间格式：HH/mm/ss
     */
    public static final String COMPACT_TIME_SLASH = "HH/mm/ss";

    /**
     * 紧凑毫秒级日期时间格式：yyyyMMddHHmmssSSS
     */
    public static final String COMPACT_DATETIME_MS = "yyyyMMddHHmmssSSS";

    /**
     * 紧凑毫秒级日期时间格式：yyyy/MM/dd/HH/mm/ss/SSS
     */
    public static final String COMPACT_DATETIME_MS_SLASH = "yyyy/MM/dd/HH/mm/ss/SSS";

    /**
     * 中文日期时间格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    public static final String CHINESE_DATETIME = "yyyy年MM月dd日 HH时mm分ss秒";

    /**
     * 中文日期格式：yyyy年MM月dd日
     */
    public static final String CHINESE_DATE = "yyyy年MM月dd日";

    /**
     * 中文时间格式：HH时mm分ss秒
     */
    public static final String CHINESE_TIME = "HH时mm分ss秒";

    /**
     * ISO 8601 日期时间格式：yyyy-MM-dd'T'HH:mm:ss
     */
    public static final String ISO_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * ISO 8601 带时区的日期时间格式：yyyy-MM-dd'T'HH:mm:ssXXX
     */
    public static final String ISO_DATETIME_WITH_ZONE = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /**
     * RFC 2822 日期格式：EEE, dd MMM yyyy HH:mm:ss Z
     */
    public static final String RFC_2822 = "EEE, dd MMM yyyy HH:mm:ss Z";

    /**
     * 年月格式：yyyy-MM
     */
    public static final String YEAR_MONTH = "yyyy-MM";

    /**
     * 月日格式：MM-dd
     */
    public static final String MONTH_DAY = "MM-dd";

    /**
     * 小时分钟格式：HH:mm
     */
    public static final String HOUR_MINUTE = "HH:mm";

    /**
     * 文件名安全的日期时间格式：yyyy-MM-dd_HH-mm-ss
     */
    public static final String FILENAME_SAFE_DATETIME = "yyyy-MM-dd_HH-mm-ss";

    /**
     * 日志格式的日期时间：yyyy-MM-dd HH:mm:ss,SSS
     */
    public static final String LOG_DATETIME = "yyyy-MM-dd HH:mm:ss,SSS";

    /**
     * URL 安全的日期时间格式：yyyyMMdd-HHmmss
     */
    public static final String URL_SAFE_DATETIME = "yyyyMMdd-HHmmss";

    // ================================ 默认时区和地区 ================================

    /**
     * 默认时区：Asia/Shanghai
     */
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * UTC 时区
     */
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    /**
     * 默认地区：中国
     */
    public static final Locale DEFAULT_LOCALE = Locale.CHINA;

    // ================================ 缓存机制 ================================

    /**
     * DateTimeFormatter 缓存
     */
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    /**
     * SimpleDateFormat 缓存（ThreadLocal 保证线程安全）
     */
    private static final ConcurrentMap<String, ThreadLocal<SimpleDateFormat>> SIMPLE_DATE_FORMAT_CACHE = new ConcurrentHashMap<>();

    // ================================ 格式化方法 ================================

    /**
     * 格式化当前时间为标准日期时间格式
     *
     * @return 格式化后的字符串
     */
    public static String now() {
        return formatNow(STANDARD_DATETIME);
    }

    /**
     * 格式化当前日期为标准日期格式
     *
     * @return 格式化后的字符串
     */
    public static String today() {
        return formatNow(STANDARD_DATE);
    }

    /**
     * 格式化当前时间为指定格式
     *
     * @param pattern 日期格式
     * @return 格式化后的字符串
     */
    public static String formatNow(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    /**
     * 格式化 LocalDateTime 为标准日期时间格式
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, STANDARD_DATETIME);
    }

    /**
     * 格式化 LocalDateTime 为指定格式
     *
     * @param dateTime 日期时间
     * @param pattern  日期格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return getFormatter(pattern).format(dateTime);
    }

    /**
     * 格式化 LocalDate 为标准日期格式
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return format(date, STANDARD_DATE);
    }

    /**
     * 格式化 LocalDate 为指定格式
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        return getFormatter(pattern).format(date);
    }

    /**
     * 格式化 LocalTime 为标准时间格式
     *
     * @param time 时间
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time) {
        return format(time, STANDARD_TIME);
    }

    /**
     * 格式化 LocalTime 为指定格式
     *
     * @param time    时间
     * @param pattern 时间格式
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        return getFormatter(pattern).format(time);
    }

    /**
     * 格式化 ZonedDateTime 为指定格式
     *
     * @param zonedDateTime 带时区的日期时间
     * @param pattern       日期格式
     * @return 格式化后的字符串
     */
    public static String format(ZonedDateTime zonedDateTime, String pattern) {
        if (zonedDateTime == null) {
            return null;
        }
        return getFormatter(pattern).format(zonedDateTime);
    }

    /**
     * 格式化 Date 为标准日期时间格式
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(Date date) {
        return format(date, STANDARD_DATETIME);
    }

    /**
     * 格式化 Date 为指定格式
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return getSimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化时间戳为标准日期时间格式
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的字符串
     */
    public static String format(long timestamp) {
        return format(timestamp, STANDARD_DATETIME);
    }

    /**
     * 格式化时间戳为指定格式
     *
     * @param timestamp 时间戳（毫秒）
     * @param pattern   日期格式
     * @return 格式化后的字符串
     */
    public static String format(long timestamp, String pattern) {
        return format(new Date(timestamp), pattern);
    }

    // ================================ 解析方法 ================================

    /**
     * 解析标准日期时间格式的字符串
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, STANDARD_DATETIME);
    }

    /**
     * 解析指定格式的日期时间字符串
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     日期格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (!StringUtils.hasText(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, getFormatter(pattern));
        } catch (DateTimeParseException e) {
            log.warn("解析日期时间失败: {} with pattern: {}", dateTimeStr, pattern, e);
            return null;
        }
    }

    /**
     * 解析标准日期格式的字符串
     *
     * @param dateStr 日期字符串
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, STANDARD_DATE);
    }

    /**
     * 解析指定格式的日期字符串
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, getFormatter(pattern));
        } catch (DateTimeParseException e) {
            log.warn("解析日期失败: {} with pattern: {}", dateStr, pattern, e);
            return null;
        }
    }

    /**
     * 解析标准时间格式的字符串
     *
     * @param timeStr 时间字符串
     * @return LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        return parseTime(timeStr, STANDARD_TIME);
    }

    /**
     * 解析指定格式的时间字符串
     *
     * @param timeStr 时间字符串
     * @param pattern 时间格式
     * @return LocalTime
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        if (!StringUtils.hasText(timeStr)) {
            return null;
        }
        try {
            return LocalTime.parse(timeStr, getFormatter(pattern));
        } catch (DateTimeParseException e) {
            log.warn("解析时间失败: {} with pattern: {}", timeStr, pattern, e);
            return null;
        }
    }

    /**
     * 解析为 Date 对象
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return Date
     */
    public static Date parseToDate(String dateStr, String pattern) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            return getSimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            log.warn("解析日期失败: {} with pattern: {}", dateStr, pattern, e);
            return null;
        }
    }

    /**
     * 智能解析日期字符串（尝试多种格式）
     *
     * @param dateStr 日期字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseSmartly(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }

        // 常用格式列表
        String[] patterns = {
            STANDARD_DATETIME,
            STANDARD_DATETIME_MS,
            COMPACT_DATETIME,
            ISO_DATETIME,
            CHINESE_DATETIME,
            FILENAME_SAFE_DATETIME,
            LOG_DATETIME,
            URL_SAFE_DATETIME
        };

        for (String pattern : patterns) {
            LocalDateTime result = parseDateTime(dateStr, pattern);
            if (result != null) {
                return result;
            }
        }

        log.warn("无法解析日期字符串: {}", dateStr);
        return null;
    }

    // ================================ 转换方法 ================================

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    /**
     * Date 转 LocalDate
     *
     * @param date Date
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    }

    /**
     * 时间戳转 LocalDateTime
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime 转时间戳
     *
     * @param localDateTime LocalDateTime
     * @return 时间戳（毫秒）
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0L;
        }
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    // ================================ 日期计算方法 ================================

    /**
     * 获取当前日期的开始时间（00:00:00）
     *
     * @return LocalDateTime
     */
    public static LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 获取当前日期的结束时间（23:59:59.999）
     *
     * @return LocalDateTime
     */
    public static LocalDateTime endOfToday() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }

    /**
     * 获取指定日期的开始时间
     *
     * @param date 日期
     * @return LocalDateTime
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * 获取指定日期的结束时间
     *
     * @param date 日期
     * @return LocalDateTime
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(LocalTime.MAX);
    }

    /**
     * 获取本周的开始日期（周一）
     *
     * @return LocalDate
     */
    public static LocalDate startOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取本周的结束日期（周日）
     *
     * @return LocalDate
     */
    public static LocalDate endOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 获取本月的开始日期
     *
     * @return LocalDate
     */
    public static LocalDate startOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月的结束日期
     *
     * @return LocalDate
     */
    public static LocalDate endOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本年的开始日期
     *
     * @return LocalDate
     */
    public static LocalDate startOfYear() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取本年的结束日期
     *
     * @return LocalDate
     */
    public static LocalDate endOfYear() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 加上指定的天数
     *
     * @param date 基准日期
     * @param days 天数
     * @return LocalDate
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date == null ? null : date.plusDays(days);
    }

    /**
     * 减去指定的天数
     *
     * @param date 基准日期
     * @param days 天数
     * @return LocalDate
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        return date == null ? null : date.minusDays(days);
    }

    /**
     * 加上指定的月数
     *
     * @param date   基准日期
     * @param months 月数
     * @return LocalDate
     */
    public static LocalDate plusMonths(LocalDate date, long months) {
        return date == null ? null : date.plusMonths(months);
    }

    /**
     * 减去指定的月数
     *
     * @param date   基准日期
     * @param months 月数
     * @return LocalDate
     */
    public static LocalDate minusMonths(LocalDate date, long months) {
        return date == null ? null : date.minusMonths(months);
    }

    /**
     * 加上指定的年数
     *
     * @param date  基准日期
     * @param years 年数
     * @return LocalDate
     */
    public static LocalDate plusYears(LocalDate date, long years) {
        return date == null ? null : date.plusYears(years);
    }

    /**
     * 减去指定的年数
     *
     * @param date  基准日期
     * @param years 年数
     * @return LocalDate
     */
    public static LocalDate minusYears(LocalDate date, long years) {
        return date == null ? null : date.minusYears(years);
    }

    // ================================ 日期比较方法 ================================

    /**
     * 计算两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的小时差
     *
     * @param start 开始日期时间
     * @param end   结束日期时间
     * @return 小时差
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的分钟差
     *
     * @param start 开始日期时间
     * @param end   结束日期时间
     * @return 分钟差
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 计算两个日期时间之间的秒数差
     *
     * @param start 开始日期时间
     * @param end   结束日期时间
     * @return 秒数差
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 判断是否是同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否同一天
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }

    /**
     * 判断是否是同一年
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否同一年
     */
    public static boolean isSameYear(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getYear() == date2.getYear();
    }

    /**
     * 判断是否是同一月
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否同一月
     */
    public static boolean isSameMonth(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth();
    }

    /**
     * 判断是否是今天
     *
     * @param date 日期
     * @return 是否今天
     */
    public static boolean isToday(LocalDate date) {
        return isSameDay(date, LocalDate.now());
    }

    /**
     * 判断是否是昨天
     *
     * @param date 日期
     * @return 是否昨天
     */
    public static boolean isYesterday(LocalDate date) {
        return isSameDay(date, LocalDate.now().minusDays(1));
    }

    /**
     * 判断是否是明天
     *
     * @param date 日期
     * @return 是否明天
     */
    public static boolean isTomorrow(LocalDate date) {
        return isSameDay(date, LocalDate.now().plusDays(1));
    }

    /**
     * 判断是否是工作日（周一到周五）
     *
     * @param date 日期
     * @return 是否工作日
     */
    public static boolean isWorkday(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue() >= DayOfWeek.MONDAY.getValue() 
               && dayOfWeek.getValue() <= DayOfWeek.FRIDAY.getValue();
    }

    /**
     * 判断是否是周末（周六或周日）
     *
     * @param date 日期
     * @return 是否周末
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 判断是否是闰年
     *
     * @param year 年份
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    // ================================ 时区相关方法 ================================

    /**
     * 转换时区
     *
     * @param zonedDateTime 带时区的日期时间
     * @param targetZoneId  目标时区
     * @return 转换后的带时区日期时间
     */
    public static ZonedDateTime convertZone(ZonedDateTime zonedDateTime, ZoneId targetZoneId) {
        if (zonedDateTime == null || targetZoneId == null) {
            return zonedDateTime;
        }
        return zonedDateTime.withZoneSameInstant(targetZoneId);
    }

    /**
     * 将 LocalDateTime 按指定时区转换为 UTC 时间
     *
     * @param localDateTime 本地日期时间
     * @param sourceZoneId  源时区
     * @return UTC 时间
     */
    public static ZonedDateTime toUtc(LocalDateTime localDateTime, ZoneId sourceZoneId) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(sourceZoneId).withZoneSameInstant(UTC_ZONE_ID);
    }

    /**
     * 将 UTC 时间转换为指定时区的 LocalDateTime
     *
     * @param utcTime      UTC 时间
     * @param targetZoneId 目标时区
     * @return 本地日期时间
     */
    public static LocalDateTime fromUtc(ZonedDateTime utcTime, ZoneId targetZoneId) {
        if (utcTime == null) {
            return null;
        }
        return utcTime.withZoneSameInstant(targetZoneId).toLocalDateTime();
    }

    // ================================ 实用方法 ================================

    /**
     * 获取年龄
     *
     * @param birthDate 出生日期
     * @return 年龄
     */
    public static int getAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 获取星座
     *
     * @param date 日期
     * @return 星座名称
     */
    public static String getZodiac(LocalDate date) {
        if (date == null) {
            return "";
        }
        
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        
        String[] zodiacArray = {
            "魔羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座",
            "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"
        };
        
        int[] dayArray = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
        
        return zodiacArray[day < dayArray[month - 1] ? month - 1 : month % 12];
    }

    /**
     * 获取生肖
     *
     * @param year 年份
     * @return 生肖
     */
    public static String getChineseZodiac(int year) {
        String[] zodiacArray = {
            "猴", "鸡", "狗", "猪", "鼠", "牛",
            "虎", "兔", "龙", "蛇", "马", "羊"
        };
        return zodiacArray[year % 12];
    }

    /**
     * 获取季度
     *
     * @param date 日期
     * @return 季度（1-4）
     */
    public static int getQuarter(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return (date.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * 获取一年中的第几周
     *
     * @param date 日期
     * @return 周数
     */
    public static int getWeekOfYear(LocalDate date) {
        if (date == null) {
            return 0;
        }
        WeekFields weekFields = WeekFields.of(DEFAULT_LOCALE);
        return date.get(weekFields.weekOfYear());
    }

    /**
     * 获取一月中的第几周
     *
     * @param date 日期
     * @return 周数
     */
    public static int getWeekOfMonth(LocalDate date) {
        if (date == null) {
            return 0;
        }
        WeekFields weekFields = WeekFields.of(DEFAULT_LOCALE);
        return date.get(weekFields.weekOfMonth());
    }

    /**
     * 获取一年中的第几天
     *
     * @param date 日期
     * @return 天数
     */
    public static int getDayOfYear(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.getDayOfYear();
    }

    /**
     * 获取月份的天数
     *
     * @param year  年份
     * @param month 月份
     * @return 天数
     */
    public static int getDaysInMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * 生成指定范围内的所有日期
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期列表
     */
    public static List<LocalDate> getDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null || start.isAfter(end)) {
            return List.of();
        }
        
        return start.datesUntil(end.plusDays(1)).toList();
    }

    /**
     * 格式化时间间隔为可读的字符串
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 格式化的时间间隔字符串
     */
    public static String formatDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "";
        }
        
        Duration duration = Duration.between(start, end);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (seconds > 0 || sb.isEmpty()) {
            sb.append(seconds).append("秒");
        }
        
        return sb.toString();
    }

    // ================================ 私有辅助方法 ================================

    /**
     * 获取 DateTimeFormatter（带缓存）
     */
    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, p -> {
            try {
                return DateTimeFormatter.ofPattern(p, DEFAULT_LOCALE);
            } catch (Exception e) {
                log.error("创建 DateTimeFormatter 失败: {}", p, e);
                throw new IllegalArgumentException("无效的日期格式: " + p, e);
            }
        });
    }

    /**
     * 获取 SimpleDateFormat（带缓存，线程安全）
     */
    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        ThreadLocal<SimpleDateFormat> threadLocal = SIMPLE_DATE_FORMAT_CACHE.computeIfAbsent(pattern, p ->
            ThreadLocal.withInitial(() -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(p, DEFAULT_LOCALE);
                    sdf.setLenient(false);
                    return sdf;
                } catch (Exception e) {
                    log.error("创建 SimpleDateFormat 失败: {}", p, e);
                    throw new IllegalArgumentException("无效的日期格式: " + p, e);
                }
            })
        );
        return threadLocal.get();
    }
} 