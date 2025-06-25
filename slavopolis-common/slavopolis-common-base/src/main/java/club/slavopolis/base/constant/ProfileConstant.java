package club.slavopolis.base.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Profile 常量类
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/20
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileConstant {

    /**
     * 开发环境
     */
    public static final String PROFILE_DEV = "dev";

    /**
     * 测试环境
     */
    public static final String PROFILE_TEST = "test";

    /**
     * 生产环境
     */
    public static final String PROFILE_PROD = "prod";
}
