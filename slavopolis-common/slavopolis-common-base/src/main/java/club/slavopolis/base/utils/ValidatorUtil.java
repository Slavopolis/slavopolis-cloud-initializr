package club.slavopolis.base.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.common.net.InternetDomainName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

import club.slavopolis.base.enums.ValidationErrorCode;
import club.slavopolis.base.exception.BizException;
import cn.hutool.core.util.CreditCodeUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证工具类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/25
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidatorUtil {

    // ================================ 常用正则表达式常量 ================================

    /**
     * 中国大陆手机号正则表达式（支持最新号段）
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    /**
     * 车牌号正则表达式（普通车牌 + 新能源车牌）
     */
    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile(
            "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$"
    );

    /**
     * 密码强度正则表达式（至少8位，包含大小写字母、数字、特殊字符中的3种）
     */
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * 颜色代码正则表达式（支持3位和6位十六进制）
     */
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(
            "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    );

    /**
     * MAC地址正则表达式
     */
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(
            "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"
    );

    // ================================ 基础验证方法（返回布尔值）================================

    /**
     * 判断对象是否为空
     *
     * @param obj 待验证对象
     * @return true：为空，false：不为空
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 待验证对象
     * @return true：不为空，false：为空
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 判断字符串是否为空（使用Apache Commons Lang3）
     *
     * @param str 待验证字符串
     * @return true：为空，false：不为空
     */
    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（使用Apache Commons Lang3）
     *
     * @param str 待验证字符串
     * @return true：为空白，false：不为空白
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 待验证字符串
     * @return true：不为空，false：为空
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param str 待验证字符串
     * @return true：不为空白，false：为空白
     */
    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * 判断集合是否为空（使用Apache Commons Collections4）
     *
     * @param collection 待验证集合
     * @return true：为空，false：不为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 待验证集合
     * @return true：不为空，false：为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return CollectionUtils.isNotEmpty(collection);
    }

    /**
     * 判断Map是否为空（使用Apache Commons Collections4）
     *
     * @param map 待验证Map
     * @return true：为空，false：不为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    /**
     * 判断Map是否不为空
     *
     * @param map 待验证Map
     * @return true：不为空，false：为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtils.isNotEmpty(map);
    }

    /**
     * 判断数组是否为空（使用Apache Commons Lang3）
     *
     * @param array 待验证数组
     * @return true：为空，false：不为空
     */
    public static boolean isEmpty(Object[] array) {
        return ArrayUtils.isEmpty(array);
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 待验证数组
     * @return true：不为空，false：为空
     */
    public static boolean isNotEmpty(Object[] array) {
        return ArrayUtils.isNotEmpty(array);
    }

    // ================================ 长度和范围验证方法 ================================

    /**
     * 验证字符串长度是否在指定范围内
     *
     * @param str 待验证字符串
     * @param min 最小长度（包含）
     * @param max 最大长度（包含）
     * @return true：在范围内，false：超出范围
     */
    public static boolean isLengthBetween(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    /**
     * 验证字符串最小长度
     *
     * @param str 待验证字符串
     * @param min 最小长度（包含）
     * @return true：满足最小长度，false：不满足
     */
    public static boolean isMinLength(String str, int min) {
        return str != null && str.length() >= min;
    }

    /**
     * 验证字符串最大长度
     *
     * @param str 待验证字符串
     * @param max 最大长度（包含）
     * @return true：满足最大长度，false：超出长度
     */
    public static boolean isMaxLength(String str, int max) {
        return str == null || str.length() <= max;
    }

    /**
     * 验证集合大小是否在指定范围内
     *
     * @param collection 待验证集合
     * @param min        最小大小（包含）
     * @param max        最大大小（包含）
     * @return true：在范围内，false：超出范围
     */
    public static boolean isSizeBetween(Collection<?> collection, int min, int max) {
        if (collection == null) {
            return min == 0;
        }
        int size = collection.size();
        return size >= min && size <= max;
    }

    // ================================ 格式验证方法 ================================

    /**
     * 验证邮箱格式（使用Apache Commons Validator）
     *
     * @param email 待验证邮箱
     * @return true：格式正确，false：格式错误
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * 验证手机号格式（中国大陆）
     *
     * @param mobile 待验证手机号
     * @return true：格式正确，false：格式错误
     */
    public static boolean isMobile(String mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 验证身份证号格式（使用Hutool）
     *
     * @param idCard 待验证身份证号
     * @return true：格式正确，false：格式错误
     */
    public static boolean isIdCard(String idCard) {
        if (isEmpty(idCard)) {
            return false;
        }
        return IdcardUtil.isValidCard(idCard);
    }

    /**
     * 验证URL格式（使用Apache Commons Validator）
     *
     * @param url 待验证URL
     * @return true：格式正确，false：格式错误
     */
    public static boolean isUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        return UrlValidator.getInstance().isValid(url);
    }

    /**
     * 验证IPv4地址格式（使用Apache Commons Validator）
     *
     * @param ip 待验证IP地址
     * @return true：格式正确，false：格式错误
     */
    public static boolean isIpv4(String ip) {
        if (isEmpty(ip)) {
            return false;
        }
        return InetAddressValidator.getInstance().isValidInet4Address(ip);
    }

    /**
     * 验证IPv6地址格式（使用Apache Commons Validator）
     *
     * @param ip 待验证IP地址
     * @return true：格式正确，false：格式错误
     */
    public static boolean isIpv6(String ip) {
        if (isEmpty(ip)) {
            return false;
        }
        return InetAddressValidator.getInstance().isValidInet6Address(ip);
    }

    /**
     * 验证IP地址格式（IPv4或IPv6）
     *
     * @param ip 待验证IP地址
     * @return true：格式正确，false：格式错误
     */
    public static boolean isIpAddress(String ip) {
        return isIpv4(ip) || isIpv6(ip);
    }

    // ================================ 数值验证方法 ================================

    /**
     * 验证字符串是否为数字（使用Apache Commons Lang3）
     *
     * @param str 待验证字符串
     * @return true：是数字，false：不是数字
     */
    public static boolean isNumber(String str) {
        return NumberUtils.isCreatable(str);
    }

    /**
     * 验证字符串是否为整数
     *
     * @param str 待验证字符串
     * @return true：是整数，false：不是整数
     */
    public static boolean isInteger(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证数值是否在指定范围内
     *
     * @param value 待验证数值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return true：在范围内，false：超出范围
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数值是否在指定范围内
     *
     * @param value 待验证数值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return true：在范围内，false：超出范围
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数值是否在指定范围内
     *
     * @param value 待验证数值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return true：在范围内，false：超出范围
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    /**
     * 验证数值是否为正数
     *
     * @param value 待验证数值
     * @return true：是正数，false：不是正数
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }

    /**
     * 验证数值是否为正数
     *
     * @param value 待验证数值
     * @return true：是正数，false：不是正数
     */
    public static boolean isPositive(long value) {
        return value > 0;
    }

    /**
     * 验证数值是否为正数
     *
     * @param value 待验证数值
     * @return true：是正数，false：不是正数
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 验证数值是否为负数
     *
     * @param value 待验证数值
     * @return true：是负数，false：不是负数
     */
    public static boolean isNegative(int value) {
        return value < 0;
    }

    /**
     * 验证数值是否为负数
     *
     * @param value 待验证数值
     * @return true：是负数，false：不是负数
     */
    public static boolean isNegative(long value) {
        return value < 0;
    }

    /**
     * 验证数值是否为负数
     *
     * @param value 待验证数值
     * @return true：是负数，false：不是负数
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    // ================================ 日期验证方法 ================================

    /**
     * 验证日期字符串格式
     *
     * @param dateStr 待验证日期字符串
     * @param pattern 日期格式
     * @return true：格式正确，false：格式错误
     */
    public static boolean isDateFormat(String dateStr, String pattern) {
        if (isEmpty(dateStr) || isEmpty(pattern)) {
            return false;
        }
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 验证日期时间字符串格式
     *
     * @param dateTimeStr 待验证日期时间字符串
     * @param pattern     日期时间格式
     * @return true：格式正确，false：格式错误
     */
    public static boolean isDateTimeFormat(String dateTimeStr, String pattern) {
        if (isEmpty(dateTimeStr) || isEmpty(pattern)) {
            return false;
        }
        try {
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 验证日期是否在指定范围内
     *
     * @param date    待验证日期
     * @param minDate 最小日期（包含）
     * @param maxDate 最大日期（包含）
     * @return true：在范围内，false：超出范围
     */
    public static boolean isDateInRange(LocalDate date, LocalDate minDate, LocalDate maxDate) {
        if (date == null || minDate == null || maxDate == null) {
            return false;
        }
        return !date.isBefore(minDate) && !date.isAfter(maxDate);
    }

    // ================================ 业务格式验证方法 ================================

    /**
     * 验证银行卡号格式（使用Apache Commons Validator）
     *
     * @param bankCard 待验证银行卡号
     * @return true：格式正确，false：格式错误
     */
    public static boolean isBankCard(String bankCard) {
        if (isEmpty(bankCard)) {
            return false;
        }
        return CreditCardValidator.genericCreditCardValidator().isValid(bankCard);
    }

    /**
     * 验证统一社会信用代码格式（使用Hutool）
     *
     * @param creditCode 待验证统一社会信用代码
     * @return true：格式正确，false：格式错误
     */
    public static boolean isCreditCode(String creditCode) {
        if (isEmpty(creditCode)) {
            return false;
        }
        return CreditCodeUtil.isCreditCode(creditCode);
    }

    /**
     * 验证车牌号格式
     *
     * @param licensePlate 待验证车牌号
     * @return true：格式正确，false：格式错误
     */
    public static boolean isLicensePlate(String licensePlate) {
        if (isEmpty(licensePlate)) {
            return false;
        }
        return LICENSE_PLATE_PATTERN.matcher(licensePlate).matches();
    }

    /**
     * 验证密码强度
     *
     * @param password 待验证密码
     * @return true：强度足够，false：强度不足
     */
    public static boolean isStrongPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证颜色代码格式
     *
     * @param colorCode 待验证颜色代码
     * @return true：格式正确，false：格式错误
     */
    public static boolean isColorCode(String colorCode) {
        if (isEmpty(colorCode)) {
            return false;
        }
        return COLOR_CODE_PATTERN.matcher(colorCode).matches();
    }

    /**
     * 验证MAC地址格式
     *
     * @param macAddress 待验证MAC地址
     * @return true：格式正确，false：格式错误
     */
    public static boolean isMacAddress(String macAddress) {
        if (isEmpty(macAddress)) {
            return false;
        }
        return MAC_ADDRESS_PATTERN.matcher(macAddress).matches();
    }

    // ================================ 编码格式验证方法 ================================

    /**
     * 验证Base64格式（使用Apache Commons Codec）
     *
     * @param base64Str 待验证Base64字符串
     * @return true：格式正确，false：格式错误
     */
    public static boolean isBase64(String base64Str) {
        if (isEmpty(base64Str)) {
            return false;
        }
        return Base64.isBase64(base64Str);
    }

    /**
     * 验证JSON格式（使用Hutool）
     *
     * @param jsonStr 待验证JSON字符串
     * @return true：格式正确，false：格式错误
     */
    public static boolean isJson(String jsonStr) {
        if (isEmpty(jsonStr)) {
            return false;
        }
        try {
            JSONUtil.parse(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证XML格式（使用Hutool）
     *
     * @param xmlStr 待验证XML字符串
     * @return true：格式正确，false：格式错误
     */
    public static boolean isXml(String xmlStr) {
        if (isEmpty(xmlStr)) {
            return false;
        }
        try {
            XmlUtil.parseXml(xmlStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ================================ 网络相关验证方法 ================================

    /**
     * 验证域名格式（使用Google Guava）
     *
     * @param domain 待验证域名
     * @return true：格式正确，false：格式错误
     */
    public static boolean isDomain(String domain) {
        if (isEmpty(domain)) {
            return false;
        }
        try {
            return InternetDomainName.isValid(domain);
        } catch (Exception e) {
            return false;
        }
    }

    // ================================ 文件相关验证方法 ================================

    /**
     * 验证文件扩展名（使用Apache Commons IO）
     *
     * @param filename          文件名
     * @param allowedExtensions 允许的扩展名列表
     * @return true：扩展名允许，false：扩展名不允许
     */
    public static boolean isFileExtensionAllowed(String filename, String... allowedExtensions) {
        if (isEmpty(filename) || ArrayUtils.isEmpty(allowedExtensions)) {
            return false;
        }
        String extension = FilenameUtils.getExtension(filename);
        return ArrayUtils.contains(allowedExtensions, extension.toLowerCase());
    }

    // ================================ require方法（验证失败抛出异常）================================

    /**
     * 要求对象不为空
     *
     * @param obj     待验证对象
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireNonNull(Object obj, String message) {
        if (isNull(obj)) {
            throw new BizException(message, ValidationErrorCode.VALUE_IS_NULL);
        }
    }

    /**
     * 要求字符串不为空
     *
     * @param str     待验证字符串
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireNonEmpty(String str, String message) {
        if (isEmpty(str)) {
            throw new BizException(message, ValidationErrorCode.STRING_IS_EMPTY);
        }
    }

    /**
     * 要求字符串不为空白
     *
     * @param str     待验证字符串
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireNonBlank(String str, String message) {
        if (isBlank(str)) {
            throw new BizException(message, ValidationErrorCode.STRING_IS_EMPTY);
        }
    }

    /**
     * 要求集合不为空
     *
     * @param collection 待验证集合
     * @param message    自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireNonEmpty(Collection<?> collection, String message) {
        if (isEmpty(collection)) {
            throw new BizException(message, ValidationErrorCode.COLLECTION_IS_EMPTY);
        }
    }

    /**
     * 要求数组不为空
     *
     * @param array   待验证数组
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireNonEmpty(Object[] array, String message) {
        if (isEmpty(array)) {
            throw new BizException(message, ValidationErrorCode.ARRAY_IS_EMPTY);
        }
    }

    /**
     * 要求字符串长度在指定范围内
     *
     * @param str     待验证字符串
     * @param min     最小长度
     * @param max     最大长度
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireLengthBetween(String str, int min, int max, String message) {
        if (!isLengthBetween(str, min, max)) {
            throw new BizException(message, ValidationErrorCode.STRING_LENGTH_OUT_OF_RANGE);
        }
    }

    /**
     * 要求邮箱格式正确
     *
     * @param email   待验证邮箱
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireEmail(String email, String message) {
        if (!isEmail(email)) {
            throw new BizException(message, ValidationErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    /**
     * 要求手机号格式正确
     *
     * @param mobile  待验证手机号
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireMobile(String mobile, String message) {
        if (!isMobile(mobile)) {
            throw new BizException(message, ValidationErrorCode.INVALID_MOBILE_FORMAT);
        }
    }

    /**
     * 要求身份证号格式正确
     *
     * @param idCard  待验证身份证号
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireIdCard(String idCard, String message) {
        if (!isIdCard(idCard)) {
            throw new BizException(message, ValidationErrorCode.INVALID_ID_CARD_FORMAT);
        }
    }

    /**
     * 要求URL格式正确
     *
     * @param url     待验证URL
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireUrl(String url, String message) {
        if (!isUrl(url)) {
            throw new BizException(message, ValidationErrorCode.INVALID_URL_FORMAT);
        }
    }

    /**
     * 要求IP地址格式正确
     *
     * @param ip      待验证IP地址
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireIpAddress(String ip, String message) {
        if (!isIpAddress(ip)) {
            throw new BizException(message, ValidationErrorCode.INVALID_IP_FORMAT);
        }
    }

    /**
     * 要求数值在指定范围内
     *
     * @param value   待验证数值
     * @param min     最小值
     * @param max     最大值
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireInRange(int value, int min, int max, String message) {
        if (!isInRange(value, min, max)) {
            throw new BizException(message, ValidationErrorCode.NUMBER_OUT_OF_RANGE);
        }
    }

    /**
     * 要求数值为正数
     *
     * @param value   待验证数值
     * @param message 自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requirePositive(int value, String message) {
        if (!isPositive(value)) {
            throw new BizException(message, ValidationErrorCode.NUMBER_NOT_POSITIVE);
        }
    }

    /**
     * 要求条件为真
     *
     * @param condition 待验证条件
     * @param message   自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new BizException(message, ValidationErrorCode.CONDITION_NOT_SATISFIED);
        }
    }

    /**
     * 要求条件为假
     *
     * @param condition 待验证条件
     * @param message   自定义错误信息
     * @throws BizException 验证失败时抛出
     */
    public static void requireFalse(boolean condition, String message) {
        if (condition) {
            throw new BizException(message, ValidationErrorCode.CONDITION_NOT_SATISFIED);
        }
    }

    /**
     * 自定义验证要求
     *
     * @param value     待验证值
     * @param predicate 验证谓词
     * @param message   自定义错误信息
     * @param <T>       值类型
     * @throws BizException 验证失败时抛出
     */
    public static <T> void requireCustom(T value, Predicate<T> predicate, String message) {
        if (!predicate.evaluate(value)) {
            throw new BizException(message, ValidationErrorCode.CUSTOM_VALIDATION_FAILED);
        }
    }

    // ================================ 链式验证器 ================================

    /**
     * 创建链式验证器
     *
     * @param value 待验证值
     * @param <T>   值类型
     * @return 链式验证器实例
     */
    public static <T> Validator<T> validator(T value) {
        return new Validator<>(value);
    }

    /**
     * 链式验证器
     *
     * @param <T> 验证值类型
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Validator<T> {

        /**
         * 待验证的值
         */
        private final T value;

        /**
         * 验证不为空
         *
         * @param message 错误信息
         * @return 当前验证器实例（支持链式调用）
         */
        public Validator<T> requireNonNull(String message) {
            ValidatorUtil.requireNonNull(value, message);
            return this;
        }

        /**
         * 验证字符串不为空
         *
         * @param message 错误信息
         * @return 当前验证器实例（支持链式调用）
         */
        @SuppressWarnings("unchecked")
        public Validator<T> requireNonEmpty(String message) {
            switch (value) {
                case String valueStr -> ValidatorUtil.requireNonEmpty(valueStr, message);
                case Collection<?> valueCollection -> ValidatorUtil.requireNonEmpty(valueCollection, message);
                case Object[] valueArray -> ValidatorUtil.requireNonEmpty(valueArray, message);
                default -> ValidatorUtil.requireNonNull(value, message);
            }
            return this;
        }

        /**
         * 验证字符串长度
         *
         * @param min     最小长度
         * @param max     最大长度
         * @param message 错误信息
         * @return 当前验证器实例（支持链式调用）
         */
        public Validator<T> requireLengthBetween(int min, int max, String message) {
            if (value instanceof String valueStr) {
                ValidatorUtil.requireLengthBetween(valueStr, min, max, message);
            }
            return this;
        }

        /**
         * 验证邮箱格式
         *
         * @param message 错误信息
         * @return 当前验证器实例（支持链式调用）
         */
        public Validator<T> requireEmail(String message) {
            if (value instanceof String valueStr) {
                ValidatorUtil.requireEmail(valueStr, message);
            }
            return this;
        }

        /**
         * 验证手机号格式
         *
         * @param message 错误信息
         * @return 当前验证器实例（支持链式调用）
         */
        public Validator<T> requireMobile(String message) {
            if (value instanceof String valueStr) {
                ValidatorUtil.requireMobile(valueStr, message);
            }
            return this;
        }

        /**
         * 自定义验证
         *
         * @param predicate 验证谓词
         * @param message   错误信息
         * @return 当前验证器实例（支持链式调用）
         */
        public Validator<T> requireCustom(Predicate<T> predicate, String message) {
            ValidatorUtil.requireCustom(value, predicate, message);
            return this;
        }
    }

    // ================================ 批量验证器 ================================

    /**
     * 创建批量验证器
     *
     * @return 批量验证器实例
     */
    public static BatchValidator batchValidator() {
        return new BatchValidator();
    }

    /**
     * 批量验证器：支持同时验证多个字段，可以选择遇到第一个错误即停止，或收集所有错误后统一处理
     */
    public static class BatchValidator {

        /**
         * 验证项列表
         */
        private final List<ValidationItem> items = new ArrayList<>();

        /**
         * 添加验证项
         *
         * @param fieldName 字段名
         * @param value     字段值
         * @param validator 验证函数
         * @param <T>       字段值类型
         * @return 当前批量验证器实例（支持链式调用）
         */
        public <T> BatchValidator add(String fieldName, T value, Function<Validator<T>, Validator<T>> validator) {
            items.add(new ValidationItem(fieldName, () -> validator.apply(ValidatorUtil.validator(value)).getValue()));
            return this;
        }

        /**
         * 执行所有验证（遇到第一个错误即停止）
         *
         * @throws BizException 验证失败时抛出
         */
        public void validate() {
            for (ValidationItem item : items) {
                item.validate();
            }
        }

        /**
         * 执行所有验证并收集所有错误
         *
         * @return 验证结果
         */
        public ValidationResult validateAll() {
            ValidationResult result = new ValidationResult();

            for (ValidationItem item : items) {
                try {
                    item.validate();
                } catch (BizException e) {
                    result.addError(item.fieldName(), e.getMessage());
                }
            }

            return result;
        }

        /**
         * 验证项
         *
         * @param fieldName          字段名
         * @param validationExecutor 验证执行器
         */
        private record ValidationItem(String fieldName, Runnable validationExecutor) {

            /**
             * 执行验证
             *
             * @throws BizException 验证失败时抛出
             */
            public void validate() {
                validationExecutor.run();
            }
        }
    }

    /**
     * 验证结果：用于收集和管理批量验证的结果，提供错误信息的获取和处理方法
     */
    public static class ValidationResult {
        /**
         * 错误信息映射表，键为字段名，值为错误信息列表
         */
        private final Map<String, List<String>> errors = new HashMap<>();

        /**
         * 添加错误信息
         *
         * @param fieldName 字段名
         * @param message   错误信息
         */
        public void addError(String fieldName, String message) {
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(message);
        }

        /**
         * 判断是否有错误
         *
         * @return true：有错误，false：无错误
         */
        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        /**
         * 获取所有错误信息
         *
         * @return 错误信息映射表
         */
        public Map<String, List<String>> getErrors() {
            return Collections.unmodifiableMap(errors);
        }

        /**
         * 获取指定字段的错误信息
         *
         * @param fieldName 字段名
         * @return 错误信息列表
         */
        public List<String> getFieldErrors(String fieldName) {
            return errors.getOrDefault(fieldName, Collections.emptyList());
        }

        /**
         * 获取第一个错误信息
         *
         * @return 第一个错误信息，如果没有错误则返回null
         */
        public String getFirstError() {
            return errors.values().stream()
                    .flatMap(List::stream)
                    .findFirst()
                    .orElse(null);
        }

        /**
         * 抛出第一个错误（如果存在）
         *
         * @throws BizException 存在错误时抛出
         */
        public void throwIfHasErrors() {
            if (hasErrors()) {
                throw new BizException(getFirstError(), ValidationErrorCode.CUSTOM_VALIDATION_FAILED);
            }
        }

        /**
         * 获取所有错误信息的字符串表示
         *
         * @return 错误信息字符串
         */
        @Override
        public String toString() {
            if (!hasErrors()) {
                return "验证通过";
            }

            StringBuilder sb = new StringBuilder("验证失败：\n");
            errors.forEach((field, messages) -> {
                sb.append("  ").append(field).append(": ");
                sb.append(String.join(", ", messages));
                sb.append("\n");
            });

            return sb.toString();
        }
    }
} 