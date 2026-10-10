import { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';
import { questionApi } from '@services/api/questionApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

function QuestionStatusDialog({ open, question, onClose, onSuccess }) {
  const [active, setActive] = useState(question?.active ?? true);
  const [submitting, setSubmitting] = useState(false);

  const handleSave = async () => {
    setSubmitting(true);
    try {
      await questionApi.updateStatus(question.id, active);
      showSnackbar('Question status updated successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update question status.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Change Question Status</DialogTitle>
      <DialogContent>
        <TextField
          select
          fullWidth
          label="Status"
          value={active ? 'true' : 'false'}
          onChange={(e) => setActive(e.target.value === 'true')}
          sx={{ mt: 1 }}
        >
          <MenuItem value="true">Active</MenuItem>
          <MenuItem value="false">Inactive</MenuItem>
        </TextField>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Cancel</Button>
        <Button onClick={handleSave} variant="contained" disabled={submitting}>
          {submitting ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default QuestionStatusDialog;