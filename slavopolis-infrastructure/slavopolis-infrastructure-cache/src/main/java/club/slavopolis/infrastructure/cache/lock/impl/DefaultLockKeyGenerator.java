package club.slavopolis.infrastructure.cache.lock.impl;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.cache.lock.core.LockKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * slavopolis-boot
 *
 * @author: slavopolis
 * @date: 2025/6/3
 * @description: 默认的锁键生成器实现
 *
 * <p>
 * 支持 SpEL 表达式解析，可以根据方法参数动态生成锁键。
 * 支持的 SpEL 表达式示例：
 * - 固定值："user:lock"
 * - 参数值："#userId"
 * - 对象属性："#user.id"
 * - 方法调用："#user.getName()"
 * - 复杂表达式："#user.id + ':' + #product.id"
 * </p>
 */
@Slf4j
@Component
public class DefaultLockKeyGenerator implements LockKeyGenerator {


    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 锁键分隔符
     */
    private static final String KEY_SEPARATOR = CommonConstants.CACHE_KEY_SEPARATOR;

    /**
     * 默认锁键前缀
     */
    private static final String DEFAULT_PREFIX = "lock";

    /**
     * 生成锁键
     *
     * @param prefix 前缀
     * @param key SpEL表达式
     * @param joinPoint 切点信息
     * @return 最终的锁键
     */
    @Override
    public String generate(String prefix, String key, ProceedingJoinPoint joinPoint) {
        // 构建基础键
        StringBuilder lockKey = new StringBuilder();

        // 添加前缀
        if (StringUtils.hasText(prefix)) {
            lockKey.append(prefix).append(KEY_SEPARATOR);
        } else {
            lockKey.append(DEFAULT_PREFIX).append(KEY_SEPARATOR);
        }

        // 解析 SpEL 表达式
        String parsedKey = parseSpelExpression(key, joinPoint);
        lockKey.append(parsedKey);

        // 返回最终的锁键
        String finalKey = lockKey.toString();
        log.debug("从表达式: {} 生成的锁键: {}", key, finalKey);

        return finalKey;
    }

    /**
     * 解析 SpEL 表达式
     *
     * @param expressionString SpEL 表达式字符串
     * @param joinPoint 切点信息
     * @return 解析后的值
     */
    private String parseSpelExpression(String expressionString, ProceedingJoinPoint joinPoint) {
        // 如果不包含 # 符号，认为是普通字符串
        if (!expressionString.contains("#")) {
            return expressionString;
        }

        try {
            // 创建 SpEL 上下文
            EvaluationContext context = createEvaluationContext(joinPoint);

            // 解析表达式
            Expression expression = parser.parseExpression(expressionString);
            Object value = expression.getValue(context);

            // 处理 null 值
            if (value == null) {
                log.warn("SpEL 表达式被评估为空: {}", expressionString);
                return "null";
            }

            return value.toString();

        } catch (Exception e) {
            log.error("解析 SpEL 表达式时出错: {}", expressionString, e);
            // 解析失败时使用原始表达式
            return expressionString;
        }
    }

    /**
     * 创建 SpEL 评估上下文
     *
     * @param joinPoint 切点信息
     * @return SpEL 上下文
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        // 设置变量
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];

            // 优先使用参数名，如果获取不到则使用 p0, p1 等
            String paramName = parameter.getName();
            context.setVariable(paramName, arg);

            // 同时设置 p0, p1 等变量，方便使用
            context.setVariable("p" + i, arg);

            // 如果参数是第一个且只有一个参数，额外设置为 value
            if (parameters.length == 1) {
                context.setVariable("value", arg);
            }
        }

        // 设置目标对象
        context.setVariable("target", joinPoint.getTarget());

        // 设置方法
        context.setVariable("method", method);

        return context;
    }
}
