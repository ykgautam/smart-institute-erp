package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceReportResponse {

    private Long studentId;

    private String studentName;

    private String courseName;

    private String batchName;

    private Long totalClasses;

    private Long presentClasses;

    private Long absentClasses;

    private BigDecimal attendancePercentage;
}