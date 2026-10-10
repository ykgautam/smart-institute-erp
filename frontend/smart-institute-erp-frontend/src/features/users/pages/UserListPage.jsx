import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import UserFormDialog from '../components/UserFormDialog';
import UserStatusDialog from '../components/UserStatusDialog';
import { useUsers } from '../hooks/useUsers';
import { getUserColumns } from '../constants/userColumns';
import { userApi } from '@services/api/userApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

// User Management is deliberately restricted to SUPER_ADMIN only — this
// controls who can access the system at all (creating/editing staff
// accounts, assigning roles), which is a higher security bar than
// regular CRUD modules. No @PreAuthorize was found on UserController in
// shared context, so this is a frontend UX-only precaution; verify with
// backend team what the actual authorization requirement should be.
function UserListPage() {
  const { rows, loading, error, searchInput, setSearchInput, refetch } = useUsers();
  const { hasRole } = useAuth();
  const canManage = hasRole(['SUPER_ADMIN']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);
  const [statusTarget, setStatusTarget] = useState(null);

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await userApi.deleteUser(confirmTarget.id);
      showSnackbar('User deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete user.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    ...getUserColumns(),
    ...(canManage
      ? [
          {
            field: 'actions',
            headerName: 'Actions',
            render: (row) => (
              <Box sx={{ display: 'flex', gap: 0.5 }}>
                <Tooltip title="Edit">
                  <IconButton size="small" onClick={() => setFormState({ mode: 'edit', user: row })}>
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
                    <DeleteOutlinedIcon fontSize="small" />
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
        Users
      </Typography>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search by name or email..."
        addLabel="Add User"
        onAddClick={canManage ? () => setFormState({ mode: 'create' }) : undefined}
      />

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No users found."
        page={0}
        size={rows.length || 10}
        totalElements={rows.length}
        onPageChange={() => {}}
        onSizeChange={() => {}}
        getRowId={(row) => row.id}
      />

      <ConfirmDialog
        open={!!confirmTarget}
        title="Delete User"
        message={`Are you sure you want to delete "${confirmTarget?.firstName} ${confirmTarget?.lastName || ''}"?`}
        confirmLabel="Delete"
        loading={deleting}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setConfirmTarget(null)}
      />

      {formState && (
        <UserFormDialog
          open={!!formState}
          mode={formState.mode}
          initialData={formState.user}
          onClose={() => setFormState(null)}
          onSuccess={refetch}
        />
      )}

      {statusTarget && (
        <UserStatusDialog
          open={!!statusTarget}
          user={statusTarget}
          onClose={() => setStatusTarget(null)}
          onSuccess={refetch}
        />
      )}
    </Box>
  );
}

export default UserListPage;