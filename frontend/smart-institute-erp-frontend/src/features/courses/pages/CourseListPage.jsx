import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import CourseFormDialog from '../components/CourseFormDialog';
import { useCourses } from '../hooks/useCourses';
import { getCourseColumns } from '../constants/courseColumns';
import { courseApi } from '@services/api/courseApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

function CourseListPage() {
  const {
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
    refetch,
  } = useCourses();

  const { hasRole } = useAuth();
  // Matches @PreAuthorize("hasRole('SUPER_ADMIN')") on CourseController —
  // stricter than Student (SUPER_ADMIN only, not INSTITUTE_ADMIN).
  const canManageCourses = hasRole(['SUPER_ADMIN']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      // Backend may reject deletion if the course has dependent
      // batches/students (Section 10 of context doc) — that business
      // error surfaces via showSnackbar verbatim, not assumed away.
      await courseApi.deleteCourse(confirmTarget.id);
      showSnackbar('Course deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete course.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    ...getCourseColumns({ sortBy, direction, onSort: toggleSort }),
    ...(canManageCourses
      ? [
          {
            field: 'actions',
            headerName: 'Actions',
            render: (row) => (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                <Tooltip title="Edit">
                  <IconButton size="small" onClick={() => setFormState({ mode: 'edit', course: row })}>
                    <EditIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete">
                  <IconButton size="small" color="error" disabled={!row.active} onClick={() => setConfirmTarget(row)}>
                    <DeleteOutlineIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
              </Box>
            ),
          },
        ]
      : []),
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Courses
      </Typography>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search by course name..."
        addLabel="Add Course"
        onAddClick={canManageCourses ? () => setFormState({ mode: 'create' }) : undefined}
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No courses found."
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={setPage}
        onSizeChange={setSize}
        getRowId={(row) => row.id}
      />

      <ConfirmDialog
        open={!!confirmTarget}
        title="Delete Course"
        message={`Are you sure you want to delete "${confirmTarget?.courseName}"? This may fail if the course has associated batches or students.`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setConfirmTarget(null)}
      />

      {formState && (
        <CourseFormDialog
          open={!!formState}
          mode={formState.mode}
          initialData={formState.course}
          onClose={() => setFormState(null)}
          onSuccess={refetch}
        />
      )}
    </Box>
  );
}

export default CourseListPage;