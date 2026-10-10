import { useState, useEffect } from 'react';
import { courseApi } from '@services/api/courseApi';
import { batchApi } from '@services/api/batchApi';

// Shared Course/Batch dropdown data source for report filters — every
// report screen that filters by course/batch reuses this instead of
// each fetching independently with duplicated logic.
export function useCourseBatchOptions() {
  const [courses, setCourses] = useState([]);
  const [batches, setBatches] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  useEffect(() => {
    async function load() {
      setLoadingOptions(true);
      try {
        const [courseRes, batchRes] = await Promise.all([
          courseApi.getCourses({ page: 0, size: 100, sortBy: 'courseName', direction: 'ASC' }),
          batchApi.getBatches({ page: 0, size: 100, sortBy: 'batchName', direction: 'ASC' }),
        ]);
        setCourses(courseRes?.content || []);
        setBatches(batchRes?.content || []);
      } catch {
        // Silent — report filters just show empty dropdowns if this fails,
        // the report itself still loads unfiltered.
      } finally {
        setLoadingOptions(false);
      }
    }
    load();
  }, []);

  return { courses, batches, loadingOptions };
}