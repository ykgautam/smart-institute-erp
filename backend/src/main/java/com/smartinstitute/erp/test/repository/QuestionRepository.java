package com.smartinstitute.erp.test.repository;

import com.smartinstitute.erp.common.enums.test.QuestionDifficulty;
import com.smartinstitute.erp.common.enums.test.QuestionType;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.test.entity.Question;
import com.smartinstitute.erp.test.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

    List<Question> findByInstituteAndActiveTrue(
            Institute institute
    );

    List<Question> findByCourseAndInstituteAndActiveTrue(
            Course course,
            Institute institute
    );

    List<Question> findByTopicAndInstituteAndActiveTrue(
            Topic topic,
            Institute institute
    );

    List<Question> findByCourseAndTopicAndInstituteAndActiveTrue(
            Course course,
            Topic topic,
            Institute institute
    );

    boolean existsByInstituteAndQuestionTextAndActiveTrue(
            Institute institute,
            String questionText
    );

    boolean existsByInstituteAndQuestionTextAndIdNotAndActiveTrue(
            Institute institute,
            String questionText,
            Long id
    );

    List<Question> findByInstituteAndActiveTrueOrderByDisplayOrderAscQuestionTextAsc(
            Institute institute
    );

    List<Question> findByCourseAndInstituteAndActiveTrueOrderByDisplayOrderAscQuestionTextAsc(
            Course course,
            Institute institute
    );

    List<Question> findByTopicAndInstituteAndActiveTrueOrderByDisplayOrderAscQuestionTextAsc(
            Topic topic,
            Institute institute
    );

    List<Question> findByCourseAndInstituteAndActiveTrueOrderByDisplayOrderAsc(
            Course course,
            Institute institute
    );

    List<Question> findByTopicAndInstituteAndActiveTrueOrderByDisplayOrderAsc(
            Topic topic,
            Institute institute
    );

    @Query("""
            SELECT q
            FROM Question q
            WHERE q.institute = :institute
              AND q.active = true
              AND (:course IS NULL OR q.course = :course)
              AND (:topic IS NULL OR q.topic = :topic)
              AND (:difficulty IS NULL OR q.difficulty = :difficulty)
              AND (:questionType IS NULL OR q.questionType = :questionType)
              AND (
                    :keyword IS NULL
                    OR LOWER(q.questionText)
                       LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY
                q.displayOrder,
                q.questionText
            """)
    List<Question> searchQuestions(
            @Param("institute") Institute institute,
            @Param("course") Course course,
            @Param("topic") Topic topic,
            @Param("difficulty") QuestionDifficulty difficulty,
            @Param("questionType") QuestionType questionType,
            @Param("keyword") String keyword
    );

    Optional<Question> findByIdAndInstitute(
            Long id,
            Institute institute
    );

    Integer findMaxDisplayOrderByTopic(Topic topic);
}