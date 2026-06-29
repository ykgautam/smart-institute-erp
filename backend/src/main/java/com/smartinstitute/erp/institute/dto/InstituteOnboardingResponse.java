package com.smartinstitute.erp.institute.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstituteOnboardingResponse {

    private Long instituteId;

    private String instituteName;

    private Long ownerUserId;

    private String ownerName;

    private String ownerEmail;

}