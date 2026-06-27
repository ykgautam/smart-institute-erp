package com.smartinstitute.erp.user.controller;

import com.smartinstitute.erp.common.constants.ApiConstants;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UserResponse;
import com.smartinstitute.erp.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        return ApiResponseUtil.success(
                response,
                "User created successfully."
        );
    }

}