package com.smartinstitute.erp.health.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.health.dto.HealthResponse;
import com.smartinstitute.erp.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.smartinstitute.erp.common.constants.ApiConstants.HEALTH;

/**
 * Health check endpoints.
 */
@RestController
@RequestMapping(HEALTH)
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> health() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        healthService.getHealth(),
                        "Application is running successfully"
                )
        );
    }

}