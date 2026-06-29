package com.smartinstitute.erp.institute.dto;

import com.smartinstitute.erp.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerRegistrationRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 150)
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile must contain exactly 10 digits."
    )
    private String mobile;

    @NotBlank
    private String password;

    private Gender gender;

}