package club.slavopolis.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 通用常量定义
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

    // ==================== 系统常量 ====================

    /**
     * 系统编码
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 系统默认时区
     */
    public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";

    /**
     * 系统默认语言
     */
    public static final String DEFAULT_LOCALE = "zh_CN";

    // ==================== 分页常量 ====================

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大页大小
     */
    public static final int MAX_PAGE_SIZE = 1000;

    // ==================== 请求追踪常量 ====================

    /**
     * 请求追踪ID
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 请求ID
     */
    public static final String REQUEST_ID = "requestId";

    /**
     * 用户ID
     */
    public static final String USER_ID = "userId";

    /**
     * 租户ID
     */
    public static final String TENANT_ID = "tenantId";

    // ==================== 数据状态常量 ====================

    /**
     * 删除标记 - 未删除
     */
    public static final Integer DELETED_NO = 0;

    /**
     * 删除标记 - 已删除
     */
    public static final Integer DELETED_YES = 1;

    /**
     * 启用状态
     */
    public static final Integer STATUS_ENABLE = 1;

    /**
     * 禁用状态
     */
    public static final Integer STATUS_DISABLE = 0;

    // ==================== 缓存常量 ====================

    /**
     * 缓存key分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = ":";

    /**
     * 缓存默认过期时间（秒）
     */
    public static final long CACHE_DEFAULT_EXPIRE = 3600L;

    // ==================== 业务常量 ====================

    /**
     * 成功标记
     */
    public static final String SUCCESS = "SUCCESS";

    /**
     * 失败标记
     */
    public static final String FAIL = "FAIL";

    /**
     * 是
     */
    public static final String YES = "Y";

    /**
     * 否
     */
    public static final String NO = "N";

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 空格
     */
    public static final String SPACE = " ";

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 下划线
     */
    public static final String UNDERSCORE = "_";

    /**
     * 横杠
     */
    public static final String HYPHEN = "-";

    /**
     * 斜杠
     */
    public static final String SLASH = "/";

    /**
     * 反斜杠
     */
    public static final String BACKSLASH = "\\";

    /**
     * 未知
     */
    public static final String UNKNOWN = "unknown";

    /**
     * 换行
     */
    public static final String NEW_LINE = "\n";

    /**
     * 制表符
     */
    public static final String TAB = "\t";

    /**
     * 左大括号
     */
    public static final String LEFT_BRACE = "{";

    /**
     * 右大括号
     */
    public static final String RIGHT_BRACE = "}";

    /**
     * 左中括号
     */
    public static final String LEFT_BRACKET = "[";

    /**
     * 右中括号
     */
    public static final String RIGHT_BRACKET = "]";

    /**
     * 左小括号
     */
    public static final String LEFT_PARENTHESIS = "(";

    /**
     * 右小括号
     */
    public static final String RIGHT_PARENTHESIS = ")";
}
