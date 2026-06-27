package com.smartinstitute.erp.health.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Health check response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    /**
     * Application name.
     */
    private String application;

    /**
     * Current application version.
     */
    private String version;

    /**
     * Active Spring profile.
     */
    private String environment;

    /**
     * Application status.
     */
    private String status;

    /**
     * Current server time.
     */
    private LocalDateTime serverTime;

}