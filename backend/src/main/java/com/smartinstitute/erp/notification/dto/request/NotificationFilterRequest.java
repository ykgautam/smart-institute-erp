package com.smartinstitute.erp.notification.dto.request;

import com.smartinstitute.erp.notification.enums.NotificationType;

/**
 * Request DTO used for filtering notifications.
 *
 * <p>
 * This DTO is intended for GET request query parameters
 * using Spring's {@code @ModelAttribute}.
 * </p>
 */
public class NotificationFilterRequest {

    /**
     * Optional read-status filter.
     *
     * <p>
     * true  -> read notifications
     * false -> unread notifications
     * null  -> both
     * </p>
     */
    private Boolean read;

    /**
     * Optional notification type filter.
     */
    private NotificationType type;

    public NotificationFilterRequest() {
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}