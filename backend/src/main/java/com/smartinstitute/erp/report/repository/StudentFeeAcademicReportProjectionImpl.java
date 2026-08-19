package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentFeeAcademicReportProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class StudentFeeAcademicReportProjectionImpl
        implements StudentFeeAcademicReportProjection {

    private Long studentId;

    private String studentName;

    private String courseName;

    private String batchName;

    private BigDecimal finalFee;

    private BigDecimal paidAmount;

    private BigDecimal pendingAmount;

    private String feeStatus;

    private BigDecimal attendancePercentage;

    private Long totalTests;

    private Long totalAttempts;

    private Long passedTests;

    private Long failedTests;

    private BigDecimal averageTestPercentage;
}