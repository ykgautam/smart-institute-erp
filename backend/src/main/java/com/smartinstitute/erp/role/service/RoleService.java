package com.smartinstitute.erp.role.service;

import com.smartinstitute.erp.role.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getRoles();
}