package com.smartinstitute.erp.test.mapper;

import com.smartinstitute.erp.test.dto.response.StudentTestResponse;
import com.smartinstitute.erp.test.dto.response.StudentTestSummaryResponse;
import com.smartinstitute.erp.test.entity.StudentTest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentTestMapper {

    @Mapping(target = "testId", source = "test.id")
    @Mapping(target = "testTitle", source = "test.title")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(
            target = "studentName",
            expression = "java(studentTest.getStudent().getFirstName() + \" \" + studentTest.getStudent().getLastName())"
    )
    StudentTestResponse toResponse(StudentTest studentTest);

    @Mapping(target = "testId", source = "test.id")
    @Mapping(target = "testTitle", source = "test.title")
    StudentTestSummaryResponse toSummaryResponse(StudentTest studentTest);

    List<StudentTestSummaryResponse> toSummaryResponseList(
            List<StudentTest> studentTests
    );

}