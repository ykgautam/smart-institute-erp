package com.smartinstitute.erp.test.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.common.enums.test.TestType;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.institute.entity.Institute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_test_title_course_topic",
                        columnNames = {
                                "institute_id",
                                "course_id",
                                "topic_id",
                                "title"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Test extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType testType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status = TestStatus.DRAFT;

    /**
     * Example:
     * 40 means student must score 40%
     */
    @Column(nullable = false)
    private Integer passingPercentage;

    @Column(nullable = false)
    private Boolean shuffleQuestions = true;

    @Column(nullable = false)
    private Boolean shuffleOptions = true;

    @Column(nullable = false)
    private Boolean timerEnabled = false;

    /**
     * Used only when timerEnabled = true
     */
    private Integer durationMinutes;

    @Column(nullable = false)
    private Boolean showExplanationAfterSubmission = true;

    /**
     * Maximum attempts allowed.
     * <p>
     * Practice Test = Unlimited (can use 999 or Integer.MAX_VALUE logic)
     * Final Test = 1
     */
    @Column(nullable = false)
    private Integer maxAttempts = 1;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Optional scheduling.
     * Null means available anytime.
     */
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToMany(
            mappedBy = "test",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TestQuestion> testQuestions = new ArrayList<>();

    public void addTestQuestion(TestQuestion testQuestion) {

        testQuestions.add(testQuestion);

        testQuestion.setTest(this);
    }

    public void removeTestQuestion(TestQuestion testQuestion) {

        testQuestions.remove(testQuestion);

        testQuestion.setTest(null);
    }

}