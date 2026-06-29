package com.smartinstitute.erp.common.pagination;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    /**
     * Current page data.
     */
    private List<T> content;

    /**
     * Current page number.
     */
    private int page;

    /**
     * Requested page size.
     */
    private int size;

    /**
     * Total number of records.
     */
    private long totalElements;

    /**
     * Total number of pages.
     */
    private int totalPages;

    /**
     * Is first page?
     */
    private boolean first;

    /**
     * Is last page?
     */
    private boolean last;

    /**
     * Number of records in current page.
     */
    private int numberOfElements;

    /**
     * Is page empty?
     */
    private boolean empty;

}