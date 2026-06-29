package com.smartinstitute.erp.common.exception;


import com.smartinstitute.erp.common.enums.ResponseStatus;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseUtil.error(
                        ex.getMessage(),
                        ResponseStatus.ERROR,
                        null));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseUtil.error(
                        ex.getMessage(),
                        ResponseStatus.ERROR,
                        null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {

        return ResponseEntity.badRequest()
                .body(ApiResponseUtil.error(
                        ex.getMessage(),
                        ResponseStatus.ERROR,
                        null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseUtil.error(
                        ex.getMessage(),
                        ResponseStatus.ERROR,
                        null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseUtil.error(
                        ex.getMessage(),
                        ResponseStatus.ERROR,
                        null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest()
                .body(ApiResponseUtil.error(
                        "Validation failed.",
                        ResponseStatus.ERROR,
                        errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {

        return ResponseEntity.internalServerError()
                .body(ApiResponseUtil.error(
                        "Something went wrong.",
                        ResponseStatus.ERROR,
                        null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .status(ResponseStatus.ERROR)
                        .message("Access Denied.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredential(
            BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Object>builder()
                        .success(false)
                        .status(ResponseStatus.ERROR)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

}