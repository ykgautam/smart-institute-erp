package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.QuestionOptionResponse;
import com.smartinstitute.erp.test.entity.QuestionOption;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionOptionMapper {

    QuestionOptionResponse toResponse(QuestionOption entity);

}