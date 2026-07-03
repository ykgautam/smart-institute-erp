package com.smartinstitute.erp.attendance.dto.request;

import com.smartinstitute.erp.common.enums.attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceEntryRequest {

    @NotNull(message = "Student Id is required.")
    private Long studentId;

    @NotNull(message = "Attendance status is required.")
    private AttendanceStatus status;

    private String remarks;

}