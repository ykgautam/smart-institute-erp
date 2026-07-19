package com.smartinstitute.erp.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {

    private Long studentId;

    private String studentName;

    private Integer testsAttempted;

    private Integer pendingTests;

    private Double averageScore;

    private Integer totalPassed;

    private Integer totalFailed;

    private List<RecentTestResponse> recentTests;

    private Double highestScore;

    private Double lowestScore;

    private Double passPercentage;

    private List<UpcomingTestResponse> upcomingTests;

}