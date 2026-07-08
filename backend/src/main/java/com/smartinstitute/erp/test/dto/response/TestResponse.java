package com.smartinstitute.erp.test.dto.response;

import com.smartinstitute.erp.common.enums.test.TestStatus;
import com.smartinstitute.erp.common.enums.test.TestType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestResponse {

    private Long id;

    private String title;

    private String description;

    private Long courseId;

    private String courseName;

    private Long topicId;

    private String topicName;

    private TestType testType;

    private TestStatus status;

    private Integer passingPercentage;

    private Boolean shuffleQuestions;

    private Boolean shuffleOptions;

    private Boolean timerEnabled;

    private Integer durationMinutes;

    private Boolean showExplanationAfterSubmission;

    private Integer maxAttempts;

    private Integer questionCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}