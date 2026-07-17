package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import com.smartinstitute.erp.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "student_tests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_test_attempt",
                        columnNames = {
                                "student_id",
                                "test_id",
                                "attempt_no"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StudentTest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "test_id",
            nullable = false
    )
    private Test test;

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentTestStatus status = StudentTestStatus.IN_PROGRESS;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private Integer totalQuestions = 0;

    @Column(nullable = false)
    private Integer correctAnswers = 0;

    @Column(nullable = false)
    private Integer wrongAnswers = 0;

    @Column(nullable = false)
    private Integer unansweredQuestions = 0;

    @Column(nullable = false)
    private Integer totalMarks = 0;

    @Column(nullable = false)
    private Integer obtainedMarks = 0;

    @Column(nullable = false)
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean passed = false;

    /**
     * Time consumed in seconds.
     */
    @Column(nullable = false)
    private Integer timeTakenInSeconds = 0;

    @OneToMany(
            mappedBy = "studentTest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StudentAnswer> answers = new ArrayList<>();

}