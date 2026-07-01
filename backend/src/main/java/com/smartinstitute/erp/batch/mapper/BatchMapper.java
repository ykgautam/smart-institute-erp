package com.smartinstitute.erp.batch.mapper;

import com.smartinstitute.erp.batch.dto.request.CreateBatchRequest;
import com.smartinstitute.erp.batch.dto.request.UpdateBatchRequest;
import com.smartinstitute.erp.batch.dto.response.BatchResponse;
import com.smartinstitute.erp.batch.entity.Batch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    Batch toEntity(CreateBatchRequest request);

    void updateEntity(
            @MappingTarget Batch batch,
            UpdateBatchRequest request
    );

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.courseName")
    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(
            target = "facultyName",
            expression = "java(batch.getFaculty() == null ? null : batch.getFaculty().getFirstName() + \" \" + batch.getFaculty().getLastName())"
    )
    BatchResponse toResponse(Batch batch);

}