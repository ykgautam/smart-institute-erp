package com.smartinstitute.erp.notification.mapper;

import com.smartinstitute.erp.notification.dto.response.NotificationResponse;
import com.smartinstitute.erp.notification.entity.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting Notification entities
 * into Notification response DTOs.
 */
@Component
public class NotificationMapper {

    /**
     * Converts a Notification entity into a response DTO.
     *
     * @param notification notification entity
     * @return notification response DTO
     */
    public NotificationResponse toResponse(Notification notification) {

        if (notification == null) {
            return null;
        }

        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setReferenceType(notification.getReferenceType());
        response.setReferenceId(notification.getReferenceId());
        response.setRead(notification.isRead());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }
}