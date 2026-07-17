package com.smartinstitute.erp.test.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class StudentTestResultResponse {

    private Long studentTestId;

    private Long testId;

    private String testTitle;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private Integer wrongAnswers;

    private Integer unansweredQuestions;

    private Integer totalMarks;

    private Integer obtainedMarks;

    private BigDecimal percentage;

    private Integer passingPercentage;

    private Boolean passed;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private Integer timeTakenInSeconds;

}