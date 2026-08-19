package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.TestPerformanceReportProjection;
import com.smartinstitute.erp.report.projection.TestPerformanceReportSummaryProjection;
import com.smartinstitute.erp.test.entity.StudentTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TestPerformanceReportRepository
        extends JpaRepository<StudentTest, Long>,
        TestPerformanceReportRepositoryCustom {

    @Query(
            value = """
        SELECT
            st.id AS studentTestId,

            s.id AS studentId,

            CONCAT(
                s.first_name,
                ' ',
                COALESCE(s.last_name, '')
            ) AS studentName,

            t.id AS testId,

            t.title AS testName,

            c.course_name AS courseName,

            b.batch_name AS batchName,

            st.attempt_no AS attemptNo,

            st.status AS status,

            st.total_marks AS totalMarks,

            st.obtained_marks AS obtainedMarks,

            st.percentage AS percentage,

            st.passed AS passed,

            st.started_at AS startedAt,

            st.submitted_at AS submittedAt

        FROM student_tests st

        JOIN students s
            ON st.student_id = s.id

        JOIN tests t
            ON st.test_id = t.id

        JOIN courses c
            ON t.course_id = c.id

        LEFT JOIN batches b
            ON s.batch_id = b.id

        WHERE s.institute_id = :instituteId

          AND (
                CAST(:testId AS BIGINT) IS NULL
                OR t.id = CAST(:testId AS BIGINT)
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
                CAST(:studentId AS BIGINT) IS NULL
                OR s.id = CAST(:studentId AS BIGINT)
          )

          AND (
                CAST(:status AS VARCHAR) IS NULL
                OR st.status = CAST(:status AS VARCHAR)
          )

          AND (
                CAST(:submittedFrom AS TIMESTAMP) IS NULL
                OR st.submitted_at >= CAST(:submittedFrom AS TIMESTAMP)
          )

          AND (
                CAST(:submittedTo AS TIMESTAMP) IS NULL
                OR st.submitted_at <= CAST(:submittedTo AS TIMESTAMP)
          )

        ORDER BY
            s.first_name ASC,
            s.last_name ASC,
            t.title ASC,
            st.attempt_no ASC
        """,

            countQuery = """
        SELECT COUNT(st.id)

        FROM student_tests st

        JOIN students s
            ON st.student_id = s.id

        JOIN tests t
            ON st.test_id = t.id

        JOIN courses c
            ON t.course_id = c.id

        LEFT JOIN batches b
            ON s.batch_id = b.id

        WHERE s.institute_id = :instituteId

          AND (
                CAST(:testId AS BIGINT) IS NULL
                OR t.id = CAST(:testId AS BIGINT)
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
                CAST(:studentId AS BIGINT) IS NULL
                OR s.id = CAST(:studentId AS BIGINT)
          )

          AND (
                CAST(:status AS VARCHAR) IS NULL
                OR st.status = CAST(:status AS VARCHAR)
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
    Page<TestPerformanceReportProjection> getTestPerformanceReport(
            @Param("instituteId") Long instituteId,
            @Param("testId") Long testId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("studentId") Long studentId,
            @Param("status") String status,
            @Param("submittedFrom") LocalDateTime submittedFrom,
            @Param("submittedTo") LocalDateTime submittedTo,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT

                COUNT(st.id) AS totalAttempts,

                COUNT(
                    CASE
                        WHEN st.status = 'SUBMITTED'
                        THEN 1
                    END
                ) AS submittedAttempts,

                COUNT(
                    CASE
                        WHEN st.status = 'AUTO_SUBMITTED'
                        THEN 1
                    END
                ) AS autoSubmittedAttempts,

                COUNT(
                    CASE
                        WHEN st.status = 'IN_PROGRESS'
                        THEN 1
                    END
                ) AS inProgressAttempts,

                COUNT(
                    CASE
                        WHEN st.passed = true
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

                COALESCE(
                    ROUND(
                        AVG(st.percentage),
                        2
                    ),
                    0
                ) AS averagePercentage,

                COALESCE(
                    SUM(st.total_marks),
                    0
                ) AS totalMarks,

                COALESCE(
                    SUM(st.obtained_marks),
                    0
                ) AS totalObtainedMarks

            FROM student_tests st

            JOIN students s
                ON st.student_id = s.id

            JOIN tests t
                ON st.test_id = t.id

            JOIN courses c
                ON t.course_id = c.id

            LEFT JOIN batches b
                ON s.batch_id = b.id

            WHERE s.institute_id = :instituteId

              AND (
                    CAST(:testId AS BIGINT) IS NULL
                    OR t.id = CAST(:testId AS BIGINT)
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
                    CAST(:studentId AS BIGINT) IS NULL
                    OR s.id = CAST(:studentId AS BIGINT)
              )

              AND (
                    CAST(:status AS VARCHAR) IS NULL
                    OR st.status = CAST(:status AS VARCHAR)
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
    TestPerformanceReportSummaryProjection
    getTestPerformanceReportSummary(
            @Param("instituteId") Long instituteId,
            @Param("testId") Long testId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("studentId") Long studentId,
            @Param("status") String status,
            @Param("submittedFrom") LocalDateTime submittedFrom,
            @Param("submittedTo") LocalDateTime submittedTo
    );
}