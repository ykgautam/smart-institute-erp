package com.smartinstitute.erp.finance.service.impl;

import com.smartinstitute.erp.audit.enums.AuditAction;
import com.smartinstitute.erp.audit.service.AuditLogService;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.finance.dto.request.FinancialCategoryRequest;
import com.smartinstitute.erp.finance.dto.response.FinancialCategoryResponse;
import com.smartinstitute.erp.finance.entity.FinancialCategory;
import com.smartinstitute.erp.finance.mapper.FinancialMapper;
import com.smartinstitute.erp.finance.repository.FinancialCategoryRepository;
import com.smartinstitute.erp.finance.service.FinancialCategoryService;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FinancialCategoryServiceImpl
        extends BaseCrudService
        implements FinancialCategoryService {

    private final FinancialCategoryRepository categoryRepository;
    private final FinancialMapper financialMapper;
    private final AuditLogService auditLogService;

    public FinancialCategoryServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            FinancialCategoryRepository categoryRepository,
            FinancialMapper financialMapper,
            AuditLogService auditLogService
    ) {
        super(securityUtil, instituteAccessValidator);
        this.categoryRepository = categoryRepository;
        this.financialMapper = financialMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    public FinancialCategoryResponse createCategory(
            FinancialCategoryRequest request
    ) {
        Institute institute = getCurrentInstitute();

        // Normalize names consistently to reduce accidental duplicates.
        String name = request.getName().trim();

        if (categoryRepository
                .existsByInstituteAndNameIgnoreCaseAndTransactionType(
                        institute,
                        name,
                        request.getTransactionType()
                )) {
            throw new BadRequestException(
                    "A category with this name and transaction type already exists."
            );
        }

        FinancialCategory category = new FinancialCategory();
        category.setInstitute(institute);
        category.setName(name);
        category.setTransactionType(request.getTransactionType());
        category.setDescription(normalizeOptionalText(
                request.getDescription()
        ));
        category.setActive(true);

        FinancialCategory savedCategory =
                categoryRepository.save(category);

        auditLogService.log(
                AuditAction.CREATE,
                "FINANCIAL_CATEGORY",
                savedCategory.getId(),
                "Created financial category: " + savedCategory.getName()
        );

        return financialMapper.toCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialCategoryResponse> getActiveCategories() {
        Institute institute = getCurrentInstitute();

        return categoryRepository
                .findByInstituteAndActiveTrueOrderByNameAsc(institute)
                .stream()
                .map(financialMapper::toCategoryResponse)
                .toList();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}