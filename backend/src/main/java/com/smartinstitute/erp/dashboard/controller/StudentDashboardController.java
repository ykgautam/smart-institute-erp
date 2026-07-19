package com.smartinstitute.erp.dashboard.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.dashboard.dto.response.StudentDashboardResponse;
import com.smartinstitute.erp.dashboard.service.StudentDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Student Dashboard",
        description = "Student Dashboard APIs"
)
@SecurityRequirement(name = "bearerAuth")
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Student Dashboard")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getDashboard() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentDashboardService.getDashboard(),
                        "Dashboard fetched successfully."
                )
        );
    }

}