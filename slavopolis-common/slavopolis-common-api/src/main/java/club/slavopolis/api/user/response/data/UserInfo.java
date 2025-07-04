package club.slavopolis.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 用户信息
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/18
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfo extends BasicUser {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否认证
     */
    private Boolean certification;


}
