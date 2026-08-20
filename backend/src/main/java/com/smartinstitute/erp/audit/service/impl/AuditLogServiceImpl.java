package com.smartinstitute.erp.audit.service.impl;

import com.smartinstitute.erp.audit.constants.AuditLogSortableFields;
import com.smartinstitute.erp.audit.dto.request.AuditLogSearchRequest;
import com.smartinstitute.erp.audit.dto.response.AuditLogResponse;
import com.smartinstitute.erp.audit.entity.AuditLog;
import com.smartinstitute.erp.audit.enums.AuditAction;
import com.smartinstitute.erp.audit.repository.AuditLogRepository;
import com.smartinstitute.erp.audit.service.AuditLogService;
import com.smartinstitute.erp.audit.specification.AuditLogSpecification;
import com.smartinstitute.erp.common.exception.InvalidRequestException;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import com.smartinstitute.erp.common.pagination.PaginationUtils;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.specification.SpecificationBuilder;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class AuditLogServiceImpl
        extends BaseCrudService
        implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            AuditLogRepository auditLogRepository
    ) {
        super(
                securityUtil,
                instituteAccessValidator
        );

        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Records a simple audit event without entity state details.
     */
    @Override
    public void log(
            AuditAction action,
            String entityType,
            Long entityId,
            String description
    ) {

        saveAuditLog(
                action,
                entityType,
                entityId,
                description,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Records an audit event including old and new entity state.
     */
    @Override
    public void log(
            AuditAction action,
            String entityType,
            Long entityId,
            String description,
            Map<String, Object> oldValue,
            Map<String, Object> newValue
    ) {

        saveAuditLog(
                action,
                entityType,
                entityId,
                description,
                oldValue,
                newValue,
                null,
                null
        );
    }

    /**
     * Central internal method responsible for persisting
     * audit log records.
     *
     * <p>
     * The institute and user are always resolved from the
     * current security context.
     * </p>
     */
    private void saveAuditLog(
            AuditAction action,
            String entityType,
            Long entityId,
            String description,
            Map<String, Object> oldValue,
            Map<String, Object> newValue,
            String ipAddress,
            String userAgent
    ) {

        /*
         * Resolve the current tenant.
         */
        Institute institute = getCurrentInstitute();

        /*
         * Resolve the currently authenticated user.
         */
        User currentUser = securityUtil.getCurrentUser();

        /*
         * Create the audit entity.
         */
        AuditLog auditLog = new AuditLog();

        auditLog.setInstitute(institute);
        auditLog.setUser(currentUser);

        auditLog.setAction(action);

        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);

        auditLog.setDescription(description);

        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);

        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);

        /*
         * Persist the audit record.
         */
        auditLogRepository.save(auditLog);

        /*
         * Never print old/new entity values in application logs.
         * Audit payloads may contain sensitive business information.
         */
        log.debug(
                "Audit event recorded. Action={}, EntityType={}, EntityId={}, UserId={}",
                action,
                entityType,
                entityId,
                currentUser != null ? currentUser.getId() : null
        );
    }

    /**
     * Returns paginated audit history for the current institute.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> getAuditLogs(
            AuditLogSearchRequest request
    ) {

        /*
         * Tenant isolation.
         */
        Institute institute = getCurrentInstitute();

        /*
         * Validate the requested sort field.
         */
        if (!AuditLogSortableFields.ALLOWED_FIELDS.contains(
                request.getSortBy()
        )) {

            throw new InvalidRequestException(
                    "Invalid sort field: " + request.getSortBy()
            );
        }

        /*
         * Convert the audit-specific request into the common
         * pagination model used throughout the application.
         */
        PaginationRequest paginationRequest = new PaginationRequest();

        paginationRequest.setPage(request.getPage());
        paginationRequest.setSize(request.getSize());
        paginationRequest.setSortBy(request.getSortBy());

        /*
         * IMPORTANT:
         * Use setDirection(), because PaginationRequest contains
         * the property named 'direction'.
         */
        paginationRequest.setDirection(
                request.getSortDirection()
        );

        /*
         * Build pagination and sorting information.
         */
        Pageable pageable =
                SpecificationBuilder.buildPageable(
                        paginationRequest
                );

        /*
         * Build dynamic search filters.
         *
         * The specification must always include the current
         * institute restriction.
         */
        Specification<AuditLog> specification =
                AuditLogSpecification.filterAuditLogs(
                        request,
                        institute
                );

        /*
         * Fetch the paginated audit records.
         */
        Page<AuditLog> page =
                auditLogRepository.findAll(
                        specification,
                        pageable
                );

        /*
         * Convert entities into API response DTOs.
         */
        List<AuditLogResponse> responses =
                page.getContent()
                        .stream()
                        .map(this::toAuditLogResponse)
                        .toList();

        /*
         * Build the standard application pagination response.
         */
        return PaginationUtils.buildPageResponse(
                page,
                responses
        );
    }

    /**
     * Converts an AuditLog entity into an API response DTO.
     *
     * <p>
     * The user can be null for system-generated audit events.
     * </p>
     */
    private AuditLogResponse toAuditLogResponse(
            AuditLog auditLog
    ) {

        User user = auditLog.getUser();

        return new AuditLogResponse(

                auditLog.getId(),

                /*
                 * User information.
                 */
                user != null
                        ? user.getId()
                        : null,

                user != null
                        ? buildUserFullName(user)
                        : null,

                user != null
                        ? user.getEmail()
                        : null,

                /*
                 * Audit operation.
                 */
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDescription(),

                /*
                 * Entity state.
                 */
                auditLog.getOldValue(),
                auditLog.getNewValue(),

                /*
                 * Client information.
                 */
                auditLog.getIpAddress(),
                auditLog.getUserAgent(),

                /*
                 * Audit timestamp.
                 */
                auditLog.getCreatedAt()
        );
    }

    /**
     * Builds the display name of the user safely.
     */
    private String buildUserFullName(
            User user
    ) {

        String firstName =
                user.getFirstName() != null
                        ? user.getFirstName()
                        : "";

        String lastName =
                user.getLastName() != null
                        ? user.getLastName()
                        : "";

        return (firstName + " " + lastName).trim();
    }
}