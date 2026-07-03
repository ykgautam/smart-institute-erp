package com.smartinstitute.erp.attendance.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceMonthlyReportResponse {

    private Long studentId;

    private String studentName;

    private Integer year;

    private Integer month;

    private Integer totalClasses;

    private Integer totalPresent;

    private Integer totalAbsent;

    private Integer totalLate;

    private Integer totalLeave;

    private Double attendancePercentage;

}