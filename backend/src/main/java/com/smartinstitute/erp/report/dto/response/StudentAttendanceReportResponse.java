package com.smartinstitute.erp.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Represents one student record in the Student Attendance Report.
 *
 * <p>
 * This DTO is the API-facing representation of the attendance
 * report. Repository projections are converted into this DTO
 * before being returned to the client.
 * </p>
 */
@Getter
@AllArgsConstructor
public class StudentAttendanceReportResponse {

    /**
     * Unique identifier of the student.
     */
    private Long studentId;

    /**
     * Full name of the student.
     */
    private String studentName;

    /**
     * Course associated with the student's batch.
     */
    private String courseName;

    /**
     * Batch associated with the student.
     */
    private String batchName;

    /**
     * Total number of classes considered for the report.
     */
    private Long totalClasses;

    /**
     * Number of classes where the student was present.
     */
    private Long presentClasses;

    /**
     * Number of classes where the student was absent.
     */
    private Long absentClasses;

    /**
     * Attendance percentage calculated from the
     * filtered attendance records.
     */
    private BigDecimal attendancePercentage;
}