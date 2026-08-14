package com.smartinstitute.erp.fee.repository;

import com.smartinstitute.erp.dashboard.admin.projection.FeeCollectionTrendProjection;
import com.smartinstitute.erp.fee.entity.FeePayment;
import com.smartinstitute.erp.fee.entity.StudentFee;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FeePaymentRepository
        extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByStudentFeeOrderByPaymentDateDesc(
            StudentFee studentFee
    );

    List<FeePayment> findByStudentFeeAndActiveTrue(
            StudentFee studentFee
    );

    @Query("""
        SELECT COALESCE(SUM(fp.amount), 0)
        FROM FeePayment fp
        WHERE fp.institute = :institute
          AND fp.paymentDate = :paymentDate
          AND fp.active = true
        """)
    BigDecimal getCollectionForDate(
            Institute institute,
            LocalDate paymentDate
    );

    @Query(value = """
        SELECT
            EXTRACT(YEAR FROM fp.payment_date)::INTEGER AS year,
            EXTRACT(MONTH FROM fp.payment_date)::INTEGER AS month,
            COALESCE(SUM(fp.amount), 0) AS totalCollection

        FROM fee_payments fp

        WHERE fp.institute_id = :#{#institute.id}
          AND fp.active = TRUE

        GROUP BY
            EXTRACT(YEAR FROM fp.payment_date),
            EXTRACT(MONTH FROM fp.payment_date)

        ORDER BY
            year,
            month
        """,
            nativeQuery = true)
    List<FeeCollectionTrendProjection> getFeeCollectionTrend(
            Institute institute
    );
}