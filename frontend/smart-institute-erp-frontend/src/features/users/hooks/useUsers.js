import { useState, useEffect, useMemo, useCallback } from 'react';
import { userApi } from '@services/api/userApi';
import { useDebouncedValue } from '@hooks/useDebouncedValue';

// Same unpaged-list + client-side-filter pattern as useFeeStructures.js
// and useStudentFees.js — GET /users returns an unpaged List<UserResponse>.
export function useUsers() {
  const [allRows, setAllRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 300);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await userApi.getAllUsers();
      setAllRows(response || []);
    } catch (err) {
      setError(err.message || 'Failed to load users.');
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
    return allRows.filter(
      (r) =>
        `${r.firstName} ${r.lastName || ''}`.toLowerCase().includes(lower) ||
        r.email?.toLowerCase().includes(lower),
    );
  }, [allRows, debouncedKeyword]);

  return { rows, loading, error, searchInput, setSearchInput, refetch: fetchAll };
}