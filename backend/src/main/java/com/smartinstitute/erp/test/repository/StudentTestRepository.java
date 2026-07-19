package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.dashboard.projection.CoursePerformanceProjection;
import com.smartinstitute.erp.dashboard.projection.MonthlyPerformanceProjection;
import com.smartinstitute.erp.dashboard.projection.TopicPerformanceProjection;
import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.test.entity.StudentTest;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentTestRepository extends
        JpaRepository<StudentTest, Long>,
        JpaSpecificationExecutor<StudentTest> {

    Optional<StudentTest> findByIdAndStudent(
            Long id,
            Student student
    );

    List<StudentTest> findByStudentOrderByStartedAtDesc(
            Student student
    );

    List<StudentTest> findByStudentAndStatusOrderByStartedAtDesc(
            Student student,
            StudentTestStatus status
    );

    Optional<StudentTest> findTopByStudentAndTestOrderByAttemptNoDesc(
            Student student,
            Test test
    );

    long countByStudentAndTest(
            Student student,
            Test test
    );

    boolean existsByStudentAndTestAndStatus(
            Student student,
            Test test,
            StudentTestStatus status
    );

    long countByStudent(Student student);

    long countByStudentAndStatus(
            Student student,
            StudentTestStatus status
    );

    List<StudentTest> findTop5ByStudentAndStatusOrderBySubmittedAtDesc(
            Student student,
            StudentTestStatus status
    );

    @Query("""
            SELECT COALESCE(AVG(st.percentage), 0)
            FROM StudentTest st
            WHERE st.student = :student
            AND st.status = com.smartinstitute.erp.common.enums.test.StudentTestStatus.SUBMITTED
            """)
    Double getAveragePercentage(
            @Param("student") Student student
    );

    long countByStudentAndPassedTrue(Student student);

    long countByStudentAndPassedFalse(Student student);

    long countByStatus(TestStatus status);

    @Query("""
            SELECT COALESCE(MAX(st.percentage),0)
            FROM StudentTest st
            WHERE st.student = :student
            AND st.status = com.smartinstitute.erp.common.enums.test.StudentTestStatus.SUBMITTED
            """)
    Double getHighestPercentage(
            @Param("student") Student student
    );

    @Query("""
            SELECT COALESCE(MIN(st.percentage),0)
            FROM StudentTest st
            WHERE st.student = :student
            AND st.status = com.smartinstitute.erp.common.enums.test.StudentTestStatus.SUBMITTED
            """)
    Double getLowestPercentage(
            @Param("student") Student student
    );

    Page<StudentTest> findByStudentOrderBySubmittedAtDesc(
            Student student,
            Pageable pageable
    );

    @Query(value = """
            SELECT
                EXTRACT(YEAR FROM submitted_at) AS year,
                EXTRACT(MONTH FROM submitted_at) AS month,
                AVG(percentage) AS averagePercentage,
                COUNT(*) AS testsAttempted
            FROM student_tests
            WHERE student_id = :#{#student.id}
              AND status = 'SUBMITTED'
            GROUP BY
                EXTRACT(YEAR FROM submitted_at),
                EXTRACT(MONTH FROM submitted_at)
            ORDER BY
                EXTRACT(YEAR FROM submitted_at),
                EXTRACT(MONTH FROM submitted_at)
            """,
            nativeQuery = true)
    List<MonthlyPerformanceProjection> getMonthlyPerformance(
            Student student
    );

    @Query(value = """
            SELECT
            
                c.id AS courseId,
            
                c.course_name AS courseName,
            
                COUNT(st.id) AS testsAttempted,
            
                SUM(
                    CASE
                        WHEN st.passed = TRUE THEN 1
                        ELSE 0
                    END
                ) AS testsPassed,
            
                AVG(st.percentage) AS averagePercentage
            
            FROM student_tests st
            
            INNER JOIN tests t
                    ON st.test_id = t.id
            
            INNER JOIN topics tp
                    ON t.topic_id = tp.id
            
            INNER JOIN courses c
                    ON tp.course_id = c.id
            
            WHERE st.student_id = :#{#student.id}
            
              AND st.status = 'SUBMITTED'
            
            GROUP BY
                c.id,
                c.course_name
            
            ORDER BY
                c.course_name
            """,
            nativeQuery = true)
    List<CoursePerformanceProjection> getCoursePerformance(
            Student student
    );

    @Query(value = """
            SELECT
            
                tp.id AS topicId,
            
                tp.name AS topicName,
            
                AVG(st.percentage) AS averagePercentage,
            
                COUNT(st.id) AS testsAttempted
            
            FROM student_tests st
            
            INNER JOIN tests t
            ON st.test_id = t.id
            
            INNER JOIN topics tp
            ON t.topic_id = tp.id
            
            WHERE st.student_id = :#{#student.id}
            
            AND st.status='SUBMITTED'
            
            GROUP BY
                tp.id,
                tp.name
            
            ORDER BY
                averagePercentage DESC
            
            LIMIT 5
            """,
            nativeQuery = true)
    List<TopicPerformanceProjection> getStrongTopics(
            Student student
    );

    @Query(value = """
            SELECT
            
                tp.id AS topicId,
            
                tp.name AS topicName,
            
                AVG(st.percentage) AS averagePercentage,
            
                COUNT(st.id) AS testsAttempted
            
            FROM student_tests st
            
            INNER JOIN tests t
            ON st.test_id = t.id
            
            INNER JOIN topics tp
            ON t.topic_id = tp.id
            
            WHERE st.student_id = :#{#student.id}
            
            AND st.status='SUBMITTED'
            
            GROUP BY
                tp.id,
                tp.name
            
            ORDER BY
                averagePercentage ASC
            
            LIMIT 5
            """,
            nativeQuery = true)
    List<TopicPerformanceProjection> getWeakTopics(
            Student student
    );
}