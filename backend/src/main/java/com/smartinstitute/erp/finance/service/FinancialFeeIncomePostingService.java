package com.smartinstitute.erp.finance.service;

import com.smartinstitute.erp.audit.enums.AuditAction;
import com.smartinstitute.erp.audit.service.AuditLogService;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.fee.entity.FeePayment;
import com.smartinstitute.erp.finance.entity.FinancialCategory;
import com.smartinstitute.erp.finance.entity.FinancialTransaction;
import com.smartinstitute.erp.finance.enums.FinancialSourceType;
import com.smartinstitute.erp.finance.enums.FinancialTransactionStatus;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import com.smartinstitute.erp.finance.repository.FinancialCategoryRepository;
import com.smartinstitute.erp.finance.repository.FinancialTransactionRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class FinancialFeeIncomePostingService {

    public static final String FEE_COLLECTION_SYSTEM_KEY = "FEE_COLLECTION";

    private static final String FEE_COLLECTION_CATEGORY_NAME =
            "Student Fee Collection";

    private final FinancialCategoryRepository categoryRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final AuditLogService auditLogService;

    public FinancialFeeIncomePostingService(
            FinancialCategoryRepository categoryRepository,
            FinancialTransactionRepository transactionRepository,
            AuditLogService auditLogService) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Posts income for a persisted fee payment.
     *
     * MANDATORY ensures this operation joins the fee-collection transaction;
     * a ledger failure must not leave the fee payment committed on its own.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void postFeePaymentIncome(FeePayment payment) {

        Objects.requireNonNull(payment, "Fee payment is required.");

        if (payment.getId() == null) {
            throw new IllegalArgumentException(
                    "Fee payment must be saved before posting income."
            );
        }

        Institute institute = Objects.requireNonNull(
                payment.getInstitute(),
                "Fee payment institute is required."
        );

        // Check first for a clear application-level duplicate guard.
        // The database unique index remains the final concurrency safeguard.
        boolean alreadyPosted =
                transactionRepository.existsByInstituteAndSourceTypeAndSourceId(
                        institute,
                        FinancialSourceType.FEE_PAYMENT,
                        payment.getId()
                );

        if (alreadyPosted) {
            return;
        }

        FinancialCategory category = categoryRepository
                .findByInstituteAndSystemKey(
                        institute,
                        FEE_COLLECTION_SYSTEM_KEY
                )
                .filter(FinancialCategory::getActive)
                .filter(c -> c.getTransactionType()
                        == FinancialTransactionType.INCOME)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The Student Fee Collection income category is not configured."
                ));

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setInstitute(institute);
        transaction.setCategory(category);
        transaction.setTransactionType(FinancialTransactionType.INCOME);

        // Use the individual payment amount, not the cumulative StudentFee balance.
        transaction.setAmount(payment.getAmount());
        transaction.setTransactionDate(payment.getPaymentDate());
        transaction.setPaymentMode(payment.getPaymentMode());
        transaction.setReferenceNumber(payment.getReceiptNumber());
        transaction.setDescription(
                "Automatic income posting for fee receipt "
                        + payment.getReceiptNumber()
        );
        transaction.setSourceType(FinancialSourceType.FEE_PAYMENT);
        transaction.setSourceId(payment.getId());
        transaction.setStatus(FinancialTransactionStatus.POSTED);

        FinancialTransaction saved = transactionRepository.save(transaction);

        auditLogService.log(
                AuditAction.CREATE,
                "FINANCIAL_TRANSACTION",
                saved.getId(),
                "Automatic fee income posted for receipt "
                        + payment.getReceiptNumber()
        );
    }
}