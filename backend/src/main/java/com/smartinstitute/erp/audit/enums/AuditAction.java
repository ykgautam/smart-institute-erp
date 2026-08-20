package com.smartinstitute.erp.audit.enums;

/**
 * Represents the type of operation recorded in the audit log.
 *
 * <p>
 * Audit actions describe what happened to a business or security
 * resource. The affected resource itself is stored separately in
 * the {@code entityType} field of {@code AuditLog}.
 * </p>
 *
 * <p>
 * Examples:
 * CREATE + STUDENT
 * UPDATE + STUDENT_FEE
 * DELETE + COURSE
 * LOGIN + USER
 * </p>
 */
public enum AuditAction {

    /**
     * A new record was created.
     */
    CREATE,

    /**
     * An existing record was updated.
     */
    UPDATE,

    /**
     * A record was deleted.
     */
    DELETE,

    /**
     * A record was activated.
     */
    ACTIVATE,

    /**
     * A record was deactivated.
     */
    DEACTIVATE,

    /**
     * A user successfully authenticated.
     */
    LOGIN,

    /**
     * A user logged out.
     */
    LOGOUT,

    /**
     * A user or system operation failed.
     */
    FAILED,

    /**
     * A security-related action was detected.
     */
    SECURITY_EVENT
}