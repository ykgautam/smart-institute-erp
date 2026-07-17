package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.QuestionOptionForStudentResponse;
import com.smartinstitute.erp.test.entity.QuestionOption;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionOptionForStudentMapper {

    QuestionOptionForStudentResponse toResponse(
            QuestionOption entity
    );

}