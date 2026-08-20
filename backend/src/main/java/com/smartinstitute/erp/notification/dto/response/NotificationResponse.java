package com.smartinstitute.erp.notification.dto.response;

import com.smartinstitute.erp.notification.enums.NotificationType;

import java.time.LocalDateTime;

/**
 * Response DTO representing a notification returned to the client.
 *
 * <p>
 * This DTO intentionally exposes only the information required
 * by the frontend notification center and does not expose the
 * underlying JPA entity.
 * </p>
 */
public class NotificationResponse {

    /**
     * Notification identifier.
     */
    private Long id;

    /**
     * Notification type.
     */
    private NotificationType type;

    /**
     * Notification title.
     */
    private String title;

    /**
     * Notification message.
     */
    private String message;

    /**
     * Type of the business entity related to the notification.
     *
     * <p>
     * Example:
     * FEE_PAYMENT, EXAM_RESULT, BATCH
     * </p>
     */
    private String referenceType;

    /**
     * Identifier of the related business entity.
     */
    private Long referenceId;

    /**
     * Indicates whether the notification has been read.
     */
    private boolean read;

    /**
     * Timestamp at which the notification was marked as read.
     */
    private LocalDateTime readAt;

    /**
     * Timestamp at which the notification was created.
     */
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}