package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.student.entity.Student;
import com.smartinstitute.erp.test.entity.StudentTest;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentTestRepository extends JpaRepository<StudentTest, Long> {

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

}