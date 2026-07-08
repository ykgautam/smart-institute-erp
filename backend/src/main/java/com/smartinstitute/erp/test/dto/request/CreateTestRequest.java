package com.smartinstitute.erp.test.dto.request;

import com.smartinstitute.erp.common.enums.test.TestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateTestRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull
    private Long courseId;

    @NotNull
    private Long topicId;

    @NotNull
    private TestType testType;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer passingPercentage;

    @NotNull
    private Boolean shuffleQuestions = true;

    @NotNull
    private Boolean shuffleOptions = true;

    @NotNull
    private Boolean timerEnabled = false;

    @Positive
    private Integer durationMinutes;

    @NotNull
    private Boolean showExplanationAfterSubmission = true;

    @NotNull
    @Positive
    private Integer maxAttempts = 1;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Valid
    @NotEmpty
    private List<TestQuestionRequest> questions;

}