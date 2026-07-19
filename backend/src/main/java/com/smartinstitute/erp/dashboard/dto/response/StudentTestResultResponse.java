package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTestResultResponse {

    private Long studentTestId;

    private Long testId;

    private String testTitle;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private Integer wrongAnswers;

    private Integer unansweredQuestions;

    private Integer obtainedMarks;

    private Integer totalMarks;

    private BigDecimal percentage;

    private Boolean passed;

    private Integer timeTakenInSeconds;

    private LocalDateTime submittedAt;

    private List<QuestionResultResponse> questions;

}