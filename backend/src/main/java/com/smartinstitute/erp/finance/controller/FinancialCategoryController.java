package com.smartinstitute.erp.finance.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.finance.dto.request.FinancialCategoryRequest;
import com.smartinstitute.erp.finance.dto.response.FinancialCategoryResponse;
import com.smartinstitute.erp.finance.service.FinancialCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/finance/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class FinancialCategoryController {

    private final FinancialCategoryService financialCategoryService;

    /**
     * Creates a category within the authenticated user's institute.
     *
     * Institute identity is intentionally excluded from the request DTO;
     * the service must derive it from the authenticated security context.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FinancialCategoryResponse>> createCategory(
            @Valid @RequestBody FinancialCategoryRequest request) {

        FinancialCategoryResponse response =
                financialCategoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseUtil.success(
                        response,
                        "Financial category created successfully."
                )
        );
    }

    /**
     * Returns active categories belonging to the current institute.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialCategoryResponse>>>
    getActiveCategories() {

        List<FinancialCategoryResponse> response =
                financialCategoryService.getActiveCategories();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Financial categories fetched successfully."
                )
        );
    }
}