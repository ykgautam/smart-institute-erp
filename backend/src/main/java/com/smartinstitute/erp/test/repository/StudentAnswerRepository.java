package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.StudentAnswer;
import com.smartinstitute.erp.test.entity.StudentTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByStudentTestOrderByQuestion_Id(
            StudentTest studentTest
    );

    Optional<StudentAnswer> findByStudentTestAndQuestion(
            StudentTest studentTest,
            Question question
    );

    boolean existsByStudentTestAndQuestion(
            StudentTest studentTest,
            Question question
    );

    long countByStudentTest(
            StudentTest studentTest
    );

    long countByStudentTestAndCorrectTrue(
            StudentTest studentTest
    );

}