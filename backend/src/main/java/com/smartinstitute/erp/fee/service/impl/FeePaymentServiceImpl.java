package com.smartinstitute.erp.fee.service.impl;

import com.smartinstitute.erp.common.enums.fee.FeeStatus;
import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.fee.dto.request.CollectFeeRequest;
import com.smartinstitute.erp.fee.dto.response.FeePaymentResponse;
import com.smartinstitute.erp.fee.entity.FeePayment;
import com.smartinstitute.erp.fee.entity.StudentFee;
import com.smartinstitute.erp.fee.mapper.FeePaymentMapper;
import com.smartinstitute.erp.fee.repository.FeePaymentRepository;
import com.smartinstitute.erp.fee.repository.StudentFeeRepository;
import com.smartinstitute.erp.fee.service.FeePaymentService;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class FeePaymentServiceImpl extends BaseCrudService
        implements FeePaymentService {

    private final FeePaymentRepository feePaymentRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentMapper feePaymentMapper;

    public FeePaymentServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            FeePaymentRepository feePaymentRepository,
            StudentFeeRepository studentFeeRepository,
            FeePaymentMapper feePaymentMapper) {

        super(securityUtil, instituteAccessValidator);

        this.feePaymentRepository = feePaymentRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.feePaymentMapper = feePaymentMapper;
    }

    @Override
    public FeePaymentResponse collectFee(CollectFeeRequest request) {

        Institute currentInstitute = getCurrentInstitute();

        StudentFee studentFee = studentFeeRepository
                .findByIdAndInstituteAndActiveTrue(
                        request.getStudentFeeId(),
                        currentInstitute
                )
                .orElseThrow(() -> new ResourceNotFoundException("Student Fee not found."));

        if (studentFee.getStatus() == FeeStatus.PAID) {

            throw new BadRequestException("Fee has already been paid.");
        }

        BigDecimal paymentAmount = BigDecimal.valueOf(request.getAmount());

        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Payment amount must be greater than zero."
            );
        }

        if (paymentAmount.compareTo(
                studentFee.getPendingAmount()) > 0) {

            throw new BadRequestException(
                    "Payment amount cannot be greater than pending amount."
            );
        }

        BigDecimal updatedPaidAmount = studentFee.getPaidAmount()
                .add(paymentAmount);

        BigDecimal updatedPendingAmount = studentFee.getPendingAmount()
                .subtract(paymentAmount);

        studentFee.setPaidAmount(updatedPaidAmount);

        studentFee.setPendingAmount(updatedPendingAmount);

        if (updatedPendingAmount.compareTo(BigDecimal.ZERO) == 0) {

            studentFee.setStatus(FeeStatus.PAID
            );

        } else {

            studentFee.setStatus(FeeStatus.PARTIALLY_PAID);
        }

        FeePayment feePayment = new FeePayment();

        feePayment.setStudentFee(studentFee);

        feePayment.setInstitute(currentInstitute);

        feePayment.setAmount(paymentAmount);

        feePayment.setPaymentDate(LocalDate.now());

        feePayment.setPaymentMode(request.getPaymentMode());

        feePayment.setTransactionReference(request.getTransactionReference());

        feePayment.setRemarks(request.getRemarks());

        feePayment.setReceiptNumber(generateReceiptNumber());

        feePayment = feePaymentRepository.save(feePayment);

        studentFeeRepository.save(studentFee);

        return feePaymentMapper.toResponse(feePayment);
    }

    @Override
    public List<FeePaymentResponse> getPaymentHistory(Long studentFeeId) {

        Institute currentInstitute = getCurrentInstitute();

        StudentFee studentFee = studentFeeRepository
                .findByIdAndInstituteAndActiveTrue(
                        studentFeeId,
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student Fee not found."
                        ));

        List<FeePayment> payments =
                feePaymentRepository
                        .findByStudentFeeAndActiveTrue(
                                studentFee
                        );

        return payments.stream()
                .map(feePaymentMapper::toResponse)
                .toList();
    }

    /**
     * Generates receipt number.
     * <p>
     * Format:
     * <p>
     * RCP-20260703-00001
     */
    private String generateReceiptNumber() {

        String date = LocalDate.now()
                .format(
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        long nextNumber = feePaymentRepository.count() + 1;

        return String.format(
                "RCP-%s-%05d",
                date,
                nextNumber
        );
    }


}