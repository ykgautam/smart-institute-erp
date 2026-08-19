package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.StudentAcademicReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentAcademicReportRepositoryImpl
        implements StudentAcademicReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<StudentAcademicReportProjection>
    getStudentAcademicReportWithSorting(
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
                       TEST COUNT
                       ========================= */

                    COALESCE(
                        (
                            SELECT COUNT(DISTINCT st.test_id)

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

                ORDER BY
                """ + sortColumn + " " + direction;

        Query query =
                entityManager.createNativeQuery(
                        sql,
                        "StudentAcademicReportMapping"
                );

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
        List<StudentAcademicReportProjection> content =
                query.getResultList();

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

            case "attendancePercentage" ->
                    """
                    COALESCE(
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
                        ),
                        0
                    )
                    """;

            case "totalTests" ->
                    """
                    (
                        SELECT COUNT(DISTINCT st.test_id)
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
}