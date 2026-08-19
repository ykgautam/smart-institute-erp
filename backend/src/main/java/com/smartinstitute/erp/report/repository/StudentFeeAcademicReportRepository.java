package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentFeeAcademicReportProjection;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentFeeAcademicReportRepository
        extends JpaRepository<Student, Long>,
        StudentFeeAcademicReportRepositoryCustom {


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

                        sf.final_fee AS finalFee,

                        sf.paid_amount AS paidAmount,

                        sf.pending_amount AS pendingAmount,

                        sf.status AS feeStatus,

                        COALESCE(
                            ROUND(
                                (
                                    COUNT(
                                        CASE
                                            WHEN a.status = 'PRESENT'
                                            THEN 1
                                        END
                                    ) * 100.0
                                ) / NULLIF(COUNT(a.id), 0),
                                2
                            ),
                            0
                        ) AS attendancePercentage,

                        COUNT(DISTINCT st.test_id) AS totalTests,

                        COUNT(DISTINCT st.id) AS totalAttempts,

                        COUNT(
                            DISTINCT CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                     AND st.passed = true
                                THEN st.id
                            END
                        ) AS passedTests,

                        COUNT(
                            DISTINCT CASE
                                WHEN st.status IN
                                    ('SUBMITTED', 'AUTO_SUBMITTED')
                                     AND st.passed = false
                                THEN st.id
                            END
                        ) AS failedTests,

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
                        ) AS averageTestPercentage

                    FROM students s

                    JOIN batches b
                        ON s.batch_id = b.id

                    JOIN courses c
                        ON b.course_id = c.id

                    LEFT JOIN student_fees sf
                        ON sf.student_id = s.id
                       AND sf.active = true

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
                        b.batch_name,
                        sf.final_fee,
                        sf.paid_amount,
                        sf.pending_amount,
                        sf.status

                    ORDER BY
                        s.first_name ASC
                    """,

            countQuery = """
                    SELECT COUNT(DISTINCT s.id)

                    FROM students s

                    JOIN batches b
                        ON s.batch_id = b.id

                    JOIN courses c
                        ON b.course_id = c.id

                    LEFT JOIN student_fees sf
                        ON sf.student_id = s.id
                       AND sf.active = true

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
    Page<StudentFeeAcademicReportProjection>
    getStudentFeeAcademicReport(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("studentId") Long studentId,
            Pageable pageable
    );
}