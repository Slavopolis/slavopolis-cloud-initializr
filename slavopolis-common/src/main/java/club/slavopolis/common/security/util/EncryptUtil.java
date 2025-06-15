package club.slavopolis.common.security.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 加密工具类
 * 
 * @author slavopolis
 * @version 1.0.0
 * @since 2024-12-25
 * 
 * Copyright (c) 2024 Slavopolis Boot
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptUtil {

    /**
     * AES算法
     */
    private static final String AES_ALGORITHM = "AES";
    
    /**
     * AES/GCM/NoPadding算法
     */
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    
    /**
     * GCM标签长度
     */
    private static final int GCM_TAG_LENGTH = 16;
    
    /**
     * GCM IV长度
     */
    private static final int GCM_IV_LENGTH = 12;
    
    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // ==================== MD5相关 ====================
    
    /**
     * MD5加密
     * 
     * @param data 原始数据
     * @return MD5哈希值（32位小写）
     */
    public static String md5(String data) {
        if (data == null) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
    
    /**
     * MD5加密（带盐值）
     * 
     * @param data 原始数据
     * @param salt 盐值
     * @return MD5哈希值（32位小写）
     */
    public static String md5WithSalt(String data, String salt) {
        if (data == null || salt == null) {
            return null;
        }
        
        return md5(data + salt);
    }

    // ==================== SHA相关 ====================
    
    /**
     * SHA-1加密
     * 
     * @param data 原始数据
     * @return SHA-1哈希值（40位小写）
     */
    public static String sha1(String data) {
        return sha(data, "SHA-1");
    }
    
    /**
     * SHA-256加密
     * 
     * @param data 原始数据
     * @return SHA-256哈希值（64位小写）
     */
    public static String sha256(String data) {
        return sha(data, "SHA-256");
    }
    
    /**
     * SHA-512加密
     * 
     * @param data 原始数据
     * @return SHA-512哈希值（128位小写）
     */
    public static String sha512(String data) {
        return sha(data, "SHA-512");
    }
    
    /**
     * SHA加密通用方法
     * 
     * @param data 原始数据
     * @param algorithm 算法名称
     * @return SHA哈希值
     */
    private static String sha(String data, String algorithm) {
        if (data == null) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(algorithm + "算法不可用", e);
        }
    }
    
    /**
     * SHA-256加密（带盐值）
     * 
     * @param data 原始数据
     * @param salt 盐值
     * @return SHA-256哈希值
     */
    public static String sha256WithSalt(String data, String salt) {
        if (data == null || salt == null) {
            return null;
        }
        
        return sha256(data + salt);
    }

    // ==================== AES对称加密 ====================
    
    /**
     * 生成AES密钥
     * 
     * @param keySize 密钥长度（128, 192, 256）
     * @return Base64编码的密钥
     */
    public static String generateAESKey(int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(keySize);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES算法不可用", e);
        }
    }
    
    /**
     * 生成256位AES密钥
     * 
     * @return Base64编码的密钥
     */
    public static String generateAESKey() {
        return generateAESKey(256);
    }
    
    /**
     * AES-GCM加密
     * 
     * @param plaintext 明文
     * @param key Base64编码的密钥
     * @return Base64编码的密文（包含IV）
     */
    public static String aesEncrypt(String plaintext, String key) {
        if (plaintext == null || key == null) {
            return null;
        }
        
        try {
            // 解码密钥
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);
            
            // 初始化加密器
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            
            // 加密
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // 组合IV和密文
            byte[] encryptedData = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);
            
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }
    
    /**
     * AES-GCM解密
     * 
     * @param ciphertext Base64编码的密文（包含IV）
     * @param key Base64编码的密钥
     * @return 明文
     */
    public static String aesDecrypt(String ciphertext, String key) {
        if (ciphertext == null || key == null) {
            return null;
        }
        
        try {
            // 解码密钥和密文
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] encryptedData = Base64.getDecoder().decode(ciphertext);
            
            // 提取IV和密文
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[encryptedData.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedData, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            
            // 初始化解密器
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            // 解密
            byte[] plaintext = cipher.doFinal(encrypted);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    // ==================== Base64编码 ====================
    
    /**
     * Base64编码
     * 
     * @param data 原始数据
     * @return Base64编码字符串
     */
    public static String base64Encode(String data) {
        if (data == null) {
            return null;
        }
        
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base64编码（字节数组）
     * 
     * @param data 原始数据
     * @return Base64编码字符串
     */
    public static String base64Encode(byte[] data) {
        if (data == null) {
            return null;
        }
        
        return Base64.getEncoder().encodeToString(data);
    }
    
    /**
     * Base64解码
     * 
     * @param encodedData Base64编码字符串
     * @return 原始字符串
     */
    public static String base64Decode(String encodedData) {
        if (encodedData == null) {
            return null;
        }
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64解码失败", e);
        }
    }
    
    /**
     * Base64解码（返回字节数组）
     * 
     * @param encodedData Base64编码字符串
     * @return 原始字节数组
     */
    public static byte[] base64DecodeToBytes(String encodedData) {
        if (encodedData == null) {
            return new byte[0];
        }
        
        try {
            return Base64.getDecoder().decode(encodedData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64解码失败", e);
        }
    }
    
    /**
     * URL安全的Base64编码
     * 
     * @param data 原始数据
     * @return URL安全的Base64编码字符串
     */
    public static String base64UrlEncode(String data) {
        if (data == null) {
            return null;
        }
        
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * URL安全的Base64解码
     * 
     * @param encodedData URL安全的Base64编码字符串
     * @return 原始字符串
     */
    public static String base64UrlDecode(String encodedData) {
        if (encodedData == null) {
            return null;
        }
        
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedData);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 URL解码失败", e);
        }
    }

    // ==================== 工具方法 ====================
    
    /**
     * 字节数组转十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * 十六进制字符串转字节数组
     * 
     * @param hex 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("无效的十六进制字符串");
        }
        
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }
    
    /**
     * 生成随机盐值
     * 
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 生成16字节随机盐值
     * 
     * @return 随机盐值
     */
    public static String generateSalt() {
        return generateSalt(16);
    }
    
    /**
     * 安全比较两个字符串（防止时序攻击）
     * 
     * @param a 字符串A
     * @param b 字符串B
     * @return 是否相等
     */
    public static boolean secureEquals(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        
        if (a == null || b == null) {
            return false;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        if (aBytes.length != bBytes.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        
        return result == 0;
    }
} 