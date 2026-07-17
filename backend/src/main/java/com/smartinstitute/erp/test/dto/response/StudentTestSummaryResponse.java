package com.smartinstitute.erp.test.dto.response;

import com.smartinstitute.erp.common.enums.test.StudentTestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class StudentTestSummaryResponse {

    private Long id;

    private Long testId;

    private String testTitle;

    private Integer attemptNo;

    private StudentTestStatus status;

    private Double percentage;

    private Boolean passed;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

}