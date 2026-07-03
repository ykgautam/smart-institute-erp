package com.smartinstitute.erp.fee.service.impl;

import com.smartinstitute.erp.common.exception.BadRequestException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.common.service.BaseCrudService;
import com.smartinstitute.erp.common.validation.InstituteAccessValidator;
import com.smartinstitute.erp.fee.dto.request.CreateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.request.UpdateFeeStructureRequest;
import com.smartinstitute.erp.fee.dto.response.FeeStructureResponse;
import com.smartinstitute.erp.fee.entity.FeeStructure;
import com.smartinstitute.erp.fee.mapper.FeeStructureMapper;
import com.smartinstitute.erp.fee.repository.FeeStructureRepository;
import com.smartinstitute.erp.fee.service.FeeStructureService;
import com.smartinstitute.erp.course.entity.Course;
import com.smartinstitute.erp.course.repository.CourseRepository;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.security.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FeeStructureServiceImpl extends BaseCrudService
        implements FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final CourseRepository courseRepository;
    private final FeeStructureMapper feeStructureMapper;

    public FeeStructureServiceImpl(
            SecurityUtil securityUtil,
            InstituteAccessValidator instituteAccessValidator,
            FeeStructureRepository feeStructureRepository,
            CourseRepository courseRepository,
            FeeStructureMapper feeStructureMapper) {

        super(securityUtil, instituteAccessValidator);

        this.feeStructureRepository = feeStructureRepository;
        this.courseRepository = courseRepository;
        this.feeStructureMapper = feeStructureMapper;
    }

    @Override
    public FeeStructureResponse createFeeStructure(
            CreateFeeStructureRequest request) {

        Institute currentInstitute = getCurrentInstitute();

        Course course = courseRepository
                .findByIdAndInstituteAndActiveTrue(
                        request.getCourseId(),
                        currentInstitute
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found."
                        ));

        if (feeStructureRepository
                .existsByCourseAndInstituteAndActiveTrue(
                        course,
                        currentInstitute
                )) {

            throw new BadRequestException(
                    "Fee Structure already exists for this course."
            );
        }

        FeeStructure feeStructure =
                feeStructureMapper.toEntity(request);

        feeStructure.setCourse(course);
        feeStructure.setInstitute(currentInstitute);
        feeStructure.setActive(true);

        feeStructure = feeStructureRepository.save(feeStructure);

        return feeStructureMapper.toResponse(feeStructure);
    }

    @Override
    public FeeStructureResponse updateFeeStructure(
            Long id,
            UpdateFeeStructureRequest request) {

        Institute currentInstitute = getCurrentInstitute();

        FeeStructure feeStructure =
                feeStructureRepository
                        .findByIdAndInstituteAndActiveTrue(
                                id,
                                currentInstitute
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Fee Structure not found."
                                ));

        feeStructureMapper.updateEntity(
                feeStructure,
                request
        );

        feeStructure = feeStructureRepository.save(feeStructure);

        return feeStructureMapper.toResponse(feeStructure);
    }

    @Override
    public FeeStructureResponse getFeeStructure(
            Long id) {

        Institute currentInstitute = getCurrentInstitute();

        FeeStructure feeStructure =
                feeStructureRepository
                        .findByIdAndInstituteAndActiveTrue(
                                id,
                                currentInstitute
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Fee Structure not found."
                                ));

        return feeStructureMapper.toResponse(
                feeStructure
        );
    }

    @Override
    public List<FeeStructureResponse> getAllFeeStructures() {

        Institute currentInstitute = getCurrentInstitute();

        List<FeeStructure> feeStructures =
                feeStructureRepository
                        .findByInstituteAndActiveTrue(
                                currentInstitute
                        );

        return feeStructures
                .stream()
                .map(feeStructureMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteFeeStructure(Long id) {

        Institute currentInstitute = getCurrentInstitute();

        FeeStructure feeStructure =
                feeStructureRepository
                        .findByIdAndInstituteAndActiveTrue(
                                id,
                                currentInstitute
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Fee Structure not found."
                                ));

        feeStructure.setActive(false);

        feeStructureRepository.save(feeStructure);
    }
}