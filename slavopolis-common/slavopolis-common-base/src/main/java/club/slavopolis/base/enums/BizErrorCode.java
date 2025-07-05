package club.slavopolis.base.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 业务错误码枚举
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/20
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum BizErrorCode implements ErrorCode {

    // ==================== 通用错误码 ====================
    DUPLICATED("DUPLICATED", "重复请求"),
    HTTP_CLIENT_ERROR("HTTP_CLIENT_ERROR", "HTTP 客户端错误"),
    HTTP_SERVER_ERROR("HTTP_SERVER_ERROR", "HTTP 服务端错误"),

    // ==================== 通知相关 ====================
    SEND_NOTICE_DUPLICATED("SEND_NOTICE_DUPLICATED", "不允许重复发送通知"),
    NOTICE_SAVE_FAILED("NOTICE_SAVE_FAILED", "通知保存失败"),

    // ==================== 状态机相关 ====================
    STATE_MACHINE_TRANSITION_FAILED("STATE_MACHINE_TRANSITION_FAILED", "状态机转换失败"),

    // ==================== 远程调用相关 ====================
    REMOTE_CALL_RESPONSE_IS_NULL("REMOTE_CALL_RESPONSE_IS_NULL", "远程调用返回结果为空"),
    REMOTE_CALL_RESPONSE_IS_FAILED("REMOTE_CALL_RESPONSE_IS_FAILED", "远程调用返回结果失败"),

    // ==================== 文件相关 ====================
    FILE_NOT_FOUND("FILE_NOT_FOUND", "文件不存在"),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "文件上传失败"),
    FILE_SIZE_EXCEEDED("FILE_SIZE_EXCEEDED", "文件大小超限"),
    FILE_TYPE_NOT_ALLOWED("FILE_TYPE_NOT_ALLOWED", "文件类型不允许"),
    FILE_DAMAGED("FILE_DAMAGED", "文件已损坏"),
    FILE_DELETE_FAILED("FILE_DELETE_FAILED", "文件删除失败"),
    FILE_INFO_ERROR("FILE_INFO_ERROR", "获取文件信息失败"),
    FILE_LIST_ERROR("FILE_LIST_ERROR", "获取文件列表失败"),
    FILE_MULTIPART_COMPLETE_FAILED("FILE_MULTIPART_COMPLETE_FAILED", "完成分片上传失败"),
    FILE_MULTIPART_ABORT_FAILED("FILE_MULTIPART_ABORT_FAILED", "取消分片上传失败");

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
