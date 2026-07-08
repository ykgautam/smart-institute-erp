package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.TestResponse;
import com.smartinstitute.erp.test.entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.courseName")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicName", source = "topic.name")
    @Mapping(
            target = "questionCount",
            expression = "java(test.getTestQuestions() == null ? 0 : test.getTestQuestions().size())"
    )
    TestResponse toResponse(Test test);

}