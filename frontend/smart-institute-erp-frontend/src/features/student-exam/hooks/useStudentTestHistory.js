import { useState, useEffect, useCallback } from 'react';
import { studentTestApi } from '@services/api/studentTestApi';
import { usePagination } from '@hooks/usePagination';
import { useDebouncedValue } from '@hooks/useDebouncedValue';
import { extractPageMeta } from '@utils/apiResponse';

// This hook is the "brain" behind the paginated Test History table on
// MyTestsPage. It owns THREE kinds of state that together decide what
// data gets fetched:
//   1. Pagination (which page, how many rows per page) — via usePagination
//   2. Filters (status dropdown, date range) — plain useState
//   3. Search text — debounced, so we don't fire an API call on every
//      keystroke (see useDebouncedValue for how that works)
//
// Whenever ANY of these change, `fetchHistory` re-runs automatically
// (that's what the useEffect dependency array at the bottom does).
export function useStudentTestHistory() {
  // page/size/setPage/setSize come from our shared pagination hook —
  // same one used by Students, Courses, Batches, etc. resetPagination()
  // lets us jump back to page 0 whenever a filter changes, so the user
  // never gets stuck on "page 3" of a now-much-smaller filtered result.
  const { page, size, setPage, setSize, resetPagination } = usePagination(10);

  // Holds the actual rows returned by the backend for the CURRENT page,
  // plus how many rows exist in total (used by the pagination control
  // to know how many "pages" there are).
  const [rows, setRows] = useState([]);
  const [totalElements, setTotalElements] = useState(0);

  // Loading/error are separate from the data itself so the UI can show
  // a spinner or an error message independently of whatever rows we
  // last successfully loaded.
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filter state: status ('' means "all statuses"), and a from/to date
  // range. These map directly to the backend's query parameters.
  const [status, setStatus] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');

  // Search text — what the user types. `debouncedSearch` is the DELAYED
  // version that only updates once the user pauses typing, which is
  // what we actually send to the API (see useDebouncedValue.js).
  const [searchInput, setSearchInput] = useState('');
  const debouncedSearch = useDebouncedValue(searchInput, 400);

  // The actual API call. Wrapped in useCallback so React doesn't
  // recreate this function on every render — it only changes when one
  // of its dependencies (page, size, filters, search) actually changes.
  const fetchHistory = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentTestApi.getHistory({
        page,
        size,
        // Send `undefined` (not empty string) for unset filters, so
        // axios omits them from the URL entirely rather than sending
        // "status=" which some backends interpret as an actual filter
        // value rather than "no filter".
        status: status || undefined,
        fromDate: dateFrom || undefined,
        toDate: dateTo || undefined,
        search: debouncedSearch || undefined,
        sort: 'submittedAt',
        direction: 'desc',
      });

      // extractPageMeta safely reads content/totalElements/totalPages
      // from whatever pagination shape the backend returns, defaulting
      // gracefully if a field is missing — see utils/apiResponse.js.
      const meta = extractPageMeta(response);
      setRows(meta.content);
      setTotalElements(meta.totalElements);
    } catch (err) {
      setError(err.message || 'Failed to load test history.');
    } finally {
      setLoading(false);
    }
  }, [page, size, status, dateFrom, dateTo, debouncedSearch]);

  // Fetch-on-mount and fetch-whenever-a-dependency-changes. This is the
  // standard data-fetching pattern used throughout the whole app — see
  // useStudents.js for the original, more heavily-commented version of
  // why this shape is used (and why the eslint-disable is needed).
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchHistory();
  }, [fetchHistory]);

  // Whenever a filter or the search term changes, go back to page 0.
  // Without this, a user filtering to a tiny result set could be
  // "stuck" on a page number that no longer exists.
  useEffect(() => {
    resetPagination();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status, dateFrom, dateTo, debouncedSearch]);

  // Everything the page component needs, bundled into one object.
  return {
    rows,
    totalElements,
    loading,
    error,
    page,
    size,
    setPage,
    setSize,
    status,
    setStatus,
    dateFrom,
    setDateFrom,
    dateTo,
    setDateTo,
    searchInput,
    setSearchInput,
    refetch: fetchHistory,
  };
}