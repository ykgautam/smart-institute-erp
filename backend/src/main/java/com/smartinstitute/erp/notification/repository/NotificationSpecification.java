package com.smartinstitute.erp.notification.repository;

import com.smartinstitute.erp.notification.entity.Notification;
import com.smartinstitute.erp.notification.enums.NotificationType;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications used for dynamically filtering notifications.
 *
 * <p>
 * Each specification represents one optional filter and can be
 * combined with other specifications using Spring Data JPA's
 * Specification API.
 * </p>
 */
public final class NotificationSpecification {

    private NotificationSpecification() {
        // Utility class.
    }

    /**
     * Filters notifications by institute.
     *
     * @param instituteId institute ID
     * @return institute filter specification
     */
    public static Specification<Notification> hasInstituteId(
            Long instituteId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("institute").get("id"),
                        instituteId
                );
    }

    /**
     * Filters notifications by recipient user.
     *
     * @param userId user ID
     * @return user filter specification
     */
    public static Specification<Notification> hasUserId(
            Long userId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    /**
     * Filters notifications by read status.
     *
     * @param read read status
     * @return read-status filter specification
     */
    public static Specification<Notification> hasReadStatus(
            Boolean read) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("read"),
                        read
                );
    }

    /**
     * Filters notifications by notification type.
     *
     * @param type notification type
     * @return notification-type filter specification
     */
    public static Specification<Notification> hasType(
            NotificationType type) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("type"),
                        type
                );
    }
}