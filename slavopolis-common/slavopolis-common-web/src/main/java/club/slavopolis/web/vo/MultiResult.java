package club.slavopolis.web.vo;

import club.slavopolis.base.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 多值结果 VO
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
public class MultiResult<T> extends Result<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    public MultiResult() {
        super();
    }

    public MultiResult(Boolean success, String code, String message, List<T> data, long total, int pageNum, int pageSize) {
        super(success, code, message, data);
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> MultiResult<T> successMulti(List<T> data, long total, int pageNum, int pageSize) {
        return new MultiResult<>(true, ResponseCode.SUCCESS.name(), ResponseCode.SUCCESS.name(), data, total, pageNum, pageSize);
    }

    public static <T> MultiResult<T> errorMulti(String errorCode, String errorMsg) {
        return new MultiResult<>(true, errorCode, errorMsg, null, 0, 0, 0);
    }
}
