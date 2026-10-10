import { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { Typography, Box, Paper, List, ListItem, ListItemText, IconButton, Tooltip, CircularProgress, Button } from '@mui/material';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import AddIcon from '@mui/icons-material/Add';
import ErrorState from '@components/tables/ErrorState';
import EmptyState from '@components/tables/EmptyState';
import ConfirmDialog from '@components/tables/ConfirmDialog';
import AddQuestionsToTestDialog from '../components/AddQuestionsToTestDialog';
import { testApi } from '@services/api/testApi';
import { testQuestionApi } from '@services/api/testQuestionApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// Reordering uses simple Up/Down buttons (no drag-and-drop library in
// the locked stack) — swaps displayOrder with the adjacent row, then
// submits the FULL reordered list via PUT .../order in one call.
function TestQuestionManagePage() {
  const { testId } = useParams();

  const [test, setTest] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  const [removeTarget, setRemoveTarget] = useState(null);
  const [removing, setRemoving] = useState(false);
  const [reordering, setReordering] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [testRes, questionsRes] = await Promise.all([
        testApi.getById(testId),
        testQuestionApi.getQuestions(testId),
      ]);
      setTest(testRes);
      setQuestions((questionsRes || []).sort((a, b) => a.displayOrder - b.displayOrder));
    } catch (err) {
      setError(err.message || 'Failed to load test questions.');
    } finally {
      setLoading(false);
    }
  }, [testId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchData();
  }, [fetchData]);

  const handleMove = async (index, direction) => {
    const targetIndex = direction === 'up' ? index - 1 : index + 1;
    if (targetIndex < 0 || targetIndex >= questions.length) return;

    const reordered = [...questions];
    [reordered[index], reordered[targetIndex]] = [reordered[targetIndex], reordered[index]];

    setReordering(true);
    try {
      const orderPayload = reordered.map((q, idx) => ({ questionId: q.questionId, displayOrder: idx + 1 }));
      await testQuestionApi.updateOrder(testId, orderPayload);
      setQuestions(reordered.map((q, idx) => ({ ...q, displayOrder: idx + 1 })));
    } catch (err) {
      showSnackbar(err.message || 'Failed to reorder questions.', 'error');
    } finally {
      setReordering(false);
    }
  };

  const handleRemoveConfirm = async () => {
    if (!removeTarget) return;
    setRemoving(true);
    try {
      await testQuestionApi.removeQuestion(testId, removeTarget.questionId);
      showSnackbar('Question removed from test.', 'success');
      setRemoveTarget(null);
      fetchData();
    } catch (err) {
      showSnackbar(err.message || 'Failed to remove question.', 'error');
    } finally {
      setRemoving(false);
    }
  };

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>;
  }
  if (error) {
    return <Paper variant="outlined"><ErrorState message={error} onRetry={fetchData} /></Paper>;
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h2">Manage Questions — {test?.title}</Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => setAddDialogOpen(true)}>
          Add Questions
        </Button>
      </Box>

      <Paper variant="outlined">
        {questions.length === 0 ? (
          <EmptyState message="No questions linked to this test yet." />
        ) : (
          <List>
            {questions.map((q, index) => (
              <ListItem
                key={q.id}
                secondaryAction={
                  <Box sx={{ display: 'flex', gap: 0.5 }}>
                    <Tooltip title="Move Up">
                      <span>
                        <IconButton size="small" disabled={index === 0 || reordering} onClick={() => handleMove(index, 'up')}>
                          <ArrowUpwardIcon fontSize="small" />
                        </IconButton>
                      </span>
                    </Tooltip>
                    <Tooltip title="Move Down">
                      <span>
                        <IconButton size="small" disabled={index === questions.length - 1 || reordering} onClick={() => handleMove(index, 'down')}>
                          <ArrowDownwardIcon fontSize="small" />
                        </IconButton>
                      </span>
                    </Tooltip>
                    <Tooltip title="Remove">
                      <IconButton size="small" color="error" onClick={() => setRemoveTarget(q)}>
                        <DeleteOutlinedIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </Box>
                }
              >
                <ListItemText primary={`${q.displayOrder}. ${q.questionText}`} secondary={`${q.marks} marks`} />
              </ListItem>
            ))}
          </List>
        )}
      </Paper>

      <ConfirmDialog
        open={!!removeTarget}
        title="Remove Question"
        message="Remove this question from the test?"
        confirmLabel="Remove"
        loading={removing}
        onConfirm={handleRemoveConfirm}
        onCancel={() => setRemoveTarget(null)}
      />

      {addDialogOpen && test && (
        <AddQuestionsToTestDialog
          open={addDialogOpen}
          test={test}
          existingQuestionIds={questions.map((q) => q.questionId)}
          onClose={() => setAddDialogOpen(false)}
          onSuccess={fetchData}
        />
      )}
    </Box>
  );
}

export default TestQuestionManagePage;