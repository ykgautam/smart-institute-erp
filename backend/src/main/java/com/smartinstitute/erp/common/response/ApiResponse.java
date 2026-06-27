package com.smartinstitute.erp.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartinstitute.erp.common.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API response wrapper used across the entire Smart Institute ERP.
 *
 * @param <T> Type of response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates whether the request was successful.
     */
    private boolean success;

    /**
     * SUCCESS | FAILED | ERROR
     */
    private ResponseStatus status;

    /**
     * Human-readable response message.
     */
    private String message;

    /**
     * Actual response payload.
     * Will be null in case of errors.
     */
    private T data;

    /**
     * List of validation or business errors.
     * Null for successful responses.
     */
    private Object errors;

    /**
     * Timestamp when the response was generated.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}