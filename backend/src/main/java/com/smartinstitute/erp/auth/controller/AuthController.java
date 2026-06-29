package com.smartinstitute.erp.auth.controller;

import com.smartinstitute.erp.auth.dto.ChangePasswordRequest;
import com.smartinstitute.erp.auth.dto.LoginRequest;
import com.smartinstitute.erp.auth.dto.LoginResponse;
import com.smartinstitute.erp.auth.dto.RefreshTokenRequest;
import com.smartinstitute.erp.auth.service.AuthService;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponseUtil.success(
                authService.login(request),
                "Login successful."
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refreshToken(
            @Valid
            @RequestBody RefreshTokenRequest request) {

        return ApiResponseUtil.success(
                authService.refreshToken(request),
                "Token refreshed successfully."
        );
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {

        return ApiResponseUtil.success(
                authService.getCurrentUser(),
                "User profile fetched successfully."
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {

        authService.logout();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Logout successful."
                )
        );
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Password changed successfully."
                )
        );
    }
}