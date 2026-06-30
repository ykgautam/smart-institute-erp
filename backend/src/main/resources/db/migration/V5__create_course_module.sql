CREATE TABLE courses
(
    id BIGSERIAL PRIMARY KEY,

    institute_id BIGINT NOT NULL,

    course_code VARCHAR(30) NOT NULL,

    course_name VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    duration INTEGER NOT NULL,

    duration_type VARCHAR(20) NOT NULL,

    fee NUMERIC(12,2) NOT NULL,

    status VARCHAR(20) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    created_by VARCHAR(100),

    updated_at TIMESTAMP,

    updated_by VARCHAR(100),

    CONSTRAINT fk_course_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT uk_course_code
        UNIQUE (institute_id, course_code),

    CONSTRAINT uk_course_name
    UNIQUE (institute_id, course_name)
);

CREATE INDEX idx_course_institute
    ON courses(institute_id);

CREATE INDEX idx_course_name
    ON courses(course_name);

CREATE INDEX idx_course_active
    ON courses(active);

