package com.smartinstitute.erp.batch.repository;

import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BatchRepository extends
        JpaRepository<Batch, Long>,
        JpaSpecificationExecutor<Batch> {

    boolean existsByInstituteAndBatchCodeIgnoreCase(
            Institute institute,
            String batchCode
    );

    boolean existsByInstituteAndBatchNameIgnoreCase(
            Institute institute,
            String batchName
    );

    Optional<Batch> findByIdAndInstituteAndActiveTrue(
            Long id,
            Institute institute
    );

    boolean existsByInstituteAndBatchCodeIgnoreCaseAndIdNot(
            Institute institute,
            String batchCode,
            Long id
    );

    boolean existsByInstituteAndBatchNameIgnoreCaseAndIdNot(
            Institute institute,
            String batchName,
            Long id
    );

    Optional<Batch> findByIdAndInstitute(Long id, Institute institute);
}