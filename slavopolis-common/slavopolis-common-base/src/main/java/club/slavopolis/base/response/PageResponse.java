package club.slavopolis.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * 分页响应基础定义
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
public class PageResponse<T> extends MultiResponse<T> {

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

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 总数据数量
     */
    private int total;

    /**
     * 创建分页响应对象
     *
     * @param dataList   数据列表
     * @param total      数据总数
     * @param pageSize   每页数量
     * @param currentPage 当前页码
     * @return 分页响应对象
     */
    public static <T> PageResponse<T> success(List<T> dataList, int total, int pageSize, int currentPage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(Boolean.TRUE);
        pageResponse.setData(dataList);
        pageResponse.setTotal(total);
        pageResponse.setPageSize(pageSize);
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setTotalPages((pageSize + total - 1) / pageSize);
        return pageResponse;
    }
}
