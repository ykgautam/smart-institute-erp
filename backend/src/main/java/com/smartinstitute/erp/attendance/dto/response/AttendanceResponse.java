package com.smartinstitute.erp.attendance.dto.response;

import com.smartinstitute.erp.common.enums.attendance.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceResponse {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long batchId;

    private String batchName;

    private LocalDate attendanceDate;

    private AttendanceStatus status;

    private String remarks;

    private Long markedBy;

    private String markedByName;

}