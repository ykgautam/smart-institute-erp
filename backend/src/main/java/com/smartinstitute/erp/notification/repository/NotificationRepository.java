package com.smartinstitute.erp.notification.repository;

import com.smartinstitute.erp.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for Notification persistence operations.
 *
 * <p>
 * Extends JpaSpecificationExecutor to support dynamic filtering
 * of notifications without creating multiple repository methods
 * for every possible filter combination.
 * </p>
 */
public interface NotificationRepository
        extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    /**
     * Finds a notification by ID for a specific user.
     *
     * <p>
     * The user condition ensures that a user can only access
     * notifications belonging to that user.
     * </p>
     *
     * @param id     notification ID
     * @param userId user ID
     * @return notification if found
     */
    Optional<Notification> findByIdAndUserId(
            Long id,
            Long userId
    );

    /**
     * Counts unread notifications for a specific user.
     *
     * @param userId user ID
     * @return unread notification count
     */
    long countByUserIdAndReadFalse(Long userId);

    /**
     * Marks all unread notifications of a specific user and
     * institute as read.
     *
     * <p>
     * A bulk update is used here instead of loading all unread
     * notifications into memory and updating them one by one.
     * </p>
     *
     * @param instituteId institute ID
     * @param userId      user ID
     * @param readAt      timestamp at which notifications are marked read
     * @return number of notifications updated
     */
    @Modifying
    @Query("""
            UPDATE Notification n
               SET n.read = true,
                   n.readAt = :readAt
             WHERE n.institute.id = :instituteId
               AND n.user.id = :userId
               AND n.read = false
            """)
    int markAllAsRead(
            @Param("instituteId") Long instituteId,
            @Param("userId") Long userId,
            @Param("readAt") LocalDateTime readAt
    );
}