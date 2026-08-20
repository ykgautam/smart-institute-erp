package com.smartinstitute.erp.notification.service;

import com.smartinstitute.erp.notification.dto.request.NotificationFilterRequest;
import com.smartinstitute.erp.notification.dto.response.NotificationResponse;
import com.smartinstitute.erp.notification.entity.Notification;
import com.smartinstitute.erp.notification.enums.NotificationType;

import java.util.List;

/**
 * Service interface for notification-related operations.
 */
public interface NotificationService {

    /**
     * Retrieves notifications for the currently authenticated user.
     *
     * @param request notification filter request
     * @return list of notification responses
     */
    List<NotificationResponse> getNotifications(
            NotificationFilterRequest request
    );

    /**
     * Retrieves the unread notification count for the
     * currently authenticated user.
     *
     * @return unread notification count
     */
    long getUnreadCount();

    /**
     * Marks a notification as read.
     *
     * @param notificationId notification ID
     */
    void markAsRead(Long notificationId);

    /**
     * Marks all notifications belonging to the current user as read.
     */
    void markAllAsRead();

    /**
     * Creates an in-application notification.
     *
     * <p>
     * This operation is intended for internal use by business
     * modules such as Fee, Attendance, Exam, and Batch.
     * </p>
     *
     * @param userId recipient user ID
     * @param instituteId institute ID
     * @param type notification type
     * @param title notification title
     * @param message notification message
     * @param referenceType related entity type
     * @param referenceId related entity ID
     * @return created notification
     */
    void createNotification(
            Long userId,
            Long instituteId,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            Long referenceId
    );
}