package club.slavopolis.common.enums;

import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 操作类型枚举
 */
@Getter
public enum OperationType {

    /**
     * 查询操作
     */
    QUERY("查询"),

    /**
     * 新增操作
     */
    CREATE("新增"),

    /**
     * 更新操作
     */
    UPDATE("更新"),

    /**
     * 删除操作
     */
    DELETE("删除"),

    /**
     * 导入操作
     */
    IMPORT("导入"),

    /**
     * 导出操作
     */
    EXPORT("导出"),

    /**
     * 登录
     */
    LOGIN("登录"),

    /**
     * 登出
     */
    LOGOUT("登出"),

    /**
     * 其他操作
     */
    OTHER("其他");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }
}
