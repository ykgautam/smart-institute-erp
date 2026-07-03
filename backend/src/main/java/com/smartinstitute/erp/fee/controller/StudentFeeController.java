package com.smartinstitute.erp.fee.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.fee.dto.request.AssignStudentFeeRequest;
import com.smartinstitute.erp.fee.dto.response.StudentFeeResponse;
import com.smartinstitute.erp.fee.service.StudentFeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student-fees")
public class StudentFeeController {

    private final StudentFeeService studentFeeService;

    public StudentFeeController(
            StudentFeeService studentFeeService) {

        this.studentFeeService = studentFeeService;
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<StudentFeeResponse> assignFee(
            @Valid @RequestBody AssignStudentFeeRequest request) {

        StudentFeeResponse response =
                studentFeeService.assignFee(request);

        return ApiResponseUtil.success(
                response,
                "Fee assigned successfully."
        );
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<StudentFeeResponse> getStudentFee(
            @PathVariable Long studentId) {

        StudentFeeResponse response =
                studentFeeService.getStudentFee(studentId);

        return ApiResponseUtil.success(
                response,"Fee assigned successfully."
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<StudentFeeResponse>> getAllStudentFees() {

        List<StudentFeeResponse> response =
                studentFeeService.getAllStudentFees();

        return ApiResponseUtil.success(
                response,"Fee assigned successfully."
        );
    }

}