package com.smartinstitute.erp.finance.enums;

/**
 * Separates incoming funds from outgoing expenses.
 * Categories and transactions use the same type so that
 * an expense cannot accidentally be assigned an income category.
 */
public enum FinancialTransactionType {
    INCOME,
    EXPENSE
}