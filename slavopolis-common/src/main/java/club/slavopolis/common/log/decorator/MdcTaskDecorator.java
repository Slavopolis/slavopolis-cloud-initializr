package club.slavopolis.common.log.decorator;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: MDC 任务装饰器
 *
 * <p>
 * 用于在异步任务执行时传递 MDC 上下文
 * </p>
 */
@Component
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        // 获取当前线程的 MDC 上下文
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            // 在新线程中设置 MDC 上下文
            if (Objects.nonNull(contextMap)) {
                MDC.setContextMap(contextMap);
            }

            try {
                // 执行任务
                runnable.run();
            } finally {
                // 清除 MDC 上下问
                MDC.clear();
            }
        };
    }
}
