import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import PublishIcon from '@mui/icons-material/Publish';
import UnpublishedIcon from '@mui/icons-material/Unpublished';
import QuizIcon from '@mui/icons-material/Quiz';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import TestFormDialog from '../components/TestFormDialog';
import { useTests } from '../hooks/useTests';
import { getTestColumns } from '../constants/testColumns';
import { testApi } from '@services/api/testApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

function TestListPage() {
  const navigate = useNavigate();
  const { rows, loading, error, searchInput, setSearchInput, refetch } = useTests();
  const { hasRole } = useAuth();
  const canManage = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'FACULTY']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);
  const [togglingPublish, setTogglingPublish] = useState(null);

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await testApi.remove(confirmTarget.id);
      showSnackbar('Test deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete test.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const handleTogglePublish = async (row) => {
    setTogglingPublish(row.id);
    try {
      if (row.status === 'PUBLISHED') {
        await testApi.setDraft(row.id);
        showSnackbar('Test moved to draft.', 'success');
      } else {
        await testApi.publish(row.id);
        showSnackbar('Test published successfully.', 'success');
      }
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update test status.', 'error');
    } finally {
      setTogglingPublish(null);
    }
  };

  const columns = [
    ...getTestColumns(),
    ...(canManage ? [{
      field: 'actions', headerName: 'Actions',
      render: (row) => (
        <Box sx={{ display: 'flex', gap: 0.5 }}>
          <Tooltip title="Manage Questions">
            <IconButton size="small" onClick={() => navigate(`/exams/tests/${row.id}/questions`)}><QuizIcon fontSize="small" /></IconButton>
          </Tooltip>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => setFormState({ mode: 'edit', item: row })}><EditIcon fontSize="small" /></IconButton>
          </Tooltip>
          <Tooltip title={row.status === 'PUBLISHED' ? 'Move to Draft' : 'Publish'}>
            <IconButton size="small" disabled={togglingPublish === row.id} onClick={() => handleTogglePublish(row)}>
              {row.status === 'PUBLISHED' ? <UnpublishedIcon fontSize="small" /> : <PublishIcon fontSize="small" />}
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton size="small" color="error" onClick={() => setConfirmTarget(row)}><DeleteOutlinedIcon fontSize="small" /></IconButton>
          </Tooltip>
        </Box>
      ),
    }] : []),
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>Tests</Typography>
      <TableToolbar searchValue={searchInput} onSearchChange={setSearchInput} searchPlaceholder="Search by test title..." addLabel="Create Test" onAddClick={canManage ? () => setFormState({ mode: 'create' }) : undefined} />
      <DataTable columns={columns} rows={rows} loading={loading} error={error} onRetry={refetch} emptyMessage="No tests found." page={0} size={rows.length || 10} totalElements={rows.length} onPageChange={() => {}} onSizeChange={() => {}} getRowId={(row) => row.id} />
      <ConfirmDialog open={!!confirmTarget} title="Delete Test" message={`Delete test "${confirmTarget?.title}"?`} confirmLabel="Delete" loading={deleting} onConfirm={handleDeleteConfirm} onCancel={() => setConfirmTarget(null)} />
      {formState && <TestFormDialog open={!!formState} mode={formState.mode} initialData={formState.item} onClose={() => setFormState(null)} onSuccess={refetch} />}
    </Box>
  );
}

export default TestListPage;