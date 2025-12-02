package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.question;

import com.hoidap.hoidapdemo.domain.model.QuestionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class QuestionStatusConverter implements AttributeConverter<QuestionStatus, Integer>{
    @Override
    public Integer convertToDatabaseColumn(QuestionStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public QuestionStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return QuestionStatus.fromCode(dbData);
    }
}
