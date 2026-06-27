package com.smartinstitute.erp.user.service;

import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

}