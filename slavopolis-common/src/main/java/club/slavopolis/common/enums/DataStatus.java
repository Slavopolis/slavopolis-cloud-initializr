package club.slavopolis.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 数据状态枚举
 */
@Getter
@AllArgsConstructor
public enum DataStatus {

    /**
     * 正常
     */
    NORMAL(0, "正常"),

    /**
     * 禁用
     */
    DISABLED(1, "禁用"),

    /**
     * 删除
     */
    DELETED(2, "删除"),

    /**
     * 锁定
     */
    LOCKED(3, "锁定"),

    /**
     * 过期
     */
    EXPIRED(4, "过期");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static DataStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (DataStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
