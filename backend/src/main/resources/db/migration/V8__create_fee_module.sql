CREATE TABLE fee_structures
(
    id BIGSERIAL PRIMARY KEY,

    course_id BIGINT NOT NULL,

    institute_id BIGINT NOT NULL,

    amount NUMERIC(12,2) NOT NULL,

    description VARCHAR(300),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_fee_structure_course
        FOREIGN KEY (course_id)
            REFERENCES courses(id),

    CONSTRAINT fk_fee_structure_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT uk_fee_structure_course_institute
        UNIQUE (course_id, institute_id)
);

-----------------------------------------------------------------------

CREATE TABLE student_fees
(
    id BIGSERIAL PRIMARY KEY,

    student_id BIGINT NOT NULL,

    fee_structure_id BIGINT NOT NULL,

    institute_id BIGINT NOT NULL,

    total_fee NUMERIC(12,2) NOT NULL,

    discount NUMERIC(12,2) NOT NULL DEFAULT 0,

    final_fee NUMERIC(12,2) NOT NULL,

    paid_amount NUMERIC(12,2) NOT NULL DEFAULT 0,

    pending_amount NUMERIC(12,2) NOT NULL,

    fee_due_date DATE,

    status VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_student_fee_student
        FOREIGN KEY (student_id)
            REFERENCES students(id),

    CONSTRAINT fk_student_fee_structure
        FOREIGN KEY (fee_structure_id)
            REFERENCES fee_structures(id),

    CONSTRAINT fk_student_fee_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT uk_student_fee_student
        UNIQUE (student_id)
);

-----------------------------------------------------------------------

CREATE TABLE fee_payments
(
    id BIGSERIAL PRIMARY KEY,

    student_fee_id BIGINT NOT NULL,

    institute_id BIGINT NOT NULL,

    amount NUMERIC(12,2) NOT NULL,

    payment_date DATE NOT NULL,

    payment_mode VARCHAR(30) NOT NULL,

    transaction_reference VARCHAR(100),

    remarks VARCHAR(300),

    receipt_number VARCHAR(50) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT fk_fee_payment_student_fee
        FOREIGN KEY (student_fee_id)
            REFERENCES student_fees(id),

    CONSTRAINT fk_fee_payment_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT uk_fee_payment_receipt
        UNIQUE (receipt_number)
);

-----------------------------------------------------------------------
-- Indexes
-----------------------------------------------------------------------

CREATE INDEX idx_fee_structure_course
    ON fee_structures(course_id);

CREATE INDEX idx_fee_structure_institute
    ON fee_structures(institute_id);

CREATE INDEX idx_student_fee_student
    ON student_fees(student_id);

CREATE INDEX idx_student_fee_institute
    ON student_fees(institute_id);

CREATE INDEX idx_fee_payment_student_fee
    ON fee_payments(student_fee_id);

CREATE INDEX idx_fee_payment_payment_date
    ON fee_payments(payment_date);

CREATE INDEX idx_fee_payment_institute
    ON fee_payments(institute_id);