package com.smartinstitute.erp.finance.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.fee.PaymentMode;
import com.smartinstitute.erp.finance.enums.FinancialSourceType;
import com.smartinstitute.erp.finance.enums.FinancialTransactionStatus;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import com.smartinstitute.erp.institute.entity.Institute;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "financial_transactions",
    indexes = {
        @Index(
            name = "idx_financial_tx_institute_date",
            columnList = "institute_id, transaction_date"
        ),
        @Index(
            name = "idx_financial_tx_institute_type_status",
            columnList = "institute_id, transaction_type, status"
        ),
        @Index(
            name = "idx_financial_tx_category",
            columnList = "category_id"
        ),
        @Index(
            name = "idx_financial_tx_source",
            columnList = "institute_id, source_type, source_id"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class FinancialTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private FinancialCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private FinancialTransactionType transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(
        name = "amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false, length = 30)
    private PaymentMode paymentMode;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "party_name", length = 200)
    private String partyName;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private FinancialSourceType sourceType = FinancialSourceType.MANUAL;

    /**
     * References the originating record, such as FeePayment.id.
     * Source IDs are not polymorphic foreign keys; the service and
     * database constraints must enforce valid source relationships.
     */
    @Column(name = "source_id")
    private Long sourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FinancialTransactionStatus status =
        FinancialTransactionStatus.POSTED;

    @Column(name = "void_reason", length = 500)
    private String voidReason;

    @Column(name = "voided_at")
    private LocalDateTime voidedAt;
}