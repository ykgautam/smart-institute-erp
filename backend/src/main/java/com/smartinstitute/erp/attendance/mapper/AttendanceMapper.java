package com.smartinstitute.erp.attendance.mapper;

import com.smartinstitute.erp.attendance.dto.request.AttendanceEntryRequest;
import com.smartinstitute.erp.attendance.dto.request.UpdateAttendanceRequest;
import com.smartinstitute.erp.attendance.dto.response.AttendanceResponse;
import com.smartinstitute.erp.attendance.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    Attendance toEntity(AttendanceEntryRequest request);

    void updateEntity(
            @MappingTarget Attendance attendance,
            UpdateAttendanceRequest request
    );

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName",
            expression = "java(attendance.getStudent().getFirstName() + \" \" + attendance.getStudent().getLastName())")
    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "batchName", source = "batch.batchName")
    @Mapping(target = "markedBy", source = "markedBy.id")
    @Mapping(target = "markedByName",
            expression = "java(attendance.getMarkedBy().getFirstName() + \" \" + attendance.getMarkedBy().getLastName())")
    AttendanceResponse toResponse(
            Attendance attendance
    );

}