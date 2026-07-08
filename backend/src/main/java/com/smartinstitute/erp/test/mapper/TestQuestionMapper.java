package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.TestQuestionResponse;
import com.smartinstitute.erp.test.entity.TestQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestQuestionMapper {

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "questionText", source = "question.questionText")
    @Mapping(target = "marks", source = "question.marks")
    TestQuestionResponse toResponse(TestQuestion entity);

}