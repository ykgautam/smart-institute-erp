import { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, List, ListItem, ListItemText, Checkbox, Typography, CircularProgress, Box } from '@mui/material';
import { questionApi } from '@services/api/questionApi';
import { testQuestionApi } from '@services/api/testQuestionApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// test: TestResponse (has courseId/topicId to scope the picker);
// existingQuestionIds: already-linked question ids, excluded from the list.
function AddQuestionsToTestDialog({ open, test, existingQuestionIds, onClose, onSuccess }) {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    async function load() {
      setLoading(true);
      try {
        const response = await questionApi.search({ courseId: test.courseId, topicId: test.topicId });
        setQuestions((response || []).filter((q) => !existingQuestionIds.includes(q.id)));
      } catch {
        showSnackbar('Failed to load available questions.', 'error');
      } finally {
        setLoading(false);
      }
    }
    load();
    setSelected([]);
  }, [open, test, existingQuestionIds]);

  const toggle = (id) => setSelected((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));

  const handleAdd = async () => {
    if (selected.length === 0) return;
    setSubmitting(true);
    try {
      await testQuestionApi.addQuestions(test.id, selected);
      showSnackbar(`${selected.length} question(s) added to test.`, 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to add questions.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Add Questions to Test</DialogTitle>
      <DialogContent>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress size={28} /></Box>
        ) : questions.length === 0 ? (
          <Typography variant="body2" color="text.secondary">No additional questions available for this course/topic.</Typography>
        ) : (
          <List dense>
            {questions.map((q) => (
              <ListItem key={q.id}>
                <Checkbox checked={selected.includes(q.id)} onChange={() => toggle(q.id)} />
                <ListItemText primary={q.questionText} secondary={`${q.marks} marks — ${q.difficulty}`} />
              </ListItem>
            ))}
          </List>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Cancel</Button>
        <Button onClick={handleAdd} variant="contained" disabled={submitting || selected.length === 0}>
          {submitting ? 'Adding...' : `Add (${selected.length})`}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AddQuestionsToTestDialog;