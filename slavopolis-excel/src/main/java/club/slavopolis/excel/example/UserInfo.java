package club.slavopolis.excel.example;

import club.slavopolis.excel.annotation.ExcelField;
import club.slavopolis.excel.annotation.ExcelSheet;
import club.slavopolis.excel.core.converter.LocalDateTimeConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 用户信息Excel示例模型
 */
@Data
@ExcelSheet(value = "用户信息", description = "用户基本信息表")
public class UserInfo {

    @ExcelField(value = "用户ID", index = 0, required = true, message = "用户ID不能为空")
    private Long id;

    @ExcelField(value = "用户名", index = 1, required = true, maxLength = 50, message = "用户名不能为空且长度不超过50")
    private String username;

    @ExcelField(value = "真实姓名", index = 2, required = true, maxLength = 20)
    private String realName;

    @ExcelField(value = "手机号", index = 3, pattern = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ExcelField(value = "邮箱", index = 4, pattern = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    @ExcelField(value = "年龄", index = 5, numberFormat = "0")
    private Integer age;

    @ExcelField(value = "性别", index = 6)
    private String gender;

    @ExcelField(value = "部门", index = 7, defaultValue = "未分配")
    private String department;

    @ExcelField(value = "职位", index = 8)
    private String position;

    @ExcelField(value = "入职时间", index = 9, converter = LocalDateTimeConverter.class, dateFormat = "yyyy-MM-dd")
    private LocalDateTime joinTime;

    @ExcelField(value = "状态", index = 10, defaultValue = "正常")
    private String status;

    @ExcelField(ignore = true)  // 忽略该字段，不参与Excel处理
    private String password;

    @ExcelField(value = "备注", index = 11, maxLength = 200)
    private String remark;
} 