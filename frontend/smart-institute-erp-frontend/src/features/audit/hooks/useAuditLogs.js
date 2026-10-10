import { useState, useEffect, useCallback } from 'react';
import { auditApi } from '@services/api/auditApi';
import { usePagination } from '@hooks/usePagination';
import { extractPageMeta } from '@utils/apiResponse';

// Follows the established paginated-list pattern (like useStudents),
// but with additional filter state (action, entityType, date range)
// specific to audit logs — server-side filtering per Section 28.
export function useAuditLogs() {
  const { page, size, setPage, setSize, resetPagination } = usePagination(20);

  const [rows, setRows] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [filters, setFilters] = useState({
    action: '',
    entityType: '',
    dateFrom: '',
    dateTo: '',
  });

  const [sortBy] = useState('createdAt');
  const [direction] = useState('DESC');

  const updateFilter = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    resetPagination();
  };

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await auditApi.getAuditLogs({
        page,
        size,
        sortBy,
        sortDirection: direction,
        action: filters.action || undefined,
        entityType: filters.entityType || undefined,
        dateFrom: filters.dateFrom || undefined,
        dateTo: filters.dateTo || undefined,
      });
      const meta = extractPageMeta(response);
      setRows(meta.content);
      setTotalElements(meta.totalElements);
    } catch (err) {
      setError(err.message || 'Failed to load audit logs.');
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, direction, filters]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchLogs();
  }, [fetchLogs]);

  return {
    rows,
    totalElements,
    loading,
    error,
    page,
    size,
    setPage,
    setSize,
    filters,
    updateFilter,
    refetch: fetchLogs,
  };
}