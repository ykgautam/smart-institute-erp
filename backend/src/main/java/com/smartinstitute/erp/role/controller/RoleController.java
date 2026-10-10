package com.smartinstitute.erp.role.controller;

import com.smartinstitute.erp.common.response.ApiResponse;
import com.smartinstitute.erp.common.response.ApiResponseUtil;

import com.smartinstitute.erp.role.constants.RoleApiConstants;
import com.smartinstitute.erp.role.dto.response.RoleResponse;
import com.smartinstitute.erp.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(RoleApiConstants.BASE_URL)
@Tag(
        name = "Role Management",
        description = "APIs for retrieving role reference data"
)
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(
            summary = "Get Roles",
            description = "Returns available roles for role selection and reference data.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Roles fetched successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getRoles() {

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        roleService.getRoles(),
                        "Roles fetched successfully."
                )
        );
    }
}