package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.dto.response.StudentAttendanceReportSummaryResponse;
import com.smartinstitute.erp.report.projection.StudentAttendanceReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Native SQL implementation for the Student Attendance Report.
 *
 * <p>
 * This repository performs student-wise attendance aggregation.
 * </p>
 *
 * <p>
 * The query starts from students and LEFT JOINs attendance so that
 * students with zero attendance records are still visible in the
 * report with zero attendance values.
 * </p>
 */
@Repository
public class StudentAttendanceReportRepositoryImpl
        implements StudentAttendanceReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches paginated student attendance information.
     *
     * <p>
     * Attendance is calculated from the filtered attendance records:
     * </p>
     *
     * <ul>
     *     <li>Total Classes = number of attendance records</li>
     *     <li>Present Classes = records with PRESENT status</li>
     *     <li>Absent Classes = records with ABSENT status</li>
     *     <li>Attendance % = present / total * 100</li>
     * </ul>
     */
    @Override
    public Page<StudentAttendanceReportProjection>
    getStudentAttendanceReport(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable,
            String sortBy,
            String sortDirection) {

        /*
         * Resolve the requested sort field through a whitelist.
         *
         * Client input is never directly used as a SQL expression.
         */
        String sortColumn =
                resolveSortColumn(sortBy);

        /*
         * Only ASC and DESC are supported.
         * Any other value defaults to ASC.
         */
        String direction =
                "DESC".equalsIgnoreCase(sortDirection)
                        ? "DESC"
                        : "ASC";

        String sql = """
                SELECT

                    /* =========================
                       STUDENT ID
                       ========================= */

                    s.id AS studentId,

                    /* =========================
                       STUDENT NAME
                       ========================= */

                    CONCAT(
                        s.first_name,
                        CASE
                            WHEN s.last_name IS NOT NULL
                                 AND s.last_name <> ''
                            THEN CONCAT(' ', s.last_name)
                            ELSE ''
                        END
                    ) AS studentName,

                    /* =========================
                       COURSE
                       ========================= */

                    c.course_name AS courseName,

                    /* =========================
                       BATCH
                       ========================= */

                    b.batch_name AS batchName,

                    /* =========================
                       TOTAL CLASSES
                       ========================= */

                    COUNT(a.id) AS totalClasses,

                    /* =========================
                       PRESENT CLASSES
                       ========================= */

                    COUNT(
                        CASE
                            WHEN a.status = 'PRESENT'
                            THEN 1
                        END
                    ) AS presentClasses,

                    /* =========================
                       ABSENT CLASSES
                       ========================= */

                    COUNT(
                        CASE
                            WHEN a.status = 'ABSENT'
                            THEN 1
                        END
                    ) AS absentClasses,

                    /* =========================
                       ATTENDANCE PERCENTAGE
                       ========================= */

                    COALESCE(
                        ROUND(
                            (
                                COUNT(
                                    CASE
                                        WHEN a.status = 'PRESENT'
                                        THEN 1
                                    END
                                ) * 100.0
                            )
                            /
                            NULLIF(
                                COUNT(a.id),
                                0
                            ),
                            2
                        ),
                        0
                    ) AS attendancePercentage

                FROM students s

                /* =========================
                   STUDENT → BATCH
                   ========================= */

                LEFT JOIN batches b
                    ON s.batch_id = b.id
                    AND b.institute_id = :instituteId
                    AND b.active = true

                /* =========================
                   BATCH → COURSE
                   ========================= */

                LEFT JOIN courses c
                    ON b.course_id = c.id
                    AND c.institute_id = :instituteId
                    AND c.active = true

                /* =========================
                   STUDENT → ATTENDANCE
                   ========================= */

                LEFT JOIN attendance a
                    ON a.student_id = s.id
                    AND a.batch_id = b.id

                    /* =========================
                       DATE FROM FILTER
                       ========================= */

                    AND (
                        CAST(:dateFrom AS DATE) IS NULL
                        OR a.attendance_date >=
                           CAST(:dateFrom AS DATE)
                    )

                    /* =========================
                       DATE TO FILTER
                       ========================= */

                    AND (
                        CAST(:dateTo AS DATE) IS NULL
                        OR a.attendance_date <=
                           CAST(:dateTo AS DATE)
                    )

                WHERE s.institute_id = :instituteId

                  /* Only active students are reported. */
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

                GROUP BY
                    s.id,
                    s.first_name,
                    s.last_name,
                    c.course_name,
                    b.batch_name

                ORDER BY
                """ + sortColumn + " " + direction;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("instituteId", instituteId);

        query.setParameter("courseId", courseId);

        query.setParameter("batchId", batchId);

        query.setParameter("studentId", studentId);

        query.setParameter("dateFrom", dateFrom);

        query.setParameter("dateTo", dateTo);

        /*
         * Apply Spring Data pagination to the native query.
         */
        query.setFirstResult((int) pageable.getOffset());

        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked") List<Object[]> rows = query.getResultList();

        /*
         * Convert native SQL rows into repository projections.
         */
        List<StudentAttendanceReportProjection> content =
                rows.stream()
                        .map(this::mapRow)
                        .toList();

        /*
         * Count query is executed separately because the main query
         * contains GROUP BY and pagination.
         */
        long total =
                countReports(
                        instituteId,
                        courseId,
                        batchId,
                        studentId
                );

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    /**
     * Resolves API sort fields to trusted SQL expressions.
     *
     * <p>
     * This whitelist prevents arbitrary SQL expressions from being
     * supplied through the sortBy request parameter.
     * </p>
     */
    private String resolveSortColumn(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "s.first_name";
        }

        return switch (sortBy) {

            case "studentName" ->
                    "s.first_name";

            case "courseName" ->
                    "c.course_name";

            case "batchName" ->
                    "b.batch_name";

            case "totalClasses" ->
                    "COUNT(a.id)";

            case "presentClasses" ->
                    """
                    COUNT(
                        CASE
                            WHEN a.status = 'PRESENT'
                            THEN 1
                        END
                    )
                    """;

            case "absentClasses" ->
                    """
                    COUNT(
                        CASE
                            WHEN a.status = 'ABSENT'
                            THEN 1
                        END
                    )
                    """;

            case "attendancePercentage" ->
                    """
                    (
                        COUNT(
                            CASE
                                WHEN a.status = 'PRESENT'
                                THEN 1
                            END
                        ) * 100.0
                    )
                    /
                    NULLIF(
                        COUNT(a.id),
                        0
                    )
                    """;

            default ->
                    "s.first_name";
        };
    }

    /**
     * Counts the number of students matching the report filters.
     *
     * <p>
     * This query intentionally counts students rather than attendance
     * records because each report row represents one student.
     * </p>
     */
    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId) {

        String sql = """
                SELECT COUNT(*)

                FROM students s

                LEFT JOIN batches b
                    ON s.batch_id = b.id
                    AND b.institute_id = :instituteId
                    AND b.active = true

                LEFT JOIN courses c
                    ON b.course_id = c.id
                    AND c.institute_id = :instituteId
                    AND c.active = true

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

        Number result =
                (Number) query.getSingleResult();

        return result.longValue();
    }

    /**
     * Converts one native SQL row into the attendance projection.
     */
    private StudentAttendanceReportProjection mapRow(
            Object[] row) {

        return new StudentAttendanceReportProjection() {

            @Override
            public Long getStudentId() {
                return toLong(row[0]);
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
            public Long getTotalClasses() {
                return toLong(row[4]);
            }

            @Override
            public Long getPresentClasses() {
                return toLong(row[5]);
            }

            @Override
            public Long getAbsentClasses() {
                return toLong(row[6]);
            }

            @Override
            public BigDecimal getAttendancePercentage() {
                return toBigDecimal(row[7]);
            }
        };
    }

    /**
     * Safely converts a native SQL numeric value into Long.
     */
    private Long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    /**
     * Safely converts a native SQL numeric value into BigDecimal.
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

    /**
     * Calculates aggregate attendance statistics for the complete
     * filtered attendance dataset.
     *
     * <p>
     * This query intentionally does not use pagination. It is used
     * to populate the summary section of the API response.
     * </p>
     *
     * <p>
     * The filters must remain consistent with the main paginated
     * attendance query so that the summary represents exactly the
     * same dataset.
     * </p>
     */
    @Override
    public StudentAttendanceReportSummaryResponse getSummary(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
            LocalDate attendanceDateFrom,
            LocalDate attendanceDateTo) {

        String sql = """
            SELECT

                /* =========================
                   TOTAL STUDENTS
                   =========================
                   Counts unique students having
                   attendance records in the
                   filtered dataset.
                   ========================= */

                COUNT(
                    DISTINCT s.id
                ) AS totalStudents,

                /* =========================
                   AVERAGE ATTENDANCE %
                   =========================
                   Calculates attendance percentage
                   for each student first and then
                   calculates the average across
                   those students.
                   ========================= */

                COALESCE(
                    ROUND(
                        AVG(
                            CASE
                                WHEN student_attendance.total_classes > 0
                                THEN
                                    (
                                        student_attendance.present_classes
                                        * 100.0
                                    )
                                    /
                                    student_attendance.total_classes
                            END
                        ),
                        2
                    ),
                    0
                ) AS averageAttendancePercentage,

                /* =========================
                   TOTAL CLASSES
                   ========================= */

                COUNT(
                    a.id
                ) AS totalClasses,

                /* =========================
                   TOTAL PRESENT
                   ========================= */

                COUNT(
                    CASE
                        WHEN a.status = 'PRESENT'
                        THEN 1
                    END
                ) AS totalPresentClasses,

                /* =========================
                   TOTAL ABSENT
                   ========================= */

                COUNT(
                    CASE
                        WHEN a.status = 'ABSENT'
                        THEN 1
                    END
                ) AS totalAbsentClasses

            FROM attendance a

            /* =========================
               STUDENT
               ========================= */

            JOIN students s
                ON a.student_id = s.id

            /* =========================
               BATCH
               ========================= */

            JOIN batches b
                ON a.batch_id = b.id

            /* =========================
               COURSE
               ========================= */

            JOIN courses c
                ON b.course_id = c.id

            /* =========================
               STUDENT-LEVEL AGGREGATION
               =========================
               Calculates total and present
               classes per student.
               ========================= */

            JOIN (
                SELECT
                    a2.student_id,

                    COUNT(a2.id)
                        AS total_classes,

                    COUNT(
                        CASE
                            WHEN a2.status = 'PRESENT'
                            THEN 1
                        END
                    ) AS present_classes

                FROM attendance a2

                JOIN students s2
                    ON a2.student_id = s2.id

                JOIN batches b2
                    ON a2.batch_id = b2.id

                JOIN courses c2
                    ON b2.course_id = c2.id

                WHERE s2.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL
                        OR c2.id = CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL
                        OR b2.id = CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:studentId AS BIGINT) IS NULL
                        OR s2.id = CAST(:studentId AS BIGINT)
                  )

                  AND (
                        CAST(:attendanceDateFrom AS DATE) IS NULL
                        OR a2.attendance_date >=
                           CAST(:attendanceDateFrom AS DATE)
                  )

                  AND (
                        CAST(:attendanceDateTo AS DATE) IS NULL
                        OR a2.attendance_date <=
                           CAST(:attendanceDateTo AS DATE)
                  )

                GROUP BY
                    a2.student_id

            ) student_attendance
                ON student_attendance.student_id = s.id

            WHERE s.active = true

              /* =========================
                 INSTITUTE FILTER
                 ========================= */

              AND c.institute_id = :instituteId

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
                 DATE FROM FILTER
                 ========================= */

              AND (
                    CAST(:attendanceDateFrom AS DATE) IS NULL
                    OR a.attendance_date >=
                       CAST(:attendanceDateFrom AS DATE)
              )

              /* =========================
                 DATE TO FILTER
                 ========================= */

              AND (
                    CAST(:attendanceDateTo AS DATE) IS NULL
                    OR a.attendance_date <=
                       CAST(:attendanceDateTo AS DATE)
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
                "attendanceDateFrom",
                attendanceDateFrom
        );

        query.setParameter(
                "attendanceDateTo",
                attendanceDateTo
        );

        Object[] row =
                (Object[]) query.getSingleResult();

        return new StudentAttendanceReportSummaryResponse(
                toLong(row[0]),
                toBigDecimal(row[1]),
                toLong(row[2]),
                toLong(row[3]),
                toLong(row[4])
        );
    }
}