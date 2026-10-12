package com.smartinstitute.erp.finance.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "financial_categories",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_financial_category_institute_name_type",
            columnNames = {
                "institute_id",
                "name",
                "transaction_type"
            }
        ),
        @UniqueConstraint(
            name = "uk_financial_category_institute_system_key",
            columnNames = {
                "institute_id",
                "system_key"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_financial_category_institute",
            columnList = "institute_id"
        ),
        @Index(
            name = "idx_financial_category_type_active",
            columnList = "institute_id, transaction_type, active"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class FinancialCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private FinancialTransactionType transactionType;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Stable identifier for categories maintained by the system.
     * For example, FEE_COLLECTION allows automatic fee posting to
     * find its category without depending on a user-editable name.
     */
    @Column(name = "system_key", length = 50)
    private String systemKey;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}