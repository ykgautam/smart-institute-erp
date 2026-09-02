import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import BlockIcon from '@mui/icons-material/Block';
import EditIcon from '@mui/icons-material/Edit';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import GroupRemoveIcon from '@mui/icons-material/GroupRemove';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import StudentFormDialog from '../components/StudentFormDialog';
import AssignBatchDialog from '../components/AssignBatchDialog';
import { useStudents } from '../hooks/useStudents';
import { getStudentColumns } from '../constants/studentColumns';
import { studentApi } from '@services/api/studentApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

function StudentListPage() {
  const {
    rows, totalElements, loading, error, page, size, setPage, setSize,
    searchInput, setSearchInput, sortBy, direction, toggleSort, refetch,
  } = useStudents();

  const { hasRole } = useAuth();
  const canManageStudents = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deactivating, setDeactivating] = useState(false);
  const [formState, setFormState] = useState(null);

  // Batch assignment state
  const [assignTarget, setAssignTarget] = useState(null); // student row, or null
  const [removeTarget, setRemoveTarget] = useState(null); // student row, or null
  const [removingBatch, setRemovingBatch] = useState(false);

  const handleDeactivateConfirm = async () => {
    if (!confirmTarget) return;
    setDeactivating(true);
    try {
      await studentApi.deactivateStudent(confirmTarget.id);
      showSnackbar('Student deactivated successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to deactivate student.', 'error');
    } finally {
      setDeactivating(false);
    }
  };

  const handleRemoveBatchConfirm = async () => {
    if (!removeTarget) return;
    setRemovingBatch(true);
    try {
      await studentApi.removeFromBatch(removeTarget.id);
      showSnackbar(`${removeTarget.fullName} removed from batch.`, 'success');
      setRemoveTarget(null);
      refetch();
    } catch (err) {
      // Backend errors here if the student wasn't assigned to any batch —
      // surfaced verbatim rather than assumed away, since StudentResponse
      // doesn't tell us their current batch status up front.
      showSnackbar(err.message || 'Failed to remove student from batch.', 'error');
    } finally {
      setRemovingBatch(false);
    }
  };

  const columns = [
    ...getStudentColumns({ sortBy, direction, onSort: toggleSort }),
    ...(canManageStudents
      ? [
          {
            field: 'actions',
            headerName: 'Actions',
            render: (row) => (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                <Tooltip title="Edit">
                  <IconButton size="small" onClick={() => setFormState({ mode: 'edit', student: row })}>
                    <EditIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Assign to Batch">
                  <IconButton size="small" disabled={!row.active} onClick={() => setAssignTarget(row)}>
                    <GroupAddIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Remove from Batch">
                  <IconButton size="small" disabled={!row.active} onClick={() => setRemoveTarget(row)}>
                    <GroupRemoveIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Deactivate">
                  <IconButton size="small" color="error" disabled={!row.active} onClick={() => setConfirmTarget(row)}>
                    <BlockIcon fontSize="small" />
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
        Students
      </Typography>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search by name..."
        addLabel="Add Student"
        onAddClick={canManageStudents ? () => setFormState({ mode: 'create' }) : undefined}
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No students found."
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={setPage}
        onSizeChange={setSize}
        getRowId={(row) => row.id}
      />

      <ConfirmDialog
        open={!!confirmTarget}
        title="Deactivate Student"
        message={`Are you sure you want to deactivate "${confirmTarget?.fullName}"? They will no longer appear as an active student.`}
        confirmLabel="Deactivate"
        confirmColor="error"
        loading={deactivating}
        onConfirm={handleDeactivateConfirm}
        onCancel={() => setConfirmTarget(null)}
      />

      <ConfirmDialog
        open={!!removeTarget}
        title="Remove from Batch"
        message={`Remove "${removeTarget?.fullName}" from their current batch? This will fail if they aren't currently assigned to one.`}
        confirmLabel="Remove"
        confirmColor="error"
        loading={removingBatch}
        onConfirm={handleRemoveBatchConfirm}
        onCancel={() => setRemoveTarget(null)}
      />

      {formState && (
        <StudentFormDialog
          open={!!formState}
          mode={formState.mode}
          initialData={formState.student}
          onClose={() => setFormState(null)}
          onSuccess={refetch}
        />
      )}

      {assignTarget && (
        <AssignBatchDialog
          open={!!assignTarget}
          student={assignTarget}
          onClose={() => setAssignTarget(null)}
          onSuccess={refetch}
        />
      )}
    </Box>
  );
}

export default StudentListPage;