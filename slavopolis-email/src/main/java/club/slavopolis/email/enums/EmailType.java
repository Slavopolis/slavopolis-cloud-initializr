package club.slavopolis.email.enums;

import lombok.Getter;

/**
 * 邮件类型枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.enums
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Getter
public enum EmailType {

    NOTIFICATION("通知类"),
    MARKETING("营销类"),
    SYSTEM("系统类"),
    VERIFICATION("验证类"),
    ALERT("告警类"),
    REPORT("报告类");

    private final String description;

    EmailType(String description) {
        this.description = description;
    }
}
