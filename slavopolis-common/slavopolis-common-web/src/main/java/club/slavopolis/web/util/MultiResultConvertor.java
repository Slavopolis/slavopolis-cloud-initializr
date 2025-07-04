package club.slavopolis.web.util;

import club.slavopolis.base.enums.ResponseCode;
import club.slavopolis.base.response.PageResponse;
import club.slavopolis.web.vo.MultiResult;

/**
 * 多结果转换器
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
public class MultiResultConvertor {

    /**
     * 将PageResponse转换为MultiResult
     *
     * @param pageResponse PageResponse对象
     * @param <T>          泛型参数
     * @return MultiResult对象
     */
    public static <T> MultiResult<T> convert(PageResponse<T> pageResponse) {
        return new MultiResult<>(
                true,
                ResponseCode.SUCCESS.name(),
                ResponseCode.SUCCESS.name(),
                pageResponse.getData(),
                pageResponse.getTotal(),
                pageResponse.getCurrentPage(),
                pageResponse.getPageSize()
        );
    }
}
