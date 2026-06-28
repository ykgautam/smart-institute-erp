package com.smartinstitute.erp.user.service;

import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UpdateUserRequest;
import com.smartinstitute.erp.user.dto.UpdateUserStatusRequest;
import com.smartinstitute.erp.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request);

    UserResponse getUserByEmail(String email);
}