package com.smartinstitute.erp.finance.repository;

import com.smartinstitute.erp.finance.entity.FinancialTransaction;
import com.smartinstitute.erp.finance.enums.FinancialSourceType;
import com.smartinstitute.erp.finance.enums.FinancialTransactionStatus;
import com.smartinstitute.erp.finance.enums.FinancialTransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.smartinstitute.erp.finance.projection.MonthlyFinanceAggregate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.smartinstitute.erp.finance.dto.response.FinancialCategoryBreakdownResponse;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface FinancialTransactionRepository
        extends JpaRepository<FinancialTransaction, Long> {

    Optional<FinancialTransaction> findByIdAndInstitute(
            Long id,
            Institute institute
    );

    List<FinancialTransaction> findByInstituteOrderByTransactionDateDescIdDesc(
            Institute institute
    );

    List<FinancialTransaction>
    findByInstituteAndTransactionTypeOrderByTransactionDateDescIdDesc(
            Institute institute,
            FinancialTransactionType transactionType
    );

    boolean existsByInstituteAndSourceTypeAndSourceId(
            Institute institute,
            FinancialSourceType sourceType,
            Long sourceId
    );

    List<FinancialTransaction>
    findByInstituteAndStatusAndTransactionType(
            Institute institute,
            FinancialTransactionStatus status,
            FinancialTransactionType transactionType
    );

    /**
     * Locks the institute-scoped transaction while a state change is processed.
     * This prevents concurrent requests from both voiding the same ledger entry.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT transaction
       FROM FinancialTransaction transaction
       WHERE transaction.id = :id
         AND transaction.institute = :institute
       """)
    Optional<FinancialTransaction> findByIdAndInstituteForUpdate(
            @Param("id") Long id,
            @Param("institute") Institute institute
    );

    /**
     * Calculates posted transaction totals for one institute and date range.
     * VOIDED records are excluded so cancelled transactions never affect reports.
     */
    @Query("""
    SELECT COALESCE(SUM(ft.amount), 0)
    FROM FinancialTransaction ft
    WHERE ft.institute = :institute
      AND ft.transactionType = :transactionType
      AND ft.status = :status
      AND ft.transactionDate BETWEEN :fromDate AND :toDate
    """)
    BigDecimal sumByInstituteAndTypeAndStatusAndDateRange(
            @Param("institute") Institute institute,
            @Param("transactionType") FinancialTransactionType transactionType,
            @Param("status") FinancialTransactionStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    /**
     * Aggregates posted income and expenses by calendar month for one institute.
     * Conditional sums keep both series in one database query.
     */
    @Query(value = """
    SELECT
        CAST(DATE_TRUNC('month', ft.transaction_date) AS date) AS "monthStart",
        COALESCE(
            SUM(CASE WHEN ft.transaction_type = 'INCOME'
                     THEN ft.amount ELSE 0 END), 0
        ) AS "totalIncome",
        COALESCE(
            SUM(CASE WHEN ft.transaction_type = 'EXPENSE'
                     THEN ft.amount ELSE 0 END), 0
        ) AS "totalExpense"
    FROM financial_transactions ft
    WHERE ft.institute_id = :instituteId
      AND ft.status = 'POSTED'
      AND ft.transaction_date >= :fromDate
      AND ft.transaction_date <= :toDate
    GROUP BY DATE_TRUNC('month', ft.transaction_date)
    ORDER BY DATE_TRUNC('month', ft.transaction_date)
    """, nativeQuery = true)
    List<MonthlyFinanceAggregate> findMonthlyFinanceAggregates(
            @Param("instituteId") Long instituteId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    /**
     * Groups posted amounts by category without loading individual transactions.
     * The category and transaction must both belong to the requested institute.
     */
    @Query("""
    SELECT new com.smartinstitute.erp.finance.dto.response.FinancialCategoryBreakdownResponse(
        category.id,
        category.name,
        transaction.transactionType,
        SUM(transaction.amount)
    )
    FROM FinancialTransaction transaction
    JOIN transaction.category category
    WHERE transaction.institute = :institute
      AND category.institute = :institute
      AND transaction.transactionType = :transactionType
      AND transaction.status = :status
      AND transaction.transactionDate BETWEEN :fromDate AND :toDate
    GROUP BY category.id, category.name, transaction.transactionType
    ORDER BY SUM(transaction.amount) DESC, category.name ASC
    """)
    List<FinancialCategoryBreakdownResponse> findCategoryBreakdown(
            @Param("institute") Institute institute,
            @Param("transactionType") FinancialTransactionType transactionType,
            @Param("status") FinancialTransactionStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}