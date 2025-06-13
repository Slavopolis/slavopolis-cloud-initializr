package club.slavopolis.common.model.http;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/13
 * @description: HTTP请求回调接口
 */
public interface HttpCallback<T> {

    /**
     * 请求成功回调
     *
     * @param response 响应结果
     */
    void onSuccess(HttpResponse<T> response);

    /**
     * 请求失败回调
     *
     * @param error 异常信息
     */
    void onError(Throwable error);

    /**
     * 请求完成回调（无论成功或失败都会调用）
     */
    default void onComplete() {
        // 默认空实现
    }

    /**
     * 创建简单的成功回调
     *
     * @param successHandler 成功处理器
     * @param <T> 响应类型
     * @return HttpCallback实例
     */
    static <T> HttpCallback<T> of(java.util.function.Consumer<HttpResponse<T>> successHandler) {
        return new HttpCallback<T>() {
            @Override
            public void onSuccess(HttpResponse<T> response) {
                successHandler.accept(response);
            }

            @Override
            public void onError(Throwable error) {
                // 默认打印错误
                error.printStackTrace();
            }
        };
    }

    /**
     * 创建完整的回调
     *
     * @param successHandler 成功处理器
     * @param errorHandler 错误处理器
     * @param <T> 响应类型
     * @return HttpCallback实例
     */
    static <T> HttpCallback<T> of(
            java.util.function.Consumer<HttpResponse<T>> successHandler,
            java.util.function.Consumer<Throwable> errorHandler) {
        return new HttpCallback<T>() {
            @Override
            public void onSuccess(HttpResponse<T> response) {
                successHandler.accept(response);
            }

            @Override
            public void onError(Throwable error) {
                errorHandler.accept(error);
            }
        };
    }
} 