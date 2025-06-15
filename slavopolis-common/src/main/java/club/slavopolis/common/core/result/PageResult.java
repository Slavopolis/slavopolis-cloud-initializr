package club.slavopolis.common.core.result;

import java.io.Serial;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class PageResult<T> extends Result<PageResult.PageData<T>> {

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

        /**
         * 是否为第一页
         */
        private boolean isFirst;

        /**
         * 是否为最后一页
         */
        private boolean isLast;

        public PageData() {
        }

        public PageData(long current, long size, long total, List<T> records) {
            this.current = current;
            this.size = size;
            this.total = total;
            this.records = records;
            this.pages = total > 0 ? (total + size - 1) / size : 0;
            this.hasPrevious = current > 1;
            this.hasNext = current < this.pages;
            this.isFirst = current == 1;
            this.isLast = current == this.pages || this.pages == 0;
        }

        /**
         * 创建分页数据 - 兼容 int 类型参数
         * 
         * @param pageNum 页码
         * @param pageSize 每页大小  
         * @param total 总记录数
         * @param records 数据列表
         * @return 分页数据
         */
        public static <T> PageData<T> of(int pageNum, int pageSize, long total, List<T> records) {
            return new PageData<>(pageNum, pageSize, total, records);
        }

        /**
         * 创建分页数据
         * 
         * @param current 页码
         * @param size 每页大小  
         * @param total 总记录数
         * @param records 数据列表
         * @return 分页数据
         */
        public static <T> PageData<T> of(long current, long size, long total, List<T> records) {
            return new PageData<>(current, size, total, records);
        }

        /**
         * 创建空分页数据
         * 
         * @param pageNum 页码
         * @param pageSize 每页大小
         * @return 空分页数据
         */
        public static <T> PageData<T> empty(int pageNum, int pageSize) {
            return new PageData<>(pageNum, pageSize, 0L, List.of());
        }

        /**
         * 创建空分页数据
         * 
         * @param current 页码
         * @param size 每页大小
         * @return 空分页数据
         */
        public static <T> PageData<T> empty(long current, long size) {
            return new PageData<>(current, size, 0L, List.of());
        }

        /**
         * 获取页码（int类型，兼容方法）
         * 
         * @return 页码
         */
        public int getPageNum() {
            return Math.toIntExact(this.current);
        }

        /**
         * 获取每页大小（int类型，兼容方法）
         * 
         * @return 每页大小
         */
        public int getPageSize() {
            return Math.toIntExact(this.size);
        }

        /**
         * 获取总页数（int类型，兼容方法）
         * 
         * @return 总页数
         */
        public int getPagesAsInt() {
            return Math.toIntExact(this.pages);
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
