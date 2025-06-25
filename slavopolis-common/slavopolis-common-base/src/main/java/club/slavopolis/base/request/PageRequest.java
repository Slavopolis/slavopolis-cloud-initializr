package club.slavopolis.base.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 基础分野请求定义
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/25
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Setter
@Getter
public class PageRequest extends BaseRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private int currentPage;

    /**
     * 每页数量
     */
    private int pageSize;
}
