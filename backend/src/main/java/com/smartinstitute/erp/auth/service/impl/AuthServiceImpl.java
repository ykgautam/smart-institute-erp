package com.smartinstitute.erp.auth.service.impl;

import com.smartinstitute.erp.auth.dto.ChangePasswordRequest;
import com.smartinstitute.erp.auth.dto.LoginRequest;
import com.smartinstitute.erp.auth.dto.LoginResponse;
import com.smartinstitute.erp.auth.dto.RefreshTokenRequest;
import com.smartinstitute.erp.auth.service.AuthService;
import com.smartinstitute.erp.common.enums.JwtTokenType;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.security.jwt.JwtProperties;
import com.smartinstitute.erp.security.jwt.JwtService;
import com.smartinstitute.erp.security.userdetails.CustomUserDetails;
import com.smartinstitute.erp.user.dto.UserResponse;
import com.smartinstitute.erp.user.entity.User;
import com.smartinstitute.erp.user.mapper.UserMapper;
import com.smartinstitute.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        return LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .build();
    }

    @Override
    public LoginResponse refreshToken(
            RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!JwtTokenType.REFRESH.name()
                .equals(jwtService.extractTokenType(refreshToken))) {

            throw new BadCredentialsException(
                    "Invalid token type.");
        }

        String email =
                jwtService.extractUsername(refreshToken);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException(
                    "Invalid refresh token.");
        }
        return LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(userDetails))
                .refreshToken(jwtService.generateRefreshToken(userDetails))
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .build();
    }

//    @Override
//    public UserResponse getCurrentUser() {
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        String email = authentication.getName();
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("User not found."));
//
//        return userMapper.toResponse(user);
//    }

    // recommended approach
    @Override
    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails principal =
                (CustomUserDetails) authentication.getPrincipal();

        return userMapper.toResponse(principal.getUser());
    }

    @Override
    public void logout() {

        /*
         * Stateless JWT Logout
         *
         * Nothing to invalidate on server.
         *
         * Client must remove:
         *  - Access Token
         *  - Refresh Token
         */
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails principal =
                (CustomUserDetails) authentication.getPrincipal();

        User user = principal.getUser();

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Current password is incorrect.");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match.");
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password.");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}