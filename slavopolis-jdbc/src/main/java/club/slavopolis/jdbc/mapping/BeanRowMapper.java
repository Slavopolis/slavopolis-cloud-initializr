package club.slavopolis.jdbc.mapping;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.jdbc.enums.MappingStrategy;
import club.slavopolis.jdbc.exception.MappingException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean行映射器实现
 * <p>基于Spring BeanPropertyRowMapper，增强映射功能</p>
 * <p>支持多种映射策略、缓存机制、自定义转换器等</p>
 * 
 * @param <T> 目标类型
 * @author Slavopolis Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Setter
@Getter
@Slf4j
public class BeanRowMapper<T> implements IntelligentRowMapper<T> {

    /**
     * 映射信息缓存
     */
    private static final Map<String, Map<String, String>> MAPPING_CACHE = new ConcurrentHashMap<>();

    /**
     * 目标类型
     */
    private final Class<T> mappedClass;

    /**
     * 映射策略
     */
    private final MappingStrategy mappingStrategy;

    /**
     * 是否检查完整映射
     */
    private boolean checkFullyPopulated = false;

    /**
     * 是否初始化字段映射
     */
    private boolean primitivesDefaultedForNullValue = false;

    /**
     * 字段映射缓存
     */
    private Map<String, PropertyDescriptor> mappedFields;

    /**
     * 已映射的属性集合
     */
    private Set<String> mappedProperties;

    /**
     * 构造函数
     * 
     * @param mappedClass 目标类型
     */
    public BeanRowMapper(Class<T> mappedClass) {
        this(mappedClass, MappingStrategy.INTELLIGENT);
    }

    /**
     * 构造函数
     * 
     * @param mappedClass 目标类型
     * @param mappingStrategy 映射策略
     */
    public BeanRowMapper(Class<T> mappedClass, MappingStrategy mappingStrategy) {
        Assert.notNull(mappedClass, "Mapped class must not be null");
        Assert.notNull(mappingStrategy, "Mapping strategy must not be null");
        this.mappedClass = mappedClass;
        this.mappingStrategy = mappingStrategy;
        initialize();
    }

    /**
     * 初始化映射信息
     */
    private void initialize() {
        this.mappedFields = new HashMap<>();
        this.mappedProperties = new HashSet<>();

        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(this.mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                String lowerCaseName = lowerCaseName(pd.getName());
                this.mappedFields.put(lowerCaseName, pd);
                String underscoreName = underscoreName(pd.getName());
                if (!lowerCaseName.equals(underscoreName)) {
                    this.mappedFields.put(underscoreName, pd);
                }
                this.mappedProperties.add(pd.getName());
            }
        }
    }

    @Override
    public MappingStrategy getMappingStrategy() {
        return mappingStrategy;
    }

    @Override
    public Class<T> targetType() {
        return mappedClass;
    }

    @Override
    @Nullable
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        initBeanWrapper(bw);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String field = lowerCaseName(StringUtils.delete(column, CommonConstants.SPACE));
            PropertyDescriptor pd = getPropertyDescriptor(field);
            
            if (pd != null) {
                try {
                    Object value = getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && log.isDebugEnabled()) {
                        log.debug("Mapping column '{}' to property '{}' of type [{}]",
                                column, pd.getName(), ClassUtils.getDescriptiveType(value));
                    }
                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    } catch (Exception ex) {
                        if (value == null && this.primitivesDefaultedForNullValue) {
                            if (log.isDebugEnabled()) {
                                log.debug("Intercepted Exception for row {} column '{}': {}",
                                        rowNumber, column, ex.getMessage());
                            }
                        } else {
                            throw new MappingException(
                                    String.format("Failed to map column '%s' to property '%s'", column, pd.getName()),
                                    ex);
                        }
                    }
                    if (populatedProperties != null) {
                        populatedProperties.add(pd.getName());
                    }
                } catch (Exception ex) {
                    throw new DataRetrievalFailureException(
                            "Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
                }
            } else {
                // No PropertyDescriptor found
                if (rowNumber == 0 && log.isDebugEnabled()) {
                    log.debug("No property found for column '{}' mapped to field '{}'", column, field);
                }
            }
        }

        if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
            throw new InvalidDataAccessApiUsageException(
                    "Given ResultSet does not contain all fields necessary to populate object of " +
                            this.mappedClass.getSimpleName() + CommonConstants.COLON_WITH_SPACE + this.mappedProperties);
        }

        return mappedObject;
    }

    /**
     * 获取属性描述符
     * 
     * @param field 字段名
     * @return 属性描述符
     */
    @Nullable
    private PropertyDescriptor getPropertyDescriptor(String field) {
        PropertyDescriptor pd = this.mappedFields.get(field);
        if (pd == null && mappingStrategy == MappingStrategy.INTELLIGENT) {
            // 尝试下划线转驼峰
            String camelField = underscoreToCamelCase(field);
            pd = this.mappedFields.get(lowerCaseName(camelField));
        }
        return pd;
    }

    /**
     * 获取列值
     * 
     * @param rs 结果集
     * @param index 列索引
     * @param pd 属性描述符
     * @return 列值
     * @throws SQLException SQL异常
     */
    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }

    /**
     * 初始化BeanWrapper
     * 
     * @param bw BeanWrapper
     */
    protected void initBeanWrapper(BeanWrapper bw) {
        // 默认实现为空，子类可以重写
    }

    /**
     * 转换为小写名称
     * 
     * @param name 名称
     * @return 小写名称
     */
    private static String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    /**
     * 转换为下划线名称
     * 
     * @param name 名称
     * @return 下划线名称
     */
    private static String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return CommonConstants.EMPTY;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 下划线转驼峰
     * 
     * @param name 下划线名称
     * @return 驼峰名称
     */
    private static String underscoreToCamelCase(String name) {
        if (!StringUtils.hasLength(name)) {
            return CommonConstants.EMPTY;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }

    /**
     * 创建新实例
     * 
     * @param <T> 目标类型
     * @param mappedClass 目标类型
     * @return 映射器实例
     */
    public static <T> BeanRowMapper<T> newInstance(Class<T> mappedClass) {
        return new BeanRowMapper<>(mappedClass);
    }

    /**
     * 创建新实例
     * 
     * @param <T> 目标类型
     * @param mappedClass 目标类型
     * @param mappingStrategy 映射策略
     * @return 映射器实例
     */
    public static <T> BeanRowMapper<T> newInstance(Class<T> mappedClass, MappingStrategy mappingStrategy) {
        return new BeanRowMapper<>(mappedClass, mappingStrategy);
    }
} 