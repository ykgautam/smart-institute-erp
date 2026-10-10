package com.smartinstitute.erp.role.repository;

import com.smartinstitute.erp.common.enums.RoleType;
import com.smartinstitute.erp.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleType name);

    /*
     * Roles are reference/master data, so returning them ordered by
     * their database ID gives the frontend a deterministic response.
     */
    List<Role> findAllByOrderByIdAsc();

}