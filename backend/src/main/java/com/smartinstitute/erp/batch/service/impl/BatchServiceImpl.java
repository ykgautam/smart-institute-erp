package com.smartinstitute.erp.batch.service.impl;

import com.smartinstitute.erp.batch.dto.request.BatchStatusRequest;
import com.smartinstitute.erp.batch.dto.request.CreateBatchRequest;
import com.smartinstitute.erp.batch.dto.request.UpdateBatchRequest;
import com.smartinstitute.erp.batch.dto.response.BatchResponse;
import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.batch.mapper.BatchMapper;
import com.smartinstitute.erp.batch.repository.BatchRepository;
import com.smartinstitute.erp.batch.service.BatchService;
import com.smartinstitute.erp.common.enums.RoleType;
import com.smartinstitute.erp.common.enums.UserStatus;
import com.smartinstitute.erp.common.exception.DuplicateResourceException;
import com.smartinstitute.erp.common.exception.InvalidRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.pagination.PageResponse;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import com.smartinstitute.erp.common.pagination.PaginationUtils;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import com.smartinstitute.erp.user.entity.User;
import com.smartinstitute.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import com.smartinstitute.erp.batch.constant.BatchSortableFields;
import com.smartinstitute.erp.batch.specification.BatchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;

    private final BatchMapper batchMapper;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final SecurityUtil securityUtil;

    @Override
    public BatchResponse createBatch(CreateBatchRequest request) {

        validateDuplicateBatch(request.getBatchCode(), request.getBatchName());

        validateDates(request.getStartDate(), request.getEndDate(), request.getStartTime(), request.getEndTime());

        Course course = getCourse(request.getCourseId());

        User faculty = getFaculty(request.getFacultyId());

        Batch batch = batchMapper.toEntity(request);

        batch.setInstitute(securityUtil.getCurrentInstitute());

        batch.setCourse(course);

        batch.setFaculty(faculty);

        Batch savedBatch = batchRepository.save(batch);

        return batchMapper.toResponse(savedBatch);
    }

    @Override
    public BatchResponse updateBatch(Long batchId, UpdateBatchRequest request) {

        Batch batch = getBatchEntity(batchId);

        validateDuplicateBatchForUpdate(batch, request);

        validateDates(request.getStartDate(), request.getEndDate(), request.getStartTime(), request.getEndTime());

        Course course = getCourse(request.getCourseId());

        User faculty = getFaculty(request.getFacultyId());

        batchMapper.updateEntity(batch, request);

        batch.setCourse(course);

        batch.setFaculty(faculty);

        Batch updatedBatch = batchRepository.save(batch);

        return batchMapper.toResponse(updatedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponse getBatchById(Long batchId) {
        Batch batch = getBatchEntity(batchId);
        return batchMapper.toResponse(batch);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BatchResponse> getBatches(PaginationRequest request) {

        PaginationUtils.validatePagination(request);

        if (!BatchSortableFields.ALLOWED_FIELDS.contains(request.getSortBy())) {

            throw new InvalidRequestException("Invalid sort field : " + request.getSortBy());
        }

        Pageable pageable = PaginationUtils.createPageable(request);

        Institute institute = securityUtil.getCurrentInstitute();

        Specification<Batch> specification = BatchSpecification.search(institute, request.getKeyword());

        Page<Batch> batchPage = batchRepository.findAll(specification, pageable);

        List<BatchResponse> responseList = batchPage.getContent().stream().map(batchMapper::toResponse).toList();

        return PaginationUtils.buildPageResponse(batchPage, responseList);
    }

    @Override
    public void updateBatchStatus(Long batchId, BatchStatusRequest request) {

        Batch batch = getBatchEntity(batchId);

        batch.setStatus(request.getStatus());

        batchRepository.save(batch);
    }

    @Override
    public void deleteBatch(Long batchId) {

        Batch batch = getBatchEntity(batchId);

        batch.setActive(false);

        batchRepository.save(batch);
    }

    // ==========================================================
    // Private Helper Methods
    // ==========================================================

    private Batch getBatchEntity(Long batchId) {

        Institute institute = securityUtil.getCurrentInstitute();

        return batchRepository.findByIdAndInstituteAndActiveTrue(batchId, institute).orElseThrow(() -> new ResourceNotFoundException("Batch not found."));
    }

    private Course getCourse(Long courseId) {

        Institute institute = securityUtil.getCurrentInstitute();

        Course course = courseRepository.findByIdAndInstituteAndActiveTrue(courseId, institute).orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        return course;
    }

    private User getFaculty(Long facultyId) {

        if (facultyId == null) {
            return null;
        }

        Institute institute = securityUtil.getCurrentInstitute();

//        User faculty = userRepository.findByIdAndInstituteAndActiveTrue(facultyId, institute).orElseThrow(() -> new ResourceNotFoundException("Faculty not found."));
        User faculty = userRepository
                .findByIdAndInstituteAndStatus(
                        facultyId,
                        institute,
                        UserStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Faculty not found."
                        ));

        if (!RoleType.FACULTY.equals(faculty.getRole().getName())) {

            throw new InvalidRequestException("Selected user is not a faculty.");
        }

        return faculty;
    }

    private void validateDuplicateBatch(String batchCode, String batchName) {

        Institute institute = securityUtil.getCurrentInstitute();

        if (batchRepository.existsByInstituteAndBatchCodeIgnoreCase(institute, batchCode)) {

            throw new DuplicateResourceException("Batch code already exists.");
        }

        if (batchRepository.existsByInstituteAndBatchNameIgnoreCase(institute, batchName)) {

            throw new DuplicateResourceException("Batch name already exists.");
        }

    }

    private void validateDates(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {

        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("End date cannot be before start date.");
        }

        if (startDate.equals(endDate) && endTime.isBefore(startTime)) {
            throw new InvalidRequestException("End time cannot be before start time.");
        }

    }

    private void validateDuplicateBatchForUpdate(
            Batch batch,
            UpdateBatchRequest request) {

        Institute institute =
                securityUtil.getCurrentInstitute();

        if (batchRepository
                .existsByInstituteAndBatchCodeIgnoreCaseAndIdNot(

                        institute,

                        request.getBatchCode(),

                        batch.getId())) {

            throw new DuplicateResourceException(
                    "Batch code already exists."
            );
        }

        if (batchRepository
                .existsByInstituteAndBatchNameIgnoreCaseAndIdNot(

                        institute,

                        request.getBatchName(),

                        batch.getId())) {

            throw new DuplicateResourceException(
                    "Batch name already exists."
            );
        }
    }
}