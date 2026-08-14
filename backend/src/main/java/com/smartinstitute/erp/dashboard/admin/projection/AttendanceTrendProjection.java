package com.smartinstitute.erp.dashboard.admin.projection;

import java.math.BigDecimal;

public interface AttendanceTrendProjection {

    Integer getYear();

    Integer getMonth();

    BigDecimal getAttendancePercentage();
}