import { useState, useEffect, useMemo, useCallback } from 'react';
import { testApi } from '@services/api/testApi';
import { useDebouncedValue } from '@hooks/useDebouncedValue';

export function useTests() {
  const [allRows, setAllRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 300);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await testApi.getAll();
      setAllRows(response || []);
    } catch (err) {
      setError(err.message || 'Failed to load tests.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchAll();
  }, [fetchAll]);

  const rows = useMemo(() => {
    if (!debouncedKeyword) return allRows;
    const lower = debouncedKeyword.toLowerCase();
    return allRows.filter((r) => r.title?.toLowerCase().includes(lower));
  }, [allRows, debouncedKeyword]);

  return { rows, loading, error, searchInput, setSearchInput, refetch: fetchAll };
}