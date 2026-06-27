package com.smartinstitute.erp.user.service.impl;

import com.smartinstitute.erp.common.enums.UserStatus;
import com.smartinstitute.erp.common.exception.DuplicateResourceException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.role.entity.Role;
import com.smartinstitute.erp.role.repository.RoleRepository;
import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UserResponse;
import com.smartinstitute.erp.user.entity.User;
import com.smartinstitute.erp.user.mapper.UserMapper;
import com.smartinstitute.erp.user.repository.UserRepository;
import com.smartinstitute.erp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists.");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with id : " + request.getRoleId()));

        User user = userMapper.toEntity(request, role);

        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}