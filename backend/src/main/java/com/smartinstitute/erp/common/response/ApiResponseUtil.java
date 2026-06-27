package com.smartinstitute.erp.common.response;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for creating standardized API responses.
 * <p>
 * All controllers in the Smart Institute ERP must use this class
 * instead of creating ApiResponse objects manually.
 */
public final class ApiResponseUtil {

    /**
     * Prevent instantiation.
     */
    private ApiResponseUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Success response with data.
     *
     * @param data    Response payload
     * @param message Success message
     * @param <T>     Payload type
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {

        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .errors(null)
                .build();
    }

    /**
     * Success Message response without data.
     *
     * @param message Success message
     * @return ApiResponse
     */
    public static ApiResponse<Void> successMessage(String message) {

        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .errors(null)
                .build();
    }

    /**
     * Error response with a single error.
     * will avoid using this
     * @param message Main error message
     * @param error   Error description
     * @return ApiResponse
     */
    public static ApiResponse<Void> error(String message, String error) {

        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(Collections.singletonList(error))
                .build();
    }

    /**
     * Error response with multiple errors.
     *
     * @param message Main error message
     * @param errors  List of errors
     * @return ApiResponse
     */
    public static ApiResponse<Void> error(String message, List<String> errors) {

        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(errors)
                .build();
    }

    /**
     * Error response with only a message.
     *
     * Example:
     * Student not found
     */
    public static ApiResponse<Void> error(String message) {

        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(null)
                .build();
    }
}