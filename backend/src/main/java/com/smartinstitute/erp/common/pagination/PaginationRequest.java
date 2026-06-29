package com.smartinstitute.erp.common.pagination;

import com.smartinstitute.erp.common.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {

    @Min(value = 0, message = "Page number cannot be negative.")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1.")
    @Max(value = 100, message = "Maximum page size is 100.")
    private int size = 10;

    private String sortBy = "id";

    private SortDirection direction = SortDirection.ASC;

    /**
     * Generic search keyword.
     * Example:
     * Rahul
     * Java
     * Pune
     */
    private String keyword;

}