package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.attendance.entity.Attendance;
import com.smartinstitute.erp.report.projection.AttendanceReportProjection;
import com.smartinstitute.erp.report.projection.AttendanceReportSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AttendanceReportRepository
        extends JpaRepository<Attendance, Long> {

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

                        COUNT(a.id) AS totalClasses,

                        COUNT(
                            CASE
                                WHEN a.status = 'PRESENT'
                                THEN 1
                            END
                        ) AS presentClasses,

                        COUNT(
                            CASE
                                WHEN a.status = 'ABSENT'
                                THEN 1
                            END
                        ) AS absentClasses,

                        CASE
                            WHEN COUNT(a.id) = 0
                            THEN 0
                            ELSE ROUND(
                                (
                                    COUNT(
                                        CASE
                                            WHEN a.status = 'PRESENT'
                                            THEN 1
                                        END
                                    ) * 100.0
                                ) / COUNT(a.id),
                                2
                            )
                        END AS attendancePercentage

                    FROM attendance a

                    JOIN students s
                        ON a.student_id = s.id

                    JOIN batches b
                        ON a.batch_id = b.id

                    JOIN courses c
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
                            CAST(:attendanceDateFrom AS DATE) IS NULL
                            OR a.attendance_date >=
                               CAST(:attendanceDateFrom AS DATE)
                      )

                      AND (
                            CAST(:attendanceDateTo AS DATE) IS NULL
                            OR a.attendance_date <=
                               CAST(:attendanceDateTo AS DATE)
                      )

                    GROUP BY
                        s.id,
                        s.first_name,
                        s.last_name,
                        c.course_name,
                        b.batch_name
                    """,

            countQuery = """
                    SELECT COUNT(*)
                    FROM (
                        SELECT
                            s.id
                        FROM attendance a

                        JOIN students s
                            ON a.student_id = s.id

                        JOIN batches b
                            ON a.batch_id = b.id

                        JOIN courses c
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
                                CAST(:attendanceDateFrom AS DATE) IS NULL
                                OR a.attendance_date >=
                                   CAST(:attendanceDateFrom AS DATE)
                          )

                          AND (
                                CAST(:attendanceDateTo AS DATE) IS NULL
                                OR a.attendance_date <=
                                   CAST(:attendanceDateTo AS DATE)
                          )

                        GROUP BY
                            s.id,
                            s.first_name,
                            s.last_name,
                            c.course_name,
                            b.batch_name
                    ) AS attendance_groups
                    """,

            nativeQuery = true
    )
    Page<AttendanceReportProjection> getAttendanceReport(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("attendanceDateFrom") LocalDate attendanceDateFrom,
            @Param("attendanceDateTo") LocalDate attendanceDateTo,
            Pageable pageable
    );

    @Query(value = """
        SELECT

            COUNT(*) AS totalStudents,

            COALESCE(
                SUM(attendance_data.total_classes),
                0
            ) AS totalClasses,

            COALESCE(
                SUM(attendance_data.present_classes),
                0
            ) AS totalPresent,

            COALESCE(
                SUM(attendance_data.absent_classes),
                0
            ) AS totalAbsent,

            COALESCE(
                ROUND(
                    AVG(
                        attendance_data.attendance_percentage
                    ),
                    2
                ),
                0
            ) AS averageAttendancePercentage,

            COUNT(
                CASE
                    WHEN attendance_data.attendance_percentage < 75
                    THEN 1
                END
            ) AS lowAttendanceStudents

        FROM (
            SELECT

                s.id AS student_id,

                COUNT(a.id) AS total_classes,

                COUNT(
                    CASE
                        WHEN a.status = 'PRESENT'
                        THEN 1
                    END
                ) AS present_classes,

                COUNT(
                    CASE
                        WHEN a.status = 'ABSENT'
                        THEN 1
                    END
                ) AS absent_classes,

                CASE
                    WHEN COUNT(a.id) = 0
                    THEN 0
                    ELSE ROUND(
                        (
                            COUNT(
                                CASE
                                    WHEN a.status = 'PRESENT'
                                    THEN 1
                                END
                            ) * 100.0
                        ) / COUNT(a.id),
                        2
                    )
                END AS attendance_percentage

            FROM attendance a

            JOIN students s
                ON a.student_id = s.id

            JOIN batches b
                ON a.batch_id = b.id

            JOIN courses c
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
                    CAST(:attendanceDateFrom AS DATE) IS NULL
                    OR a.attendance_date >=
                       CAST(:attendanceDateFrom AS DATE)
              )

              AND (
                    CAST(:attendanceDateTo AS DATE) IS NULL
                    OR a.attendance_date <=
                       CAST(:attendanceDateTo AS DATE)
              )

            GROUP BY
                s.id

        ) AS attendance_data
        """,
            nativeQuery = true)
    AttendanceReportSummaryProjection
    getAttendanceReportSummary(
            @Param("instituteId") Long instituteId,
            @Param("courseId") Long courseId,
            @Param("batchId") Long batchId,
            @Param("attendanceDateFrom") LocalDate attendanceDateFrom,
            @Param("attendanceDateTo") LocalDate attendanceDateTo
    );
}