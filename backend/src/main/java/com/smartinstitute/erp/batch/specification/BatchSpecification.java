package com.smartinstitute.erp.batch.specification;

import com.smartinstitute.erp.batch.entity.Batch;
import com.smartinstitute.erp.institute.entity.Institute;
import org.springframework.data.jpa.domain.Specification;

public final class BatchSpecification {

    private BatchSpecification() {
    }

    public static Specification<Batch> search(
            Institute institute,
            String keyword) {

        return (root, query, cb) -> {

            var predicate = cb.and(

                    cb.equal(
                            root.get("institute"),
                            institute
                    ),

                    cb.isTrue(
                            root.get("active")
                    )
            );

            if (keyword == null
                    || keyword.isBlank()) {

                return predicate;
            }

            String searchKeyword =
                    "%" + keyword.toLowerCase() + "%";

            return cb.and(

                    predicate,

                    cb.or(

                            cb.like(

                                    cb.lower(
                                            root.get("batchCode")
                                    ),

                                    searchKeyword
                            ),

                            cb.like(

                                    cb.lower(
                                            root.get("batchName")
                                    ),

                                    searchKeyword
                            )
                    )
            );

        };

    }

}