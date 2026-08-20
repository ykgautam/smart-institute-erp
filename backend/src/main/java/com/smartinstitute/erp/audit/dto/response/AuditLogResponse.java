package com.smartinstitute.erp.audit.dto.response;

import com.smartinstitute.erp.audit.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * API response representing an audit history record.
 *
 * <p>
 * This DTO exposes audit information without exposing the
 * AuditLog JPA entity directly to API consumers.
 * </p>
 */
@Getter
@AllArgsConstructor
public class AuditLogResponse {

    /**
     * Unique audit record identifier.
     */
    private Long id;

    /**
     * User who performed the audited action.
     */
    private Long userId;

    /**
     * Name of the user who performed the action.
     */
    private String userName;

    /**
     * Email address of the user who performed the action.
     */
    private String userEmail;

    /**
     * Type of operation performed.
     */
    private AuditAction action;

    /**
     * Type of affected business entity.
     */
    private String entityType;

    /**
     * Identifier of the affected business entity.
     */
    private Long entityId;

    /**
     * Human-readable description of the operation.
     */
    private String description;

    /**
     * Entity state before the operation.
     */
    private Map<String, Object> oldValue;

    /**
     * Entity state after the operation.
     */
    private Map<String, Object> newValue;

    /**
     * Client IP address.
     */
    private String ipAddress;

    /**
     * Client user-agent information.
     */
    private String userAgent;

    /**
     * Date and time when the audit record was created.
     */
    private LocalDateTime createdAt;
}