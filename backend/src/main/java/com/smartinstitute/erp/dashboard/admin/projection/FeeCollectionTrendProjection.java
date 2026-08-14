package com.smartinstitute.erp.dashboard.admin.projection;

import java.math.BigDecimal;

public interface FeeCollectionTrendProjection {

    Integer getYear();

    Integer getMonth();

    BigDecimal getTotalCollection();
}