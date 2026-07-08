package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.TopicResponse;
import com.smartinstitute.erp.test.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.courseName")
    @Mapping(target = "courseCode", source = "course.courseCode")
    @Mapping(target = "instituteId", source = "institute.id")
    TopicResponse toResponse(Topic topic);

}