package com.smartinstitute.erp.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinstitute.erp.common.enums.ResponseStatus;
import com.smartinstitute.erp.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .success(false)
                .status(ResponseStatus.valueOf(HttpStatus.FORBIDDEN.name()))
                .message("Access Denied.")
                .timestamp(LocalDateTime.now())
                .build();

        new ObjectMapper().writeValue(
                response.getOutputStream(),
                apiResponse);
    }
}