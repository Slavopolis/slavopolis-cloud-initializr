package club.slavopolis.file.enums;

import lombok.Getter;

/**
 * 访问权限枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
public enum AccessPermission {
    /**
     * 私有
     */
    PRIVATE("私有"),
    
    /**
     * 公开只读
     */
    PUBLIC_READ("公开只读"),
    
    /**
     * 公开读写
     */
    PUBLIC_READ_WRITE("公开读写"),
    
    /**
     * 租户内共享
     */
    TENANT_SHARED("租户共享");

    private final String description;

    AccessPermission(String description) {
        this.description = description;
    }

}