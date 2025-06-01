package club.slavopolis.common.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 基础查询对象, 用于复杂查询条件的封装
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseQuery extends PageRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * ID列表
     */
    private List<Long> ids;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门ID列表（包含子部门）
     */
    private List<Long> deptIds;

    /**
     * 是否导出
     */
    private boolean export = false;

    /**
     * 自定义SQL条件（谨慎使用）
     */
    private String customSql;

    /**
     * 检查是否有时间范围
     */
    public boolean hasTimeRange() {
        return startTime != null && endTime != null;
    }

    /**
     * 检查是否有关键词
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
}
