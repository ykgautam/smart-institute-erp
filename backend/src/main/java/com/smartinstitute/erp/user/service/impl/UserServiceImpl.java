package com.smartinstitute.erp.user.service.impl;

import com.smartinstitute.erp.common.enums.UserStatus;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.DuplicateResourceException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.role.entity.Role;
import com.smartinstitute.erp.role.repository.RoleRepository;
import com.smartinstitute.erp.user.dto.CreateUserRequest;
import com.smartinstitute.erp.user.dto.UpdateUserRequest;
import com.smartinstitute.erp.user.dto.UpdateUserStatusRequest;
import com.smartinstitute.erp.user.dto.UserResponse;
import com.smartinstitute.erp.user.entity.User;
import com.smartinstitute.erp.user.mapper.UserMapper;
import com.smartinstitute.erp.user.repository.UserRepository;
import com.smartinstitute.erp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findByIdAndStatusNot(id,UserStatus.DELETED)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id : " + id));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return userMapper.toResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id,
                                   UpdateUserRequest request) {

        User user = userRepository.findByIdAndStatusNot(id,UserStatus.DELETED)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + id));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id : "
                                        + request.getRoleId()));

        userMapper.updateEntity(user, request, role);

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + id));

        user.setStatus(UserStatus.DELETED);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id,
                                         UpdateUserStatusRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + id));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BadRequestException(
                    "Deleted user cannot be activated.");
        }

        if (request.getStatus() == UserStatus.DELETED) {
            throw new BadRequestException(
                    "Use Delete API to delete user.");
        }

        user.setStatus(request.getStatus());

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        User user = userRepository
                .findByEmailAndStatusNot(email, UserStatus.DELETED)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email : " + email));

        return userMapper.toResponse(user);
    }

}