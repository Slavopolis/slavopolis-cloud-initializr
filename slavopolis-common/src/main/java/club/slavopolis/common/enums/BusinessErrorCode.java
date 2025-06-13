package club.slavopolis.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: 业务错误码枚举, 用于更细分的业务错误定义
 */
@Getter
@AllArgsConstructor
public enum BusinessErrorCode {

    // ==================== 用户模块 20000-20999 ====================
    USER_NOT_FOUND(20001, "用户不存在"),
    USER_ALREADY_EXISTS(20002, "用户已存在"),
    USER_STATUS_ABNORMAL(20003, "用户状态异常"),
    USER_PASSWORD_WEAK(20004, "密码强度不足"),
    USER_EMAIL_EXISTS(20005, "邮箱已被使用"),
    USER_PHONE_EXISTS(20006, "手机号已被使用"),

    // ==================== 订单模块 21000-21999 ====================
    ORDER_NOT_FOUND(21001, "订单不存在"),
    ORDER_STATUS_ERROR(21002, "订单状态错误"),
    ORDER_AMOUNT_ERROR(21003, "订单金额错误"),
    ORDER_EXPIRED(21004, "订单已过期"),
    ORDER_CANCELLED(21005, "订单已取消"),
    ORDER_PAID(21006, "订单已支付"),

    // ==================== 商品模块 22000-22999 ====================
    PRODUCT_NOT_FOUND(22001, "商品不存在"),
    PRODUCT_OFF_SHELF(22002, "商品已下架"),
    PRODUCT_STOCK_INSUFFICIENT(22003, "商品库存不足"),
    PRODUCT_PRICE_CHANGED(22004, "商品价格已变更"),

    // ==================== 支付模块 23000-23999 ====================
    PAYMENT_FAILED(23001, "支付失败"),
    PAYMENT_TIMEOUT(23002, "支付超时"),
    PAYMENT_CANCELLED(23003, "支付已取消"),
    PAYMENT_AMOUNT_ERROR(23004, "支付金额错误"),
    PAYMENT_METHOD_NOT_SUPPORTED(23005, "支付方式不支持"),

    // ==================== 文件模块 24000-24999 ====================
    FILE_NOT_FOUND(24001, "文件不存在"),
    FILE_UPLOAD_FAILED(24002, "文件上传失败"),
    FILE_SIZE_EXCEEDED(24003, "文件大小超限"),
    FILE_TYPE_NOT_ALLOWED(24004, "文件类型不允许"),
    FILE_DAMAGED(24005, "文件已损坏");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;
}
