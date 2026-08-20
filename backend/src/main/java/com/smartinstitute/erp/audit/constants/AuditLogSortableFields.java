package com.smartinstitute.erp.audit.constants;

import java.util.Set;

/**
 * Defines fields that are allowed for sorting audit history.
 *
 * <p>
 * Sorting fields are explicitly whitelisted to prevent invalid
 * property names from reaching the data-access layer.
 * </p>
 */
public final class AuditLogSortableFields {

    private AuditLogSortableFields() {
    }

    /**
     * Fields that can be used for sorting audit history.
     */
    public static final Set<String> ALLOWED_FIELDS = Set.of(

            "id",

            "action",

            "entityType",

            "entityId",

            "createdAt"
    );
}