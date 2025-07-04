package club.slavopolis.base.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 基础数据库字段常量类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/4
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseDbFieldConstants {

    public static final String ENABLE_FLAG = "enable_flag";
    public static final String CREATE_BY = "create_by";
    public static final String CREATE_TIME = "create_time";
    public static final String DELETE_FLAG = "delete_flag";
    public static final String DELETE_TIME = "delete_time";
    public static final String LAST_UPDATE_BY = "last_update_by";
    public static final String LAST_UPDATE_TIME = "last_update_time";
}
