package com.smartinstitute.erp.user.controller;

import com.smartinstitute.erp.common.constants.ApiConstants;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UpdateUserRequest;
import com.smartinstitute.erp.user.dto.UpdateUserStatusRequest;
import com.smartinstitute.erp.user.dto.UserResponse;
import com.smartinstitute.erp.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        return ApiResponseUtil.success(
                response,
                "User created successfully."
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        userService.getUserById(id),
                        "User fetched successfully."
                )
        );
    }

    // get all user
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        userService.getAllUsers(),
                        "Users fetched successfully."
                )
        );
    }

    // update user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        userService.updateUser(id, request),
                        "User updated successfully."
                )
        );
    }

    // delete user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "User deleted successfully."
                )
        );
    }

    // update user status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        userService.updateUserStatus(id, request),
                        "User status updated successfully."
                )
        );
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        userService.getUserByEmail(email),
                        "User fetched successfully."
                )
        );
    }
}