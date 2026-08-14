package com.smartinstitute.erp.dashboard.admin.dto.response;

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
public class AdminDashboardResponse {

    private Integer totalStudents;

    private Integer activeStudents;

    private Integer totalCourses;

    private Integer totalBatches;

    private Double todayAttendancePercentage;

    private Integer pendingFeeStudents;

    private BigDecimal todayCollection;

    private Integer upcomingTests;

}