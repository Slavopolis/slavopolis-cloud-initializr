package club.slavopolis.base.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/25
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ResponseCode {

    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    NOT_FOUND("NOT_FOUND", "未找到"),
    NOT_ALLOWED("NOT_ALLOWED", "不允许"),
    NOT_AUTHORIZED("NOT_AUTHORIZED", "未授权"),
    NOT_SUPPORTED("NOT_SUPPORTED", "不支持"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "服务器内部错误"),
    BAD_REQUEST("BAD_REQUEST", "请求错误"),
    UNAUTHORIZED("UNAUTHORIZED", "未授权"),
    FORBIDDEN("FORBIDDEN", "禁止访问"),
    DUPLICATE_ENTRY("DUPLICATE_ENTRY", "重复数据"),
    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "非法参数"),
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误"),
    BIZ_ERROR("BIZ_ERROR", "业务错误");

    private final String code;
    private final String message;
}
