package com.smartinstitute.erp.auth.service;

import com.smartinstitute.erp.auth.dto.LoginRequest;
import com.smartinstitute.erp.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}