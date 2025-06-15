package club.slavopolis.infrastructure.persistence.jdbc.parameter;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * 类型安全的参数源接口
 * <p>扩展Spring的SqlParameterSource，提供类型安全的参数绑定功能</p>
 * <p>支持Bean对象的自动解析、Map参数的直接绑定以及混合参数模式的处理</p>
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface TypeSafeParameterSource extends SqlParameterSource {

    /**
     * 获取参数值并进行类型转换
     * 
     * @param <T> 目标类型
     * @param paramName 参数名
     * @param requiredType 目标类型
     * @return 转换后的参数值
     */
    <T> T getValue(String paramName, Class<T> requiredType);

    /**
     * 获取参数值并指定默认值
     * 
     * @param <T> 目标类型
     * @param paramName 参数名
     * @param requiredType 目标类型
     * @param defaultValue 默认值
     * @return 参数值或默认值
     */
    <T> T getValue(String paramName, Class<T> requiredType, T defaultValue);

    /**
     * 检查参数是否存在
     * 
     * @param paramName 参数名
     * @return 如果参数存在返回true
     */
    boolean hasParameter(String paramName);

    /**
     * 获取所有参数名
     * 
     * @return 参数名数组
     */
    @Override
    String[] getParameterNames();

    /**
     * 验证必需参数
     * 
     * @param requiredParams 必需参数名数组
     * @throws IllegalArgumentException 如果缺少必需参数
     */
    void validateRequiredParameters(String... requiredParams);

    /**
     * 添加验证器
     * 
     * @param validator 参数验证器
     * @return 当前参数源实例（支持链式调用）
     */
    TypeSafeParameterSource addValidator(ParameterValidator validator);

    /**
     * 执行参数验证
     * 
     * @throws IllegalArgumentException 如果验证失败
     */
    void validate();
} 