package com.smartinstitute.erp.institute.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstituteOnboardingRequest {

    @Valid
    @NotNull
    private CreateInstituteRequest institute;

    @Valid
    @NotNull
    private OwnerRegistrationRequest owner;

}