package com.smartinstitute.erp.audit.repository;

import com.smartinstitute.erp.audit.entity.AuditLog;
import com.smartinstitute.erp.audit.enums.AuditAction;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for AuditLog persistence and audit-history queries.
 *
 * <p>
 * JpaSpecificationExecutor is included because the audit history
 * API will support multiple optional filters.
 * </p>
 */
public interface AuditLogRepository extends
        JpaRepository<AuditLog, Long>,
        JpaSpecificationExecutor<AuditLog> {

    /**
     * Finds an audit record by ID within the specified institute.
     *
     * <p>
     * Institute filtering is required to maintain tenant isolation.
     * </p>
     */
    Optional<AuditLog> findByIdAndInstitute(
            Long id,
            Institute institute
    );

    /**
     * Finds all audit records performed by a particular user
     * within an institute.
     */
    List<AuditLog> findByInstituteAndUser(
            Institute institute,
            User user
    );

    /**
     * Finds audit records for a specific action within an institute.
     */
    List<AuditLog> findByInstituteAndAction(
            Institute institute,
            AuditAction action
    );

    /**
     * Finds the audit history of a particular business entity
     * within an institute.
     *
     * <p>
     * Example:
     * entityType = "STUDENT"
     * entityId = 10
     * </p>
     */
    List<AuditLog> findByInstituteAndEntityTypeAndEntityId(
            Institute institute,
            String entityType,
            Long entityId
    );

    /**
     * Finds audit records created within the specified date range
     * for an institute.
     */
    List<AuditLog> findByInstituteAndCreatedAtBetween(
            Institute institute,
            LocalDateTime from,
            LocalDateTime to
    );

    /**
     * Counts all audit records belonging to an institute.
     */
    long countByInstitute(
            Institute institute
    );
}