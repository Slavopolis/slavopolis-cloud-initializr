package club.slavopolis.common.response;

import club.slavopolis.common.enums.ResultCode;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 响应构建器, 提供链式调用的响应构建方式
 */
public class ResultBuilder {

    private ResultBuilder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 创建成功响应构建器
     */
    public static <T> Builder<T> success() {
        return new Builder<T>().code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage());
    }

    /**
     * 创建失败响应构建器
     */
    public static <T> Builder<T> failed() {
        return new Builder<T>().code(ResultCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ResultCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    /**
     * 创建自定义响应构建器
     */
    public static <T> Builder<T> custom() {
        return new Builder<>();
    }

    /**
     * 响应构建器
     */
    public static class Builder<T> {
        private int code;
        private String message;
        private T data;
        private String traceId;

        public Builder<T> code(int code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder<T> resultCode(ResultCode resultCode) {
            this.code = resultCode.getCode();
            this.message = resultCode.getMessage();
            return this;
        }

        public Result<T> build() {
            Result<T> result = new Result<>(code, message, data);
            if (traceId != null) {
                result.setTraceId(traceId);
            }
            return result;
        }
    }
}
