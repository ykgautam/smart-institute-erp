package com.smartinstitute.erp.notification.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.notification.enums.NotificationType;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Represents an in-application notification for an ERP user.
 *
 * <p>
 * Notifications are tenant-specific and are associated with
 * both an institute and a recipient user.
 * </p>
 *
 * <p>
 * The notification module currently supports only in-app
 * notifications. External delivery mechanisms such as
 * email, SMS, WhatsApp, or Kafka are intentionally not part
 * of the current implementation.
 * </p>
 */
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notification_institute",
                        columnList = "institute_id"
                ),
                @Index(
                        name = "idx_notification_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_notification_user_read",
                        columnList = "user_id, is_read"
                ),
                @Index(
                        name = "idx_notification_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_notification_institute_created",
                        columnList = "institute_id, created_at"
                )
        }
)
public class Notification extends BaseEntity {

    /**
     * Institute to which this notification belongs.
     *
     * <p>
     * Mandatory for maintaining tenant isolation.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "institute_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_institute")
    )
    private Institute institute;

    /**
     * User who should receive this notification.
     *
     * <p>
     * Nullable because the system may support
     * institute-wide notifications in future.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_notification_user")
    )
    private User user;

    /**
     * Business type of the notification.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private NotificationType type;

    /**
     * Short title displayed to the user.
     */
    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    /**
     * Detailed notification message.
     */
    @Column(
            nullable = false,
            length = 1000
    )
    private String message;

    /**
     * Type of the business entity related to this notification.
     *
     * <p>
     * Examples:
     * FEE_PAYMENT, EXAM_RESULT, BATCH, ATTENDANCE
     * </p>
     */
    @Column(
            name = "reference_type",
            length = 100
    )
    private String referenceType;

    /**
     * ID of the business entity related to this notification.
     *
     * <p>
     * This field is intentionally not represented as a JPA
     * relationship because a notification can refer to
     * different business entities.
     * </p>
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * Indicates whether the user has read the notification.
     */
    @Column(
            name = "is_read",
            nullable = false
    )
    private boolean read;

    /**
     * Timestamp at which the notification was marked as read.
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification() {
    }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}