package club.slavopolis.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 安全相关常量定义
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {

    /**
     * JWT密钥
     */
    public static final String JWT_SECRET_KEY = "jwt.secret";

    /**
     * JWT过期时间（秒）
     */
    public static final long JWT_EXPIRATION = 7200L;

    /**
     * JWT刷新时间（秒）
     */
    public static final long JWT_REFRESH_EXPIRATION = 604800L;

    /**
     * 加密算法 - AES
     */
    public static final String ALGORITHM_AES = "AES";

    /**
     * 加密算法 - RSA
     */
    public static final String ALGORITHM_RSA = "RSA";

    /**
     * 签名算法 - SHA256
     */
    public static final String ALGORITHM_SHA256 = "SHA-256";

    /**
     * 签名算法 - MD5
     */
    public static final String ALGORITHM_MD5 = "MD5";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 验证码长度
     */
    public static final int CAPTCHA_LENGTH = 6;

    /**
     * 验证码过期时间（秒）
     */
    public static final long CAPTCHA_EXPIRATION = 300L;
}
