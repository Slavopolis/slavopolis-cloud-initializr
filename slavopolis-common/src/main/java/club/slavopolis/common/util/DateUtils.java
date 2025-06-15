package club.slavopolis.common.util;

import club.slavopolis.common.core.util.DateTimeUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 日期时间工具类, 基于Java 8+ Time API
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    // ==================== 格式化 ====================

    /**
     * 格式化日期时间
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化为标准日期时间格式
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return format(dateTime, DateTimeUtil.DATETIME_PATTERN);
    }

    /**
     * 格式化为标准日期格式
     */
    public static String formatDate(LocalDate date) {
        return format(date, DateTimeUtil.DATE_PATTERN);
    }

    // ==================== 解析 ====================

    /**
     * 解析日期时间字符串
     */
    public static LocalDateTime parseDateTime(String dateTime, String pattern) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析日期字符串
     */
    public static LocalDate parseDate(String date, String pattern) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析标准日期时间格式
     */
    public static LocalDateTime parseDateTime(String dateTime) {
        return parseDateTime(dateTime, DateTimeUtil.DATETIME_PATTERN);
    }

    /**
     * 解析标准日期格式
     */
    public static LocalDate parseDate(String date) {
        return parseDate(date, DateTimeUtil.DATE_PATTERN);
    }

    // ==================== 转换 ====================

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 时间戳转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime转时间戳
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    // ==================== 计算 ====================

    /**
     * 计算两个日期之间的天数
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的秒数
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 获取当天开始时间
     */
    public static LocalDateTime getDayStart(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 获取当天结束时间
     */
    public static LocalDateTime getDayEnd(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * 获取本月第一天
     */
    public static LocalDate getMonthFirst(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最后一天
     */
    public static LocalDate getMonthLast(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 判断是否为工作日（简单判断，不考虑节假日）
     */
    public static boolean isWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
}
