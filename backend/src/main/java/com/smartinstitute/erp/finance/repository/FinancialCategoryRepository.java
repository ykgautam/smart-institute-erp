package com.smartinstitute.erp.finance.repository;

import com.smartinstitute.erp.finance.entity.FinancialCategory;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialCategoryRepository
        extends JpaRepository<FinancialCategory, Long> {

    boolean existsByInstituteAndNameIgnoreCaseAndTransactionType(
            Institute institute,
            String name,
            FinancialTransactionType transactionType
    );

    boolean existsByInstituteAndSystemKey(
            Institute institute,
            String systemKey
    );

    Optional<FinancialCategory> findByIdAndInstitute(
            Long id,
            Institute institute
    );

    List<FinancialCategory> findByInstituteOrderByNameAsc(
            Institute institute
    );

    List<FinancialCategory> findByInstituteAndActiveTrueOrderByNameAsc(
            Institute institute
    );

    Optional<FinancialCategory> findByInstituteAndSystemKey(
            Institute institute,
            String systemKey
    );
}