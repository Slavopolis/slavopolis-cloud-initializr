package club.slavopolis.base.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import club.slavopolis.base.exception.IdGenerationException;
import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 唯一ID生成工具类
 * 
 * <h3>支持的ID类型：</h3>
 * <ul>
 *   <li>UUID: 标准UUID、紧凑UUID、短UUID</li>
 *   <li>雪花算法: 分布式环境下的高性能ID</li>
 *   <li>时间戳ID: 基于时间的有序ID</li>
 *   <li>序列号ID: 自增序列号</li>
 *   <li>业务ID: 带前缀的业务标识</li>
 * </ul>
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
public final class UniqueIdUtil {

    // ================================ 常量定义 ================================

    /**
     * Base62字符集（数字+大小写字母）
     */
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    /**
     * Base36字符集（数字+小写字母）
     */
    private static final String BASE36_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
    
    /**
     * 时间格式化器
     */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    
    /**
     * 短UUID长度
     */
    private static final int SHORT_UUID_LENGTH = 8;
    
    /**
     * 默认随机数长度
     */
    private static final int DEFAULT_RANDOM_LENGTH = 6;

    // ================================ 线程安全组件 ================================

    /**
     * 全局序列号生成器
     */
    private static final AtomicLong SEQUENCE_GENERATOR = new AtomicLong(1);
    
    /**
     * 雪花算法实例
     */
    private static final SnowflakeIdGenerator SNOWFLAKE = new SnowflakeIdGenerator();
    
    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ================================ UUID系列 ================================

    /**
     * 生成标准UUID
     * <p>
     * 格式: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     *
     * @return 标准UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成紧凑UUID（无分隔符）
     * <p>
     * 格式: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx (32位)
     *
     * @return 紧凑UUID字符串
     */
    public static String compactUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成短UUID
     * <p>
     * 基于UUID生成8位短ID，适用于用户友好的场景
     *
     * @return 8位短UUID字符串
     */
    public static String shortUuid() {
        return shortUuid(SHORT_UUID_LENGTH);
    }

    /**
     * 生成指定长度的短UUID
     *
     * @param length 长度（4-16位）
     * @return 指定长度的短UUID字符串
     * @throws IllegalArgumentException 长度超出范围时抛出
     */
    public static String shortUuid(int length) {
        if (length < 4 || length > 16) {
            throw new IllegalArgumentException("短UUID长度必须在4-16位之间");
        }
        
        String uuid = compactUuid();
        return uuid.substring(0, length).toUpperCase();
    }

    // ================================ 雪花算法ID ================================

    /**
     * 生成雪花算法ID
     * <p>
     * 64位长整型ID，保证在分布式环境下的唯一性和有序性
     *
     * @return 雪花算法ID字符串
     */
    public static String snowflake() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

    /**
     * 生成雪花算法ID（Long类型）
     *
     * @return 雪花算法ID
     */
    public static long snowflakeLong() {
        return SNOWFLAKE.nextId();
    }

    // ================================ 时间戳ID ================================

    /**
     * 生成时间戳ID
     * <p>
     * 格式: yyyyMMddHHmmssSSS + 6位随机数
     *
     * @return 时间戳ID字符串
     */
    public static String timestamp() {
        return timestamp(DEFAULT_RANDOM_LENGTH);
    }

    /**
     * 生成带指定随机数长度的时间戳ID
     *
     * @param randomLength 随机数长度
     * @return 时间戳ID字符串
     */
    public static String timestamp(int randomLength) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String random = randomNumeric(randomLength);
        return timestamp + random;
    }

    /**
     * 生成纳秒时间戳ID
     * <p>
     * 基于System.nanoTime()生成，适用于高并发场景
     *
     * @return 纳秒时间戳ID字符串
     */
    public static String nanoTimestamp() {
        long nanoTime = System.nanoTime();
        long currentTime = System.currentTimeMillis();
        return currentTime + String.valueOf(nanoTime).substring(0, 6);
    }

    /**
     * 生成简洁时间戳ID
     * <p>
     * 格式: yyMMddHHmmss + 4位随机数
     *
     * @return 简洁时间戳ID字符串
     */
    public static String compactTimestamp() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        String random = randomNumeric(4);
        return timestamp + random;
    }

    // ================================ 序列号ID ================================

    /**
     * 生成全局序列号ID
     * <p>
     * 自增序列号，从1开始递增
     *
     * @return 序列号ID字符串
     */
    public static String sequence() {
        return String.valueOf(SEQUENCE_GENERATOR.getAndIncrement());
    }

    /**
     * 生成带前缀的序列号ID
     *
     * @param prefix 前缀
     * @return 带前缀的序列号ID字符串
     */
    public static String sequence(String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return sequence();
        }
        return prefix + SEQUENCE_GENERATOR.getAndIncrement();
    }

    /**
     * 生成固定长度的序列号ID
     *
     * @param length 总长度（不包含前缀）
     * @return 固定长度的序列号ID字符串
     */
    public static String paddedSequence(int length) {
        return paddedSequence(null, length);
    }

    /**
     * 生成带前缀的固定长度序列号ID
     *
     * @param prefix 前缀
     * @param length 数字部分长度
     * @return 带前缀的固定长度序列号ID字符串
     */
    public static String paddedSequence(String prefix, int length) {
        long seq = SEQUENCE_GENERATOR.getAndIncrement();
        String seqStr = String.valueOf(seq);
        
        // 使用StringBuilder避免字符串拼接构造格式说明符
        StringBuilder paddedSeq = new StringBuilder(length);
        int paddingLength = length - seqStr.length();
        
        // 添加前导零
        for (int i = 0; i < paddingLength; i++) {
            paddedSeq.append('0');
        }
        paddedSeq.append(seqStr);
        
        return StringUtils.hasText(prefix) ? prefix + paddedSeq : paddedSeq.toString();
    }

    // ================================ 业务ID ================================

    /**
     * 生成业务ID
     * <p>
     * 格式: 前缀 + 时间戳 + 随机数
     *
     * @param prefix 业务前缀
     * @return 业务ID字符串
     */
    public static String businessId(String prefix) {
        return businessId(prefix, DEFAULT_RANDOM_LENGTH);
    }

    /**
     * 生成带指定随机数长度的业务ID
     *
     * @param prefix       业务前缀
     * @param randomLength 随机数长度
     * @return 业务ID字符串
     */
    public static String businessId(String prefix, int randomLength) {
        if (!StringUtils.hasText(prefix)) {
            throw new IllegalArgumentException("业务前缀不能为空");
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = randomNumeric(randomLength);
        return prefix + timestamp + random;
    }

    /**
     * 生成文件ID
     * <p>
     * 专门用于文件标识的ID生成
     *
     * @return 文件ID字符串
     */
    public static String fileId() {
        return businessId("FILE", 4);
    }

    /**
     * 生成用户ID
     * <p>
     * 专门用于用户标识的ID生成
     *
     * @return 用户ID字符串
     */
    public static String userId() {
        return businessId("USER", 4);
    }

    /**
     * 生成订单ID
     * <p>
     * 专门用于订单标识的ID生成
     *
     * @return 订单ID字符串
     */
    public static String orderId() {
        return businessId("ORDER", 6);
    }

    // ================================ 随机数生成 ================================

    /**
     * 生成指定长度的数字随机字符串
     *
     * @param length 长度
     * @return 数字随机字符串
     */
    public static String randomNumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的字母数字混合随机字符串
     *
     * @param length 长度
     * @return 字母数字混合随机字符串
     */
    public static String randomAlphanumeric(int length) {
        return randomString(length, BASE62_CHARS);
    }

    /**
     * 生成指定长度的小写字母数字混合随机字符串
     *
     * @param length 长度
     * @return 小写字母数字混合随机字符串
     */
    public static String randomAlphanumericLower(int length) {
        return randomString(length, BASE36_CHARS);
    }

    /**
     * 根据指定字符集生成随机字符串
     *
     * @param length 长度
     * @param chars  字符集
     * @return 随机字符串
     */
    private static String randomString(int length, String chars) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    // ================================ 安全随机数 ================================

    /**
     * 生成加密安全的随机数字字符串
     *
     * @param length 长度
     * @return 安全随机数字字符串
     */
    public static String secureRandomNumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(SECURE_RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成加密安全的随机字母数字字符串
     *
     * @param length 长度
     * @return 安全随机字母数字字符串
     */
    public static String secureRandomAlphanumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(BASE62_CHARS.length());
            sb.append(BASE62_CHARS.charAt(index));
        }
        return sb.toString();
    }

    // ================================ 工具方法 ================================

    /**
     * 验证ID是否为UUID格式
     *
     * @param id ID字符串
     * @return 是否为UUID格式
     */
    public static boolean isUuid(String id) {
        if (!StringUtils.hasText(id)) {
            return false;
        }
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 验证ID是否为数字格式
     *
     * @param id ID字符串
     * @return 是否为数字格式
     */
    public static boolean isNumeric(String id) {
        if (!StringUtils.hasText(id)) {
            return false;
        }
        return id.matches("\\d+");
    }

    /**
     * 获取ID生成统计信息
     *
     * @return 统计信息字符串
     */
    public static String getStatistics() {
        return String.format("当前序列号: %d, 雪花算法节点: %d", 
            SEQUENCE_GENERATOR.get(), SNOWFLAKE.getWorkerId());
    }

    // ================================ 雪花算法实现 ================================

    /**
     * 雪花算法ID生成器
     * <p>
     * 64位ID构成：1位符号位 + 41位时间戳 + 10位工作机器ID + 12位序列号
     */
    @Getter
    private static class SnowflakeIdGenerator {
        
        /** 开始时间戳 (2020-01-01) */
        private static final long EPOCH = 1577808000000L;
        
        /** 工作机器ID位数 */
        private static final long WORKER_ID_BITS = 10L;
        
        /** 序列号位数 */
        private static final long SEQUENCE_BITS = 12L;
        
        /** 工作机器ID最大值 */
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        
        /** 序列号最大值 */
        private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
        
        /** 工作机器ID左移位数 */
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        
        /** 时间戳左移位数 */
        private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        
        /** 工作机器ID
         */
        private final long workerId;
        
        /** 序列号 */
        private long sequence = 0L;
        
        /** 上次生成ID的时间戳 */
        private long lastTimestamp = -1L;
        
        /**
         * 构造函数
         */
        public SnowflakeIdGenerator() {
            // 根据机器特征生成工作机器ID
            this.workerId = generateWorkerId();
        }
        
        /**
         * 生成唯一ID
         *
         * @return 唯一ID
         */
        public synchronized long nextId() {
            long timestamp = getCurrentTimestamp();
            
            // 时钟回拨检查
            if (timestamp < lastTimestamp) {
                long offset = lastTimestamp - timestamp;
                if (offset <= 5) {
                    try {
                        // 使用LockSupport.parkNanos替代Thread.sleep
                        LockSupport.parkNanos((offset << 1) * 1_000_000L); // 转换为纳秒
                        timestamp = getCurrentTimestamp();
                        if (timestamp < lastTimestamp) {
                            throw new IdGenerationException("时钟回拨异常，拒绝生成ID");
                        }
                    } catch (Exception e) {
                        throw new IdGenerationException("等待时钟同步失败", e);
                    }
                } else {
                    throw new IdGenerationException("时钟回拨异常，拒绝生成ID");
                }
            }
            
            // 同一毫秒内序列号递增
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            
            lastTimestamp = timestamp;
            
            // 组装ID
            return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                    | (workerId << WORKER_ID_SHIFT)
                    | sequence;
        }
        
        /**
         * 获取当前时间戳
         */
        private long getCurrentTimestamp() {
            return System.currentTimeMillis();
        }
        
        /**
         * 等待下一毫秒
         */
        private long tilNextMillis(long lastTimestamp) {
            long timestamp = getCurrentTimestamp();
            while (timestamp <= lastTimestamp) {
                timestamp = getCurrentTimestamp();
            }
            return timestamp;
        }
        
        /**
         * 生成工作机器ID
         */
        private long generateWorkerId() {
            try {
                String sb = java.net.InetAddress.getLocalHost().getHostName() +
                        System.getProperty("user.name") +
                        System.getProperty("java.runtime.version");
                
                return (sb.hashCode() & 0xFFFFFFFFL) % (MAX_WORKER_ID + 1);
            } catch (Exception e) {
                log.warn("生成工作机器ID失败，使用随机数: {}", e.getMessage());
                return ThreadLocalRandom.current().nextLong(MAX_WORKER_ID + 1);
            }
        }
    }
} 