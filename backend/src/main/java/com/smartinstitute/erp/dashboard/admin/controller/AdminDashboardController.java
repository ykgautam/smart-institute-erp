package com.smartinstitute.erp.dashboard.admin.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.dashboard.admin.dto.response.*;
import com.smartinstitute.erp.dashboard.admin.service.AdminDashboardService;
import com.smartinstitute.erp.dashboard.dto.response.CoursePerformanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>>
    getDashboard() {

        return ResponseEntity.ok(

                ApiResponseUtil.success(

                        adminDashboardService.getDashboard(),

                        "Dashboard fetched successfully."
                )
        );
    }

    @GetMapping("/student-growth")
    public ResponseEntity<ApiResponse<List<StudentGrowthResponse>>>
    getStudentGrowth() {

        List<StudentGrowthResponse> response =
                adminDashboardService.getStudentGrowth();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Student growth fetched successfully."
                )
        );
    }

    @GetMapping("/attendance-trend")
    public ResponseEntity<ApiResponse<List<AttendanceTrendResponse>>>
    getAttendanceTrend() {

        List<AttendanceTrendResponse> response =
                adminDashboardService.getAttendanceTrend();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Attendance trend fetched successfully."

                )
        );
    }

    @GetMapping("/fee-collection-trend")
    public ResponseEntity<ApiResponse<List<FeeCollectionTrendResponse>>>
    getFeeCollectionTrend() {

        List<FeeCollectionTrendResponse> response =
                adminDashboardService.getFeeCollectionTrend();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Fee collection trend fetched successfully."
                )
        );
    }

    @GetMapping("/course-performance")
    public ResponseEntity<ApiResponse<List<CoursePerformanceResponse>>>
    getCoursePerformance() {

        List<CoursePerformanceResponse> response =
                adminDashboardService.getCoursePerformance();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Course performance fetched successfully."
                )
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<AdminAnalyticsResponse>>
    getAnalyticsSummary() {

        AdminAnalyticsResponse response =
                adminDashboardService.getAnalyticsSummary();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Dashboard analytics fetched successfully."
                )
        );
    }
}