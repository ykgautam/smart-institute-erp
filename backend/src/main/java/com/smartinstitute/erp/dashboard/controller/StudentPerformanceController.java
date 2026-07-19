package com.smartinstitute.erp.dashboard.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.dashboard.dto.response.StudentPerformanceResponse;
import com.smartinstitute.erp.dashboard.service.StudentPerformanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(
        name = "Student Performance",
        description = "Student Performance APIs"
)
@SecurityRequirement(name = "bearerAuth")
public class StudentPerformanceController {

    private final StudentPerformanceService
            studentPerformanceService;

    @GetMapping("/performance")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentPerformanceResponse>>
    getPerformance() {

        return ResponseEntity.ok(

                ApiResponseUtil.success(

                        studentPerformanceService
                                .getPerformance(),

                        "Performance fetched successfully."
                )
        );
    }

}