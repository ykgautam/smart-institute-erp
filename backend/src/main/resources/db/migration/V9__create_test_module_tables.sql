-- ============================================================
-- V9__create_test_module_tables.sql
-- Module : Test / Examination
-- Description :
--      Creates Topic and Test tables
-- ============================================================

-- ============================================================
-- TOPICS
-- ============================================================

CREATE TABLE topics
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    display_order INT NOT NULL DEFAULT 1,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    course_id BIGINT NOT NULL,

    institute_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT uk_topic_name_course
        UNIQUE (institute_id, course_id, name),

    CONSTRAINT fk_topic_course
        FOREIGN KEY (course_id)
            REFERENCES courses (id),

    CONSTRAINT fk_topic_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes (id)
);

CREATE INDEX idx_topic_course
    ON topics(course_id);

CREATE INDEX idx_topic_institute
    ON topics(institute_id);

CREATE INDEX idx_topic_active
    ON topics(active);

-- ============================================================
-- TESTS
-- ============================================================

CREATE TABLE tests
(
    id BIGSERIAL PRIMARY KEY,

    title VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    course_id BIGINT NOT NULL,

    topic_id BIGINT NOT NULL,

    institute_id BIGINT NOT NULL,

    test_type VARCHAR(20) NOT NULL,

    status VARCHAR(20) NOT NULL,

    passing_percentage INT NOT NULL,

    shuffle_questions BOOLEAN NOT NULL DEFAULT TRUE,

    shuffle_options BOOLEAN NOT NULL DEFAULT TRUE,

    timer_enabled BOOLEAN NOT NULL DEFAULT FALSE,

    duration_minutes INT,

    show_explanation_after_submission BOOLEAN
        NOT NULL DEFAULT TRUE,

    max_attempts INT NOT NULL DEFAULT 1,

    start_time TIMESTAMP,
    end_time TIMESTAMP,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT uk_test_title_course_topic
        UNIQUE (
                institute_id,
                course_id,
                topic_id,
                title
            ),

    CONSTRAINT fk_test_course
        FOREIGN KEY (course_id)
            REFERENCES courses(id),

    CONSTRAINT fk_test_topic
        FOREIGN KEY (topic_id)
            REFERENCES topics(id),

    CONSTRAINT fk_test_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT chk_test_passing_percentage
    CHECK (passing_percentage BETWEEN 0 AND 100)
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_test_course
    ON tests(course_id);

CREATE INDEX idx_test_topic
    ON tests(topic_id);

CREATE INDEX idx_test_institute
    ON tests(institute_id);

CREATE INDEX idx_test_status
    ON tests(status);

CREATE INDEX idx_test_type
    ON tests(test_type);

CREATE INDEX idx_test_active
    ON tests(active);
