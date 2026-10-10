import { useState, useEffect } from 'react';
import { Typography, Box, IconButton, Tooltip, Paper, Grid, TextField, MenuItem } from '@mui/material';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import EditIcon from '@mui/icons-material/Edit';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import DataTable from '@components/tables/DataTable';
import TableToolbar from '@components/tables/TableToolbar';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import QuestionFormDialog from '../components/QuestionFormDialog';
import { useQuestions } from '../hooks/useQuestions';
import { getQuestionColumns } from '../constants/questionColumns';
import { questionApi } from '@services/api/questionApi';
import { courseApi } from '@services/api/courseApi';
import { topicApi } from '@services/api/topicApi';
import { useAuth } from '@hooks/useAuth';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { QUESTION_DIFFICULTY } from '@constants/enums';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import QuestionStatusDialog from '../components/QuestionStatusDialog';

function QuestionListPage() {
  const { rows, loading, error, filters, updateFilter, searchInput, setSearchInput, refetch } = useQuestions();
  const { hasRole } = useAuth();
  const canManage = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN', 'FACULTY']);

  const [courses, setCourses] = useState([]);
  const [topics, setTopics] = useState([]);
  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [formState, setFormState] = useState(null);
  const [statusTarget, setStatusTarget] = useState(null);

  useEffect(() => {
    async function loadFilterOptions() {
      try {
        const [courseRes, topicRes] = await Promise.all([
          courseApi.getCourses({ page: 0, size: 100, sortBy: 'courseName', direction: 'ASC' }),
          topicApi.getAll(),
        ]);
        setCourses(courseRes?.content || []);
        setTopics(topicRes || []);
      } catch {
        showSnackbar('Failed to load filter options.', 'error');
      }
    }
    loadFilterOptions();
  }, []);

  const filteredTopicOptions = filters.courseId ? topics.filter((t) => t.courseId === Number(filters.courseId)) : topics;

  const handleDeleteConfirm = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await questionApi.remove(confirmTarget.id);
      showSnackbar('Question deleted successfully.', 'success');
      setConfirmTarget(null);
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to delete question.', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const handleDuplicate = async (row) => {
    try {
      await questionApi.duplicate(row.id);
      showSnackbar('Question duplicated successfully.', 'success');
      refetch();
    } catch (err) {
      showSnackbar(err.message || 'Failed to duplicate question.', 'error');
    }
  };

  const columns = [
    ...getQuestionColumns(),
    ...(canManage ? [{
      field: 'actions', headerName: 'Actions',
      render: (row) => (
        <Box sx={{ display: 'flex', gap: 0.5 }}>
          <Tooltip title="Edit"><IconButton size="small" onClick={() => setFormState({ mode: 'edit', item: row })}><EditIcon fontSize="small" /></IconButton></Tooltip>
          <Tooltip title="Duplicate"><IconButton size="small" onClick={() => handleDuplicate(row)}><ContentCopyIcon fontSize="small" /></IconButton></Tooltip>
          <Tooltip title="Change Status"><IconButton size="small" onClick={() => setStatusTarget(row)}><SwapHorizIcon fontSize="small" /></IconButton></Tooltip>
          <Tooltip title="Delete"><IconButton size="small" color="error" onClick={() => setConfirmTarget(row)}><DeleteOutlinedIcon fontSize="small" /></IconButton></Tooltip>
        </Box>
      ),
    }] : []),
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>Question Bank</Typography>

      <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField select fullWidth size="small" label="Course" value={filters.courseId} onChange={(e) => { updateFilter('courseId', e.target.value); updateFilter('topicId', ''); }}>
              <MenuItem value="">All Courses</MenuItem>
              {courses.map((c) => <MenuItem key={c.id} value={c.id}>{c.courseName}</MenuItem>)}
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField select fullWidth size="small" label="Topic" value={filters.topicId} onChange={(e) => updateFilter('topicId', e.target.value)}>
              <MenuItem value="">All Topics</MenuItem>
              {filteredTopicOptions.map((t) => <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>)}
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField select fullWidth size="small" label="Difficulty" value={filters.difficulty} onChange={(e) => updateFilter('difficulty', e.target.value)}>
              <MenuItem value="">All Difficulties</MenuItem>
              <MenuItem value={QUESTION_DIFFICULTY.EASY}>Easy</MenuItem>
              <MenuItem value={QUESTION_DIFFICULTY.MEDIUM}>Medium</MenuItem>
              <MenuItem value={QUESTION_DIFFICULTY.HARD}>Hard</MenuItem>
            </TextField>
          </Grid>
        </Grid>
      </Paper>

      <TableToolbar
        searchValue={searchInput}
        onSearchChange={setSearchInput}
        searchPlaceholder="Search question text..."
        addLabel="Add Question"
        onAddClick={canManage ? () => setFormState({ mode: 'create' }) : undefined}
      />

      <DataTable columns={columns} rows={rows} loading={loading} error={error} onRetry={refetch} emptyMessage="No questions found." page={0} size={rows.length || 10} totalElements={rows.length} onPageChange={() => {}} onSizeChange={() => {}} getRowId={(row) => row.id} />

      <ConfirmDialog open={!!confirmTarget} title="Delete Question" message="Are you sure you want to delete this question?" confirmLabel="Delete" loading={deleting} onConfirm={handleDeleteConfirm} onCancel={() => setConfirmTarget(null)} />
      {statusTarget && (
        <QuestionStatusDialog
          open={!!statusTarget}
          question={statusTarget}
          onClose={() => setStatusTarget(null)}
          onSuccess={refetch}
        />
      )}
      {formState && <QuestionFormDialog open={!!formState} mode={formState.mode} initialData={formState.item} onClose={() => setFormState(null)} onSuccess={refetch} />}
    </Box>
  );
}

export default QuestionListPage;