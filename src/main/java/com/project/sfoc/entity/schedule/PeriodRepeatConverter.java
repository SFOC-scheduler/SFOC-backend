package com.project.sfoc.entity.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;

import java.time.LocalDate;

public class PeriodRepeatConverter implements AttributeConverter<PeriodRepeat, String> {
    private final ObjectMapper objectMapper;

    public PeriodRepeatConverter() {
        objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        javaTimeModule.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        objectMapper.registerModule(javaTimeModule);
    }

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(PeriodRepeat attribute) {
        return objectMapper.writeValueAsString(attribute);
    }

    @Override
    @SneakyThrows
    public PeriodRepeat convertToEntityAttribute(String dbData) {
        return objectMapper.readValue(dbData, PeriodRepeat.class);
    }
}
