package com.smartinstitute.erp.audit.service;

import com.smartinstitute.erp.audit.dto.request.AuditLogSearchRequest;
import com.smartinstitute.erp.audit.dto.response.AuditLogResponse;
import com.smartinstitute.erp.audit.enums.AuditAction;
import com.smartinstitute.erp.common.pagination.PageResponse;

import java.util.Map;

public interface AuditLogService {

    /**
     * Creates and stores an audit log record.
     *
     * @param action      action performed
     * @param entityType  affected entity type
     * @param entityId    affected entity ID
     * @param description human-readable description
     */
    void log(
            AuditAction action,
            String entityType,
            Long entityId,
            String description
    );

    /**
     * Creates and stores an audit log record including
     * old and new entity state.
     *
     * @param action      action performed
     * @param entityType  affected entity type
     * @param entityId    affected entity ID
     * @param description human-readable description
     * @param oldValue    entity state before the operation
     * @param newValue    entity state after the operation
     */
    void log(
            AuditAction action,
            String entityType,
            Long entityId,
            String description,
            Map<String, Object> oldValue,
            Map<String, Object> newValue
    );

    /**
     * Returns paginated audit history for the current institute.
     *
     * @param request audit search and pagination filters
     * @return paginated audit history
     */
    PageResponse<AuditLogResponse> getAuditLogs(
            AuditLogSearchRequest request
    );
}