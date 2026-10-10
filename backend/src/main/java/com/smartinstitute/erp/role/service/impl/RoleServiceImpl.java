package com.smartinstitute.erp.role.service.impl;

import com.smartinstitute.erp.role.dto.response.RoleResponse;
import com.smartinstitute.erp.role.entity.Role;
import com.smartinstitute.erp.role.repository.RoleRepository;
import com.smartinstitute.erp.role.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponse> getRoles() {

        return roleRepository
                .findAllByOrderByIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RoleResponse toResponse(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}