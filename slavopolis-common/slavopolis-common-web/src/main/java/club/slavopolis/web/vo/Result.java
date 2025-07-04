package club.slavopolis.web.vo;

import club.slavopolis.base.enums.ResponseCode;
import club.slavopolis.base.response.SingleResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 结果 VO
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
@Setter
public class Result<T> {

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应码枚举
     */
    private Boolean success;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public Result() {
    }

    public Result(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(Boolean success, String code, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public Result(SingleResponse<T> singleResponse) {
        this.success = singleResponse.getSuccess();
        this.data = singleResponse.getData();
        this.code = singleResponse.getCode();
        this.message = singleResponse.getMessage();
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, ResponseCode.SUCCESS.name(), ResponseCode.SUCCESS.name(), data);
    }

    public static <T> Result<T> error(String errorCode, String errorMsg) {
        return new Result<>(false, errorCode, errorMsg, null);
    }
}
