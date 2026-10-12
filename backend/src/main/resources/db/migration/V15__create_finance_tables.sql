-- ============================================================
-- Module: Finance
-- Purpose: Configurable financial categories and ledger entries
-- Tenant isolation is enforced through institute_id.
-- ============================================================

CREATE TABLE financial_categories
(
    id BIGSERIAL PRIMARY KEY,

    institute_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,

    transaction_type VARCHAR(20) NOT NULL,

    description VARCHAR(500),

    -- Stable system identifier for automatic postings.
    -- NULL for ordinary user-created categories.
    system_key VARCHAR(50),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_at TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM',

    CONSTRAINT fk_financial_category_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT chk_financial_category_type
        CHECK (transaction_type IN ('INCOME', 'EXPENSE')),

    CONSTRAINT uk_financial_category_institute_name_type
        UNIQUE (institute_id, name, transaction_type),

    CONSTRAINT uk_financial_category_institute_system_key
        UNIQUE (institute_id, system_key)
);

CREATE INDEX idx_financial_category_institute
    ON financial_categories(institute_id);

CREATE INDEX idx_financial_category_type_active
    ON financial_categories(institute_id, transaction_type, active);


CREATE TABLE financial_transactions
(
    id BIGSERIAL PRIMARY KEY,

    institute_id BIGINT NOT NULL,

    category_id BIGINT NOT NULL,

    transaction_type VARCHAR(20) NOT NULL,

    transaction_date DATE NOT NULL,

    amount NUMERIC(12, 2) NOT NULL,

    payment_mode VARCHAR(30) NOT NULL,

    reference_number VARCHAR(100),

    party_name VARCHAR(200),

    description VARCHAR(1000),

    source_type VARCHAR(30) NOT NULL DEFAULT 'MANUAL',

    source_id BIGINT,

    status VARCHAR(20) NOT NULL DEFAULT 'POSTED',

    void_reason VARCHAR(500),

    voided_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_at TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM',

    CONSTRAINT fk_financial_tx_institute
        FOREIGN KEY (institute_id)
            REFERENCES institutes(id),

    CONSTRAINT fk_financial_tx_category
        FOREIGN KEY (category_id)
            REFERENCES financial_categories(id),

    CONSTRAINT chk_financial_tx_type
        CHECK (transaction_type IN ('INCOME', 'EXPENSE')),

    CONSTRAINT chk_financial_tx_amount
        CHECK (amount > 0),

    CONSTRAINT chk_financial_tx_source_type
        CHECK (source_type IN ('MANUAL', 'FEE_PAYMENT')),

    CONSTRAINT chk_financial_tx_status
        CHECK (status IN ('POSTED', 'VOIDED')),

    CONSTRAINT chk_financial_tx_void_reason
        CHECK (
            status <> 'VOIDED'
                OR (
                void_reason IS NOT NULL
                    AND LENGTH(TRIM(void_reason)) > 0
                )
            )
);

CREATE INDEX idx_financial_tx_institute_date
    ON financial_transactions(institute_id, transaction_date);

CREATE INDEX idx_financial_tx_institute_type_status
    ON financial_transactions(
                              institute_id,
                              transaction_type,
                              status
        );

CREATE INDEX idx_financial_tx_category
    ON financial_transactions(category_id);

CREATE INDEX idx_financial_tx_source
    ON financial_transactions(
                              institute_id,
                              source_type,
                              source_id
        );

-- A fee payment must never produce duplicate income ledger entries.
-- Manual transactions can have NULL source IDs and remain independent.
CREATE UNIQUE INDEX uk_financial_tx_fee_payment_source
    ON financial_transactions(institute_id, source_id)
    WHERE source_type = 'FEE_PAYMENT'
      AND source_id IS NOT NULL;