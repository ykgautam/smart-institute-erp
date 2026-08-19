package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentAcademicReportProjection;
import com.smartinstitute.erp.report.projection.StudentAcademicReportSummaryProjection;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentAcademicReportRepository
        extends JpaRepository<Student, Long> {

    @Query(
            value = """
                    SELECT
                        s.id AS studentId,

                        CONCAT(
                            s.first_name,
                            ' ',
                            COALESCE(s.last_name, '')
                        ) AS studentName,

                        c.course_name AS courseName,

                        b.batch_name AS batchName,

                        CAST(
                            CASE
                                WHEN COUNT(DISTINCT a.id) = 0
                                THEN 0
                                ELSE ROUND(
                                    (
                                        COUNT(
                                            DISTINCT CASE
                                                WHEN a.status = 'PRESENT'
                                                THEN a.id
                                            END
                                        ) * 100.0
                                    )
                                    /
                                    COUNT(DISTINCT a.id),
                                    2
                                )
                            END
                            AS NUMERIC
                        ) AS attendancePercentage,

                        COUNT(DISTINCT st.test_id) AS totalTests,

                        COUNT(DISTINCT st.id) AS totalAttempts,

                        COUNT(
                            DISTINCT CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                     AND st.passed = true
                                THEN st.test_id
                            END
                        ) AS passedTests,

                        COUNT(
                            DISTINCT CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                     AND st.passed = false
                                THEN st.test_id
                            END
                        ) AS failedTests,

                        CAST(
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
                            )
                            AS NUMERIC
                        ) AS averageTestPercentage

                    FROM students s

                    LEFT JOIN batches b
                        ON s.batch_id = b.id

                    LEFT JOIN courses c
                        ON b.course_id = c.id

                    LEFT JOIN attendance a
                        ON a.student_id = s.id

                    LEFT JOIN student_tests st
                        ON st.student_id = s.id

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

                    GROUP BY
                        s.id,
                        s.first_name,
                        s.last_name,
                        c.course_name,
                        b.batch_name

                    ORDER BY
                        s.first_name ASC,
                        s.last_name ASC
                    """,

            countQuery = """
                    SELECT COUNT(DISTINCT s.id)

                    FROM students s

                    LEFT JOIN batches b
                        ON s.batch_id = b.id

                    LEFT JOIN courses c
                        ON b.course_id = c.id

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
                    """,

            nativeQuery = true
    )
    Page<StudentAcademicReportProjection> getStudentAcademicReport(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("studentId") Long studentId,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT
                    COUNT(DISTINCT s.id) AS totalStudents,

                    ROUND(
                        COALESCE(
                            AVG(
                                (
                                    SELECT
                                        CASE
                                            WHEN COUNT(a.id) = 0
                                            THEN 0
                                            ELSE
                                                (
                                                    COUNT(
                                                        CASE
                                                            WHEN a.status = 'PRESENT'
                                                            THEN 1
                                                        END
                                                    ) * 100.0
                                                ) / COUNT(a.id)
                                        END
                                    FROM attendance a
                                    WHERE a.student_id = s.id
                                )
                            ),
                            0
                        ),
                        2
                    ) AS averageAttendancePercentage,

                    COALESCE(
                        SUM(
                            (
                                SELECT COUNT(DISTINCT st.test_id)
                                FROM student_tests st
                                WHERE st.student_id = s.id
                            )
                        ),
                        0
                    ) AS totalTests,

                    COALESCE(
                        SUM(
                            (
                                SELECT COUNT(st.id)
                                FROM student_tests st
                                WHERE st.student_id = s.id
                            )
                        ),
                        0
                    ) AS totalAttempts,

                    COALESCE(
                        SUM(
                            (
                                SELECT COUNT(st.id)
                                FROM student_tests st
                                WHERE st.student_id = s.id
                                  AND st.status IN
                                      ('SUBMITTED', 'AUTO_SUBMITTED')
                                  AND st.passed = true
                            )
                        ),
                        0
                    ) AS passedTests,

                    COALESCE(
                        SUM(
                            (
                                SELECT COUNT(st.id)
                                FROM student_tests st
                                WHERE st.student_id = s.id
                                  AND st.status IN
                                      ('SUBMITTED', 'AUTO_SUBMITTED')
                                  AND st.passed = false
                            )
                        ),
                        0
                    ) AS failedTests,

                    ROUND(
                        COALESCE(
                            AVG(
                                (
                                    SELECT
                                        AVG(st.percentage)
                                    FROM student_tests st
                                    WHERE st.student_id = s.id
                                      AND st.status IN
                                          ('SUBMITTED', 'AUTO_SUBMITTED')
                                )
                            ),
                            0
                        ),
                        2
                    ) AS averageTestPercentage

                FROM students s

                LEFT JOIN batches b
                    ON s.batch_id = b.id

                LEFT JOIN courses c
                    ON b.course_id = c.id

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
                """,
            nativeQuery = true
    )
    StudentAcademicReportSummaryProjection
    getStudentAcademicReportSummary(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId
    );
}