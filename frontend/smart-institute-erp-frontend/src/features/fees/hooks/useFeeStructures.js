import { useState, useEffect, useMemo, useCallback } from 'react';
import { feeStructureApi } from '@services/api/feeStructureApi';
import { useDebouncedValue } from '@hooks/useDebouncedValue';

// Unlike useStudents/useCourses/useBatches, this hook does NOT use
// usePagination — the backend endpoint is unpaged (see feeStructureApi.js
// note). Search filtering happens client-side on the full fetched list.
export function useFeeStructures() {
  const [allRows, setAllRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 300);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await feeStructureApi.getAll();
      setAllRows(response || []);
    } catch (err) {
      setError(err.message || 'Failed to load fee structures.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchAll();
  }, [fetchAll]);

  // Client-side filter — acceptable here specifically because the
  // backend provides no server-side search for this resource, and fee
  // structures are typically a small list (one per course, roughly).
  const rows = useMemo(() => {
    if (!debouncedKeyword) return allRows;
    const lower = debouncedKeyword.toLowerCase();
    return allRows.filter((r) => r.courseName?.toLowerCase().includes(lower));
  }, [allRows, debouncedKeyword]);

  return {
    rows,
    loading,
    error,
    searchInput,
    setSearchInput,
    refetch: fetchAll,
  };
}