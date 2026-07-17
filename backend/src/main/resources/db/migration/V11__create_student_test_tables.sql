-- ============================================================
-- V11__create_student_test_tables.sql
-- Module : Online Examination
-- Description :
--      Creates Student Test and Student Answer tables
-- ============================================================

-- ============================================================
-- STUDENT TESTS
-- ============================================================

CREATE TABLE student_tests
(
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,

    test_id BIGINT NOT NULL,

    attempt_no INT NOT NULL,

    status VARCHAR(20) NOT NULL,

    started_at TIMESTAMP NOT NULL,

    submitted_at TIMESTAMP,

    total_questions INT NOT NULL DEFAULT 0,

    correct_answers INT NOT NULL DEFAULT 0,

    wrong_answers INT NOT NULL DEFAULT 0,

    unanswered_questions INT NOT NULL DEFAULT 0,

    total_marks INT NOT NULL DEFAULT 0,

    obtained_marks INT NOT NULL DEFAULT 0,

    percentage DECIMAL(5,2) NOT NULL DEFAULT 0,

    passed BOOLEAN NOT NULL DEFAULT FALSE,

    time_taken_in_seconds INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT uk_student_test_attempt
        UNIQUE (
                student_id,
                test_id,
                attempt_no
            ),

    CONSTRAINT fk_student_test_student
        FOREIGN KEY (student_id)
            REFERENCES students(id),

    CONSTRAINT fk_student_test_test
        FOREIGN KEY (test_id)
            REFERENCES tests(id)
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_student_test_student
    ON student_tests(student_id);

CREATE INDEX idx_student_test_test
    ON student_tests(test_id);

CREATE INDEX idx_student_test_status
    ON student_tests(status);

CREATE INDEX idx_student_test_started_at
    ON student_tests(started_at);

CREATE INDEX idx_student_test_submitted_at
    ON student_tests(submitted_at);

-- ============================================================
-- STUDENT ANSWERS
-- ============================================================

CREATE TABLE student_answers
(
    id BIGSERIAL PRIMARY KEY,

    student_test_id BIGINT NOT NULL,

    question_id BIGINT NOT NULL,

    selected_option_id BIGINT,

    correct BOOLEAN NOT NULL DEFAULT FALSE,

    marks_obtained INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT uk_student_answer
        UNIQUE (
                student_test_id,
                question_id
            ),

    CONSTRAINT fk_student_answer_student_test
        FOREIGN KEY (student_test_id)
            REFERENCES student_tests(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_student_answer_question
        FOREIGN KEY (question_id)
            REFERENCES questions(id),

    CONSTRAINT fk_student_answer_option
        FOREIGN KEY (selected_option_id)
            REFERENCES question_options(id)
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_student_answer_student_test
    ON student_answers(student_test_id);

CREATE INDEX idx_student_answer_question
    ON student_answers(question_id);

CREATE INDEX idx_student_answer_option
    ON student_answers(selected_option_id);

CREATE INDEX idx_student_answer_correct
    ON student_answers(correct);