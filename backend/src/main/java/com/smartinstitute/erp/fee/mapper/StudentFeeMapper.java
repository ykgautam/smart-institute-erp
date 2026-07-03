package com.smartinstitute.erp.fee.mapper;

import com.smartinstitute.erp.fee.dto.response.StudentFeeResponse;
import com.smartinstitute.erp.fee.entity.StudentFee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StudentFeeMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(
            target = "studentName",
            expression = "java(entity.getStudent().getFirstName() + " +
                    "(entity.getStudent().getLastName() == null ? \"\" : \" \" + entity.getStudent().getLastName()))"
    )
//    @Mapping(target = "feeStructureId", source = "feeStructure.id")
//    @Mapping(target = "courseName", source = "feeStructure.course.courseName")
    StudentFeeResponse toResponse(StudentFee entity);

}