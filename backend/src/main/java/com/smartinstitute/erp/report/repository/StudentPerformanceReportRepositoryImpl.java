package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentPerformanceReportProjection;
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

/**
 * Custom repository implementation for student performance reports.
 *
 * <p>
 * This repository uses a native SQL query because the student performance
 * report requires aggregation across students, batches, courses, tests
 * and student test attempts.
 * </p>
 *
 * <p>
 * The implementation supports:
 * </p>
 *
 * <ul>
 *     <li>Institute-level filtering</li>
 *     <li>Student filtering</li>
 *     <li>Course filtering</li>
 *     <li>Batch filtering</li>
 *     <li>Submission date filtering</li>
 *     <li>Pagination</li>
 *     <li>Whitelisted dynamic sorting</li>
 * </ul>
 */
@Repository
public class StudentPerformanceReportRepositoryImpl
        implements StudentPerformanceReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches the student performance report with filtering,
     * pagination and safe dynamic sorting.
     *
     * <p>
     * One result row represents one student-course-batch combination.
     * Only test attempts belonging to the student's course are included.
     * </p>
     */
    @Override
    public Page<StudentPerformanceReportProjection>
    getStudentPerformanceReportWithSorting(
            Long instituteId,
            Long studentId,
            Long courseId,
            Long batchId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection) {

        String sortColumn =
                resolveSortColumn(sortBy);

        String direction =
                "DESC".equalsIgnoreCase(sortDirection)
                        ? "DESC"
                        : "ASC";

        /*
         * Native SQL used for student-level performance aggregation.
         *
         * Important:
         * The order of selected columns must exactly match mapRow().
         */
        String sql = """
                SELECT

                    s.id AS studentId,

                    CONCAT(
                        s.first_name,
                        CASE
                            WHEN s.last_name IS NOT NULL
                                 AND s.last_name <> ''
                            THEN CONCAT(' ', s.last_name)
                            ELSE ''
                        END
                    ) AS studentName,

                    c.course_name AS courseName,

                    b.batch_name AS batchName,

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
                       AVERAGE PERCENTAGE
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
                       HIGHEST PERCENTAGE
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
                       LOWEST PERCENTAGE
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

                /* =========================
                   STUDENT → BATCH
                   ========================= */

                JOIN batches b
                    ON s.batch_id = b.id

                /* =========================
                   BATCH → COURSE
                   ========================= */

                JOIN courses c
                    ON b.course_id = c.id

                /* =========================
                   STUDENT → TEST ATTEMPTS
                   ========================= */

                LEFT JOIN student_tests st
                    ON st.student_id = s.id

                /* =========================
                   TEST → COURSE
                   ========================= */

                LEFT JOIN tests t
                    ON st.test_id = t.id
                    AND t.course_id = c.id

                WHERE s.institute_id = :instituteId

                  AND s.active = true

                  AND b.active = true

                  AND c.active = true

                  /* =========================
                     STUDENT FILTER
                     ========================= */

                  AND (
                        CAST(:studentId AS BIGINT) IS NULL
                        OR s.id = CAST(:studentId AS BIGINT)
                  )

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
                     SUBMISSION FROM FILTER
                     ========================= */

                  AND (
                        CAST(:submittedFrom AS TIMESTAMP) IS NULL
                        OR st.submitted_at >=
                           CAST(:submittedFrom AS TIMESTAMP)
                  )

                  /* =========================
                     SUBMISSION TO FILTER
                     ========================= */

                  AND (
                        CAST(:submittedTo AS TIMESTAMP) IS NULL
                        OR st.submitted_at <=
                           CAST(:submittedTo AS TIMESTAMP)
                  )

                GROUP BY
                    s.id,
                    s.first_name,
                    s.last_name,
                    c.id,
                    c.course_name,
                    b.id,
                    b.batch_name

                ORDER BY
                """ + sortColumn + " " + direction;

        Query query =
                entityManager.createNativeQuery(sql);

        query.setParameter(
                "instituteId",
                instituteId
        );

        query.setParameter(
                "studentId",
                studentId
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

        /*
         * Apply pagination.
         */
        query.setFirstResult(
                (int) pageable.getOffset()
        );

        query.setMaxResults(
                pageable.getPageSize()
        );

        @SuppressWarnings("unchecked")
        List<Object[]> rows =
                query.getResultList();

        /*
         * Convert native SQL rows into projection objects.
         */
        List<StudentPerformanceReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        /*
         * Count the number of report rows without pagination.
         */
        long total =
                countReports(
                        instituteId,
                        studentId,
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

    /**
     * Resolves the requested sort field to a predefined SQL expression.
     *
     * <p>
     * Client-provided SQL is never directly appended to the query.
     * Only explicitly supported fields are allowed.
     */
    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "s.first_name";
        }

        return switch (sortBy) {

            case "studentName" ->
                    "s.first_name";

            case "courseName" ->
                    "c.course_name";

            case "batchName" ->
                    "b.batch_name";

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
                    "s.first_name";
        };
    }

    /**
     * Counts the total number of student performance report rows.
     *
     * <p>
     * The same student/course/batch grouping used by the main query
     * is used here so that pagination metadata remains accurate.
     */
    private long countReports(
            Long instituteId,
            Long studentId,
            Long courseId,
            Long batchId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo) {

        String sql = """
                SELECT COUNT(*)
                FROM (

                    SELECT
                        s.id,
                        c.id,
                        b.id

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

                      AND b.active = true

                      AND c.active = true

                      AND (
                            CAST(:studentId AS BIGINT) IS NULL
                            OR s.id = CAST(:studentId AS BIGINT)
                      )

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
                        s.id,
                        c.id,
                        b.id

                ) report_rows
                """;

        Query query =
                entityManager.createNativeQuery(sql);

        query.setParameter(
                "instituteId",
                instituteId
        );

        query.setParameter(
                "studentId",
                studentId
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

    /**
     * Maps one native SQL result row to the report projection.
     *
     * <p>
     * SQL column indexes must remain synchronized with the SELECT
     * statement above.
     * </p>
     */
    private StudentPerformanceReportProjection mapRow(
            Object[] row) {

        return new StudentPerformanceReportProjection() {

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
            public Long getTotalAttempts() {
                return toLong(row[4]);
            }

            @Override
            public Long getSubmittedAttempts() {
                return toLong(row[5]);
            }

            @Override
            public Long getPassedAttempts() {
                return toLong(row[6]);
            }

            @Override
            public Long getFailedAttempts() {
                return toLong(row[7]);
            }

            @Override
            public BigDecimal getAveragePercentage() {
                return toBigDecimal(row[8]);
            }

            @Override
            public BigDecimal getHighestPercentage() {
                return toBigDecimal(row[9]);
            }

            @Override
            public BigDecimal getLowestPercentage() {
                return toBigDecimal(row[10]);
            }
        };
    }

    /**
     * Safely converts a database numeric value to Long.
     */
    private Long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    /**
     * Safely converts a database numeric value to BigDecimal.
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
}