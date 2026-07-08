package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.test.QuestionDifficulty;
import com.smartinstitute.erp.common.enums.test.QuestionType;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "questions"
)
@Getter
@Setter
@NoArgsConstructor
public class Question extends BaseEntity {

    @Column(nullable = false, length = 2000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType questionType = QuestionType.MCQ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionDifficulty difficulty;

    @Column(length = 3000)
    private String explanation;

    @Column(nullable = false)
    private Integer marks = 1;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            nullable = false
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "topic_id",
            nullable = false
    )
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "institute_id",
            nullable = false
    )
    private Institute institute;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionOption> options = new ArrayList<>();

    @Column(nullable = false)
    private Integer displayOrder = 1;

    @OneToMany(
            mappedBy = "question"
    )
    private List<TestQuestion> testQuestions = new ArrayList<>();
}