package club.slavopolis.common.log.aspect;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.common.log.annotation.LogPerformance;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 性能监控切面
 *
 * <p>
 * 该切面用于监控方法执行性能，自动记录方法的执行时间、参数和返回值。
 * 通过 @LogPerformance 注解标记需要监控的方法，支持自定义慢速阈值和日志级别。
 * 性能日志会输出到专门的日志文件中，便于性能分析和优化。
 * </p>
 */
@Slf4j
@Aspect
@Component
public class PerformanceLogAspect {

    private static final Logger performanceLogger = LoggerFactory.getLogger("club.slavopolis.common.log.annotation.LogPerformance");

    /**
     * 环绕通知，监控方法执行性能
     *
     * @param joinPoint 切点对象
     * @param logPerformance 性能监控注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(logPerformance)")
    public Object logPerformance(ProceedingJoinPoint joinPoint, LogPerformance logPerformance) throws Throwable {
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = signature.getName();

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 记录方法参数（如果启用）
        if (logPerformance.logArgs()) {
            Object[] args = joinPoint.getArgs();
            String argsStr = Arrays.toString(args);
            performanceLogger.debug("开始执行方法 - {}.{}, 方法参数: {}", className, methodName, argsStr);
        }

        // 记录返回值
        Object result = null;
        // 记录异常信息
        Throwable error = null;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            // 计算执行时间
            long duration = System.currentTimeMillis() - startTime;

            // 构建性能日志信息
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("方法: ").append(className).append(CommonConstants.DOT).append(methodName);
            logMessage.append(", 执行时长: ").append(duration).append("ms");

            if (logPerformance.logResult() && Objects.isNull(error)) {
                logMessage.append(", 执行结果: ").append(result);
            }

            if (Objects.nonNull(error)) {
                logMessage.append(", 错误信息: ").append(error.getClass().getSimpleName()).append(" - ").append(error.getMessage());
            }

            // 根据执行时间判断日志级别
            if (duration > logPerformance.slowThreshold()) {
                performanceLogger.warn("方法性能过慢 - {}", logMessage);
            } else if (Objects.nonNull(error)) {
                performanceLogger.error("方法执行错误 - {}", logMessage);
            } else {
                performanceLogger.info("{}", logMessage);
            }

            // 如果执行时间超过阈值，记录详细的堆栈信息
            if (duration > logPerformance.slowThreshold() && logPerformance.logStackTrace()) {
                performanceLogger.warn("慢速方法堆栈跟踪:", new SlowMethodException(duration));
            }
        }
    }

    /**
     * 慢方法异常，用于记录堆栈信息
     */
    private static class SlowMethodException extends Exception {
        public SlowMethodException(long duration) {
            super("慢速方法执行 " + duration + "ms");
        }
    }
}
