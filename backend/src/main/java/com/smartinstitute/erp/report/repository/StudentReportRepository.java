package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentReportProjection;
import com.smartinstitute.erp.report.projection.StudentReportSummaryProjection;
import com.smartinstitute.erp.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudentReportRepository
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

                    s.email AS email,

                    c.course_name AS courseName,

                    b.batch_name AS batchName,

                    s.status AS status

                FROM students s

                LEFT JOIN batches b
                       ON s.batch_id = b.id

                LEFT JOIN courses c
                       ON b.course_id = c.id

                WHERE s.institute_id = :instituteId

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:status AS VARCHAR) IS NULL
                        OR s.status = CAST(:status AS VARCHAR)
                  )

                  AND (
                        CAST(:admissionDateFrom AS DATE) IS NULL
                        OR s.admission_date >= CAST(:admissionDateFrom AS DATE)
                  )

                  AND (
                        CAST(:admissionDateTo AS DATE) IS NULL
                        OR s.admission_date <= CAST(:admissionDateTo AS DATE)
                  )

                ORDER BY
                    s.first_name ASC,
                    s.last_name ASC
                """,

            countQuery = """
                SELECT COUNT(*)
                FROM students s

                LEFT JOIN batches b
                       ON s.batch_id = b.id

                LEFT JOIN courses c
                       ON b.course_id = c.id

                WHERE s.institute_id = :instituteId

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:status AS VARCHAR) IS NULL
                        OR s.status = CAST(:status AS VARCHAR)
                  )

                  AND (
                        CAST(:admissionDateFrom AS DATE) IS NULL
                        OR s.admission_date >= CAST(:admissionDateFrom AS DATE)
                  )

                  AND (
                        CAST(:admissionDateTo AS DATE) IS NULL
                        OR s.admission_date <= CAST(:admissionDateTo AS DATE)
                  )
                """,
            nativeQuery = true
    )
    Page<StudentReportProjection> getStudentReport(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("status") String status,
            @Param("admissionDateFrom") LocalDate admissionDateFrom,
            @Param("admissionDateTo") LocalDate admissionDateTo,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT
                    s.status AS status,
                    COUNT(*) AS count

                FROM students s

                LEFT JOIN batches b
                       ON s.batch_id = b.id

                LEFT JOIN courses c
                       ON b.course_id = c.id

                WHERE s.institute_id = :instituteId

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b.id = CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:status AS VARCHAR) IS NULL
                        OR s.status = CAST(:status AS VARCHAR)
                  )

                  AND (
                        CAST(:admissionDateFrom AS DATE) IS NULL
                        OR s.admission_date >= CAST(:admissionDateFrom AS DATE)
                  )

                  AND (
                        CAST(:admissionDateTo AS DATE) IS NULL
                        OR s.admission_date <= CAST(:admissionDateTo AS DATE)
                  )

                GROUP BY s.status

                ORDER BY s.status
                """,
            nativeQuery = true
    )
    List<StudentReportSummaryProjection> getStudentReportSummary(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("status") String status,
            @Param("admissionDateFrom") LocalDate admissionDateFrom,
            @Param("admissionDateTo") LocalDate admissionDateTo
    );
}