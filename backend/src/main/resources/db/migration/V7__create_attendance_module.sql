CREATE TABLE attendance
(
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,

    batch_id BIGINT NOT NULL,

    attendance_date DATE NOT NULL,

    status VARCHAR(20) NOT NULL,

    remarks VARCHAR(500),

    marked_by BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id)
            REFERENCES students(id),

    CONSTRAINT fk_attendance_batch
        FOREIGN KEY (batch_id)
            REFERENCES batches(id),

    CONSTRAINT fk_attendance_marked_by
        FOREIGN KEY (marked_by)
            REFERENCES users(id),

    CONSTRAINT uk_attendance_student_date
        UNIQUE (student_id, attendance_date)
);

CREATE INDEX idx_attendance_student
    ON attendance(student_id);

CREATE INDEX idx_attendance_batch
    ON attendance(batch_id);

CREATE INDEX idx_attendance_date
    ON attendance(attendance_date);