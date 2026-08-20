package com.smartinstitute.erp.notification.enums;

/**
 * Defines the business types of notifications supported
 * by the Smart Institute ERP.
 */
public enum NotificationType {

    /**
     * General system notification.
     */
    GENERAL,

    /**
     * Notification created for institute announcements.
     */
    ANNOUNCEMENT,

    /**
     * Reminder related to pending or upcoming fee payment.
     */
    FEE_REMINDER,

    /**
     * Notification sent after successful fee payment.
     */
    FEE_PAYMENT_SUCCESS,

    /**
     * Notification related to attendance.
     */
    ATTENDANCE_ALERT,

    /**
     * Notification related to exam scheduling.
     */
    EXAM_SCHEDULE,

    /**
     * Notification related to published exam results.
     */
    EXAM_RESULT,

    /**
     * Notification related to batch updates.
     */
    BATCH_UPDATE,

    /**
     * System-level notification.
     */
    SYSTEM
}