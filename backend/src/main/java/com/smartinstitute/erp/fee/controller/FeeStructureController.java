package com.smartinstitute.erp.fee.controller;


import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.fee.dto.request.CreateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.request.UpdateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.response.FeeStructureResponse;
import com.smartinstitute.erp.fee.service.FeeStructureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-structures")
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    public FeeStructureController(
            FeeStructureService feeStructureService) {

        this.feeStructureService = feeStructureService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<FeeStructureResponse> createFeeStructure(
            @Valid @RequestBody CreateFeeStructureRequest request) {

        FeeStructureResponse response =
                feeStructureService.createFeeStructure(request);

        return ApiResponseUtil.success(
                response,
                "Fee Structure created successfully."
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<FeeStructureResponse> updateFeeStructure(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFeeStructureRequest request) {

        FeeStructureResponse response =
                feeStructureService.updateFeeStructure(id, request);

        return ApiResponseUtil.success(
                response,
                "Fee Structure updated successfully."
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<FeeStructureResponse> getFeeStructure(
            @PathVariable Long id) {

        FeeStructureResponse response =
                feeStructureService.getFeeStructure(id);

        return ApiResponseUtil.success(response,"Fee Structure details fetched successfully.");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FACULTY')")
    public ApiResponse<List<FeeStructureResponse>> getAllFeeStructures() {

        List<FeeStructureResponse> response =
                feeStructureService.getAllFeeStructures();

        return ApiResponseUtil.success(response,"All Fee structure details fetched successfully.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<Void> deleteFeeStructure(
            @PathVariable Long id) {

        feeStructureService.deleteFeeStructure(id);

        return ApiResponseUtil.success(
                null,
                "Fee Structure deleted successfully."
        );
    }
}