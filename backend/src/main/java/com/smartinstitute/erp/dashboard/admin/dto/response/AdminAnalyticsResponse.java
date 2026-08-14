package com.smartinstitute.erp.dashboard.admin.dto.response;

import com.smartinstitute.erp.dashboard.dto.response.CoursePerformanceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAnalyticsResponse {

    private List<StudentGrowthResponse> studentGrowth;

    private List<AttendanceTrendResponse> attendanceTrend;

    private List<FeeCollectionTrendResponse> feeCollectionTrend;

    private List<CoursePerformanceResponse> coursePerformance;
}