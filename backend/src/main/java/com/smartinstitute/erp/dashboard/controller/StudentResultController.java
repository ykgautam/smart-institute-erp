package com.smartinstitute.erp.dashboard.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.dashboard.dto.response.StudentTestResultResponse;
import com.smartinstitute.erp.dashboard.service.StudentResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student/tests")
@RequiredArgsConstructor
@Tag(
        name = "Student Test Result",
        description = "Student Test Result APIs"
)
@SecurityRequirement(name = "bearerAuth")
public class StudentResultController {

    private final StudentResultService studentResultService;

    @GetMapping("/{studentTestId}/result")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Student Test Result")
    public ResponseEntity<ApiResponse<StudentTestResultResponse>>
    getResult(

            @PathVariable
            Long studentTestId) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentResultService.getResult(studentTestId),
                        "Student test result fetched successfully."
                )
        );
    }
}