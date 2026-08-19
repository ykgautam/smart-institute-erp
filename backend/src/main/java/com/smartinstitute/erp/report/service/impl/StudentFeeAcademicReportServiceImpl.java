package com.smartinstitute.erp.report.service.impl;

import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.report.dto.request.StudentFeeAcademicReportRequest;
import com.smartinstitute.erp.report.dto.response.StudentFeeAcademicReportPageResponse;
import com.smartinstitute.erp.report.projection.StudentFeeAcademicReportProjection;
import com.smartinstitute.erp.report.repository.StudentFeeAcademicReportRepository;
import com.smartinstitute.erp.report.service.StudentFeeAcademicReportService;
import com.smartinstitute.erp.security.util.SecurityUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentFeeAcademicReportServiceImpl
        extends BaseCrudService
        implements StudentFeeAcademicReportService {

    private final StudentFeeAcademicReportRepository repository;

    public StudentFeeAcademicReportServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            StudentFeeAcademicReportRepository repository) {
        super(securityUtil, instituteAccessValidator);
        this.repository = repository;
    }

    @Override
    public StudentFeeAcademicReportPageResponse
    getStudentFeeAcademicReport(
            StudentFeeAcademicReportRequest request) {

        Long instituteId = getCurrentInstituteId();

        Pageable pageable = PageRequest.of(
                request.getPage(), request.getSize()
        );

        Page<StudentFeeAcademicReportProjection> page =
                repository.getStudentFeeAcademicReportWithSorting(
                        instituteId,
                        request.getCourseId(),
                        request.getBatchId(),
                        request.getStudentId(),
                        pageable,
                        request.getSortBy(),
                        request.getSortDirection()
                );

        return new StudentFeeAcademicReportPageResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }


}