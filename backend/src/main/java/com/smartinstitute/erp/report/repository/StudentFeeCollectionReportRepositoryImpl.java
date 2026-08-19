package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.dto.response.StudentFeeCollectionReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentFeeCollectionReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class StudentFeeCollectionReportRepositoryImpl
        implements StudentFeeCollectionReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches paginated student fee collection records.
     *
     * <p>
     * This report represents the current fee collection snapshot
     * stored in the student_fees table.
     *
     * <p>
     * The paid amount is taken from student_fees.paid_amount.
     * This is NOT a payment-transaction history report because the
     * current StudentFee model does not contain individual payment
     * transaction records.
     *
     * <p>
     * Institute isolation is enforced through instituteId supplied
     * by the service layer.
     */
    @Override
    public Page<StudentFeeCollectionReportProjection>
    getStudentFeeCollectionReport(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            String feeStatus,
            Pageable pageable,
            String sortBy,
            String sortDirection) {

        /*
         * Resolve the client-provided sort field through a whitelist.
         *
         * This prevents arbitrary SQL from being injected into
         * the ORDER BY clause.
         */
        String sortColumn =
                resolveSortColumn(sortBy);

        /*
         * Only ASC and DESC are supported.
         *
         * Any invalid/missing value defaults to ASC.
         */
        String direction =
                "DESC".equalsIgnoreCase(sortDirection)
                        ? "DESC"
                        : "ASC";

        /*
         * Main paginated report query.
         *
         * One row represents one StudentFee record.
         */
        String sql = """
                SELECT
                
                    /* =========================
                       STUDENT ID
                       ========================= */
                
                    s.id AS studentId,
                
                    /* =========================
                       STUDENT NAME
                       ========================= */
                
                    CONCAT(
                        s.first_name,
                        CASE
                            WHEN s.last_name IS NOT NULL
                                 AND s.last_name <> ''
                            THEN CONCAT(' ', s.last_name)
                            ELSE ''
                        END
                    ) AS studentName,
                
                    /* =========================
                       COURSE
                       ========================= */
                
                    c.course_name AS courseName,
                
                    /* =========================
                       BATCH
                       ========================= */
                
                    b.batch_name AS batchName,
                
                    /* =========================
                       TOTAL FEE
                       ========================= */
                
                    COALESCE(
                        sf.total_fee,
                        0
                    ) AS totalFee,
                
                    /* =========================
                       DISCOUNT
                       ========================= */
                
                    COALESCE(
                        sf.discount,
                        0
                    ) AS discount,
                
                    /* =========================
                       FINAL FEE
                       ========================= */
                
                    COALESCE(
                        sf.final_fee,
                        0
                    ) AS finalFee,
                
                    /* =========================
                       PAID AMOUNT
                       ========================= */
                
                    COALESCE(
                        sf.paid_amount,
                        0
                    ) AS paidAmount,
                
                    /* =========================
                       PENDING AMOUNT
                       ========================= */
                
                    COALESCE(
                        sf.pending_amount,
                        0
                    ) AS pendingAmount,
                
                    /* =========================
                       FEE STATUS
                       ========================= */
                
                    sf.status AS feeStatus,
                
                /* =========================
                   FEE DUE DATE
                   ========================= */
                
                    sf.fee_due_date AS feeDueDate
                
                FROM student_fees sf
                
                /* =========================
                   STUDENT
                   ========================= */
                
                JOIN students s
                    ON sf.student_id = s.id
                
                /* =========================
                   BATCH
                   ========================= */
                
                LEFT JOIN batches b
                    ON s.batch_id = b.id
                
                /* =========================
                   COURSE
                   ========================= */
                
                LEFT JOIN courses c
                    ON b.course_id = c.id
                
                WHERE sf.institute_id = :instituteId
                
                  /*
                   * Only active student records
                   * are included.
                   */
                
                  AND s.active = true
                
                  /*
                   * Only active fee records
                   * are included.
                   */
                
                  AND sf.active = true
                
                  /*
                   * Collection report should contain
                   * records where some amount has been
                   * collected.
                   *
                   * paid_amount > 0
                   */
                
                  AND COALESCE(
                        sf.paid_amount,
                        0
                  ) > 0
                
                  /* =========================
                     COURSE FILTER
                     ========================= */
                
                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )
                
                  /* =========================
                     BATCH FILTER
                     ========================= */
                
                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )
                
                  /* =========================
                     STUDENT FILTER
                     ========================= */
                
                  AND (
                        CAST(:studentId AS BIGINT) IS NULL
                        OR s.id = CAST(:studentId AS BIGINT)
                  )
                
                  /* =========================
                     FEE STATUS FILTER
                     ========================= */
                
                  AND (
                        CAST(:feeStatus AS VARCHAR) IS NULL
                        OR sf.status = CAST(:feeStatus AS VARCHAR)
                  )
                
                ORDER BY
                """ + sortColumn + " " + direction;

        /*
         * Create native SQL query.
         */
        Query query = entityManager.createNativeQuery(sql);

        /*
         * Institute filter.
         */
        query.setParameter("instituteId", instituteId);

        /*
         * Optional course filter.
         */
        query.setParameter("courseId", courseId);

        /*
         * Optional batch filter.
         */
        query.setParameter("batchId", batchId);

        /*
         * Optional student filter.
         */
        query.setParameter("studentId", studentId);

        /*
         * Optional fee status filter.
         */
        query.setParameter("feeStatus", feeStatus);

        /*
         * Apply pagination.
         */
        query.setFirstResult((int) pageable.getOffset());

        query.setMaxResults(pageable.getPageSize());

        /*
         * Execute native query.
         */
        @SuppressWarnings("unchecked")
        List<Object[]> rows =
                query.getResultList();

        /*
         * Convert native Object[] rows into
         * report projection objects.
         */
        List<StudentFeeCollectionReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        /*
         * Count the complete filtered dataset.
         *
         * This value is used by Spring's Page implementation
         * to calculate totalElements and totalPages.
         */
        long total =
                countReports(
                        instituteId,
                        courseId,
                        batchId,
                        studentId,
                        feeStatus
                );

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    /**
     * Resolves API sort fields into trusted SQL expressions.
     *
     * <p>
     * Never concatenate the raw sortBy value directly into SQL.
     * Only fields explicitly defined below are allowed.
     */
    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "sf.paid_amount";
        }

        return switch (sortBy) {

            case "studentName" -> "CONCAT(s.first_name, ' ', s.last_name)";

            case "courseName" -> "c.course_name";

            case "batchName" -> "b.batch_name";

            case "totalFee" -> "sf.total_fee";

            case "discount" -> "sf.discount";

            case "finalFee" -> "sf.final_fee";

            case "paidAmount" -> "sf.paid_amount";

            case "pendingAmount" -> "sf.pending_amount";

            case "feeStatus" -> "sf.status";

            default -> "sf.paid_amount";
        };
    }

    /**
     * Counts the total number of fee records matching the
     * same filters used by the main report query.
     *
     * <p>
     * The conditions must remain synchronized with the main
     * query. Otherwise pagination metadata could become incorrect.
     */
    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            String feeStatus) {

        String sql = """
                SELECT COUNT(*)
                
                FROM student_fees sf
                
                JOIN students s
                    ON sf.student_id = s.id
                
                LEFT JOIN batches b
                    ON s.batch_id = b.id
                
                LEFT JOIN courses c
                    ON b.course_id = c.id
                
                WHERE sf.institute_id = :instituteId
                
                  /* Only active students. */
                  AND s.active = true
                
                  /* Only active fee records. */
                  AND sf.active = true
                
                  /*
                   * Collection report only includes
                   * records having a positive paid amount.
                   */
                  AND COALESCE(
                        sf.paid_amount,
                        0
                  ) > 0
                
                  /* Course filter. */
                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )
                
                  /* Batch filter. */
                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )
                
                  /* Student filter. */
                  AND (
                        CAST(:studentId AS BIGINT) IS NULL
                        OR s.id = CAST(:studentId AS BIGINT)
                  )
                
                  /* Fee status filter. */
                  AND (
                        CAST(:feeStatus AS VARCHAR) IS NULL
                        OR sf.status = CAST(:feeStatus AS VARCHAR)
                  )
                """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("instituteId", instituteId);

        query.setParameter("courseId", courseId);

        query.setParameter("batchId", batchId);

        query.setParameter("studentId", studentId);

        query.setParameter("feeStatus", feeStatus);

        Number result = (Number) query.getSingleResult();

        return result.longValue();
    }

    /**
     * Maps a native SQL result row into
     * StudentFeeCollectionReportProjection.
     *
     * <p>
     * The indexes below must always match the SELECT column order
     * in the main report query.
     */
    private StudentFeeCollectionReportProjection mapRow(
            Object[] row) {

        return new StudentFeeCollectionReportProjection() {

            @Override
            public Long getStudentId() {
                return toLong(row[0]);
            }

            @Override
            public String getStudentName() {
                return (String) row[1];
            }

            @Override
            public String getCourseName() {
                return (String) row[2];
            }

            @Override
            public String getBatchName() {
                return (String) row[3];
            }

            @Override
            public BigDecimal getTotalFee() {
                return toBigDecimal(row[4]);
            }

            @Override
            public BigDecimal getDiscount() {
                return toBigDecimal(row[5]);
            }

            @Override
            public BigDecimal getFinalFee() {
                return toBigDecimal(row[6]);
            }

            @Override
            public BigDecimal getPaidAmount() {
                return toBigDecimal(row[7]);
            }

            @Override
            public BigDecimal getPendingAmount() {
                return toBigDecimal(row[8]);
            }

            @Override
            public com.smartinstitute.erp.common.enums.fee.FeeStatus
            getFeeStatus() {

                if (row[9] == null) {
                    return null;
                }

                return com.smartinstitute.erp.common.enums.fee.FeeStatus
                        .valueOf(row[9].toString());
            }

            @Override
            public LocalDate getFeeDueDate() {

                if (row[10] == null) {
                    return null;
                }

                if (row[10] instanceof java.sql.Date date) {
                    return date.toLocalDate();
                }

                return LocalDate.parse(
                        row[10].toString()
                );
            }
        };
    }

    /**
     * Safely converts a native SQL numeric value to Long.
     */
    private Long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    /**
     * Safely converts a native SQL numeric value to BigDecimal.
     */
    private BigDecimal toBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        return new BigDecimal(
                value.toString()
        );
    }

    /**
     * Fetches the complete fee collection summary for the applied filters.
     *
     * <p>
     * This query intentionally does not apply pagination because the summary
     * must represent the complete filtered dataset rather than only the
     * records visible on the current page.
     * </p>
     */
    public StudentFeeCollectionReportSummaryResponse getSummary(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            String feeStatus) {

        String sql = """
                SELECT
                
                    /* =========================
                       TOTAL STUDENTS
                       =========================
                       Counts unique students having
                       matching fee collection records.
                       ========================= */
                
                    COUNT(
                        DISTINCT s.id
                    ) AS totalStudents,
                
                    /* =========================
                       STUDENTS WITH PAYMENTS
                       =========================
                       Counts unique students whose
                       paid amount is greater than zero.
                       ========================= */
                
                    COUNT(
                        DISTINCT CASE
                            WHEN COALESCE(sf.paid_amount, 0) > 0
                            THEN s.id
                        END
                    ) AS studentsWithPayments,
                
                    /* =========================
                       TOTAL FEE
                       ========================= */
                
                    COALESCE(
                        SUM(sf.total_fee),
                        0
                    ) AS totalFee,
                
                    /* =========================
                       TOTAL DISCOUNT
                       ========================= */
                
                    COALESCE(
                        SUM(sf.discount),
                        0
                    ) AS totalDiscount,
                
                    /* =========================
                       TOTAL FINAL FEE
                       ========================= */
                
                    COALESCE(
                        SUM(sf.final_fee),
                        0
                    ) AS totalFinalFee,
                
                    /* =========================
                       TOTAL PAID AMOUNT
                       ========================= */
                
                    COALESCE(
                        SUM(sf.paid_amount),
                        0
                    ) AS totalPaidAmount,
                
                    /* =========================
                       TOTAL PENDING AMOUNT
                       ========================= */
                
                    COALESCE(
                        SUM(sf.pending_amount),
                        0
                    ) AS totalPendingAmount
                
                FROM student_fees sf
                
                /* =========================
                   STUDENT
                   ========================= */
                
                JOIN students s
                    ON sf.student_id = s.id
                
                /* =========================
                   BATCH
                   ========================= */
                
                LEFT JOIN batches b
                    ON s.batch_id = b.id
                
                /* =========================
                   COURSE
                   ========================= */
                
                LEFT JOIN courses c
                    ON b.course_id = c.id
                
                WHERE sf.institute_id = :instituteId
                
                  /* Only active students. */
                  AND s.active = true
                
                  /* Only active fee records. */
                  AND sf.active = true
                
                  /* =========================
                     COURSE FILTER
                     ========================= */
                
                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )
                
                  /* =========================
                     BATCH FILTER
                     ========================= */
                
                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )
                
                  /* =========================
                     STUDENT FILTER
                     ========================= */
                
                  AND (
                        CAST(:studentId AS BIGINT) IS NULL
                        OR s.id = CAST(:studentId AS BIGINT)
                  )
                
                  /* =========================
                     FEE STATUS FILTER
                     ========================= */
                
                  AND (
                        CAST(:feeStatus AS VARCHAR) IS NULL
                        OR sf.status = CAST(:feeStatus AS VARCHAR)
                  )
                """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("instituteId", instituteId);

        query.setParameter("courseId", courseId);

        query.setParameter("batchId", batchId);

        query.setParameter("studentId", studentId);

        query.setParameter("feeStatus", feeStatus);

        Object[] row = (Object[]) query.getSingleResult();

        /*
         * IMPORTANT:
         *
         * The constructor argument order must match:
         *
         * 0 -> totalStudents              Long
         * 1 -> studentsWithPayments       Long
         * 2 -> totalFee                   BigDecimal
         * 3 -> totalDiscount              BigDecimal
         * 4 -> totalFinalFee              BigDecimal
         * 5 -> totalPaidAmount            BigDecimal
         * 6 -> totalPendingAmount         BigDecimal
         */

        return new StudentFeeCollectionReportSummaryResponse(
                toLong(row[0]),
                toLong(row[1]),
                toBigDecimal(row[2]),
                toBigDecimal(row[3]),
                toBigDecimal(row[4]),
                toBigDecimal(row[5]),
                toBigDecimal(row[6])
        );
    }

}