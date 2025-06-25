package club.slavopolis.base.response;

import club.slavopolis.base.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 单值响应基础定义
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/25
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
@Setter
public class SingleResponse<T> extends BaseResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据
     */
    private T data;

    /**
     * 创建成功响应
     *
     * @param data 数据
     * @return 响应
     */
    public static <T> SingleResponse<T> success(T data) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(Boolean.TRUE);
        response.setData(data);
        return response;
    }

    /**
     * 创建失败响应
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     * @return 响应
     */
    public static <T> SingleResponse<T> failed(String errorCode, String errorMessage) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setCode(errorCode);
        response.setMessage(errorMessage);
        return response;
    }
}
