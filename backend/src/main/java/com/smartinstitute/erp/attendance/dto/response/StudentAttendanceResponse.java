package com.smartinstitute.erp.attendance.dto.response;

import com.smartinstitute.erp.common.enums.attendance.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentAttendanceResponse {

    private LocalDate attendanceDate;

    private AttendanceStatus status;

    private String remarks;

}