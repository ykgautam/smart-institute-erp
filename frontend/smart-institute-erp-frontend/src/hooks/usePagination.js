import { useState, useCallback } from 'react';

// Manages page/size state using the EXACT vocabulary of the backend's
// pagination response (Section 26: page, size, totalElements, totalPages).
// Feature hooks (useStudents, useCourses, etc.) consume this so every
// module's pagination behaves identically and no translation layer
// is needed between frontend state and backend query params.
export function usePagination(initialSize = 10) {
  const [page, setPage] = useState(0); // Spring Data pages are 0-indexed
  const [size, setSize] = useState(initialSize);

  const handlePageChange = useCallback((newPage) => {
    setPage(newPage);
  }, []);

  const handleSizeChange = useCallback((newSize) => {
    setSize(newSize);
    setPage(0); // Reset to first page — a stale page number with a new
                // page size could point past the end of the result set.
  }, []);

  const resetPagination = useCallback(() => {
    setPage(0);
  }, []);

  return { page, size, setPage: handlePageChange, setSize: handleSizeChange, resetPagination };
}