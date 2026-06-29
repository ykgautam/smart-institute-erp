package com.smartinstitute.erp.institute.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.institute.dto.*;
import com.smartinstitute.erp.institute.service.InstituteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/institutes")
@RequiredArgsConstructor
public class InstituteController {

    private final InstituteService instituteService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstituteResponse>> createInstitute(
            @Valid @RequestBody CreateInstituteRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        instituteService.createInstitute(request),
                        "Institute created successfully."
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstituteResponse>> getInstituteById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        instituteService.getInstituteById(id),
                        "Institute fetched successfully."
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<InstituteResponse>>> getAllInstitutes() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        instituteService.getAllInstitutes(),
                        "Institutes fetched successfully."
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstituteResponse>> updateInstitute(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInstituteRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        instituteService.updateInstitute(id, request),
                        "Institute updated successfully."
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInstitute(
            @PathVariable Long id) {

        instituteService.deleteInstitute(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        null,
                        "Institute deleted successfully."
                )
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstituteResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInstituteStatusRequest request) {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        instituteService.updateInstituteStatus(id, request),
                        "Institute status updated successfully."
                )
        );
    }

    @PostMapping("/onboard")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstituteOnboardingResponse>>
    onboardInstitute(
            @Valid
            @RequestBody
            InstituteOnboardingRequest request) {

        return ResponseEntity.ok(

                ApiResponseUtil.success(
                        instituteService.onboardInstitute(request),
                        "Institute onboarded successfully."
                )
        );
    }

}