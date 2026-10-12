package com.smartinstitute.erp.finance.service;

import com.smartinstitute.erp.finance.dto.request.FinancialCategoryRequest;
import com.smartinstitute.erp.finance.dto.response.FinancialCategoryResponse;

import java.util.List;

public interface FinancialCategoryService {

    FinancialCategoryResponse createCategory(
            FinancialCategoryRequest request
    );

    List<FinancialCategoryResponse> getActiveCategories();
}