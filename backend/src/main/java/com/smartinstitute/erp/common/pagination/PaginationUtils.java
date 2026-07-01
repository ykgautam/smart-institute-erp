package com.smartinstitute.erp.common.pagination;

import com.smartinstitute.erp.common.enums.SortDirection;
import com.smartinstitute.erp.common.exception.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static <T> PageResponse<T> buildPageResponse(
            Page<?> page,
            List<T> content) {

        return PageResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .build();
    }

    public static Pageable createPageable(PaginationRequest request) {

        Sort.Direction direction = request.getDirection() == SortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, request.getSortBy())
        );
    }

    public static void validatePagination(
            PaginationRequest request) {

        if (request.getSize() > 100) {
            throw new InvalidRequestException(
                    "Page size cannot exceed 100."
            );
        }

        if (request.getPage() < 0) {

            throw new InvalidRequestException(
                    "Page number cannot be negative."
            );
        }

        if (request.getSize() <= 0) {

            throw new InvalidRequestException(
                    "Page size must be greater than zero."
            );
        }

    }

}