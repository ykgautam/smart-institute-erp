package com.smartinstitute.erp.batch.constant;

import java.util.Set;

public final class BatchSortableFields {

    private BatchSortableFields() {
    }

    public static final Set<String> ALLOWED_FIELDS = Set.of(

            "id",

            "batchCode",

            "batchName",

            "startDate",

            "endDate",

            "capacity",

            "status",

            "createdAt"

    );

}