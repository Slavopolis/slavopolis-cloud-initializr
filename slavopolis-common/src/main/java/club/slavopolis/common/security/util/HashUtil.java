package club.slavopolis.common.security.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 哈希工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HashUtil {

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ==================== 一致性哈希 ====================
    
    /**
     * 计算字符串的一致性哈希值
     * 
     * @param input 输入字符串
     * @return 哈希值
     */
    public static int consistentHash(String input) {
        if (input == null) {
            return 0;
        }
        
        // 使用FNV-1a算法
        int hash = 0x811c9dc5;
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        
        for (byte b : bytes) {
            hash ^= (b & 0xff);
            hash *= 0x01000193;
        }
        
        return hash;
    }
    
    /**
     * 计算字符串的一致性哈希值（指定范围）
     * 
     * @param input 输入字符串
     * @param range 哈希范围
     * @return 哈希值（0到range-1）
     */
    public static int consistentHash(String input, int range) {
        if (range <= 0) {
            throw new IllegalArgumentException("范围必须大于0");
        }
        
        int hash = consistentHash(input);
        return Math.abs(hash) % range;
    }

    // ==================== MurmurHash ====================
    
    /**
     * MurmurHash3算法（32位）
     * 
     * @param data 输入数据
     * @param seed 种子值
     * @return 哈希值
     */
    public static int murmurHash3(String data, int seed) {
        if (data == null) {
            return 0;
        }
        
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return murmurHash3(bytes, seed);
    }
    
    /**
     * MurmurHash3算法（32位）
     * 
     * @param data 输入数据
     * @param seed 种子值
     * @return 哈希值
     */
    public static int murmurHash3(byte[] data, int seed) {
        if (data == null) {
            return 0;
        }
        
        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;
        final int r1 = 15;
        final int r2 = 13;
        final int m = 5;
        final int n = 0xe6546b64;
        
        int hash = seed;
        int len = data.length;
        // round down to 4 byte block
        int roundedEnd = len & 0xfffffffc;
        
        for (int i = 0; i < roundedEnd; i += 4) {
            int k = (data[i] & 0xff) | ((data[i + 1] & 0xff) << 8) |
                    ((data[i + 2] & 0xff) << 16) | (data[i + 3] << 24);
            k *= c1;
            k = (k << r1) | (k >>> (32 - r1));
            k *= c2;
            hash ^= k;
            hash = ((hash << r2) | (hash >>> (32 - r2))) * m + n;
        }
        
        int k1 = 0;
        switch (len & 0x03) {
            case 3:
                k1 = (data[roundedEnd + 2] & 0xff) << 16;
                // fall through
            case 2:
                k1 |= (data[roundedEnd + 1] & 0xff) << 8;
                // fall through
            case 1:
                k1 |= (data[roundedEnd] & 0xff);
                k1 *= c1;
                k1 = (k1 << r1) | (k1 >>> (32 - r1));
                k1 *= c2;
                hash ^= k1;
        }
        
        hash ^= len;
        hash ^= (hash >>> 16);
        hash *= 0x85ebca6b;
        hash ^= (hash >>> 13);
        hash *= 0xc2b2ae35;
        hash ^= (hash >>> 16);
        
        return hash;
    }

    // ==================== CityHash ====================
    
    /**
     * CityHash算法（简化版）
     * 
     * @param data 输入数据
     * @return 哈希值
     */
    public static long cityHash(String data) {
        if (data == null) {
            return 0L;
        }
        
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return cityHash(bytes);
    }
    
    /**
     * CityHash算法（简化版）
     * 
     * @param data 输入数据
     * @return 哈希值
     */
    public static long cityHash(byte[] data) {
        if (data == null || data.length == 0) {
            return 0L;
        }
        
        long hash = 0x9ae16a3b2f90404fL;
        
        for (byte b : data) {
            hash ^= (b & 0xff);
            hash *= 0x9e3779b97f4a7c15L;
            hash = Long.rotateLeft(hash, 31);
        }
        
        hash ^= hash >>> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >>> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >>> 33;
        
        return hash;
    }

    // ==================== 分布式哈希 ====================
    
    /**
     * 计算分片索引
     * 
     * @param key 分片键
     * @param shardCount 分片数量
     * @return 分片索引
     */
    public static int getShardIndex(String key, int shardCount) {
        if (shardCount <= 0) {
            throw new IllegalArgumentException("分片数量必须大于0");
        }
        
        return Math.abs(consistentHash(key)) % shardCount;
    }
    
    /**
     * 计算负载均衡索引
     * 
     * @param key 负载均衡键
     * @param serverCount 服务器数量
     * @return 服务器索引
     */
    public static int getLoadBalanceIndex(String key, int serverCount) {
        if (serverCount <= 0) {
            throw new IllegalArgumentException("服务器数量必须大于0");
        }
        
        return Math.abs(murmurHash3(key, 0)) % serverCount;
    }

    // ==================== 哈希环 ====================
    
    /**
     * 一致性哈希环节点
     */
    @Getter
    public static class HashRingNode {
        private final String nodeId;
        private final int hash;
        
        public HashRingNode(String nodeId) {
            this.nodeId = nodeId;
            this.hash = consistentHash(nodeId);
        }

    }
    
    /**
     * 在哈希环中查找节点
     * 
     * @param key 查找键
     * @param nodes 节点列表
     * @return 对应的节点
     */
    public static HashRingNode findNodeInRing(String key, java.util.List<HashRingNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        
        if (nodes.size() == 1) {
            return nodes.getFirst();
        }
        
        int keyHash = consistentHash(key);
        
        // 按哈希值排序
        java.util.List<HashRingNode> sortedNodes = new java.util.ArrayList<>(nodes);
        sortedNodes.sort((a, b) -> Integer.compare(a.getHash(), b.getHash()));
        
        // 查找第一个哈希值大于等于key哈希值的节点
        for (HashRingNode node : sortedNodes) {
            if (node.getHash() >= keyHash) {
                return node;
            }
        }
        
        // 如果没找到，返回第一个节点（环形结构）
        return sortedNodes.getFirst();
    }

    // ==================== 哈希验证 ====================
    
    /**
     * 验证数据完整性
     * 
     * @param data 原始数据
     * @param expectedHash 期望的哈希值
     * @param algorithm 哈希算法
     * @return 是否匹配
     */
    public static boolean verifyIntegrity(String data, String expectedHash, String algorithm) {
        if (data == null || expectedHash == null || algorithm == null) {
            return false;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            String actualHash = bytesToHex(hash);
            return EncryptUtil.secureEquals(expectedHash.toLowerCase(), actualHash.toLowerCase());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持的哈希算法: " + algorithm, e);
        }
    }
    
    /**
     * 验证SHA-256哈希
     * 
     * @param data 原始数据
     * @param expectedHash 期望的哈希值
     * @return 是否匹配
     */
    public static boolean verifySha256(String data, String expectedHash) {
        return verifyIntegrity(data, expectedHash, "SHA-256");
    }

    // ==================== 哈希表相关 ====================
    
    /**
     * 计算哈希表索引
     * 
     * @param key 键
     * @param tableSize 表大小
     * @return 索引
     */
    public static int getHashTableIndex(String key, int tableSize) {
        if (tableSize <= 0) {
            throw new IllegalArgumentException("表大小必须大于0");
        }
        
        return Math.abs(key.hashCode()) % tableSize;
    }
    
    /**
     * 计算下一个质数大小（用于哈希表扩容）
     * 
     * @param n 当前大小
     * @return 下一个质数
     */
    public static int nextPrime(int n) {
        if (n <= 1) {
            return 2;
        }
        
        int candidate = n + 1;
        while (!isPrime(candidate)) {
            candidate++;
        }
        
        return candidate;
    }
    
    /**
     * 判断是否为质数
     * 
     * @param n 数字
     * @return 是否为质数
     */
    private static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        
        return true;
    }

    // ==================== 随机哈希 ====================
    
    /**
     * 生成随机哈希种子
     * 
     * @return 随机种子
     */
    public static int randomSeed() {
        return SECURE_RANDOM.nextInt();
    }
    
    /**
     * 生成随机哈希种子（指定范围）
     * 
     * @param bound 上界（不包含）
     * @return 随机种子
     */
    public static int randomSeed(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    // ==================== 工具方法 ====================
    
    /**
     * 字节数组转十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * 计算哈希碰撞概率（生日悖论）
     * 
     * @param hashBits 哈希位数
     * @param items 项目数量
     * @return 碰撞概率
     */
    public static double calculateCollisionProbability(int hashBits, long items) {
        if (hashBits <= 0 || items <= 0) {
            return 0.0;
        }
        
        double hashSpace = Math.pow(2, hashBits);
        if (items >= hashSpace) {
            return 1.0;
        }
        
        // 使用生日悖论公式的近似计算
        double exponent = -(items * (items - 1)) / (2 * hashSpace);
        return 1 - Math.exp(exponent);
    }
    
    /**
     * 计算建议的哈希表大小
     * 
     * @param expectedItems 预期项目数量
     * @param loadFactor 负载因子
     * @return 建议的表大小
     */
    public static int suggestHashTableSize(int expectedItems, double loadFactor) {
        if (expectedItems <= 0 || loadFactor <= 0 || loadFactor > 1) {
            throw new IllegalArgumentException("无效的参数");
        }
        
        int suggestedSize = (int) Math.ceil(expectedItems / loadFactor);
        return nextPrime(suggestedSize);
    }
} 