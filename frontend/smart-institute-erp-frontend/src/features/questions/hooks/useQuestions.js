import { useState, useEffect, useCallback } from 'react';
import { questionApi } from '@services/api/questionApi';
import { useDebouncedValue } from '@hooks/useDebouncedValue';

// Always hits the /questions/search endpoint (server-side filtering) —
// simpler single code path than switching between getAll and search.
export function useQuestions() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [filters, setFilters] = useState({ courseId: '', topicId: '', difficulty: '' });
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 400);

  const updateFilter = (key, value) => setFilters((prev) => ({ ...prev, [key]: value }));

  const fetchQuestions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await questionApi.search({
        courseId: filters.courseId || undefined,
        topicId: filters.topicId || undefined,
        difficulty: filters.difficulty || undefined,
        keyword: debouncedKeyword || undefined,
      });
      setRows(response || []);
    } catch (err) {
      setError(err.message || 'Failed to load questions.');
    } finally {
      setLoading(false);
    }
  }, [filters, debouncedKeyword]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchQuestions();
  }, [fetchQuestions]);

  return { rows, loading, error, filters, updateFilter, searchInput, setSearchInput, refetch: fetchQuestions };
}