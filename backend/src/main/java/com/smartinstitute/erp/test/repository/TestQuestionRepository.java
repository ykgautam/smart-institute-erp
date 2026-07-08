package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.Test;
import com.smartinstitute.erp.test.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

    List<TestQuestion> findByTestAndActiveTrueOrderByDisplayOrderAsc(Test test);

    Optional<TestQuestion> findByTestAndQuestionAndActiveTrue(
            Test test,
            Question question
    );

    List<TestQuestion> findByQuestionAndActiveTrue(Question question);

    List<TestQuestion> findByTestOrderByDisplayOrderAsc(Test test);

    boolean existsByTestAndQuestion(
            Test test,
            Question question
    );

    Optional<TestQuestion> findByTestAndQuestion(
            Test test,
            Question question
    );
}