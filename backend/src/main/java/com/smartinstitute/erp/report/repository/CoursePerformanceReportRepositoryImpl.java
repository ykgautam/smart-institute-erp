package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.CoursePerformanceReportProjection;
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
 * Custom repository implementation for the Course Performance Report.
 *
 * <p>
 * This repository is responsible for fetching course-wise aggregated
 * academic performance data using a native SQL query.
 * </p>
 *
 * <p>
 * The report supports:
 * </p>
 *
 * <ul>
 *     <li>Institute-level filtering</li>
 *     <li>Course filtering</li>
 *     <li>Submission date filtering</li>
 *     <li>Pagination</li>
 *     <li>Safe dynamic sorting</li>
 * </ul>
 */
@Repository
public class CoursePerformanceReportRepositoryImpl
        implements CoursePerformanceReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches course-wise performance report with pagination and sorting.
     *
     * <p>
     * One result row is generated per course.
     * </p>
     *
     * <p>
     * Important:
     * Submission date filters are applied inside the LEFT JOIN
     * condition instead of the WHERE clause. This ensures that
     * courses having no matching attempts are still included
     * in the report.
     * </p>
     */
    @Override
    public Page<CoursePerformanceReportProjection>
    getCoursePerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Pageable pageable,
            String sortBy,
            String sortDirection) {

        /*
         * Resolve the requested sort field to a predefined SQL expression.
         *
         * This prevents arbitrary SQL expressions from being supplied
         * through the sortBy request parameter.
         */
        String sortColumn = resolveSortColumn(sortBy);

        /*
         * Only ASC and DESC are allowed.
         * Any value other than DESC defaults to ASC.
         */
        String direction = "DESC".equalsIgnoreCase(sortDirection)
                ? "DESC" : "ASC";

        String sql = """
                SELECT
                
                    c.id AS courseId,
                
                    c.course_name AS courseName,
                
                    /* =========================
                       TOTAL STUDENTS
                       =========================
                       Counts unique active students
                       belonging to batches of the course.
                       ========================= */
                
                    COUNT(DISTINCT s.id) AS totalStudents,
                
                    /* =========================
                       STUDENTS ATTEMPTED
                       =========================
                       Counts unique students having
                       at least one submitted or
                       auto-submitted attempt.
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
                       =========================
                       Counts all student test attempt
                       records associated with the course.
                       ========================= */
                
                    COUNT(
                        CASE
                            WHEN st.id IS NOT NULL
                            THEN 1
                        END
                    ) AS totalAttempts,
                
                    /* =========================
                       SUBMITTED ATTEMPTS
                       =========================
                       Includes submitted and
                       auto-submitted attempts.
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
                       =========================
                       Counts submitted attempts
                       where passed = true.
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
                       =========================
                       Counts submitted attempts
                       where passed = false.
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
                       =========================
                       Average percentage of
                       submitted attempts.
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
                
                FROM courses c
                
                /* =========================
                   COURSE → BATCH
                   =========================
                   A course can contain multiple
                   active batches.
                   ========================= */
                
                LEFT JOIN batches b
                    ON b.course_id = c.id
                    AND b.institute_id = :instituteId
                    AND b.active = true
                
                /* =========================
                   BATCH → STUDENT
                   =========================
                   Only active students belonging
                   to the current institute are included.
                   ========================= */
                
                LEFT JOIN students s
                    ON s.batch_id = b.id
                    AND s.institute_id = :instituteId
                    AND s.active = true
                
                /* =========================
                   STUDENT → TEST ATTEMPTS
                   =========================
                   Date filters are intentionally
                   applied inside this LEFT JOIN.
                
                   This is important because putting
                   the date filters in WHERE would
                   remove courses having zero attempts.
                   ========================= */
                
                LEFT JOIN student_tests st
                    ON st.student_id = s.id
                
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
                
                /* =========================
                   TEST → COURSE
                   =========================
                   Ensures that the student test
                   belongs to the current course.
                   ========================= */
                
                LEFT JOIN tests t
                    ON st.test_id = t.id
                    AND t.course_id = c.id
                
                WHERE c.institute_id = :instituteId
                
                  /* Only active courses are reported. */
                  AND c.active = true
                
                  /* =========================
                     COURSE FILTER
                     =========================
                     If courseId is null, all courses
                     are included.
                     ========================= */
                
                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )
                
                /* =========================
                   GROUPING
                   =========================
                   One result row per course.
                   ========================= */
                
                GROUP BY
                    c.id,
                    c.course_name
                
                /* =========================
                   SORTING
                   =========================
                   sortColumn is resolved using
                   resolveSortColumn().
                   ========================= */
                
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
         * Optional submission date filters.
         */
        query.setParameter("submittedFrom", submittedFrom);

        query.setParameter("submittedTo", submittedTo);

        /*
         * Apply pagination.
         */
        query.setFirstResult((int) pageable.getOffset());

        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows =
                query.getResultList();

        /*
         * Convert native query rows into
         * CoursePerformanceReportProjection objects.
         */
        List<CoursePerformanceReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        /*
         * Count total courses matching the
         * institute and course filters.
         */
        long total =
                countReports(
                        instituteId,
                        courseId
                );

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    /**
     * Resolves API sort fields into safe SQL expressions.
     *
     * <p>
     * Only predefined fields are allowed.
     * </p>
     */
    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "c.course_name";
        }

        return switch (sortBy) {

            case "courseName" -> "c.course_name";

            case "totalStudents" -> "COUNT(DISTINCT s.id)";

            case "studentsAttempted" -> """
                    COUNT(
                        DISTINCT CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.student_id
                        END
                    )
                    """;

            case "totalAttempts" -> """
                    COUNT(
                        CASE
                            WHEN st.id IS NOT NULL
                            THEN 1
                        END
                    )
                    """;

            case "submittedAttempts" -> """
                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN 1
                        END
                    )
                    """;

            case "passedAttempts" -> """
                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = true
                            THEN 1
                        END
                    )
                    """;

            case "failedAttempts" -> """
                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = false
                            THEN 1
                        END
                    )
                    """;

            case "averagePercentage" -> """
                    AVG(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.percentage
                        END
                    )
                    """;

            case "highestPercentage" -> """
                    MAX(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.percentage
                        END
                    )
                    """;

            case "lowestPercentage" -> """
                    MIN(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN st.percentage
                        END
                    )
                    """;

            default -> "c.course_name";
        };
    }

    /**
     * Counts total courses matching the report filters.
     *
     * <p>
     * Date filters are intentionally not applied here because
     * the report is course-based. A course should remain in the
     * result even when it has zero attempts for the requested
     * date range.
     * </p>
     */
    private long countReports(
            Long instituteId,
            Long courseId) {

        String sql = """
                SELECT COUNT(*)
                
                FROM courses c
                
                WHERE c.institute_id = :instituteId
                
                  AND c.active = true
                
                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )
                """;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("instituteId", instituteId);

        query.setParameter("courseId", courseId);

        Number result =
                (Number) query.getSingleResult();

        return result.longValue();
    }

    /**
     * Maps native SQL result row to the projection.
     *
     * <p>
     * The indexes below must always match the exact
     * SELECT column order.
     * </p>
     */
    private CoursePerformanceReportProjection mapRow(
            Object[] row) {

        return new CoursePerformanceReportProjection() {

            @Override
            public Long getCourseId() {
                return ((Number) row[0]).longValue();
            }

            @Override
            public String getCourseName() {
                return (String) row[1];
            }

            @Override
            public Long getTotalStudents() {
                return ((Number) row[2]).longValue();
            }

            @Override
            public Long getStudentsAttempted() {
                return ((Number) row[3]).longValue();
            }

            @Override
            public Long getTotalAttempts() {
                return ((Number) row[4]).longValue();
            }

            @Override
            public Long getSubmittedAttempts() {
                return ((Number) row[5]).longValue();
            }

            @Override
            public Long getPassedAttempts() {
                return ((Number) row[6]).longValue();
            }

            @Override
            public Long getFailedAttempts() {
                return ((Number) row[7]).longValue();
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
     * Safely converts native SQL numeric values
     * into BigDecimal.
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