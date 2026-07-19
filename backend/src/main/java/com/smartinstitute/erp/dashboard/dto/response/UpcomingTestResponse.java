package com.smartinstitute.erp.dashboard.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingTestResponse {

    private Long testId;

    private String testTitle;

    private String courseName;

    private Integer durationInMinutes;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}