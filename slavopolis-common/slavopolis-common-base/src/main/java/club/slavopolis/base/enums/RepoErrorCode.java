package club.slavopolis.base.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Repository 操作异常枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/20
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum RepoErrorCode implements ErrorCode {

    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误"),
    INSERT_FAILED("INSERT_FAILED", "数据库插入失败"),
    UPDATE_FAILED("UPDATE_FAILED", "数据库更新失败"),
    ;

    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
