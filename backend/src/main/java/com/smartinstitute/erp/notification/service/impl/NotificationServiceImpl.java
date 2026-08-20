package com.smartinstitute.erp.notification.service.impl;

import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.notification.dto.request.NotificationFilterRequest;
import com.smartinstitute.erp.notification.dto.response.NotificationResponse;
import com.smartinstitute.erp.notification.entity.Notification;
import com.smartinstitute.erp.notification.enums.NotificationType;
import com.smartinstitute.erp.notification.mapper.NotificationMapper;
import com.smartinstitute.erp.notification.repository.NotificationRepository;
import com.smartinstitute.erp.notification.repository.NotificationSpecification;
import com.smartinstitute.erp.notification.service.NotificationService;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.user.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Notification operations.
 *
 * <p>
 * Responsible for:
 * </p>
 *
 * <ul>
 *     <li>Retrieving notifications for the authenticated user</li>
 *     <li>Applying notification filters</li>
 *     <li>Retrieving unread notification count</li>
 *     <li>Marking notifications as read</li>
 *     <li>Creating notifications for business modules</li>
 * </ul>
 *
 * <p>
 * Extends BaseCrudService so that notification operations reuse
 * the application's existing security and institute-access
 * infrastructure.
 * </p>
 */
@Service
@Transactional()
public class NotificationServiceImpl
        extends BaseCrudService
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EntityManager entityManager;

    public NotificationServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            EntityManager entityManager) {

        super(
                securityUtil,
                instituteAccessValidator
        );

        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.entityManager = entityManager;
    }

    /**
     * Retrieves notifications for the currently authenticated user.
     *
     * @param request notification filter request
     * @return list of notification responses
     */
    @Override
    public List<NotificationResponse> getNotifications(
            NotificationFilterRequest request) {

        Long instituteId = getCurrentInstituteId();
        Long userId = securityUtil.getCurrentUserId();

//        // temporary
//        createNotification(
//                userId,
//                instituteId,
//                NotificationType.FEE_PAYMENT_SUCCESS,
//                "Fee Payment Successful",
//                "Your fee payment has been successfully recorded.",
//                "FEE_PAYMENT",
//                1L
//        );

        Specification<Notification> specification =
                NotificationSpecification.hasInstituteId(instituteId)
                        .and(NotificationSpecification.hasUserId(userId));

        if (request != null && request.getRead() != null) {

            specification = specification.and(
                    NotificationSpecification.hasReadStatus(
                            request.getRead()
                    )
            );
        }

        if (request != null && request.getType() != null) {

            specification = specification.and(
                    NotificationSpecification.hasType(
                            request.getType()
                    )
            );
        }

        List<Notification> notifications =
                notificationRepository.findAll(
                        specification,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        return notifications.stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves unread notification count for the
     * currently authenticated user.
     *
     * @return unread notification count
     */
    @Override
    public long getUnreadCount() {

        Long userId = securityUtil.getCurrentUserId();

        return notificationRepository.countByUserIdAndReadFalse(
                userId
        );
    }

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId notification ID
     */
    @Override
    @Transactional
    public void markAsRead(Long notificationId) {

        Long userId = securityUtil.getCurrentUserId();

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found."
                                )
                        );

        /*
         * Avoid unnecessary database update when the
         * notification is already marked as read.
         */
        if (notification.isRead()) {
            return;
        }

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    /**
     * Marks all unread notifications belonging to the
     * currently authenticated user as read.
     */
    @Override
    @Transactional
    public void markAllAsRead() {

        Long instituteId = getCurrentInstituteId();
        Long userId = securityUtil.getCurrentUserId();

        notificationRepository.markAllAsRead(
                instituteId,
                userId,
                LocalDateTime.now()
        );
    }

    /**
     * Creates an in-application notification.
     *
     * <p>
     * Entity references are obtained using EntityManager#getReference.
     * This avoids loading the complete User and Institute entities
     * when only their IDs are required for the notification relationship.
     * </p>
     *
     * @param userId recipient user ID
     * @param instituteId institute ID
     * @param type notification type
     * @param title notification title
     * @param message notification message
     * @param referenceType related business entity type
     * @param referenceId related business entity ID
     */
    @Override
    @Transactional
    public void createNotification(
            Long userId,
            Long instituteId,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            Long referenceId) {

        User user =
                entityManager.getReference(
                        User.class,
                        userId
                );

        Institute institute =
                entityManager.getReference(
                        Institute.class,
                        instituteId
                );

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setInstitute(institute);

        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);

        notification.setRead(false);
        notification.setReadAt(null);

        notificationRepository.save(notification);
    }
}