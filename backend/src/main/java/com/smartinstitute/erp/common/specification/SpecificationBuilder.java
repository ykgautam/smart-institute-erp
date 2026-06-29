package com.smartinstitute.erp.common.specification;

import com.smartinstitute.erp.common.enums.SortDirection;
import com.smartinstitute.erp.common.pagination.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class SpecificationBuilder {

    private SpecificationBuilder() {
    }

    public static Pageable buildPageable(
            PaginationRequest request) {

        Sort.Direction direction =
                request.getDirection() == SortDirection.DESC
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, request.getSortBy())
        );
    }

}