package com.smartinstitute.erp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required.")
    private String currentPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 50,
            message = "Password must be between 8 and 50 characters.")
    private String newPassword;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

}