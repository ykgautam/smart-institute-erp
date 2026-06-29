package com.smartinstitute.erp.institute.repository;

import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstituteRepository
        extends JpaRepository<Institute, Long> {

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    Optional<Institute> findByEmail(String email);

    Optional<Institute> findByMobile(String mobile);

}