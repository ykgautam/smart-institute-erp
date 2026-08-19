package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.fee.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for StudentFee based fee collection reporting.
 */
public interface StudentFeeCollectionReportRepository
        extends JpaRepository<StudentFee, Long>,
                StudentFeeCollectionReportRepositoryCustom {
}