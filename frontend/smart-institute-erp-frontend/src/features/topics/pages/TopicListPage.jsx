import { useState } from 'react';
import { Typography, Box, IconButton, Tooltip } from '@mui/material';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import TopicFormDialog from '../components/TopicFormDialog';
import { useTopics } from '../hooks/useTopics';
import { getTopicColumns } from '../constants/topicColumns';
import { topicApi } from '@services/api/topicApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';

function TopicListPage() {
  const { rows, loading, error, searchInput, setSearchInput, refetch } = useTopics();
  const { hasRole } = useAuth();
  const canManage = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN']);

  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await topicApi.remove(confirmTarget.id);
      showSnackbar('Topic deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete topic.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const columns = [
    ...getTopicColumns(),
    ...(canManage ? [{
      field: 'actions', headerName: 'Actions',
      render: (row) => (
        <Box sx={{ display: 'flex', gap: 0.5 }}>
          <Tooltip title="Edit"><IconButton size="small" onClick={() => setFormState({ mode: 'edit', item: row })}><EditIcon fontSize="small" /></IconButton></Tooltip>
          <Tooltip title="Delete"><IconButton size="small" color="error" onClick={() => setConfirmTarget(row)}><DeleteOutlinedIcon fontSize="small" /></IconButton></Tooltip>
        </Box>
      ),
    }] : []),
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>Topics</Typography>
      <TableToolbar searchValue={searchInput} onSearchChange={setSearchInput} searchPlaceholder="Search by topic or course..." addLabel="Add Topic" onAddClick={canManage ? () => setFormState({ mode: 'create' }) : undefined} />
      <DataTable columns={columns} rows={rows} loading={loading} error={error} onRetry={refetch} emptyMessage="No topics found." page={0} size={rows.length || 10} totalElements={rows.length} onPageChange={() => {}} onSizeChange={() => {}} getRowId={(row) => row.id} />
      <ConfirmDialog open={!!confirmTarget} title="Delete Topic" message={`Delete topic "${confirmTarget?.name}"?`} confirmLabel="Delete" loading={deleting} onConfirm={handleDeleteConfirm} onCancel={() => setConfirmTarget(null)} />
      {formState && <TopicFormDialog open={!!formState} mode={formState.mode} initialData={formState.item} onClose={() => setFormState(null)} onSuccess={refetch} />}
    </Box>
  );
}

export default TopicListPage;