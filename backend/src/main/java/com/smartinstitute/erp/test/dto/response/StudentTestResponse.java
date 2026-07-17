package com.smartinstitute.erp.test.dto.response;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class StudentTestResponse {

    private Long id;

    private Long testId;

    private String testTitle;

    private Long studentId;

    private String studentName;

    private Integer attemptNo;

    private StudentTestStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private Integer wrongAnswers;

    private Integer unansweredQuestions;

    private Integer totalMarks;

    private Integer obtainedMarks;

    private Double percentage;

    private Boolean passed;

    private Integer timeTakenInSeconds;

}