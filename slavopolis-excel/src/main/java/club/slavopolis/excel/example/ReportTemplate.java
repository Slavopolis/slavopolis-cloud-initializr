package club.slavopolis.excel.example;

import club.slavopolis.excel.annotation.ExcelField;
import club.slavopolis.excel.annotation.ExcelTemplate;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 报表模板Excel示例模型
 */
@Data
@ExcelTemplate(
    value = "classpath:excel/templates/monthly_report.xlsx",
    name = "monthly_report",
    description = "月度报表模板",
    horizontal = false,
    startRow = 2,
    startColumn = 1,
    autoRowHeight = true,
    autoColumnWidth = true,
    cacheSeconds = 1800
)
public class ReportTemplate {

    @ExcelField("报表标题")
    private String title;

    @ExcelField("报表日期")
    private LocalDate reportDate;

    @ExcelField("部门名称")
    private String departmentName;

    @ExcelField("负责人")
    private String manager;

    @ExcelField("总收入")
    private BigDecimal totalIncome;

    @ExcelField("总支出")
    private BigDecimal totalExpense;

    @ExcelField("净利润")
    private BigDecimal netProfit;

    @ExcelField("增长率")
    private String growthRate;

    @ExcelField("备注说明")
    private String remarks;

    // 计算字段
    public BigDecimal calculateNetProfit() {
        if (totalIncome != null && totalExpense != null) {
            return totalIncome.subtract(totalExpense);
        }
        return BigDecimal.ZERO;
    }
} 