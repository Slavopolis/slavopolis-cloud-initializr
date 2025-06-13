package club.slavopolis.common.request;

import club.slavopolis.common.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 分页请求对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRequest extends BaseRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码最小值为1")
    private Integer current = CommonConstants.DEFAULT_PAGE_NUM;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小最小值为1")
    @Max(value = CommonConstants.MAX_PAGE_SIZE, message = "每页大小最大值为" + CommonConstants.MAX_PAGE_SIZE)
    private Integer size = CommonConstants.DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    private List<OrderItem> orders;

    /**
     * 是否进行总数统计
     */
    private boolean searchCount = true;

    /**
     * 排序项
     */
    @Data
    public static class OrderItem {
        /**
         * 排序字段
         */
        private String column;

        /**
         * 是否升序
         */
        private boolean asc = true;

        public OrderItem() {
        }

        public OrderItem(String column) {
            this.column = column;
        }

        public OrderItem(String column, boolean asc) {
            this.column = column;
            this.asc = asc;
        }

        /**
         * 创建升序排序项
         */
        public static OrderItem asc(String column) {
            return new OrderItem(column, true);
        }

        /**
         * 创建降序排序项
         */
        public static OrderItem desc(String column) {
            return new OrderItem(column, false);
        }
    }

    /**
     * 添加升序排序
     */
    public PageRequest addOrderAsc(String column) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(OrderItem.asc(column));
        return this;
    }

    /**
     * 添加降序排序
     */
    public PageRequest addOrderDesc(String column) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(OrderItem.desc(column));
        return this;
    }

    /**
     * 获取偏移量
     */
    @JsonIgnore
    public long getOffset() {
        return (long) (current - 1) * size;
    }
}
