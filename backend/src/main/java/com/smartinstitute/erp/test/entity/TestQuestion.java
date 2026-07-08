package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "test_questions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_test_question",
                        columnNames = {
                                "test_id",
                                "question_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TestQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "test_id",
            nullable = false
    )
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "question_id",
            nullable = false
    )
    private Question question;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean active = true;

}