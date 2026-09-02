import { useState, useEffect, useCallback } from 'react';
import { studentApi } from '@services/api/studentApi';
import { usePagination } from '@hooks/usePagination';
import { useDebouncedValue } from '@hooks/useDebouncedValue';
import { extractPageMeta } from '@utils/apiResponse';

// Combines pagination + debounced search + the actual API call into one
// hook. StudentListPage just consumes what this returns — it doesn't
// know about query params, debouncing, or loading-state bookkeeping.
//
// WHY A CUSTOM HOOK (for reference, since you're new to this pattern):
// This lets StudentListPage.jsx stay focused on JSX/layout, while all
// the "how do we fetch and manage student data" logic lives here,
// reusable if we ever need a second student-list-like view.
export function useStudents() {
  const { page, size, setPage, setSize, resetPagination } = usePagination(10);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 400);

  const [rows, setRows] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Sorting state — defaults match the controller's commented-out
  // defaults (sortBy=id... we use firstName as a friendlier default
  // for a name-focused list, both are safe/likely-sortable fields).
  const [sortBy, setSortBy] = useState('firstName');
  const [direction, setDirection] = useState('ASC');

  const fetchStudents = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentApi.getStudents({
        page,
        size,
        sortBy,
        direction,
        keyword: debouncedKeyword || undefined, // omit empty keyword from query
      });
      const meta = extractPageMeta(response);
      setRows(meta.content);
      setTotalElements(meta.totalElements);
    } catch (err) {
      setError(err.message || 'Failed to load students.');
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, direction, debouncedKeyword]);

  // Fetch-on-mount / fetch-on-dependency-change pattern. ESLint's newer
  // 'set-state-in-effect' rule flags this because it assumes a
  // Suspense-based data source or a library like React Query — neither
  // is part of our locked stack (Section 3/37: plain Axios only).
  // This is a standard, valid data-fetching pattern; every future
  // feature hook (useCourses, useBatches, etc.) will follow the same
  // shape, so we suppress this rule here deliberately rather than
  // introducing a new dependency just to satisfy the linter.
  //
  // NOTE: the disable comment must sit directly above the exact line
  // that triggers the rule (fetchStudents() itself), not above the
  // useEffect(...) line — eslint-disable-next-line only affects the
  // single line immediately following it.
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchStudents();
  }, [fetchStudents]);

   // Whenever the search term changes, go back to page 0 — otherwise the
  // user could be stuck on page 3 of a search result that only has 1 page.
  // Note: resetPagination() internally calls setPage(0), but since it's
  // an indirect call (via a function reference, not a direct setState
  // call), ESLint's set-state-in-effect rule doesn't flag this one —
  // only the eslint-disable for exhaustive-deps is needed here.
  useEffect(() => {
    resetPagination();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedKeyword]);

  const toggleSort = (field) => {
    if (sortBy === field) {
      setDirection((prev) => (prev === 'ASC' ? 'DESC' : 'ASC'));
    } else {
      setSortBy(field);
      setDirection('ASC');
    }
  };

  return {
    rows,
    totalElements,
    loading,
    error,
    page,
    size,
    setPage,
    setSize,
    searchInput,
    setSearchInput,
    sortBy,
    direction,
    toggleSort,
    refetch: fetchStudents, // exposed so the page can force a refresh after create/deactivate
  };
}