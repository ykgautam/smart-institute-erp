package com.smartinstitute.erp.finance.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MonthlyFinanceAggregate {

    LocalDate getMonthStart();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}