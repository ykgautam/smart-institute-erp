package com.smartinstitute.erp.audit.controller;

import com.smartinstitute.erp.audit.dto.request.AuditLogSearchRequest;
import com.smartinstitute.erp.audit.dto.response.AuditLogResponse;
import com.smartinstitute.erp.audit.service.AuditLogService;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for viewing audit history.
 *
 * <p>
 * Audit records are read-only through this API.
 * The API does not expose create, update or delete operations because
 * audit history represents historical system records.
 * </p>
 *
 * <p>
 * Tenant isolation is handled by the service layer using the institute
 * associated with the currently authenticated user.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(
            AuditLogService auditLogService
    ) {
        this.auditLogService = auditLogService;
    }

    /**
     * Returns paginated audit history for the current institute.
     *
     * <p>
     * Optional filters can be used to search audit records by:
     * </p>
     *
     * <ul>
     *     <li>User</li>
     *     <li>Action</li>
     *     <li>Entity type</li>
     *     <li>Entity ID</li>
     *     <li>Date range</li>
     * </ul>
     *
     * @param request audit search and pagination filters
     * @return paginated audit history
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<
            ApiResponse<PageResponse<AuditLogResponse>>
            > getAuditLogs(
            @Valid AuditLogSearchRequest request
    ) {

        /*
         * The service automatically restricts results to the
         * currently authenticated user's institute.
         */
        PageResponse<AuditLogResponse> response =
                auditLogService.getAuditLogs(
                        request
                );

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        response,
                        "Audit logs fetched successfully."
                )
        );
    }
}