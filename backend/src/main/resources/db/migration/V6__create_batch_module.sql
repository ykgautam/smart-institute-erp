CREATE TABLE batches
(
    id BIGSERIAL PRIMARY KEY,

    batch_code VARCHAR(30) NOT NULL,
    batch_name VARCHAR(100) NOT NULL,

    course_id BIGINT NOT NULL,
    faculty_id BIGINT,
    institute_id BIGINT NOT NULL,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    capacity INTEGER NOT NULL,

    status VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_batch_course
        FOREIGN KEY (course_id)
            REFERENCES courses(id),

    CONSTRAINT fk_batch_faculty
        FOREIGN KEY (faculty_id)
            REFERENCES users(id),

    CONSTRAINT fk_batch_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT uk_batch_code
        UNIQUE (institute_id, batch_code),

    CONSTRAINT uk_batch_name
        UNIQUE (institute_id, batch_name)
);

ALTER TABLE students
    ADD COLUMN batch_id BIGINT;

ALTER TABLE students
    ADD CONSTRAINT fk_student_batch
        FOREIGN KEY (batch_id)
            REFERENCES batches(id);