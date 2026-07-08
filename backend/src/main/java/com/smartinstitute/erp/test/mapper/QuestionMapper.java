package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.QuestionResponse;
import com.smartinstitute.erp.test.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = QuestionOptionMapper.class
)
public interface QuestionMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.courseName")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicName", source = "topic.name")
    @Mapping(target = "options", source = "options")
    QuestionResponse toResponse(Question entity);

}