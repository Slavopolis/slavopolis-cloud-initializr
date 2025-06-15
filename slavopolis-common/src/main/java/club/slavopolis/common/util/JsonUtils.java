package club.slavopolis.common.util;

import club.slavopolis.common.core.exception.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/10
 * @description: JSON工具类, 基于Jackson的JSON序列化和反序列化工具
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 注册Java时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 禁用日期作为时间戳
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略未知属性
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 空值不序列化
        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
            throw new SystemException("JSON序列化失败", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串
     */
    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to pretty JSON", e);
            throw new SystemException("JSON序列化失败", e);
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Failed to convert JSON to object", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * JSON字符串转对象（TypeReference）
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("Failed to convert JSON to object with TypeReference", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * JSON字符串转List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            log.error("Failed to convert JSON to List", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * JSON字符串转Map
     */
    public static Map<String, Object> toMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to convert JSON to Map", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> objectToMap(Object object) {
        if (object == null) {
            return Collections.emptyMap();
        }
        return OBJECT_MAPPER.convertValue(object, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Map转对象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(map, clazz);
    }
}
