package com.smartinstitute.erp.user.mapper;

import com.smartinstitute.erp.common.enums.UserStatus;
import com.smartinstitute.erp.role.entity.Role;
import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UserResponse;
import com.smartinstitute.erp.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request, Role role) {

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        // Password hashing will be added in Sprint 1.11
        user.setPassword(request.getPassword());

        user.setGender(request.getGender());
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(role);

        return user;
    }

    public UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .gender(user.getGender())
                .status(user.getStatus())
                .role(user.getRole().getName())
                .build();
    }
}