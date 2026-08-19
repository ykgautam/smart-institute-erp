package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.TestWisePerformanceReportProjection;
import com.smartinstitute.erp.test.entity.StudentTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TestWisePerformanceReportRepository
        extends JpaRepository<StudentTest, Long>,
        TestWisePerformanceReportRepositoryCustom {

    @Query(
            value = """
                    SELECT
                        t.id AS testId,

                        t.title AS testName,

                        c.course_name AS courseName,

                        tp.name AS topicName,

                        COUNT(st.id) AS totalAttempts,

                        COUNT(
                            CASE
                                WHEN st.status IN ('SUBMITTED', 'AUTO_SUBMITTED')
                                THEN 1
                            END
                        ) AS submittedAttempts,

                        COUNT(
                            CASE
                                WHEN st.status IN ('SUBMITTED', 'AUTO_SUBMITTED')
                                     AND st.passed = true
                                THEN 1
                            END
                        ) AS passedAttempts,

                        COUNT(
                            CASE
                                WHEN st.status IN ('SUBMITTED', 'AUTO_SUBMITTED')
                                     AND st.passed = false
                                THEN 1
                            END
                        ) AS failedAttempts,

                        ROUND(
                            AVG(
                                CASE
                                    WHEN st.status IN ('SUBMITTED', 'AUTO_SUBMITTED')
                                    THEN st.percentage
                                END
                            ),
                            2
                        ) AS averagePercentage,

                        ROUND(
                            MAX(
                                CASE
                                    WHEN st.status IN ('SUBMITTED', 'AUTO_SUBMITTED')
                                    THEN st.percentage
                                END
                            ),
                            2
                        ) AS highestPercentage,

                        ROUND(
                            MIN(
                                CASE
                                    WHEN st.status IN ('SUBMITTED', 'AUTO_SUBMITTED')
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
                            OR st.submitted_at >= CAST(:submittedFrom AS TIMESTAMP)
                      )

                      AND (
                            CAST(:submittedTo AS TIMESTAMP) IS NULL
                            OR st.submitted_at <= CAST(:submittedTo AS TIMESTAMP)
                      )

                    GROUP BY
                        t.id,
                        t.title,
                        c.course_name,
                        tp.name

                    ORDER BY
                        t.title ASC
                    """,

            countQuery = """
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
                            OR st.submitted_at >= CAST(:submittedFrom AS TIMESTAMP)
                      )

                      AND (
                            CAST(:submittedTo AS TIMESTAMP) IS NULL
                            OR st.submitted_at <= CAST(:submittedTo AS TIMESTAMP)
                      )
                    """,

            nativeQuery = true
    )
    Page<TestWisePerformanceReportProjection> getTestWisePerformanceReport(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("testId") Long testId,
            @Param("submittedFrom") LocalDateTime submittedFrom,
            @Param("submittedTo") LocalDateTime submittedTo,
            Pageable pageable
    );
}