package club.slavopolis.common.response;

import club.slavopolis.common.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 分页响应体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> extends Result<PageResult.PageData<T>>  {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分页数据包装类
     */
    @Data
    public static class PageData<T> {
        /**
         * 当前页码
         */
        private long current;

        /**
         * 每页大小
         */
        private long size;

        /**
         * 总记录数
         */
        private long total;

        /**
         * 总页数
         */
        private long pages;

        /**
         * 数据列表
         */
        private List<T> records;

        /**
         * 是否有上一页
         */
        private boolean hasPrevious;

        /**
         * 是否有下一页
         */
        private boolean hasNext;

        public PageData() {
        }

        public PageData(long current, long size, long total, List<T> records) {
            this.current = current;
            this.size = size;
            this.total = total;
            this.records = records;
            this.pages = (total + size - 1) / size;
            this.hasPrevious = current > 1;
            this.hasNext = current < this.pages;
        }
    }

    /**
     * 创建分页成功响应
     */
    public static <T> PageResult<T> success(long current, long size, long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(new PageData<>(current, size, total, records));
        return result;
    }

    /**
     * 创建空分页响应
     */
    public static <T> PageResult<T> empty(long current, long size) {
        return success(current, size, 0, List.of());
    }
}
