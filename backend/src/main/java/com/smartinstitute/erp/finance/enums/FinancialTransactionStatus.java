package com.smartinstitute.erp.finance.enums;

/**
 * Financial records are retained after voiding so that
 * historical financial activity remains auditable.
 */
public enum FinancialTransactionStatus {
    POSTED,
    VOIDED
}