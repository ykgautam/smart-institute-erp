package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPerformanceResponse {

    private Double overallAverage;

    private Double highestScore;

    private Double lowestScore;

    private Integer testsAttempted;

    private Integer testsPassed;

    private Integer testsFailed;

    private Double passPercentage;

    private List<MonthlyPerformanceResponse> monthlyPerformance;

    private List<CoursePerformanceResponse> coursePerformance;

    private List<TopicPerformanceResponse> strongTopics;

    private List<TopicPerformanceResponse> weakTopics;

}