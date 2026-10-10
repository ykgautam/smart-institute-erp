import { useState, useEffect, useMemo, useCallback } from 'react';
import { topicApi } from '@services/api/topicApi';
import { useDebouncedValue } from '@hooks/useDebouncedValue';

export function useTopics() {
  const [allRows, setAllRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 300);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await topicApi.getAll();
      setAllRows(response || []);
    } catch (err) {
      setError(err.message || 'Failed to load topics.');
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
    return allRows.filter((r) => r.name?.toLowerCase().includes(lower) || r.courseName?.toLowerCase().includes(lower));
  }, [allRows, debouncedKeyword]);

  return { rows, loading, error, searchInput, setSearchInput, refetch: fetchAll };
}