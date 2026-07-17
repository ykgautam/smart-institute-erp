package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "student_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_answer",
                        columnNames = {
                                "student_test_id",
                                "question_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StudentAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_test_id",
            nullable = false
    )
    private StudentTest studentTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "question_id",
            nullable = false
    )
    private Question question;

    /**
     * Selected option.
     * Null means student skipped the question.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    @Column(nullable = false)
    private Boolean correct = false;

    @Column(nullable = false)
    private Integer marksObtained = 0;

}