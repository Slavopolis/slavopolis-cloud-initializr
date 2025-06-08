package club.slavopolis.common.log.aspect;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.log.annotation.BusinessLog;
import club.slavopolis.common.log.model.BusinessLogInfo;
import club.slavopolis.common.util.HttpUtils;
import club.slavopolis.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/7
 * @description: 业务日志切面
 *
 * <p>
 * 拦截标记了 @BusinessLog 注解的方法，自动记录业务操作日志。
 * 支持 SpEL 表达式解析，可以动态生成日志内容。
 * </p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BusinessLogAspect {

    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知，记录业务日志
     *
     * @param joinPoint 切点对象
     * @param businessLog 业务日志注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(businessLog)")
    public Object logBusiness(ProceedingJoinPoint joinPoint, BusinessLog businessLog) throws Throwable {
        // 构建业务日志信息
        BusinessLogInfo logInfo = new BusinessLogInfo();
        logInfo.setTraceId(MDC.get(CommonConstants.TRACE_ID));
        logInfo.setUserId(MDC.get(CommonConstants.USER_ID));
        logInfo.setOperateTime(LocalDateTime.now());
        logInfo.setModule(businessLog.module());
        logInfo.setType(businessLog.type().name());
        logInfo.setTypeDesc(businessLog.type().getDescription());

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        logInfo.setMethod(className + CommonConstants.DOT + methodName);

        // 记录请求参数
        if (businessLog.logArgs()) {
            Object[] args = joinPoint.getArgs();
            logInfo.setArgs(Arrays.toString(args));
        }

        // 解析操作描述（支持 SpEL）
        String description = parseDescription(businessLog.description(), joinPoint);
        logInfo.setDescription(description);

        // 记录执行结果
        Object result = null;
        // 记录执行异常
        Exception error = null;
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 执行方法
            result = joinPoint.proceed();

            // 记录返回值（如果启用）
            if (businessLog.logResult() && Objects.nonNull(result)) {
                logInfo.setResult(JsonUtils.toJson(result));
            }

            // 记录成功状态
            logInfo.setSuccess(Boolean.TRUE);
            return result;

        } catch (Exception e) {
            error = e;
            logInfo.setSuccess(Boolean.FALSE);

            // 记录异常信息（如果启用）
            if (businessLog.logException()) {
                logInfo.setErrorMsg(e.getMessage());
                logInfo.setErrorDetail(HttpUtils.getStackTrace(e));
            }

            // 抛出异常，以便上层处理
            throw e;
        } finally {
            // 记录执行时间
            long duration = System.currentTimeMillis() - startTime;
            logInfo.setDuration(duration);

            // 输出业务日志
            logBusinessOperation(logInfo, error);
        }
    }

    /**
     * 解析操作描述，支持 SpEL 表达式
     *
     * @param descriptionExpression SpEL 表达式
     * @param joinPoint 切点对象
     * @return 解析后的操作描述
     */
    private String parseDescription(String descriptionExpression, ProceedingJoinPoint joinPoint) {
        if (descriptionExpression == null || descriptionExpression.isEmpty()) {
            return "";
        }

        // 如果不包含 SpEL 表达式，直接返回
        if (!descriptionExpression.contains("#")) {
            return descriptionExpression;
        }

        try {
            // 创建 SpEL 上下文
            EvaluationContext context = createEvaluationContext(joinPoint);

            // 解析表达式
            Expression expression = parser.parseExpression(descriptionExpression, new org.springframework.expression.common.TemplateParserContext());
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            log.error("解析描述表达式失败: {}", descriptionExpression, e);
            return descriptionExpression;
        }
    }

    /**
     * 创建 SpEL 评估上下文
     *
     * @param joinPoint 切点对象
     * @return SpEL 评估上下文
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 获取方法参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // 设置变量
        if (parameterNames != null && args != null) {
            for (int i = 0; i < parameterNames.length && i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        return context;
    }

    /**
     * 记录业务操作日志
     *
     * @param logInfo 业务日志信息
     * @param error 异常信息（如果有）
     */
    private void logBusinessOperation(BusinessLogInfo logInfo, Exception error) {
        String logMessage = String.format("业务操作 - 模块: %s, 操作类型: %s, 操作描述: %s, 执行时常: %dms, 是否成功: %s",
                logInfo.getModule(),
                logInfo.getTypeDesc(),
                logInfo.getDescription(),
                logInfo.getDuration(),
                logInfo.getSuccess());

        if (Objects.nonNull(error)) {
            log.error(logMessage, error);
        } else if (logInfo.getDuration() > 3000) {
            log.warn("业务运行缓慢 - {}", logMessage);
        } else {
            log.info(logMessage);
        }

        // 同时输出结构化日志
        log.info("业务日志详细信息: {}", JsonUtils.toJson(logInfo));
    }
}
