package com.smartinstitute.erp.audit.specification;

import com.smartinstitute.erp.audit.dto.request.AuditLogSearchRequest;
import com.smartinstitute.erp.audit.entity.AuditLog;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds dynamic specifications for audit history searches.
 *
 * <p>
 * Every audit search is restricted to the current institute
 * to maintain tenant isolation.
 * </p>
 */
public final class AuditLogSpecification {

    private AuditLogSpecification() {
    }

    /**
     * Builds a specification using the provided optional filters.
     *
     * @param request search filters
     * @param institute current institute
     * @return combined audit log specification
     */
    public static Specification<AuditLog> filterAuditLogs(
            AuditLogSearchRequest request,
            Institute institute
    ) {

        return (root, query, criteriaBuilder) -> {

            /*
             * Store all dynamically applicable predicates.
             */
            List<jakarta.persistence.criteria.Predicate> predicates =
                    new ArrayList<>();

            /*
             * Mandatory tenant isolation.
             *
             * Audit records must never be queried without restricting
             * the result to the current institute.
             */
            predicates.add(
                    criteriaBuilder.equal(
                            root.get("institute"),
                            institute
                    )
            );

            /*
             * Optional user filter.
             */
            if (request.getUserId() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("user").get("id"),
                                request.getUserId()
                        )
                );
            }

            /*
             * Optional audit action filter.
             */
            if (request.getAction() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("action"),
                                request.getAction()
                        )
                );
            }

            /*
             * Optional entity type filter.
             */
            if (request.getEntityType() != null
                    && !request.getEntityType().isBlank()) {

                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.upper(
                                        root.get("entityType")
                                ),
                                request.getEntityType()
                                        .trim()
                                        .toUpperCase()
                        )
                );
            }

            /*
             * Optional entity ID filter.
             */
            if (request.getEntityId() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("entityId"),
                                request.getEntityId()
                        )
                );
            }

            /*
             * Optional start date/time filter.
             */
            if (request.getDateFrom() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                request.getDateFrom()
                        )
                );
            }

            /*
             * Optional end date/time filter.
             */
            if (request.getDateTo() != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"),
                                request.getDateTo()
                        )
                );
            }

            /*
             * Combine all predicates using AND.
             */
            return criteriaBuilder.and(
                    predicates.toArray(
                            new jakarta.persistence.criteria.Predicate[0]
                    )
            );
        };
    }
}