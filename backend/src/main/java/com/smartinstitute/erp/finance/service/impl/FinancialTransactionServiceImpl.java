package com.smartinstitute.erp.finance.service.impl;

import com.smartinstitute.erp.audit.enums.AuditAction;
import com.smartinstitute.erp.audit.service.AuditLogService;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.finance.dto.request.FinancialTransactionRequest;
import com.smartinstitute.erp.finance.dto.response.FinancialTransactionResponse;
import com.smartinstitute.erp.finance.entity.FinancialCategory;
import com.smartinstitute.erp.finance.entity.FinancialTransaction;
import com.smartinstitute.erp.finance.enums.FinancialSourceType;
import com.smartinstitute.erp.finance.enums.FinancialTransactionStatus;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import com.smartinstitute.erp.finance.mapper.FinancialMapper;
import com.smartinstitute.erp.finance.repository.FinancialCategoryRepository;
import com.smartinstitute.erp.finance.repository.FinancialTransactionRepository;
import com.smartinstitute.erp.finance.service.FinancialTransactionService;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;


@Service
@Transactional
public class FinancialTransactionServiceImpl
        extends BaseCrudService
        implements FinancialTransactionService {

    private final FinancialCategoryRepository categoryRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final FinancialMapper financialMapper;
    private final AuditLogService auditLogService;

    public FinancialTransactionServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            FinancialCategoryRepository categoryRepository,
            FinancialTransactionRepository transactionRepository,
            FinancialMapper financialMapper,
            AuditLogService auditLogService
    ) {
        super(securityUtil, instituteAccessValidator);
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.financialMapper = financialMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    public FinancialTransactionResponse createTransaction(
            FinancialTransactionRequest request
    ) {
        Institute institute = getCurrentInstitute();

        FinancialCategory category = categoryRepository
                .findByIdAndInstitute(request.getCategoryId(), institute)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Financial category not found."
                ));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new BadRequestException(
                    "Inactive financial categories cannot be used."
            );
        }

        // Category type must match transaction type to prevent
        // income from being recorded against an expense category.
        if (category.getTransactionType() != request.getTransactionType()) {
            throw new BadRequestException(
                    "Category type must match transaction type."
            );
        }

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setInstitute(institute);
        transaction.setCategory(category);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMode(request.getPaymentMode());
        transaction.setReferenceNumber(normalizeOptionalText(
                request.getReferenceNumber()
        ));
        transaction.setPartyName(normalizeOptionalText(
                request.getPartyName()
        ));
        transaction.setDescription(normalizeOptionalText(
                request.getDescription()
        ));

        // Manual API requests cannot create or impersonate fee-payment
        // entries. Automatic fee posting will use a separate service path.
        transaction.setSourceType(FinancialSourceType.MANUAL);
        transaction.setSourceId(null);
        transaction.setStatus(FinancialTransactionStatus.POSTED);

        FinancialTransaction savedTransaction =
                transactionRepository.save(transaction);

        auditLogService.log(
                AuditAction.CREATE,
                "FINANCIAL_TRANSACTION",
                savedTransaction.getId(),
                "Created " + savedTransaction.getTransactionType()
                        + " transaction for amount "
                        + savedTransaction.getAmount()
        );

        return financialMapper.toTransactionResponse(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialTransactionResponse getTransaction(Long id) {
        Institute institute = getCurrentInstitute();

        FinancialTransaction transaction = transactionRepository
                .findByIdAndInstitute(id, institute)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Financial transaction not found."
                ));

        return financialMapper.toTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialTransactionResponse> getTransactions(
            FinancialTransactionType type
    ) {
        Institute institute = getCurrentInstitute();

        List<FinancialTransaction> transactions;

        if (type == null) {
            transactions = transactionRepository
                    .findByInstituteOrderByTransactionDateDescIdDesc(
                            institute
                    );
        } else {
            transactions = transactionRepository
                    .findByInstituteAndTransactionTypeOrderByTransactionDateDescIdDesc(
                            institute,
                            type
                    );
        }

        return transactions.stream()
                .map(financialMapper::toTransactionResponse)
                .toList();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    @Override
    @Transactional
    public FinancialTransactionResponse voidTransaction(Long id, String reason) {

        Institute currentInstitute = getCurrentInstitute();

        // Lock the row during the state transition so two concurrent requests
        // cannot both process the same transaction as a successful void.
        FinancialTransaction transaction = transactionRepository
                .findByIdAndInstituteForUpdate(id, currentInstitute)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Financial transaction not found."
                ));

        if (transaction.getStatus() == FinancialTransactionStatus.VOIDED) {
            throw new BadRequestException(
                    "Financial transaction has already been voided."
            );
        }

        String normalizedReason = reason == null ? "" : reason.trim();

        // Validate again at the service boundary because this method may
        // eventually be called from another internal workflow, not just HTTP.
        if (normalizedReason.isBlank()) {
            throw new BadRequestException(
                    "Void reason is required."
            );
        }

        if (normalizedReason.length() > 500) {
            throw new BadRequestException(
                    "Void reason cannot exceed 500 characters."
            );
        }

        transaction.setStatus(FinancialTransactionStatus.VOIDED);
        transaction.setVoidReason(normalizedReason);
        transaction.setVoidedAt(LocalDateTime.now());

        FinancialTransaction savedTransaction =
                transactionRepository.save(transaction);

        // Reuse the central audit service so void actions follow the same
        // audit trail as other Finance operations.
        auditLogService.log(
                AuditAction.UPDATE,
                "FINANCIAL_TRANSACTION",
                savedTransaction.getId(),
                "Financial transaction voided. Reason: " + normalizedReason
        );

        return financialMapper.toTransactionResponse(savedTransaction);
    }

}