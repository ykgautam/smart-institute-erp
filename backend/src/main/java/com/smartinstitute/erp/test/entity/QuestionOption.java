package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "question_options",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_question_option",
                        columnNames = {
                                "question_id",
                                "option_text"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class QuestionOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "question_id",
            nullable = false
    )
    private Question question;

    @Column(nullable = false, length = 1000)
    private String optionText;

    @Column(nullable = false)
    private Boolean correct = false;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean active = true;

}