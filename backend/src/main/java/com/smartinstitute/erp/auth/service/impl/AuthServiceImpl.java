package com.smartinstitute.erp.auth.service.impl;

import com.smartinstitute.erp.auth.dto.LoginRequest;
import com.smartinstitute.erp.auth.dto.LoginResponse;
import com.smartinstitute.erp.auth.service.AuthService;
import com.smartinstitute.erp.security.jwt.JwtProperties;
import com.smartinstitute.erp.security.jwt.JwtService;
import com.smartinstitute.erp.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

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
}