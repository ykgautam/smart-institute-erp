package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.fee.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentFeeOutstandingReportRepository
        extends JpaRepository<StudentFee, Long>,
                StudentFeeOutstandingReportRepositoryCustom {
}