import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import BatchFormDialog from '../components/BatchFormDialog';
import BatchStatusDialog from '../components/BatchStatusDialog';
import { useBatches } from '../hooks/useBatches';
import { getBatchColumns } from '../constants/batchColumns';
import { batchApi } from '@services/api/batchApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

function BatchListPage() {
  const {
    rows, totalElements, loading, error, page, size, setPage, setSize,
    searchInput, setSearchInput, sortBy, direction, toggleSort, refetch,
  } = useBatches();

  const { hasRole } = useAuth();
  // BatchController has NO @PreAuthorize (see Sprint 4 assumption flag) —
  // gating to these roles is a frontend UX choice, not a backend mirror.
  const canManageBatches = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);
  const [statusTarget, setStatusTarget] = useState(null);

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await batchApi.deleteBatch(confirmTarget.id);
      showSnackbar('Batch deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete batch.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    ...getBatchColumns({ sortBy, direction, onSort: toggleSort }),
    ...(canManageBatches
      ? [
          {
            field: 'actions',
            headerName: 'Actions',
            render: (row) => (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                <Tooltip title="Edit">
                  <IconButton size="small" onClick={() => setFormState({ mode: 'edit', batch: row })}>
                    <EditIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Change Status">
                  <IconButton size="small" onClick={() => setStatusTarget(row)}>
                    <SwapHorizIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete">
                  <IconButton size="small" color="error" onClick={() => setConfirmTarget(row)}>
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
        Batches
      </Typography>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search by batch name..."
        addLabel="Add Batch"
        onAddClick={canManageBatches ? () => setFormState({ mode: 'create' }) : undefined}
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No batches found."
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={setPage}
        onSizeChange={setSize}
        getRowId={(row) => row.id}
      />

      <ConfirmDialog
        open={!!confirmTarget}
        title="Delete Batch"
        message={`Are you sure you want to delete "${confirmTarget?.batchName}"? This may fail if students are currently assigned.`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setConfirmTarget(null)}
      />

      {formState && (
        <BatchFormDialog
          open={!!formState}
          mode={formState.mode}
          initialData={formState.batch}
          onClose={() => setFormState(null)}
          onSuccess={refetch}
        />
      )}

      {statusTarget && (
        <BatchStatusDialog
          open={!!statusTarget}
          batch={statusTarget}
          onClose={() => setStatusTarget(null)}
          onSuccess={refetch}
        />
      )}
    </Box>
  );
}

export default BatchListPage;