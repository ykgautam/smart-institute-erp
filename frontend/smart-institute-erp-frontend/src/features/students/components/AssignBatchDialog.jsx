import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  MenuItem,
  TextField,
  CircularProgress,
  Box,
  Typography,
} from '@mui/material';
import { batchApi } from '@services/api/batchApi';
import { studentApi } from '@services/api/studentApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// Assigns a student to a batch. StudentResponse doesn't expose the
// student's CURRENT batch (see Part 4 assumption flag — backend gap),
// so this dialog can't pre-select or warn about an existing assignment;
// if the student is already assigned, the backend's own business-rule
// error ("Student is already assigned to a batch.") surfaces via
// Snackbar when the request fails.
function AssignBatchDialog({ open, student, onClose, onSuccess }) {
  const [batches, setBatches] = useState([]);
  const [loadingBatches, setLoadingBatches] = useState(true);
  const [selectedBatchId, setSelectedBatchId] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;

    async function loadBatches() {
      setLoadingBatches(true);
      try {
        // size=100 as a pragmatic dropdown-population fetch — BatchController
        // has no dedicated "getAll" endpoint (unlike Student's /getAll),
        // so we page through the standard list endpoint with a large size.
        const response = await batchApi.getBatches({
          page: 0,
          size: 100,
          sortBy: 'batchName',
          direction: 'ASC',
        });
        setBatches(response?.content || []);
      } catch {
        showSnackbar('Failed to load batches.', 'error');
      } finally {
        setLoadingBatches(false);
      }
    }

    loadBatches();
    setSelectedBatchId('');
  }, [open]);

  const handleAssign = async () => {
    if (!selectedBatchId) return;
    setSubmitting(true);
    try {
      await studentApi.assignToBatch(student.id, selectedBatchId);
      showSnackbar(`${student.fullName} assigned to batch successfully.`, 'success');
      onSuccess();
      onClose();
    } catch (err) {
      // Surfaces backend business errors verbatim (e.g. "Batch capacity
      // has been reached.", "Student is already assigned to a batch.")
      // per Section 11's UX requirement — no generic fallback message.
      showSnackbar(err.message || 'Failed to assign student to batch.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Assign to Batch</DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Assign <strong>{student?.fullName}</strong> to a batch.
        </Typography>

        {loadingBatches ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 3 }}>
            <CircularProgress size={28} />
          </Box>
        ) : (
          <TextField
            select
            fullWidth
            label="Batch"
            value={selectedBatchId}
            onChange={(e) => setSelectedBatchId(e.target.value)}
            disabled={batches.length === 0}
            helperText={batches.length === 0 ? 'No batches available.' : ''}
          >
            {batches.map((batch) => (
              <MenuItem key={batch.id} value={batch.id}>
                {batch.batchCode} — {batch.batchName} ({batch.courseName})
                {typeof batch.studentCount === 'number' && typeof batch.capacity === 'number'
                  ? ` · ${batch.studentCount}/${batch.capacity}`
                  : ''}
              </MenuItem>
            ))}
          </TextField>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>
          Cancel
        </Button>
        <Button
          onClick={handleAssign}
          variant="contained"
          disabled={submitting || loadingBatches || !selectedBatchId}
        >
          {submitting ? 'Assigning...' : 'Assign'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AssignBatchDialog;