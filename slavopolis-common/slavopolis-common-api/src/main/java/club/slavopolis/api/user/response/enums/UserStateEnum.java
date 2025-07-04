package club.slavopolis.api.user.response.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/18
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum UserStateEnum {

    INITIALIZED("INITIALIZED", "初始化"),
    ACTIVE("ACTIVE", "正常"),
    BLOCKED("BLOCKED", "封禁"),
    AUTHENTICATED("AUTHENTICATED", "认证"),
    DELETED("DELETED", "删除");

    private final String code;
    private final String description;

    /**
     * 根据code获取枚举值
     *
     * @param code 枚举值code
     * @return 枚举值
     */
    public String fromCode(String code) {
        for (UserStateEnum value : values()) {
            if (value.code.equals(code)) {
                return value.description;
            }
        }
        return null;
    }
}
