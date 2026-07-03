package com.smartinstitute.erp.fee.controller;


import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.fee.dto.request.CollectFeeRequest;
import com.smartinstitute.erp.fee.dto.response.FeePaymentResponse;
import com.smartinstitute.erp.fee.service.FeePaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-payments")
public class FeePaymentController {

    private final FeePaymentService feePaymentService;

    public FeePaymentController(FeePaymentService feePaymentService) {

        this.feePaymentService = feePaymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<FeePaymentResponse> collectFee(
            @Valid @RequestBody CollectFeeRequest request) {

        FeePaymentResponse response = feePaymentService.collectFee(request);

        return ApiResponseUtil.success(
                response,
                "Fee collected successfully."
        );
    }

    @GetMapping("/history/{studentFeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<FeePaymentResponse>> getPaymentHistory(
            @PathVariable Long studentFeeId) {

        List<FeePaymentResponse> response =
                feePaymentService.getPaymentHistory(
                        studentFeeId
                );

        return ApiResponseUtil.success(
                response, "Payment History fetched successfully."
        );
    }

}