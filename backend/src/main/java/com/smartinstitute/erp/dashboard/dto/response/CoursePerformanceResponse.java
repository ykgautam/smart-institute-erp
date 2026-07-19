package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePerformanceResponse {

    private Long courseId;

    private String courseName;

    private Integer testsAttempted;

    private Integer testsPassed;

    private Double averagePercentage;

    private Double passPercentage;

}