package club.slavopolis.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 多响应基础定义
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
public class MultiResponse<T> extends BaseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据
     */
    private transient List<T> data;

    /**
     * 创建一个多响应对象
     *
     * @param dataList 数据列表
     * @param <T>      数据类型
     * @return 多响应对象
     */
    public static <T> MultiResponse<T> success(List<T> dataList) {
        MultiResponse<T> response = new MultiResponse<>();
        response.setSuccess(Boolean.TRUE);
        response.setData(dataList);
        return response;
    }
}
