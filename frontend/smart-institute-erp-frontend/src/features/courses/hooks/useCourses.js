import { useState, useEffect, useCallback } from 'react';
import { courseApi } from '@services/api/courseApi';
import { usePagination } from '@hooks/usePagination';
import { useDebouncedValue } from '@hooks/useDebouncedValue';
import { extractPageMeta } from '@utils/apiResponse';

// Identical shape to useStudents.js — the established pattern every
// module's list hook follows (pagination + debounced search + fetch).
export function useCourses() {
  const { page, size, setPage, setSize, resetPagination } = usePagination(10);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 400);

  const [rows, setRows] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Confirmed via CourseSortableFields.ALLOWED_FIELDS — no guessing.
  const [sortBy, setSortBy] = useState('courseName');
  const [direction, setDirection] = useState('ASC');

  const fetchCourses = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await courseApi.getCourses({
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
      setError(err.message || 'Failed to load courses.');
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, direction, debouncedKeyword]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchCourses();
  }, [fetchCourses]);

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
    refetch: fetchCourses,
  };
}