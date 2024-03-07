package com.project.sfoc.entity.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;

public class PeriodRepeatConverter implements AttributeConverter<PeriodRepeat, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
