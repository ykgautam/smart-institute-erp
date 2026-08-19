package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.TestWisePerformanceReportProjection;
import com.smartinstitute.erp.report.repository.TestWisePerformanceReportRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TestWisePerformanceReportRepositoryImpl
        implements TestWisePerformanceReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<TestWisePerformanceReportProjection>
    getTestWisePerformanceReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
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
                    t.id AS testId,

                    t.title AS testName,

                    c.course_name AS courseName,

                    tp.name AS topicName,

                    COUNT(st.id) AS totalAttempts,

                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                            THEN 1
                        END
                    ) AS submittedAttempts,

                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = true
                            THEN 1
                        END
                    ) AS passedAttempts,

                    COUNT(
                        CASE
                            WHEN st.status IN
                                ('SUBMITTED', 'AUTO_SUBMITTED')
                                AND st.passed = false
                            THEN 1
                        END
                    ) AS failedAttempts,

                    ROUND(
                        AVG(
                            CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                THEN st.percentage
                            END
                        ),
                        2
                    ) AS averagePercentage,

                    ROUND(
                        MAX(
                            CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                THEN st.percentage
                            END
                        ),
                        2
                    ) AS highestPercentage,

                    ROUND(
                        MIN(
                            CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                THEN st.percentage
                            END
                        ),
                        2
                    ) AS lowestPercentage

                FROM student_tests st

                JOIN tests t
                    ON st.test_id = t.id

                JOIN courses c
                    ON t.course_id = c.id

                JOIN topics tp
                    ON t.topic_id = tp.id

                LEFT JOIN students s
                    ON st.student_id = s.id

                LEFT JOIN batches b
                    ON s.batch_id = b.id

                WHERE t.institute_id = :instituteId

                  AND t.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
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

                GROUP BY
                    t.id,
                    t.title,
                    c.course_name,
                    tp.name

                ORDER BY
                """ + sortColumn + " " + direction;

        Query query =
                entityManager.createNativeQuery(
                        sql,
                        "TestWisePerformanceReportMapping"
                );

        query.setParameter("instituteId", instituteId);
        query.setParameter("courseId", courseId);
        query.setParameter("batchId", batchId);
        query.setParameter("testId", testId);
        query.setParameter("submittedFrom", submittedFrom);
        query.setParameter("submittedTo", submittedTo);

        query.setFirstResult(
                (int) pageable.getOffset()
        );

        query.setMaxResults(
                pageable.getPageSize()
        );

        @SuppressWarnings("unchecked")
        List<TestWisePerformanceReportProjection> content =
                query.getResultList();

        long total =
                countReports(
                        instituteId,
                        courseId,
                        batchId,
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
            return "t.title";
        }

        return switch (sortBy) {

            case "testName" ->
                    "t.title";

            case "courseName" ->
                    "c.course_name";

            case "topicName" ->
                    "tp.name";

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
                    "AVG(st.percentage)";

            case "highestPercentage" ->
                    "MAX(st.percentage)";

            case "lowestPercentage" ->
                    "MIN(st.percentage)";

            default ->
                    "t.title";
        };
    }

    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long testId,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo) {

        String sql = """
                SELECT COUNT(DISTINCT t.id)

                FROM student_tests st

                JOIN tests t
                    ON st.test_id = t.id

                JOIN courses c
                    ON t.course_id = c.id

                LEFT JOIN students s
                    ON st.student_id = s.id

                LEFT JOIN batches b
                    ON s.batch_id = b.id

                WHERE t.institute_id = :instituteId

                  AND t.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
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

        query.setParameter("instituteId", instituteId);
        query.setParameter("courseId", courseId);
        query.setParameter("batchId", batchId);
        query.setParameter("testId", testId);
        query.setParameter("submittedFrom", submittedFrom);
        query.setParameter("submittedTo", submittedTo);

        Number result =
                (Number) query.getSingleResult();

        return result.longValue();
    }
}