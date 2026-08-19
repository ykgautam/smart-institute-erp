package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentFeeAcademicReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentFeeAcademicReportRepositoryImpl
        implements StudentFeeAcademicReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<StudentFeeAcademicReportProjection>
    getStudentFeeAcademicReportWithSorting(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId,
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
                        COALESCE(s.last_name, '')
                    ) AS studentName,

                    c.course_name AS courseName,

                    b.batch_name AS batchName,

                    /* =========================
                       FEE DETAILS
                       ========================= */

                    sf.final_fee AS finalFee,

                    sf.paid_amount AS paidAmount,

                    sf.pending_amount AS pendingAmount,

                    sf.status AS feeStatus,

                    /* =========================
                       ATTENDANCE
                       ========================= */

                    COALESCE(
                        (
                            SELECT ROUND(
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
                                END,
                                2
                            )

                            FROM attendance a

                            WHERE a.student_id = s.id

                              AND a.batch_id = b.id
                        ),
                        0
                    ) AS attendancePercentage,

                    /* =========================
                       TOTAL TESTS
                       ========================= */

                    COALESCE(
                        (
                            SELECT COUNT(
                                DISTINCT st.test_id
                            )

                            FROM student_tests st

                            JOIN tests t
                                ON st.test_id = t.id

                            WHERE st.student_id = s.id

                              AND t.course_id = c.id

                              AND st.status IN
                                  ('SUBMITTED', 'AUTO_SUBMITTED')
                        ),
                        0
                    ) AS totalTests,

                    /* =========================
                       TOTAL ATTEMPTS
                       ========================= */

                    COALESCE(
                        (
                            SELECT COUNT(st.id)

                            FROM student_tests st

                            JOIN tests t
                                ON st.test_id = t.id

                            WHERE st.student_id = s.id

                              AND t.course_id = c.id

                              AND st.status IN
                                  ('SUBMITTED', 'AUTO_SUBMITTED')
                        ),
                        0
                    ) AS totalAttempts,

                    /* =========================
                       PASSED TESTS
                       ========================= */

                    COALESCE(
                        (
                            SELECT COUNT(st.id)

                            FROM student_tests st

                            JOIN tests t
                                ON st.test_id = t.id

                            WHERE st.student_id = s.id

                              AND t.course_id = c.id

                              AND st.status IN
                                  ('SUBMITTED', 'AUTO_SUBMITTED')

                              AND st.passed = true
                        ),
                        0
                    ) AS passedTests,

                    /* =========================
                       FAILED TESTS
                       ========================= */

                    COALESCE(
                        (
                            SELECT COUNT(st.id)

                            FROM student_tests st

                            JOIN tests t
                                ON st.test_id = t.id

                            WHERE st.student_id = s.id

                              AND t.course_id = c.id

                              AND st.status IN
                                  ('SUBMITTED', 'AUTO_SUBMITTED')

                              AND st.passed = false
                        ),
                        0
                    ) AS failedTests,

                    /* =========================
                       AVERAGE TEST %
                       ========================= */

                    COALESCE(
                        (
                            SELECT ROUND(
                                AVG(st.percentage),
                                2
                            )

                            FROM student_tests st

                            JOIN tests t
                                ON st.test_id = t.id

                            WHERE st.student_id = s.id

                              AND t.course_id = c.id

                              AND st.status IN
                                  ('SUBMITTED', 'AUTO_SUBMITTED')
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

                WHERE s.institute_id = :instituteId

                  AND s.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL

                        OR c.id =
                           CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL

                        OR b.id =
                           CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:studentId AS BIGINT) IS NULL

                        OR s.id =
                           CAST(:studentId AS BIGINT)
                  )

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

        query.setFirstResult(
                (int) pageable.getOffset()
        );

        query.setMaxResults(
                pageable.getPageSize()
        );

        @SuppressWarnings("unchecked")
        List<Object[]> rows =
                query.getResultList();

        List<StudentFeeAcademicReportProjection> content =
                mapRows(rows);

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

            case "finalFee" ->
                    "sf.final_fee";

            case "paidAmount" ->
                    "sf.paid_amount";

            case "pendingAmount" ->
                    "sf.pending_amount";

            case "feeStatus" ->
                    "sf.status";

            case "attendancePercentage" ->
                    """
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

                        FROM attendance a

                        WHERE a.student_id = s.id

                          AND a.batch_id = b.id
                    )
                    """;

            case "totalTests" ->
                    """
                    (
                        SELECT COUNT(
                            DISTINCT st.test_id
                        )

                        FROM student_tests st

                        JOIN tests t
                            ON st.test_id = t.id

                        WHERE st.student_id = s.id

                          AND t.course_id = c.id

                          AND st.status IN
                              ('SUBMITTED', 'AUTO_SUBMITTED')
                    )
                    """;

            case "totalAttempts" ->
                    """
                    (
                        SELECT COUNT(st.id)

                        FROM student_tests st

                        JOIN tests t
                            ON st.test_id = t.id

                        WHERE st.student_id = s.id

                          AND t.course_id = c.id

                          AND st.status IN
                              ('SUBMITTED', 'AUTO_SUBMITTED')
                    )
                    """;

            case "passedTests" ->
                    """
                    (
                        SELECT COUNT(st.id)

                        FROM student_tests st

                        JOIN tests t
                            ON st.test_id = t.id

                        WHERE st.student_id = s.id

                          AND t.course_id = c.id

                          AND st.status IN
                              ('SUBMITTED', 'AUTO_SUBMITTED')

                          AND st.passed = true
                    )
                    """;

            case "failedTests" ->
                    """
                    (
                        SELECT COUNT(st.id)

                        FROM student_tests st

                        JOIN tests t
                            ON st.test_id = t.id

                        WHERE st.student_id = s.id

                          AND t.course_id = c.id

                          AND st.status IN
                              ('SUBMITTED', 'AUTO_SUBMITTED')

                          AND st.passed = false
                    )
                    """;

            case "averageTestPercentage" ->
                    """
                    (
                        SELECT COALESCE(
                            AVG(st.percentage),
                            0
                        )

                        FROM student_tests st

                        JOIN tests t
                            ON st.test_id = t.id

                        WHERE st.student_id = s.id

                          AND t.course_id = c.id

                          AND st.status IN
                              ('SUBMITTED', 'AUTO_SUBMITTED')
                    )
                    """;

            default ->
                    "s.first_name";
        };
    }

    private long countReports(
            Long instituteId,
            Long courseId,
            Long batchId,
            Long studentId) {

        String sql = """
                SELECT COUNT(s.id)

                FROM students s

                JOIN batches b
                    ON s.batch_id = b.id

                JOIN courses c
                    ON b.course_id = c.id

                WHERE s.institute_id = :instituteId

                  AND s.active = true

                  AND (
                        CAST(:courseId AS BIGINT) IS NULL

                        OR c.id =
                           CAST(:courseId AS BIGINT)
                  )

                  AND (
                        CAST(:batchId AS BIGINT) IS NULL

                        OR b.id =
                           CAST(:batchId AS BIGINT)
                  )

                  AND (
                        CAST(:studentId AS BIGINT) IS NULL

                        OR s.id =
                           CAST(:studentId AS BIGINT)
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

    private List<StudentFeeAcademicReportProjection>
    mapRows(List<Object[]> rows) {

        List<StudentFeeAcademicReportProjection> result =
                new ArrayList<>();

        for (Object[] row : rows) {

            result.add(
                    new StudentFeeAcademicReportProjectionImpl(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            (java.math.BigDecimal) row[4],
                            (java.math.BigDecimal) row[5],
                            (java.math.BigDecimal) row[6],
                            (String) row[7],
                            toBigDecimal(row[8]),
                            ((Number) row[9]).longValue(),
                            ((Number) row[10]).longValue(),
                            ((Number) row[11]).longValue(),
                            ((Number) row[12]).longValue(),
                            toBigDecimal(row[13])
                    )
            );
        }

        return result;
    }

    private java.math.BigDecimal toBigDecimal(
            Object value) {

        if (value == null) {
            return java.math.BigDecimal.ZERO;
        }

        if (value instanceof java.math.BigDecimal decimal) {
            return decimal;
        }

        return new java.math.BigDecimal(
                value.toString()
        );
    }
}