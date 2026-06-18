package com.teaching.competition.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration("commonJacksonConfig")
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
        DateTimeFormatter responseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return builder -> {
            builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
            builder.serializers(new LocalDateTimeSerializer(responseFormatter));
            builder.deserializerByType(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
        };
    }

    private static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        private static final List<DateTimeFormatter> FORMATTERS = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        );

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String value = p.getText();
            if (value == null || value.isBlank()) {
                return null;
            }
            String text = value.trim();

            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(text, formatter);
                } catch (DateTimeParseException ignored) {
                    // Try next formatter
                }
            }

            try {
                return OffsetDateTime.parse(text).toLocalDateTime();
            } catch (DateTimeParseException ignored) {
                // Fall through
            }

            throw new DateTimeParseException("Unsupported LocalDateTime format", text, 0);
        }
    }
}
