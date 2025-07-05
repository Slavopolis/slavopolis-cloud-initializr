package club.slavopolis.base.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/5
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum OperationType {

     QUERY("查询"),
     CREATE("新增"),
      UPDATE("更新"),
     DELETE("删除"),
     IMPORT("导入"),
     EXPORT("导出"),
     LOGIN("登录"),
     LOGOUT("登出"),
     OTHER("其他");

     private final String description;
}
