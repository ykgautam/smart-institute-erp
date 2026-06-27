package com.smartinstitute.erp.user.dto;

import com.smartinstitute.erp.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String mobile;

    @NotBlank
    private String password;

    private Gender gender;

    private Long roleId;

}