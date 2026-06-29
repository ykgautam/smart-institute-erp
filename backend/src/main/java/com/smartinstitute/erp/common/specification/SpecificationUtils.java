package com.smartinstitute.erp.common.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public final class SpecificationUtils {

    private SpecificationUtils() {
    }

    public static Predicate likeIgnoreCase(
            CriteriaBuilder cb,
            Expression<String> field,
            String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return cb.conjunction();
        }

        return cb.like(
                cb.lower(field),
                "%" + keyword.toLowerCase() + "%"
        );
    }

    public static Predicate equal(
            CriteriaBuilder cb,
            Expression<?> field,
            Object value) {

        if (value == null) {
            return cb.conjunction();
        }

        return cb.equal(field, value);
    }

    public static Predicate isTrue(
            CriteriaBuilder cb,
            Expression<Boolean> field) {

        return cb.isTrue(field);
    }

}