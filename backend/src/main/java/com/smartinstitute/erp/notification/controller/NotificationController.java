package com.smartinstitute.erp.notification.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;
import com.smartinstitute.erp.notification.dto.request.NotificationFilterRequest;
import com.smartinstitute.erp.notification.dto.response.NotificationResponse;
import com.smartinstitute.erp.notification.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for notification operations.
 *
 * <p>
 * This controller follows the established thin-controller pattern
 * used throughout the ERP application.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    /**
     * Retrieves notifications for the currently authenticated user.
     *
     * <p>
     * Optional filters:
     * <ul>
     *     <li>read=true/false</li>
     *     <li>type=FEE_REMINDER</li>
     * </ul>
     * </p>
     *
     * @param request notification filter request
     * @return list of notifications
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @ModelAttribute NotificationFilterRequest request) {


        List<NotificationResponse> response =
                notificationService.getNotifications(request);

        return ApiResponseUtil.success(
                response,
                "Notifications fetched successfully."
        );
    }

    /**
     * Retrieves the unread notification count for the
     * currently authenticated user.
     *
     * @return unread notification count
     */
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Long> getUnreadCount() {

        long unreadCount =
                notificationService.getUnreadCount();

        return ApiResponseUtil.success(
                unreadCount,
                "Unread notification count fetched successfully."
        );
    }

    /**
     * Marks a specific notification as read.
     *
     * @param id notification ID
     * @return success response
     */
    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long id) {

        notificationService.markAsRead(id);

        return ApiResponseUtil.success(
                null,
                "Notification marked as read successfully."
        );
    }

    /**
     * Marks all notifications of the currently authenticated
     * user as read.
     *
     * @return success response
     */
    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ApiResponseUtil.success(
                null,
                "All notifications marked as read successfully."
        );
    }
}