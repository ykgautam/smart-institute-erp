package com.smartinstitute.erp.report.repository;

import com.smartinstitute.erp.report.projection.DashboardSummaryProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepositoryImpl
        implements DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardSummaryProjection getDashboardSummary(
            Long instituteId) {

        String sql = """
                SELECT

                    /* =========================
                       STUDENTS
                       ========================= */

                    (
                        SELECT COUNT(*)
                        FROM students s
                        WHERE s.institute_id = :instituteId
                    ) AS totalStudents,

                    (
                        SELECT COUNT(*)
                        FROM students s
                        WHERE s.institute_id = :instituteId
                          AND s.active = true
                    ) AS activeStudents,

                    /* =========================
                       COURSES
                       ========================= */

                    (
                        SELECT COUNT(*)
                        FROM courses c
                        WHERE c.institute_id = :instituteId
                    ) AS totalCourses,

                    (
                        SELECT COUNT(*)
                        FROM courses c
                        WHERE c.institute_id = :instituteId
                          AND c.active = true
                    ) AS activeCourses,

                    /* =========================
                       BATCHES
                       ========================= */

                    (
                        SELECT COUNT(*)
                        FROM batches b
                        WHERE b.institute_id = :instituteId
                    ) AS totalBatches,

                    (
                        SELECT COUNT(*)
                        FROM batches b
                        WHERE b.institute_id = :instituteId
                          AND b.active = true
                    ) AS activeBatches
                """;

        Query query =
                entityManager.createNativeQuery(sql);

        query.setParameter(
                "instituteId",
                instituteId
        );

        Object[] row =
                (Object[]) query.getSingleResult();

        return new DashboardSummaryProjection() {

            @Override
            public Long getTotalStudents() {
                return toLong(row[0]);
            }

            @Override
            public Long getActiveStudents() {
                return toLong(row[1]);
            }

            @Override
            public Long getTotalCourses() {
                return toLong(row[2]);
            }

            @Override
            public Long getActiveCourses() {
                return toLong(row[3]);
            }

            @Override
            public Long getTotalBatches() {
                return toLong(row[4]);
            }

            @Override
            public Long getActiveBatches() {
                return toLong(row[5]);
            }
        };
    }

    /**
     * Safely converts a native SQL numeric result into Long.
     */
    private Long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }
}