package com.smartinstitute.erp.dashboard.admin.service;

import com.smartinstitute.erp.dashboard.admin.dto.response.*;
import com.smartinstitute.erp.dashboard.dto.response.CoursePerformanceResponse;

import java.util.List;

public interface AdminDashboardService {

    AdminDashboardResponse getDashboard();

    List<StudentGrowthResponse> getStudentGrowth();

    List<AttendanceTrendResponse> getAttendanceTrend();

    List<FeeCollectionTrendResponse> getFeeCollectionTrend();

    List<CoursePerformanceResponse> getCoursePerformance();

    AdminAnalyticsResponse getAnalyticsSummary();
}