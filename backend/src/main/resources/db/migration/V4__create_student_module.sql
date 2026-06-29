CREATE TABLE students
(
    id BIGSERIAL PRIMARY KEY,

    admission_number VARCHAR(30) NOT NULL,
    roll_number VARCHAR(30),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),

    gender VARCHAR(20) NOT NULL,

    date_of_birth DATE,

    mobile VARCHAR(20),

    email VARCHAR(150),

    father_name VARCHAR(150) NOT NULL,

    mother_name VARCHAR(150),

    guardian_mobile VARCHAR(20),

    address VARCHAR(300),

    city VARCHAR(100),

    state VARCHAR(100),

    pincode VARCHAR(10),

    photo_path VARCHAR(300),

    admission_date DATE,

    status VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    institute_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT fk_student_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT uk_student_admission
        UNIQUE(institute_id, admission_number),

    CONSTRAINT uk_student_mobile
        UNIQUE(institute_id, mobile),

    CONSTRAINT uk_student_email
        UNIQUE(institute_id, email)
);

CREATE INDEX idx_student_institute
    ON students(institute_id);

CREATE INDEX idx_student_name
    ON students(first_name, last_name);

CREATE INDEX idx_student_status
    ON students(status);