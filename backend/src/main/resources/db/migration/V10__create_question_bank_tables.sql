-- ============================================================
-- V10__create_question_bank_tables.sql
-- Module : Question Bank
-- Description :
--      Creates Question and Question Option tables
-- ============================================================


-- ============================================================
-- QUESTIONS
-- ============================================================

CREATE TABLE questions
(
    id BIGSERIAL PRIMARY KEY,

    question_text VARCHAR(2000) NOT NULL,

    question_type VARCHAR(20) NOT NULL,

    difficulty VARCHAR(20) NOT NULL,

    explanation VARCHAR(3000),

    marks INT NOT NULL DEFAULT 1,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    course_id BIGINT NOT NULL,

    topic_id BIGINT NOT NULL,

    institute_id BIGINT NOT NULL,

    display_order INT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_question_course
        FOREIGN KEY (course_id)
            REFERENCES courses(id),

    CONSTRAINT fk_question_topic
        FOREIGN KEY (topic_id)
            REFERENCES topics(id),

    CONSTRAINT fk_question_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id)
);


CREATE INDEX idx_question_course
    ON questions(course_id);

CREATE INDEX idx_question_topic
    ON questions(topic_id);

CREATE INDEX idx_question_institute
    ON questions(institute_id);

CREATE INDEX idx_question_active
    ON questions(active);

CREATE INDEX idx_question_difficulty
    ON questions(difficulty);

CREATE INDEX idx_question_type
    ON questions(question_type);



-- ============================================================
-- QUESTION OPTIONS
-- ============================================================

CREATE TABLE question_options
(
    id BIGSERIAL PRIMARY KEY,

    question_id BIGINT NOT NULL,

    option_text VARCHAR(1000) NOT NULL,

    correct BOOLEAN NOT NULL DEFAULT FALSE,

    display_order INT NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT uk_question_option
        UNIQUE (question_id, option_text),

    CONSTRAINT fk_question_option_question
        FOREIGN KEY (question_id)
            REFERENCES questions(id)
);


CREATE INDEX idx_question_option_question
    ON question_options(question_id);

CREATE INDEX idx_question_option_active
    ON question_options(active);

CREATE INDEX idx_question_option_display_order
    ON question_options(display_order);


-- ============================================================
-- TEST QUESTIONS
-- ============================================================

CREATE TABLE test_questions
(
    id BIGSERIAL,

    test_id BIGINT NOT NULL,

    question_id BIGINT NOT NULL,

    display_order INT NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT pk_test_questions
        PRIMARY KEY (id),

    CONSTRAINT uk_test_question
        UNIQUE (test_id, question_id),

    CONSTRAINT fk_test_question_test
        FOREIGN KEY (test_id)
            REFERENCES tests(id),

    CONSTRAINT fk_test_question_question
        FOREIGN KEY (question_id)
            REFERENCES questions(id)
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_test_question_test
    ON test_questions(test_id);

CREATE INDEX idx_test_question_question
    ON test_questions(question_id);

CREATE INDEX idx_test_question_display_order
    ON test_questions(display_order);

CREATE INDEX idx_test_question_active
    ON test_questions(active);
