package club.slavopolis.common.core.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import club.slavopolis.common.core.constants.CommonConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ID生成器工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdGenerator {

    /**
     * 雪花算法实例
     */
    private static final SnowflakeIdGenerator SNOWFLAKE = new SnowflakeIdGenerator();
    
    /**
     * 序列号生成器
     */
    private static final AtomicLong SEQUENCE = new AtomicLong(0);
    
    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // ==================== UUID相关 ====================
    
    /**
     * 生成标准UUID（带横线）
     * 
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成简化UUID（不带横线）
     * 
     * @return 32位UUID字符串
     */
    public static String simpleUuid() {
        return UUID.randomUUID().toString().replace(CommonConstants.HYPHEN, CommonConstants.EMPTY);
    }
    
    /**
     * 生成大写UUID（不带横线）
     * 
     * @return 32位大写UUID字符串
     */
    public static String upperUuid() {
        return simpleUuid().toUpperCase();
    }
    
    // ==================== 雪花算法 ====================
    
    /**
     * 生成雪花算法ID
     * 
     * @return 雪花算法ID
     */
    public static long snowflakeId() {
        return SNOWFLAKE.nextId();
    }
    
    /**
     * 生成雪花算法ID字符串
     * 
     * @return 雪花算法ID字符串
     */
    public static String snowflakeIdStr() {
        return String.valueOf(snowflakeId());
    }
    
    // ==================== 时间戳相关 ====================
    
    /**
     * 生成时间戳ID（毫秒级）
     * 
     * @return 时间戳ID
     */
    public static long timestampId() {
        return System.currentTimeMillis();
    }
    
    /**
     * 生成时间戳ID字符串
     * 
     * @return 时间戳ID字符串
     */
    public static String timestampIdStr() {
        return String.valueOf(timestampId());
    }
    
    /**
     * 生成带随机数的时间戳ID
     * 
     * @return 时间戳+随机数ID
     */
    public static String timestampRandomId() {
        return timestampId() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }
    
    // ==================== 序列号相关 ====================
    
    /**
     * 生成递增序列号
     * 
     * @return 序列号
     */
    public static long sequenceId() {
        return SEQUENCE.incrementAndGet();
    }
    
    /**
     * 生成递增序列号字符串
     * 
     * @return 序列号字符串
     */
    public static String sequenceIdStr() {
        return String.valueOf(sequenceId());
    }
    
    /**
     * 生成带前缀的序列号
     * 
     * @param prefix 前缀
     * @param length 序列号长度（不足补0）
     * @return 带前缀的序列号
     */
    public static String sequenceIdWithPrefix(String prefix, int length) {
        long seq = sequenceId();
        String format = "%s%0" + length + "d";
        return String.format(format, prefix, seq);
    }
    
    // ==================== 业务ID相关 ====================
    
    /**
     * 生成用户ID
     * 
     * @return 用户ID
     */
    public static String userId() {
        return "U" + timestampRandomId();
    }
    
    /**
     * 生成租户ID
     * 
     * @return 租户ID
     */
    public static String tenantId() {
        return "T" + timestampRandomId();
    }
    
    /**
     * 生成角色ID
     * 
     * @return 角色ID
     */
    public static String roleId() {
        return "R" + timestampRandomId();
    }
    
    /**
     * 生成权限ID
     * 
     * @return 权限ID
     */
    public static String permissionId() {
        return "P" + timestampRandomId();
    }
    
    /**
     * 生成会话ID
     * 
     * @return 会话ID
     */
    public static String sessionId() {
        return "S" + simpleUuid();
    }
    
    /**
     * 生成请求ID
     * 
     * @return 请求ID
     */
    public static String requestId() {
        return "REQ" + timestampRandomId();
    }
    
    /**
     * 生成追踪ID
     * 
     * @return 追踪ID
     */
    public static String traceId() {
        return "TRC" + simpleUuid();
    }
    
    // ==================== 订单号相关 ====================
    
    /**
     * 生成订单号
     * 
     * @return 订单号
     */
    public static String orderNo() {
        return generateBusinessNo("ORD");
    }
    
    /**
     * 生成支付单号
     * 
     * @return 支付单号
     */
    public static String paymentNo() {
        return generateBusinessNo("PAY");
    }
    
    /**
     * 生成退款单号
     * 
     * @return 退款单号
     */
    public static String refundNo() {
        return generateBusinessNo("REF");
    }
    
    /**
     * 生成业务单号
     * 
     * @param prefix 前缀
     * @return 业务单号
     */
    private static String generateBusinessNo(String prefix) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + dateStr + random;
    }
    
    // ==================== 随机字符串 ====================
    
    /**
     * 生成随机字符串（数字+字母）
     * 
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        return randomString(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }
    
    /**
     * 生成随机数字字符串
     * 
     * @param length 长度
     * @return 随机数字字符串
     */
    public static String randomNumeric(int length) {
        return randomString(length, "0123456789");
    }
    
    /**
     * 生成随机字母字符串
     * 
     * @param length 长度
     * @return 随机字母字符串
     */
    public static String randomAlphabetic(int length) {
        return randomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }
    
    /**
     * 生成随机字符串
     * 
     * @param length 长度
     * @param chars 字符集
     * @return 随机字符串
     */
    public static String randomString(int length, String chars) {
        if (length <= 0 || chars == null || chars.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    // ==================== 验证码相关 ====================
    
    /**
     * 生成数字验证码
     * 
     * @param length 长度
     * @return 数字验证码
     */
    public static String verifyCode(int length) {
        return randomNumeric(length);
    }
    
    /**
     * 生成6位数字验证码
     * 
     * @return 6位数字验证码
     */
    public static String verifyCode() {
        return verifyCode(6);
    }
    
    /**
     * 雪花算法ID生成器
     */
    private static class SnowflakeIdGenerator {
        
        /**
         * 起始时间戳 (2024-01-01 00:00:00)
         */
        private static final long START_TIMESTAMP = 1704067200000L;
        
        /**
         * 机器ID位数
         */
        private static final long MACHINE_ID_BITS = 5L;
        
        /**
         * 数据中心ID位数
         */
        private static final long DATACENTER_ID_BITS = 5L;
        
        /**
         * 序列号位数
         */
        private static final long SEQUENCE_BITS = 12L;
        
        /**
         * 机器ID最大值
         */
        private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
        
        /**
         * 数据中心ID最大值
         */
        private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
        
        /**
         * 序列号最大值
         */
        private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
        
        /**
         * 机器ID左移位数
         */
        private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
        
        /**
         * 数据中心ID左移位数
         */
        private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
        
        /**
         * 时间戳左移位数
         */
        private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;
        
        /**
         * 机器ID
         */
        private final long machineId;
        
        /**
         * 数据中心ID
         */
        private final long datacenterId;
        
        /**
         * 序列号
         */
        private long sequence = 0L;
        
        /**
         * 上次生成ID的时间戳
         */
        private long lastTimestamp = -1L;
        
        /**
         * 构造函数
         */
        public SnowflakeIdGenerator() {
            this(1L, 1L);
        }
        
        /**
         * 构造函数
         * 
         * @param machineId 机器ID
         * @param datacenterId 数据中心ID
         */
        public SnowflakeIdGenerator(long machineId, long datacenterId) {
            if (machineId > MAX_MACHINE_ID || machineId < 0) {
                throw new IllegalArgumentException("机器ID不能大于" + MAX_MACHINE_ID + "或小于0");
            }
            if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
                throw new IllegalArgumentException("数据中心ID不能大于" + MAX_DATACENTER_ID + "或小于0");
            }
            this.machineId = machineId;
            this.datacenterId = datacenterId;
        }
        
        /**
         * 生成下一个ID
         * 
         * @return ID
         */
        public synchronized long nextId() {
            long timestamp = System.currentTimeMillis();
            
            // 时钟回拨检查
            if (timestamp < lastTimestamp) {
                throw new RuntimeException("时钟回拨，拒绝生成ID");
            }
            
            // 同一毫秒内
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                // 序列号溢出
                if (sequence == 0) {
                    timestamp = waitNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            
            lastTimestamp = timestamp;
            
            // 生成ID
            return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                    | (datacenterId << DATACENTER_ID_SHIFT)
                    | (machineId << MACHINE_ID_SHIFT)
                    | sequence;
        }
        
        /**
         * 等待下一毫秒
         * 
         * @param lastTimestamp 上次时间戳
         * @return 下一毫秒时间戳
         */
        private long waitNextMillis(long lastTimestamp) {
            long timestamp = System.currentTimeMillis();
            while (timestamp <= lastTimestamp) {
                timestamp = System.currentTimeMillis();
            }
            return timestamp;
        }
    }
} 