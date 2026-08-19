package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.BatchPerformanceReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BatchPerformanceReportRepositoryImpl
        implements BatchPerformanceReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<BatchPerformanceReportProjection>
    getBatchPerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection) {

        String sortColumn = resolveSortColumn(sortBy);

        String direction =
                "DESC".equalsIgnoreCase(sortDirection)
                        ? "DESC"
                        : "ASC";

        String sql = """
                SELECT

                    b.id AS batchId,

                    b.batch_name AS batchName,

                    c.course_name AS courseName,

                    /* =========================
                       TOTAL STUDENTS
                       ========================= */

                    COUNT(DISTINCT s.id) AS totalStudents,

                    /* =========================
                       STUDENTS ATTEMPTED
                       Unique students having
                       submitted/auto-submitted
                       attempts
                       ========================= */

                    COUNT(
                        DISTINCT CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.student_id
                        END
                    ) AS studentsAttempted,

                    /* =========================
                       TOTAL ATTEMPTS
                       ========================= */

                    COUNT(
                        CASE
                            WHEN st.id IS NOT NULL
                            THEN 1
                        END
                    ) AS totalAttempts,

                    /* =========================
                       SUBMITTED ATTEMPTS
                       ========================= */

                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN 1
                        END
                    ) AS submittedAttempts,

                    /* =========================
                       PASSED ATTEMPTS
                       ========================= */

                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = true
                            THEN 1
                        END
                    ) AS passedAttempts,

                    /* =========================
                       FAILED ATTEMPTS
                       ========================= */

                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = false
                            THEN 1
                        END
                    ) AS failedAttempts,

                    /* =========================
                       AVERAGE %
                       ========================= */

                    COALESCE(
                        ROUND(
                            AVG(
                                CASE
                                    WHEN st.status IN
                                        ('SUBMITTED', 'AUTO_SUBMITTED')
                                    THEN st.percentage
                                END
                            ),
                            2
                        ),
                        0
                    ) AS averagePercentage,

                    /* =========================
                       HIGHEST %
                       ========================= */

                    COALESCE(
                        ROUND(
                            MAX(
                                CASE
                                    WHEN st.status IN
                                        ('SUBMITTED', 'AUTO_SUBMITTED')
                                    THEN st.percentage
                                END
                            ),
                            2
                        ),
                        0
                    ) AS highestPercentage,

                    /* =========================
                       LOWEST %
                       ========================= */

                    COALESCE(
                        ROUND(
                            MIN(
                                CASE
                                    WHEN st.status IN
                                        ('SUBMITTED', 'AUTO_SUBMITTED')
                                    THEN st.percentage
                                END
                            ),
                            2
                        ),
                        0
                    ) AS lowestPercentage

                FROM students s

                JOIN batches b
                    ON s.batch_id = b.id

                JOIN courses c
                    ON b.course_id = c.id

                LEFT JOIN student_tests st
                    ON st.student_id = s.id

                LEFT JOIN tests t
                    ON st.test_id = t.id
                    AND t.course_id = c.id

                WHERE s.institute_id = :instituteId

                  AND s.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:submittedFrom AS TIMESTAMP) IS NULL
                        OR st.submitted_at >=
                           CAST(:submittedFrom AS TIMESTAMP)
                  )

                  AND (
                        CAST(:submittedTo AS TIMESTAMP) IS NULL
                        OR st.submitted_at <=
                           CAST(:submittedTo AS TIMESTAMP)
                  )

                GROUP BY
                    b.id,
                    b.batch_name,
                    c.course_name

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

        query.setParameter(
                "submittedFrom",
                submittedFrom
        );

        query.setParameter(
                "submittedTo",
                submittedTo
        );

        query.setFirstResult(
                (int) pageable.getOffset()
        );

        query.setMaxResults(
                pageable.getPageSize()
        );

        @SuppressWarnings("unchecked")
        List<Object[]> rows =
                query.getResultList();

        List<BatchPerformanceReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        long total =
                countReports(
                        instituteId,
                        courseId,
                        batchId,
                        submittedFrom,
                        submittedTo
                );

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "b.batch_name";
        }

        return switch (sortBy) {

            case "batchName" ->
                    "b.batch_name";

            case "courseName" ->
                    "c.course_name";

            case "totalStudents" ->
                    "COUNT(DISTINCT s.id)";

            case "studentsAttempted" ->
                    """
                    COUNT(
                        DISTINCT CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.student_id
                        END
                    )
                    """;

            case "totalAttempts" ->
                    """
                    COUNT(
                        CASE
                            WHEN st.id IS NOT NULL
                            THEN 1
                        END
                    )
                    """;

            case "submittedAttempts" ->
                    """
                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN 1
                        END
                    )
                    """;

            case "passedAttempts" ->
                    """
                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = true
                            THEN 1
                        END
                    )
                    """;

            case "failedAttempts" ->
                    """
                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = false
                            THEN 1
                        END
                    )
                    """;

            case "averagePercentage" ->
                    """
                    AVG(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.percentage
                        END
                    )
                    """;

            case "highestPercentage" ->
                    """
                    MAX(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.percentage
                        END
                    )
                    """;

            case "lowestPercentage" ->
                    """
                    MIN(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.percentage
                        END
                    )
                    """;

            default ->
                    "b.batch_name";
        };
    }

    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo) {

        String sql = """
                SELECT COUNT(*)

                FROM batches b

                JOIN courses c
                    ON b.course_id = c.id

                WHERE b.institute_id = :instituteId

                  AND b.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )

                  AND EXISTS (

                        SELECT 1

                        FROM students s

                        JOIN student_tests st
                            ON st.student_id = s.id

                        WHERE s.batch_id = b.id

                          AND s.active = true

                          AND (
                                CAST(:submittedFrom AS TIMESTAMP) IS NULL
                                OR st.submitted_at >=
                                   CAST(:submittedFrom AS TIMESTAMP)
                          )

                          AND (
                                CAST(:submittedTo AS TIMESTAMP) IS NULL
                                OR st.submitted_at <=
                                   CAST(:submittedTo AS TIMESTAMP)
                          )
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
                "submittedFrom",
                submittedFrom
        );

        query.setParameter(
                "submittedTo",
                submittedTo
        );

        Number result =
                (Number) query.getSingleResult();

        return result.longValue();
    }

    private BatchPerformanceReportProjection mapRow(
            Object[] row) {

        return new BatchPerformanceReportProjection() {

            @Override
            public Long getBatchId() {
                return toLong(row[0]);
            }

            @Override
            public String getBatchName() {
                return (String) row[1];
            }

            @Override
            public String getCourseName() {
                return (String) row[2];
            }

            @Override
            public Long getTotalStudents() {
                return toLong(row[3]);
            }

            @Override
            public Long getStudentsAttempted() {
                return toLong(row[4]);
            }

            @Override
            public Long getTotalAttempts() {
                return toLong(row[5]);
            }

            @Override
            public Long getSubmittedAttempts() {
                return toLong(row[6]);
            }

            @Override
            public Long getPassedAttempts() {
                return toLong(row[7]);
            }

            @Override
            public Long getFailedAttempts() {
                return toLong(row[8]);
            }

            @Override
            public BigDecimal getAveragePercentage() {
                return toBigDecimal(row[9]);
            }

            @Override
            public BigDecimal getHighestPercentage() {
                return toBigDecimal(row[10]);
            }

            @Override
            public BigDecimal getLowestPercentage() {
                return toBigDecimal(row[11]);
            }
        };
    }

    private Long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        if (value instanceof Number number) {
            return BigDecimal.valueOf(
                    number.doubleValue()
            );
        }

        return new BigDecimal(
                value.toString()
        );
    }
}