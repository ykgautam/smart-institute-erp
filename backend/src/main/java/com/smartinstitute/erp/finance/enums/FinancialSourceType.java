package com.smartinstitute.erp.finance.enums;

/**
 * Identifies how a ledger transaction originated.
 * FEE_PAYMENT links an income entry to an existing fee payment.
 */
public enum FinancialSourceType {
    MANUAL,
    FEE_PAYMENT
}