import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import FeeStructureFormDialog from '../components/FeeStructureFormDialog';
import { useFeeStructures } from '../hooks/useFeeStructures';
import { getFeeStructureColumns } from '../constants/feeStructureColumns';
import { feeStructureApi } from '@services/api/feeStructureApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

function FeeStructureListPage() {
  const { rows, loading, error, searchInput, setSearchInput, refetch } = useFeeStructures();

  const { hasRole } = useAuth();
  const canManage = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await feeStructureApi.remove(confirmTarget.id);
      showSnackbar('Fee structure deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete fee structure.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    ...getFeeStructureColumns(),
    ...(canManage
      ? [
          {
            field: 'actions',
            headerName: 'Actions',
            render: (row) => (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                <Tooltip title="Edit">
                  <IconButton size="small" onClick={() => setFormState({ mode: 'edit', item: row })}>
                    <EditIcon fontSize="small" />
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
        Fee Structures
      </Typography>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search by course name..."
        addLabel="Add Fee Structure"
        onAddClick={canManage ? () => setFormState({ mode: 'create' }) : undefined}
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No fee structures found."
        // No server-side pagination for this resource — pass through
        // values that render the whole filtered list without a working
        // pager (DataTable still needs these props; totalElements
        // matches rows.length so the pagination bar reflects reality).
        page={0}
        size={rows.length || 10}
        totalElements={rows.length}
        onPageChange={() => {}}
        onSizeChange={() => {}}
        getRowId={(row) => row.id}
      />

      <ConfirmDialog
        open={!!confirmTarget}
        title="Delete Fee Structure"
        message={`Are you sure you want to delete the fee structure for "${confirmTarget?.courseName}"?`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setConfirmTarget(null)}
      />

      {formState && (
        <FeeStructureFormDialog
          open={!!formState}
          mode={formState.mode}
          initialData={formState.item}
          onClose={() => setFormState(null)}
          onSuccess={refetch}
        />
      )}
    </Box>
  );
}

export default FeeStructureListPage;