package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentTestPerformanceReportProjection;
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
public class StudentTestPerformanceReportRepositoryImpl
        implements StudentTestPerformanceReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<StudentTestPerformanceReportProjection>
    getStudentTestPerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            Long testId,
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

        String sql = """
                SELECT

                    s.id AS studentId,

                    CONCAT(
                        s.first_name,
                        ' ',
                        s.last_name
                    ) AS studentName,

                    c.course_name AS courseName,

                    b.batch_name AS batchName,

                    /* =========================
                       TOTAL ATTEMPTS
                       ========================= */

                    COUNT(st.id) AS totalAttempts,

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
                     TEST FILTER
                     ========================= */

                  AND (
                        CAST(:testId AS BIGINT) IS NULL
                        OR t.id = CAST(:testId AS BIGINT)
                  )

                  /* =========================
                     DATE FILTER
                     ========================= */

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
                    s.first_name,
                    s.last_name,
                    c.course_name,
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
                "courseId",
                courseId
        );

        query.setParameter(
                "batchId",
                batchId
        );

        query.setParameter(
                "studentId",
                studentId
        );

        query.setParameter(
                "testId",
                testId
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

        List<StudentTestPerformanceReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        long total =
                countReports(
                        instituteId,
                        courseId,
                        batchId,
                        studentId,
                        testId,
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
            return "studentName";
        }

        return switch (sortBy) {

            case "studentName" ->
                    "CONCAT(s.first_name, ' ', s.last_name)";

            case "courseName" ->
                    "c.course_name";

            case "batchName" ->
                    "b.batch_name";

            case "totalAttempts" ->
                    "COUNT(st.id)";

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
                    "CONCAT(s.first_name, ' ', s.last_name)";
        };
    }

    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            Long testId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo) {

        String sql = """
                SELECT COUNT(DISTINCT s.id)

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
                        CAST(:studentId AS BIGINT) IS NULL
                        OR s.id = CAST(:studentId AS BIGINT)
                  )

                  AND (
                        CAST(:testId AS BIGINT) IS NULL
                        OR t.id = CAST(:testId AS BIGINT)
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
                "studentId",
                studentId
        );

        query.setParameter(
                "testId",
                testId
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

    private StudentTestPerformanceReportProjection mapRow(
            Object[] row) {

        return new StudentTestPerformanceReportProjection() {

            @Override
            public Long getStudentId() {
                return ((Number) row[0]).longValue();
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