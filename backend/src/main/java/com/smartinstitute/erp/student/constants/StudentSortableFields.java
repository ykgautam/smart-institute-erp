package com.smartinstitute.erp.student.constants;

import java.util.Set;

public final class StudentSortableFields {

    private StudentSortableFields() {
    }

    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "id",
            "firstName",
            "lastName",
            "email",
            "mobile",
            "admissionNumber",
            "rollNumber",
            "createdAt"
    );

}