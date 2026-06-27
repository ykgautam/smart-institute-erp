package com.smartinstitute.erp.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates whether the API request was successful.
     */
    private boolean success;

    /**
     * Human-readable message.
     */
    private String message;

    /**
     * Actual response payload.
     */
    private T data;

    /**
     * Response creation timestamp.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}