package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.TestPerformanceReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class TestPerformanceReportRepositoryImpl
        implements TestPerformanceReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<TestPerformanceReportProjection>
    getTestPerformanceReportWithSorting(
            Long instituteId,
            Long studentId,
            Long testId,
            Long courseId,
            Long batchId,
            String status,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortColumn,
            String sortDirection) {

        // custom implementation

        return null;
    }
}