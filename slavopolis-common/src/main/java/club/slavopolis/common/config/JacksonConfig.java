package club.slavopolis.common.config;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.constant.DateConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: Jackson配置类， 配置JSON序列化和反序列化规则
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfig {

    /**
     * Jackson构建器自定义器
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            log.info("Customizing Jackson ObjectMapper");

            // 设置时区
            builder.timeZone(TimeZone.getTimeZone(CommonConstants.DEFAULT_TIMEZONE));

            // 设置日期格式
            builder.simpleDateFormat(DateConstants.DATETIME_PATTERN);

            // Java 8 时间模块
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            // LocalDateTime
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateConstants.DATETIME_PATTERN)));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateConstants.DATETIME_PATTERN)));

            // LocalDate
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DateConstants.DATE_PATTERN)));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateConstants.DATE_PATTERN)));

            // LocalTime
            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DateConstants.TIME_PATTERN)));
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DateConstants.TIME_PATTERN)));

            builder.modules(javaTimeModule);

            // 序列化特性
            builder.featuresToDisable(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    SerializationFeature.FAIL_ON_EMPTY_BEANS
            );

            // 反序列化特性
            builder.featuresToDisable(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    DeserializationFeature.FAIL_ON_INVALID_SUBTYPE
            );

            // 设置null值不序列化
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        };
    }

    /**
     * 创建ObjectMapper
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        log.info("Creating primary ObjectMapper");
        return builder.build();
    }
}
