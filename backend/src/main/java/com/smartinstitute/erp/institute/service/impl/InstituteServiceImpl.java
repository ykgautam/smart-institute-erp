package com.smartinstitute.erp.institute.service.impl;

import com.smartinstitute.erp.common.enums.InstituteStatus;
import com.smartinstitute.erp.common.enums.RoleType;
import com.smartinstitute.erp.common.enums.UserStatus;
import com.smartinstitute.erp.common.exception.DuplicateResourceException;
import com.smartinstitute.erp.common.exception.ResourceNotFoundException;
import com.smartinstitute.erp.institute.dto.*;
import com.smartinstitute.erp.institute.entity.Institute;
import com.smartinstitute.erp.institute.mapper.InstituteMapper;
import com.smartinstitute.erp.institute.repository.InstituteRepository;
import com.smartinstitute.erp.institute.service.InstituteService;
import com.smartinstitute.erp.role.entity.Role;
import com.smartinstitute.erp.role.repository.RoleRepository;
import com.smartinstitute.erp.user.entity.User;
import com.smartinstitute.erp.user.mapper.UserMapper;
import com.smartinstitute.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InstituteServiceImpl implements InstituteService {

    private final InstituteRepository instituteRepository;
    private final InstituteMapper instituteMapper;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public InstituteResponse createInstitute(CreateInstituteRequest request) {

        validateInstitute(request);

        Institute institute = instituteMapper.toEntity(request);

        institute = instituteRepository.save(institute);

        return instituteMapper.toResponse(institute);
    }

    @Override
    @Transactional(readOnly = true)
    public InstituteResponse getInstituteById(Long id) {

        Institute institute = findInstitute(id);

        return instituteMapper.toResponse(institute);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstituteResponse> getAllInstitutes() {

        return instituteRepository.findAll()
                .stream()
                .map(instituteMapper::toResponse)
                .toList();
    }

    @Override
    public InstituteResponse updateInstitute(
            Long id,
            UpdateInstituteRequest request) {

        Institute institute = findInstitute(id);

        validateUpdate(id, request);

        instituteMapper.updateEntity(institute, request);

        institute = instituteRepository.save(institute);

        return instituteMapper.toResponse(institute);
    }

    @Override
    public void deleteInstitute(Long id) {

        Institute institute = findInstitute(id);

        institute.setActive(false);
        institute.setStatus(InstituteStatus.INACTIVE);

        instituteRepository.save(institute);
    }

    @Override
    public InstituteResponse updateInstituteStatus(
            Long id,
            UpdateInstituteStatusRequest request) {

        Institute institute = findInstitute(id);

        institute.setActive(request.getActive());

        institute.setStatus(
                request.getActive()
                        ? InstituteStatus.ACTIVE
                        : InstituteStatus.INACTIVE
        );

        instituteRepository.save(institute);

        return instituteMapper.toResponse(institute);
    }

    // =====================================================
    // Private Methods
    // =====================================================

    private Institute findInstitute(Long id) {

        return instituteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institute not found with id : " + id
                        ));
    }

    private void validateInstitute(CreateInstituteRequest request) {

        if (request.getEmail() != null &&
                instituteRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Institute email already exists.");
        }

        if (request.getMobile() != null &&
                instituteRepository.existsByMobile(request.getMobile())) {

            throw new DuplicateResourceException(
                    "Institute mobile already exists.");
        }
    }

    private void validateUpdate(
            Long id,
            UpdateInstituteRequest request) {

        if (request.getEmail() != null) {

            instituteRepository.findByEmail(request.getEmail())
                    .ifPresent(existing -> {

                        if (!existing.getId().equals(id)) {

                            throw new DuplicateResourceException(
                                    "Institute email already exists.");
                        }
                    });
        }

        if (request.getMobile() != null) {

            instituteRepository.findByMobile(request.getMobile())
                    .ifPresent(existing -> {

                        if (!existing.getId().equals(id)) {

                            throw new DuplicateResourceException(
                                    "Institute mobile already exists.");
                        }
                    });
        }
    }

    @Override
    @Transactional
    public InstituteOnboardingResponse onboardInstitute(
            InstituteOnboardingRequest request) {

        validateInstitute(request.getInstitute());

        validateOwner(request.getOwner());

        Institute institute =
                instituteMapper.toEntity(request.getInstitute());

        institute = instituteRepository.save(institute);

        RoleType roleType = RoleType.SUPER_ADMIN;
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "SUPER_ADMIN role not found."
                        ));

        OwnerRegistrationRequest ownerRequest = request.getOwner();

        User owner = new User();

        owner.setFirstName(ownerRequest.getFirstName());
        owner.setLastName(ownerRequest.getLastName());
        owner.setEmail(ownerRequest.getEmail());
        owner.setMobile(ownerRequest.getMobile());

        owner.setPassword(
                passwordEncoder.encode(ownerRequest.getPassword())
        );

        owner.setGender(ownerRequest.getGender());

        owner.setStatus(UserStatus.ACTIVE);

        owner.setRole(role);

        owner.setInstitute(institute);

        owner = userRepository.save(owner);

        institute.setOwner(owner);

        instituteRepository.save(institute);

        return InstituteOnboardingResponse.builder()
                .instituteId(institute.getId())
                .instituteName(institute.getName())
                .ownerUserId(owner.getId())
                .ownerName(
                        owner.getFirstName() + " " +
                                owner.getLastName()
                )
                .ownerEmail(owner.getEmail())
                .build();
    }

    private void validateOwner(
            OwnerRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Owner email already exists.");
        }

        if (request.getMobile() != null &&
                userRepository.existsByMobile(request.getMobile())) {

            throw new DuplicateResourceException(
                    "Owner mobile already exists.");
        }
    }
}