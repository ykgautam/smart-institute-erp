package com.smartinstitute.erp.fee.mapper;

import com.smartinstitute.erp.fee.dto.request.CreateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.request.UpdateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.response.FeeStructureResponse;
import com.smartinstitute.erp.fee.entity.FeeStructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeeStructureMapper {

    FeeStructure toEntity(CreateFeeStructureRequest request);

    void updateEntity(
            @MappingTarget FeeStructure entity,
            UpdateFeeStructureRequest request
    );

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.courseName")
    FeeStructureResponse toResponse(FeeStructure entity);

}