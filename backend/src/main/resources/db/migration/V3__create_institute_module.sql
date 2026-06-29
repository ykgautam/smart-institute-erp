-- ==========================================================
-- Sprint 3.2
-- Institute Module
-- ==========================================================

-- ==========================================================
-- Create Institutes Table
-- ==========================================================

CREATE TABLE institutes
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(150) NOT NULL,

    email VARCHAR(100) UNIQUE,

    mobile VARCHAR(20) UNIQUE,

    landline VARCHAR(20),

    address VARCHAR(300),

    city VARCHAR(100),

    state VARCHAR(100),

    pincode VARCHAR(10),

    gst_number VARCHAR(30),

    website VARCHAR(150),

    logo_path VARCHAR(300),

    type VARCHAR(30) NOT NULL,

    status VARCHAR(20) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    owner_user_id BIGINT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    CONSTRAINT chk_institute_type
        CHECK (type IN (
                        'COMPUTER_INSTITUTE',
                        'SCHOOL',
                        'COLLEGE',
                        'TRAINING_CENTER'
            )),

    CONSTRAINT chk_institute_status
        CHECK (status IN (
                          'ACTIVE',
                          'INACTIVE'
            ))
);

-- ==========================================================
-- Add institute_id to Users
-- ==========================================================

ALTER TABLE users
    ADD COLUMN institute_id BIGINT;

-- ==========================================================
-- Foreign Keys
-- ==========================================================

ALTER TABLE users
    ADD CONSTRAINT fk_user_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id);

ALTER TABLE institutes
    ADD CONSTRAINT fk_institute_owner
        FOREIGN KEY (owner_user_id)
            REFERENCES users(id);

-- ==========================================================
-- Indexes
-- ==========================================================

CREATE INDEX idx_user_institute
    ON users(institute_id);

CREATE INDEX idx_institute_owner
    ON institutes(owner_user_id);

CREATE INDEX idx_institute_name
    ON institutes(name);

CREATE INDEX idx_institute_email
    ON institutes(email);

CREATE INDEX idx_institute_mobile
    ON institutes(mobile);