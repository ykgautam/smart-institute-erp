package com.smartinstitute.erp.auth.service;

import com.smartinstitute.erp.auth.dto.LoginRequest;
import com.smartinstitute.erp.auth.dto.LoginResponse;
import com.smartinstitute.erp.auth.dto.RefreshTokenRequest;
import com.smartinstitute.erp.user.dto.UserResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    UserResponse getCurrentUser();
}