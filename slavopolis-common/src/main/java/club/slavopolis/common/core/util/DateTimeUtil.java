package club.slavopolis.common.core.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 日期时间工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    // ==================== 日期时间常量 ====================

    /**
     * 标准日期格式
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 标准时间格式
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 紧凑时间格式
     */
    public static final String COMPACT_TIME_PATTERN = "HHmmss";

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

    /**
     * 中文日期格式：yyyy年MM月dd日
     */
    public static final String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";

    /**
     * 中文日期时间格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    public static final String CHINESE_DATETIME_PATTERN = "yyyy年MM月dd日 HH时mm分ss秒";

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

    // ==================== 常用格式化器 ====================
    
    /**
     * 默认日期格式：yyyy-MM-dd
     */
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    /**
     * 默认时间格式：HH:mm:ss
     */
    public static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
    
    /**
     * 默认日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    
    /**
     * 紧凑日期格式：yyyyMMdd
     */
    public static final DateTimeFormatter COMPACT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_COMPACT_PATTERN);
    
    /**
     * 紧凑时间格式：HHmmss
     */
    public static final DateTimeFormatter COMPACT_TIME_FORMATTER = DateTimeFormatter.ofPattern(COMPACT_TIME_PATTERN);
    
    /**
     * 紧凑日期时间格式：yyyyMMddHHmmss
     */
    public static final DateTimeFormatter COMPACT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_COMPACT_PATTERN);
    
    /**
     * ISO日期时间格式：yyyy-MM-dd'T'HH:mm:ss
     */
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATETIME_PATTERN);
    
    /**
     * 中文日期格式：yyyy年MM月dd日
     */
    public static final DateTimeFormatter CHINESE_DATE_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATE_PATTERN);
    
    /**
     * 中文日期时间格式：yyyy年MM月dd日 HH时mm分ss秒
     */
    public static final DateTimeFormatter CHINESE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATETIME_PATTERN);
    
    // ==================== 当前时间获取 ====================
    
    /**
     * 获取当前日期
     * 
     * @return 当前日期
     */
    public static LocalDate now() {
        return LocalDate.now();
    }
    
    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    public static LocalTime nowTime() {
        return LocalTime.now();
    }
    
    /**
     * 获取当前日期时间
     * 
     * @return 当前日期时间
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * 获取当前时间戳（毫秒）
     * 
     * @return 当前时间戳
     */
    public static long nowTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取当前时间戳（秒）
     * 
     * @return 当前时间戳（秒）
     */
    public static long nowTimestampSeconds() {
        return Instant.now().getEpochSecond();
    }
    
    // ==================== 格式化方法 ====================
    
    /**
     * 格式化日期（默认格式）
     * 
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return format(date, DEFAULT_DATE_FORMATTER);
    }
    
    /**
     * 格式化时间（默认格式）
     * 
     * @param time 时间
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time) {
        return format(time, DEFAULT_TIME_FORMATTER);
    }
    
    /**
     * 格式化日期时间（默认格式）
     * 
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_DATETIME_FORMATTER);
    }
    
    /**
     * 格式化日期
     * 
     * @param date 日期
     * @param formatter 格式化器
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, DateTimeFormatter formatter) {
        return Objects.nonNull(date) ? date.format(formatter) : null;
    }
    
    /**
     * 格式化时间
     * 
     * @param time 时间
     * @param formatter 格式化器
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time, DateTimeFormatter formatter) {
        return Objects.nonNull(time) ? time.format(formatter) : null;
    }
    
    /**
     * 格式化日期时间
     * 
     * @param dateTime 日期时间
     * @param formatter 格式化器
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return Objects.nonNull(dateTime) ? dateTime.format(formatter) : null;
    }
    
    /**
     * 格式化日期
     * 
     * @param date 日期
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        return format(date, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化时间
     * 
     * @param time 时间
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time, String pattern) {
        return format(time, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化日期时间
     * 
     * @param dateTime 日期时间
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return format(dateTime, DateTimeFormatter.ofPattern(pattern));
    }
    
    // ==================== 解析方法 ====================
    
    /**
     * 解析日期字符串（默认格式）
     * 
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_FORMATTER);
    }
    
    /**
     * 解析时间字符串（默认格式）
     * 
     * @param timeStr 时间字符串
     * @return 时间对象
     */
    public static LocalTime parseTime(String timeStr) {
        return parseTime(timeStr, DEFAULT_TIME_FORMATTER);
    }
    
    /**
     * 解析日期时间字符串（默认格式）
     * 
     * @param dateTimeStr 日期时间字符串
     * @return 日期时间对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }
    
    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @param formatter 格式化器
     * @return 日期对象
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
        try {
            return Objects.nonNull(dateStr) ? LocalDate.parse(dateStr, formatter) : null;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式错误: " + dateStr, e);
        }
    }
    
    /**
     * 解析时间字符串
     * 
     * @param timeStr 时间字符串
     * @param formatter 格式化器
     * @return 时间对象
     */
    public static LocalTime parseTime(String timeStr, DateTimeFormatter formatter) {
        try {
            return Objects.nonNull(timeStr) ? LocalTime.parse(timeStr, formatter) : null;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式错误: " + timeStr, e);
        }
    }
    
    /**
     * 解析日期时间字符串
     * 
     * @param dateTimeStr 日期时间字符串
     * @param formatter 格式化器
     * @return 日期时间对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, DateTimeFormatter formatter) {
        try {
            return Objects.nonNull(dateTimeStr) ? LocalDateTime.parse(dateTimeStr, formatter) : null;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期时间格式错误: " + dateTimeStr, e);
        }
    }
    
    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @param pattern 格式模式
     * @return 日期对象
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        return parseDate(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 解析时间字符串
     * 
     * @param timeStr 时间字符串
     * @param pattern 格式模式
     * @return 时间对象
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        return parseTime(timeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 解析日期时间字符串
     * 
     * @param dateTimeStr 日期时间字符串
     * @param pattern 格式模式
     * @return 日期时间对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        return parseDateTime(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    // ==================== 转换方法 ====================
    
    /**
     * LocalDateTime转Date
     * 
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Objects.nonNull(localDateTime) ? 
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    /**
     * LocalDate转Date
     * 
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date toDate(LocalDate localDate) {
        return Objects.nonNull(localDate) ? 
            Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    /**
     * Date转LocalDateTime
     * 
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return Objects.nonNull(date) ? 
            LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()) : null;
    }
    
    /**
     * Date转LocalDate
     * 
     * @param date Date对象
     * @return LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        return Objects.nonNull(date) ? 
            date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
    
    /**
     * 时间戳转LocalDateTime
     * 
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
    
    /**
     * LocalDateTime转时间戳
     * 
     * @param localDateTime LocalDateTime对象
     * @return 时间戳（毫秒）
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        return Objects.nonNull(localDateTime) ? 
            localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
    }
    
    /**
     * LocalDate转时间戳
     * 
     * @param localDate LocalDate对象
     * @return 时间戳（毫秒）
     */
    public static long toTimestamp(LocalDate localDate) {
        return Objects.nonNull(localDate) ? 
            localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
    }
    
    // ==================== 计算方法 ====================
    
    /**
     * 计算两个日期之间的天数
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * 计算两个日期时间之间的小时数
     * 
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 小时数差
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算两个日期时间之间的分钟数
     * 
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 分钟数差
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算两个日期时间之间的秒数
     * 
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 秒数差
     */
    public static long secondsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.SECONDS.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算两个日期时间之间的毫秒数
     * 
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 毫秒数差
     */
    public static long millisBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MILLIS.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算年龄
     * 
     * @param birthDate 出生日期
     * @return 年龄
     */
    public static int calculateAge(LocalDate birthDate) {
        return Objects.nonNull(birthDate) ? (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now()) : 0;
    }
    
    // ==================== 日期操作 ====================
    
    /**
     * 获取月初日期
     * 
     * @param date 日期
     * @return 月初日期
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return Objects.nonNull(date) ? date.with(TemporalAdjusters.firstDayOfMonth()) : null;
    }
    
    /**
     * 获取月末日期
     * 
     * @param date 日期
     * @return 月末日期
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return Objects.nonNull(date) ? date.with(TemporalAdjusters.lastDayOfMonth()) : null;
    }
    
    /**
     * 获取年初日期
     * 
     * @param date 日期
     * @return 年初日期
     */
    public static LocalDate getFirstDayOfYear(LocalDate date) {
        return Objects.nonNull(date) ? date.with(TemporalAdjusters.firstDayOfYear()) : null;
    }
    
    /**
     * 获取年末日期
     * 
     * @param date 日期
     * @return 年末日期
     */
    public static LocalDate getLastDayOfYear(LocalDate date) {
        return Objects.nonNull(date) ? date.with(TemporalAdjusters.lastDayOfYear()) : null;
    }
    
    /**
     * 获取下一个工作日
     * 
     * @param date 日期
     * @return 下一个工作日
     */
    public static LocalDate getNextWorkday(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        
        LocalDate nextDay = date.plusDays(1);
        while (isWeekend(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }
    
    /**
     * 判断是否为周末
     * 
     * @param date 日期
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDate date) {
        if (Objects.isNull(date)) {
            return false;
        }
        
        int dayOfWeek = date.getDayOfWeek().getValue();
        // 周六或周日
        return dayOfWeek == 6 || dayOfWeek == 7;
    }
    
    /**
     * 判断是否为闰年
     * 
     * @param year 年份
     * @return 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return LocalDate.of(year, 1, 1).isLeapYear();
    }
    
    /**
     * 判断日期是否在指定范围内
     * 
     * @param date 待检查的日期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(date) || Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return false;
        }
        
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    /**
     * 判断日期时间是否在指定范围内
     * 
     * @param dateTime 待检查的日期时间
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (Objects.isNull(dateTime) || Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
            return false;
        }
        
        return !dateTime.isBefore(startDateTime) && !dateTime.isAfter(endDateTime);
    }
    
    // ==================== 时区相关 ====================
    
    /**
     * 转换时区
     * 
     * @param dateTime 日期时间
     * @param fromZone 源时区
     * @param toZone 目标时区
     * @return 转换后的日期时间
     */
    public static LocalDateTime convertTimeZone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        
        ZonedDateTime zonedDateTime = dateTime.atZone(fromZone);
        return zonedDateTime.withZoneSameInstant(toZone).toLocalDateTime();
    }
    
    /**
     * 转换为UTC时间
     * 
     * @param dateTime 本地日期时间
     * @return UTC日期时间
     */
    public static LocalDateTime toUtc(LocalDateTime dateTime) {
        return convertTimeZone(dateTime, ZoneId.systemDefault(), ZoneOffset.UTC);
    }
    
    /**
     * 从UTC时间转换为本地时间
     * 
     * @param utcDateTime UTC日期时间
     * @return 本地日期时间
     */
    public static LocalDateTime fromUtc(LocalDateTime utcDateTime) {
        return convertTimeZone(utcDateTime, ZoneOffset.UTC, ZoneId.systemDefault());
    }
    
    // ==================== 持续时间 ====================
    
    /**
     * 格式化持续时间
     * 
     * @param duration 持续时间
     * @return 格式化后的字符串（如：1天2小时3分钟）
     */
    public static String formatDuration(Duration duration) {
        if (Objects.isNull(duration)) {
            return "";
        }
        
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
        if (seconds > 0) {
            sb.append(seconds).append("秒");
        }
        
        return !sb.isEmpty() ? sb.toString() : "0秒";
    }
    
    /**
     * 解析持续时间字符串（如：PT1H30M表示1小时30分钟）
     * 
     * @param durationStr 持续时间字符串
     * @return 持续时间对象
     */
    public static Duration parseDuration(String durationStr) {
        try {
            return Objects.nonNull(durationStr) ? Duration.parse(durationStr) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("持续时间格式错误: " + durationStr, e);
        }
    }
} 