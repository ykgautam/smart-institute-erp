package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.fee.entity.StudentFee;
import com.smartinstitute.erp.report.projection.FeeCollectionReportProjection;
import com.smartinstitute.erp.report.projection.FeeCollectionReportSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FeeCollectionReportRepository
        extends JpaRepository<StudentFee, Long> {

    @Query(value = """
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

            sf.status AS status

        FROM student_fees sf

        JOIN students s
            ON sf.student_id = s.id

        JOIN fee_structures fs
            ON sf.fee_structure_id = fs.id

        JOIN courses c
            ON fs.course_id = c.id

        LEFT JOIN batches b
            ON s.batch_id = b.id

        WHERE sf.institute_id = :instituteId
          AND sf.active = true

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
                OR sf.status = CAST(:status AS VARCHAR)
          )

          AND (
                CAST(:feeDueDateFrom AS DATE) IS NULL
                OR sf.fee_due_date >= CAST(:feeDueDateFrom AS DATE)
          )

          AND (
                CAST(:feeDueDateTo AS DATE) IS NULL
                OR sf.fee_due_date <= CAST(:feeDueDateTo AS DATE)
          )
            ORDER BY
                s.first_name,
                s.last_name
        """,
            countQuery = """
        SELECT COUNT(*)

        FROM student_fees sf

        JOIN students s
            ON sf.student_id = s.id

        JOIN fee_structures fs
            ON sf.fee_structure_id = fs.id

        JOIN courses c
            ON fs.course_id = c.id

        LEFT JOIN batches b
            ON s.batch_id = b.id

        WHERE sf.institute_id = :instituteId
          AND sf.active = true

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
                OR sf.status = CAST(:status AS VARCHAR)
          )

          AND (
                CAST(:feeDueDateFrom AS DATE) IS NULL
                OR sf.fee_due_date >= CAST(:feeDueDateFrom AS DATE)
          )

          AND (
                CAST(:feeDueDateTo AS DATE) IS NULL
                OR sf.fee_due_date <= CAST(:feeDueDateTo AS DATE)
          )
        """,
            nativeQuery = true)
    Page<FeeCollectionReportProjection> getFeeCollectionReport(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("status") String status,
            @Param("feeDueDateFrom") LocalDate feeDueDateFrom,
            @Param("feeDueDateTo") LocalDate feeDueDateTo,
            Pageable pageable
    );


    @Query(value = """
        SELECT

            COUNT(sf.id) AS totalStudents,

            COALESCE(
                SUM(sf.final_fee),
                0
            ) AS totalFee,

            COALESCE(
                SUM(sf.paid_amount),
                0
            ) AS totalPaid,

            COALESCE(
                SUM(sf.pending_amount),
                0
            ) AS totalPending,

            COUNT(
                CASE
                    WHEN sf.status = 'PENDING'
                    THEN 1
                END
            ) AS pendingStudents,

            COUNT(
                CASE
                    WHEN sf.status = 'PARTIALLY_PAID'
                    THEN 1
                END
            ) AS partiallyPaidStudents,

            COUNT(
                CASE
                    WHEN sf.status = 'PAID'
                    THEN 1
                END
            ) AS paidStudents

        FROM student_fees sf

        JOIN students s
            ON sf.student_id = s.id

        JOIN fee_structures fs
            ON sf.fee_structure_id = fs.id

        JOIN courses c
            ON fs.course_id = c.id

        LEFT JOIN batches b
            ON s.batch_id = b.id

        WHERE sf.institute_id = :instituteId
          AND sf.active = true

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
                OR sf.status = CAST(:status AS VARCHAR)
          )

          AND (
                CAST(:feeDueDateFrom AS DATE) IS NULL
                OR sf.fee_due_date >= CAST(:feeDueDateFrom AS DATE)
          )

          AND (
                CAST(:feeDueDateTo AS DATE) IS NULL
                OR sf.fee_due_date <= CAST(:feeDueDateTo AS DATE)
          )
        """,
            nativeQuery = true)
    FeeCollectionReportSummaryProjection
    getFeeCollectionReportSummary(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("status") String status,
            @Param("feeDueDateFrom") LocalDate feeDueDateFrom,
            @Param("feeDueDateTo") LocalDate feeDueDateTo
    );
}