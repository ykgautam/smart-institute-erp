ALTER TABLE students
    ADD COLUMN user_id BIGINT;

ALTER TABLE students
    ADD CONSTRAINT fk_student_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

ALTER TABLE students
    ADD CONSTRAINT uk_student_user
        UNIQUE(user_id);