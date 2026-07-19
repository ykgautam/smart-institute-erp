package com.smartinstitute.erp.dashboard.controller;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.dashboard.dto.response.StudentTestHistoryResponse;
import com.smartinstitute.erp.dashboard.service.StudentTestHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/student/tests")
@RequiredArgsConstructor
@Tag(name = "Student Test History")
@SecurityRequirement(name = "bearerAuth")
public class StudentTestHistoryController {

    private final StudentTestHistoryService studentTestHistoryService;

    @GetMapping("/history")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Student Test History")
    public ResponseEntity<ApiResponse<Page<StudentTestHistoryResponse>>> getHistory(

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(required = false)
            StudentTestStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            String search,

            @RequestParam(defaultValue = "submittedAt")
            String sort,

            @RequestParam(defaultValue = "desc")
            String direction
            ) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        studentTestHistoryService.getHistory(
                                page,
                                size,
                                status,
                                fromDate,
                                toDate,
                                search,
                                sort,
                                direction
                        ),
                        "Student test history fetched successfully."
                )
        );
    }

}