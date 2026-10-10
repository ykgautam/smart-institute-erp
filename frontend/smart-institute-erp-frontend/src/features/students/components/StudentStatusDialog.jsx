import { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';
import { studentApi } from '@services/api/studentApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { STUDENT_STATUS } from '@constants/enums';

// Distinct from "Deactivate" (which uses the DELETE endpoint to soft-
// delete a student, active=false). This dialog uses PATCH /status to
// set the student's lifecycle status explicitly — ACTIVE, INACTIVE,
// COMPLETED, or DROPPED — matching StudentStatus's full enum, which was
// previously only reachable via the binary Deactivate action.
function StudentStatusDialog({ open, student, onClose, onSuccess }) {
  const [status, setStatus] = useState(student?.status || '');
  const [submitting, setSubmitting] = useState(false);

  const handleSave = async () => {
    if (!status) return;
    setSubmitting(true);
    try {
      await studentApi.updateStatus(student.id, status);
      showSnackbar('Student status updated successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update student status.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Change Student Status</DialogTitle>
      <DialogContent>
        <TextField select fullWidth label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ mt: 1 }}>
          <MenuItem value={STUDENT_STATUS.ACTIVE}>Active</MenuItem>
          <MenuItem value={STUDENT_STATUS.INACTIVE}>Inactive</MenuItem>
          <MenuItem value={STUDENT_STATUS.COMPLETED}>Completed</MenuItem>
          <MenuItem value={STUDENT_STATUS.DROPPED}>Dropped</MenuItem>
        </TextField>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Cancel</Button>
        <Button onClick={handleSave} variant="contained" disabled={submitting || !status}>
          {submitting ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default StudentStatusDialog;