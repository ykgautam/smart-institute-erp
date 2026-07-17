package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.QuestionForStudentResponse;
import com.smartinstitute.erp.test.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = QuestionOptionForStudentMapper.class
)
public interface StudentQuestionMapper {

    @Mapping(target = "options", source = "options")
    QuestionForStudentResponse toResponse(
            Question question
    );

    List<QuestionForStudentResponse> toResponseList(
            List<Question> questions
    );

}