import { useState, useEffect, useCallback } from 'react';
import { batchApi } from '@services/api/batchApi';
import { usePagination } from '@hooks/usePagination';
import { useDebouncedValue } from '@hooks/useDebouncedValue';
import { extractPageMeta } from '@utils/apiResponse';

// Same established pattern as useStudents/useCourses.
export function useBatches() {
  const { page, size, setPage, setSize, resetPagination } = usePagination(10);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 400);

  const [rows, setRows] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Confirmed via BatchSortableFields.ALLOWED_FIELDS.
  const [sortBy, setSortBy] = useState('batchName');
  const [direction, setDirection] = useState('ASC');

  const fetchBatches = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await batchApi.getBatches({
        page,
        size,
        sortBy,
        direction,
        keyword: debouncedKeyword || undefined,
      });
      const meta = extractPageMeta(response);
      setRows(meta.content);
      setTotalElements(meta.totalElements);
    } catch (err) {
      setError(err.message || 'Failed to load batches.');
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, direction, debouncedKeyword]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchBatches();
  }, [fetchBatches]);

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
    refetch: fetchBatches,
  };
}