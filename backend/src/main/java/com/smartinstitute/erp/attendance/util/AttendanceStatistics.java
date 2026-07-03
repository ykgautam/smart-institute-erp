package com.smartinstitute.erp.attendance.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceStatistics {

    private int totalClasses;

    private int totalPresent;

    private int totalAbsent;

    private int totalLate;

    private int totalLeave;

    private double attendancePercentage;

}