import { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';
import { batchApi } from '@services/api/batchApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { BATCH_STATUS } from '@constants/enums';

// Separate dialog because status is updated via its own endpoint
// (PATCH /batches/{id}/status) and isn't part of Create/UpdateBatchRequest.
function BatchStatusDialog({ open, batch, onClose, onSuccess }) {
  const [status, setStatus] = useState(batch?.status || '');
  const [submitting, setSubmitting] = useState(false);

  const handleSave = async () => {
    if (!status) return;
    setSubmitting(true);
    try {
      await batchApi.updateStatus(batch.id, status);
      showSnackbar('Batch status updated successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update batch status.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Change Batch Status</DialogTitle>
      <DialogContent>
        <TextField select fullWidth label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ mt: 1 }}>
          <MenuItem value={BATCH_STATUS.PLANNED}>Planned</MenuItem>
          <MenuItem value={BATCH_STATUS.ACTIVE}>Active</MenuItem>
          <MenuItem value={BATCH_STATUS.COMPLETED}>Completed</MenuItem>
          <MenuItem value={BATCH_STATUS.CANCELLED}>Cancelled</MenuItem>
        </TextField>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>
          Cancel
        </Button>
        <Button onClick={handleSave} variant="contained" disabled={submitting || !status}>
          {submitting ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default BatchStatusDialog;