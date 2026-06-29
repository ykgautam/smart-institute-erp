package com.smartinstitute.erp.common.pagination;

import org.springframework.data.domain.Page;

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

}