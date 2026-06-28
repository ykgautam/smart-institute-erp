package com.smartinstitute.erp.user.dto;

import com.smartinstitute.erp.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number.")
    private String mobile;

    @NotNull(message = "Gender is required.")
    private Gender gender;

    @NotNull(message = "Role Id is required.")
    private Long roleId;
}