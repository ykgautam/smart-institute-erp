package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentFeeOutstandingReportProjection;
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
public class StudentFeeOutstandingReportRepositoryImpl
        implements StudentFeeOutstandingReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches student-wise outstanding fee information.
     *
     * <p>
     * The query starts from student_fees because this report is primarily
     * a financial report. Student, batch and course information is joined
     * only to enrich the report response.
     * </p>
     *
     * <p>
     * Only records with pendingAmount greater than zero are returned.
     * Therefore fully-paid students are excluded from this outstanding
     * fee report.
     * </p>
     */
    @Override
    public Page<StudentFeeOutstandingReportProjection>
    getStudentFeeOutstandingReport(
            Long instituteId,
            Long courseId,
            Long batchId,
            String feeStatus,
            LocalDate dueDateFrom,
            LocalDate dueDateTo,
            Pageable pageable,
            String sortBy,
            String sortDirection) {

        String sortColumn =
                resolveSortColumn(sortBy);

        String direction =
                "DESC".equalsIgnoreCase(sortDirection)
                        ? "DESC"
                        : "ASC";

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
                
                    sf.total_fee AS totalFee,
                
                    /* =========================
                       DISCOUNT
                       ========================= */
                
                    sf.discount AS discount,
                
                    /* =========================
                       FINAL FEE
                       ========================= */
                
                    sf.final_fee AS finalFee,
                
                    /* =========================
                       PAID AMOUNT
                       ========================= */
                
                    sf.paid_amount AS paidAmount,
                
                    /* =========================
                       PENDING AMOUNT
                       ========================= */
                
                    sf.pending_amount AS pendingAmount,
                
                    /* =========================
                       FEE STATUS
                       ========================= */
                
                    sf.status AS feeStatus,
                
                    /* =========================
                       DUE DATE
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
                
                  /* Only active students are reported. */
                  AND s.active = true
                
                  /* Only active fee records are reported. */
                  AND sf.active = true
                
                  /* =========================
                     OUTSTANDING FILTER
                     ========================= */
                
                  AND COALESCE(
                        sf.pending_amount,
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
                     FEE STATUS FILTER
                     ========================= */
                
                  AND (
                        CAST(:feeStatus AS VARCHAR) IS NULL
                        OR sf.status = CAST(:feeStatus AS VARCHAR)
                  )
                
                  /* =========================
                     DUE DATE FROM
                     ========================= */
                
                  AND (
                        CAST(:dueDateFrom AS DATE) IS NULL
                        OR sf.fee_due_date >=
                           CAST(:dueDateFrom AS DATE)
                  )
                
                  /* =========================
                     DUE DATE TO
                     ========================= */
                
                  AND (
                        CAST(:dueDateTo AS DATE) IS NULL
                        OR sf.fee_due_date <=
                           CAST(:dueDateTo AS DATE)
                  )
                
                ORDER BY
                """ + sortColumn + " " + direction;

        Query query =
                entityManager.createNativeQuery(sql);

        query.setParameter(
                "instituteId",
                instituteId
        );

        query.setParameter(
                "courseId",
                courseId
        );

        query.setParameter(
                "batchId",
                batchId
        );

        query.setParameter("feeStatus", feeStatus);

        query.setParameter("dueDateFrom", dueDateFrom);

        query.setParameter("dueDateTo", dueDateTo);

        query.setFirstResult((int) pageable.getOffset());

        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked") List<Object[]> rows = query.getResultList();

        List<StudentFeeOutstandingReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        long total =
                countReports(
                        instituteId,
                        courseId,
                        batchId,
                        feeStatus,
                        dueDateFrom,
                        dueDateTo
                );

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    /**
     * Converts the API sort field into a trusted SQL expression.
     *
     * <p>
     * Client-provided values are never directly concatenated into SQL.
     * This whitelist protects the dynamic ORDER BY clause from SQL injection.
     * </p>
     */
    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "sf.pending_amount";
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

            case "feeDueDate" -> "sf.fee_due_date";

            default -> "sf.pending_amount";
        };
    }

    /**
     * Counts the total number of outstanding fee records.
     *
     * <p>
     * The filtering conditions must match the main query so that
     * pagination metadata remains accurate.
     * </p>
     */
    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            String feeStatus,
            LocalDate dueDateFrom,
            LocalDate dueDateTo) {

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
                
                  AND s.active = true
                
                  AND sf.active = true
                
                  /* Only outstanding records. */
                  AND COALESCE(
                        sf.pending_amount,
                        0
                  ) > 0
                
                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )
                
                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )
                
                  AND (
                        CAST(:feeStatus AS VARCHAR) IS NULL
                        OR sf.status = CAST(:feeStatus AS VARCHAR)
                  )
                
                  AND (
                        CAST(:dueDateFrom AS DATE) IS NULL
                        OR sf.fee_due_date >=
                           CAST(:dueDateFrom AS DATE)
                  )
                
                  AND (
                        CAST(:dueDateTo AS DATE) IS NULL
                        OR sf.fee_due_date <=
                           CAST(:dueDateTo AS DATE)
                  )
                """;

        Query query =
                entityManager.createNativeQuery(sql);

        query.setParameter(
                "instituteId",
                instituteId
        );

        query.setParameter(
                "courseId",
                courseId
        );

        query.setParameter(
                "batchId",
                batchId
        );

        query.setParameter(
                "feeStatus",
                feeStatus
        );

        query.setParameter(
                "dueDateFrom",
                dueDateFrom
        );

        query.setParameter(
                "dueDateTo",
                dueDateTo
        );

        Number result =
                (Number) query.getSingleResult();

        return result.longValue();
    }

    /**
     * Converts the native SQL Object[] result into the projection
     * expected by the service layer.
     */
    private StudentFeeOutstandingReportProjection mapRow(
            Object[] row) {

        return new StudentFeeOutstandingReportProjection() {

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
     * Calculates the overall financial summary for the report.
     *
     * <p>
     * Important:
     * The summary intentionally does NOT use:
     *
     * <pre>
     * pending_amount > 0
     * </pre>
     * <p>
     * for the financial totals.
     * <p>
     * This allows the summary to represent the complete financial
     * position of the filtered students, including students whose
     * fees are already fully paid.
     * </p>
     *
     * <p>
     * However, studentsWithOutstandingFees counts only students
     * whose pending amount is greater than zero.
     * </p>
     */
    @Override
    public Object[] getStudentFeeOutstandingReportSummary(
            Long instituteId,
            Long courseId,
            Long batchId,
            String feeStatus,
            LocalDate dueDateFrom,
            LocalDate dueDateTo) {

        String sql = """
                SELECT
                
                    /* =========================
                       TOTAL STUDENTS
                       ========================= */
                
                    COUNT(
                        DISTINCT s.id
                    ) AS totalStudents,
                
                    /* =========================
                       STUDENTS WITH OUTSTANDING
                       ========================= */
                
                    COUNT(
                        DISTINCT CASE
                            WHEN COALESCE(
                                sf.pending_amount,
                                0
                            ) > 0
                            THEN s.id
                        END
                    ) AS studentsWithOutstandingFees,
                
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
                       TOTAL PAID
                       ========================= */
                
                    COALESCE(
                        SUM(sf.paid_amount),
                        0
                    ) AS totalPaidAmount,
                
                    /* =========================
                       TOTAL PENDING
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
                     FEE STATUS FILTER
                     ========================= */
                
                  AND (
                        CAST(:feeStatus AS VARCHAR) IS NULL
                        OR sf.status = CAST(:feeStatus AS VARCHAR)
                  )
                
                  /* =========================
                     DUE DATE FROM
                     ========================= */
                
                  AND (
                        CAST(:dueDateFrom AS DATE) IS NULL
                        OR sf.fee_due_date >=
                           CAST(:dueDateFrom AS DATE)
                  )
                
                  /* =========================
                     DUE DATE TO
                     ========================= */
                
                  AND (
                        CAST(:dueDateTo AS DATE) IS NULL
                        OR sf.fee_due_date <=
                           CAST(:dueDateTo AS DATE)
                  )
                """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("instituteId", instituteId);

        query.setParameter("courseId", courseId);

        query.setParameter("batchId", batchId);

        query.setParameter("feeStatus", feeStatus);

        query.setParameter("dueDateFrom", dueDateFrom);

        query.setParameter("dueDateTo", dueDateTo);

        return (Object[]) query.getSingleResult();
    }
}