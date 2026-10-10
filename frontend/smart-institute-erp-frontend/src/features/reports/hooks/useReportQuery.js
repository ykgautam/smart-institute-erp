import { useState, useEffect, useCallback } from 'react';
import { usePagination } from '@hooks/usePagination';

// Generic paginated-report hook — every admin report shares the same
// mechanic (filters -> paginated response with optional summary), even
// though each report's DTO fields differ. This is the ONE place that
// mechanic lives (Section 24: consistent filters/pagination/summary
// pattern across all report screens).
export function useReportQuery(fetchFn, initialFilters = {}, defaultSort = { sortBy: 'id', sortDirection: 'DESC' }) {
  const { page, size, setPage, setSize, resetPagination } = usePagination(10);
  const [filters, setFilters] = useState(initialFilters);
  const [rows, setRows] = useState([]);
  const [summary, setSummary] = useState(null);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const updateFilter = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    resetPagination();
  };

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const cleanFilters = Object.fromEntries(
        Object.entries(filters).filter(([, v]) => v !== '' && v != null),
      );
      const response = await fetchFn({
        page,
        size,
        sortBy: defaultSort.sortBy,
        sortDirection: defaultSort.sortDirection,
        ...cleanFilters,
      });
      setRows(response?.content || []);
      setSummary(response?.summary || null);
      setTotalElements(response?.totalElements || 0);
    } catch (err) {
      setError(err.message || 'Failed to load report.');
    } finally {
      setLoading(false);
    }
  }, [fetchFn, page, size, filters, defaultSort.sortBy, defaultSort.sortDirection]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchData();
  }, [fetchData]);

  return { rows, summary, totalElements, loading, error, page, size, setPage, setSize, filters, updateFilter, refetch: fetchData };
}