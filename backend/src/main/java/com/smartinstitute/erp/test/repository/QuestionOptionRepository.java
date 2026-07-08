package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    List<QuestionOption> findByQuestionAndActiveTrueOrderByDisplayOrderAsc(
            Question question
    );

}