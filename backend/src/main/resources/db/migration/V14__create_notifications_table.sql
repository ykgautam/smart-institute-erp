-- ============================================================
-- V14__create_notifications_table.sql
-- Module : Notification
-- Description :
--      Creates the notifications table for storing
--      in-application notifications for ERP users.
-- ============================================================


-- ============================================================
-- NOTIFICATIONS
-- ============================================================

CREATE TABLE notifications
(
    id BIGSERIAL PRIMARY KEY,

    institute_id BIGINT NOT NULL,

    user_id BIGINT,

    type VARCHAR(50) NOT NULL,

    title VARCHAR(200) NOT NULL,

    message VARCHAR(1000) NOT NULL,

    reference_type VARCHAR(100),

    reference_id BIGINT,

    is_read BOOLEAN NOT NULL DEFAULT FALSE,

    read_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_notification_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);


-- ============================================================
-- INDEXES
-- ============================================================

-- Supports institute-level notification queries.
CREATE INDEX idx_notification_institute
    ON notifications(institute_id);

-- Supports filtering notifications by recipient user.
CREATE INDEX idx_notification_user
    ON notifications(user_id);

-- Supports retrieving unread notifications for a user.
CREATE INDEX idx_notification_user_read
    ON notifications(user_id, is_read);

-- Supports chronological notification queries.
CREATE INDEX idx_notification_created_at
    ON notifications(created_at);

-- Supports the common tenant + chronological query:
-- WHERE institute_id = ?
-- ORDER BY created_at DESC
CREATE INDEX idx_notification_institute_created
    ON notifications(institute_id, created_at);