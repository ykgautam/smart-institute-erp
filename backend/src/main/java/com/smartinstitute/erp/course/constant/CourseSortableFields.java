package com.smartinstitute.erp.course.constant;

import java.util.Set;

public final class CourseSortableFields {

    private CourseSortableFields() {
    }

    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "id",
            "courseCode",
            "courseName",
            "duration",
            "fee",
            "status",
            "createdAt"
    );

}