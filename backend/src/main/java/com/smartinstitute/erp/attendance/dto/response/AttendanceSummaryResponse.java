package com.smartinstitute.erp.attendance.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceSummaryResponse {

    private Long studentId;

    private String studentName;

    private Integer totalPresent;

    private Integer totalAbsent;

    private Integer totalLate;

    private Integer totalLeave;

    private Double attendancePercentage;

    private Integer totalClasses;

}