package club.slavopolis.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础用户信息，仅提供基础字段
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
public class BasicUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nikeName;

    /**
     * 用户头像地址
     */
    private String profilePictureUrl;
}
