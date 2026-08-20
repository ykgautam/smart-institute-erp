-- ============================================================
-- V13__create_audit_logs_table.sql
-- Module : Audit
-- Description :
--      Creates centralized audit log table for tracking
--      important business and security-related activities.
-- ============================================================

-- ============================================================
-- AUDIT LOGS
-- ============================================================

CREATE TABLE audit_logs
(
    id BIGSERIAL PRIMARY KEY,

    institute_id BIGINT NOT NULL,

    user_id BIGINT,

    action VARCHAR(50) NOT NULL,

    entity_type VARCHAR(100) NOT NULL,

    entity_id BIGINT,

    description VARCHAR(1000),

    old_value JSONB,

    new_value JSONB,

    ip_address VARCHAR(100),

    user_agent VARCHAR(500),

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_audit_log_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT fk_audit_log_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

-- ============================================================
-- INDEXES
-- ============================================================

-- Supports institute-level audit queries.
CREATE INDEX idx_audit_log_institute
    ON audit_logs(institute_id);

-- Supports filtering audit records by user.
CREATE INDEX idx_audit_log_user
    ON audit_logs(user_id);

-- Supports filtering audit records by action.
CREATE INDEX idx_audit_log_action
    ON audit_logs(action);

-- Supports retrieving the history of a specific entity.
CREATE INDEX idx_audit_log_entity
    ON audit_logs(entity_type, entity_id);

-- Supports chronological audit queries.
CREATE INDEX idx_audit_log_created_at
    ON audit_logs(created_at);

-- Supports the common tenant + chronological query:
-- WHERE institute_id = ?
-- ORDER BY created_at DESC
CREATE INDEX idx_audit_log_institute_created
    ON audit_logs(institute_id, created_at);