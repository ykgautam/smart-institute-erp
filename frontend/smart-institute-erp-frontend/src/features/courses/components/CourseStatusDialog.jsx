import { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';
import { courseApi } from '@services/api/courseApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { COURSE_STATUS } from '@constants/enums';

function CourseStatusDialog({ open, course, onClose, onSuccess }) {
  const [status, setStatus] = useState(course?.status || '');
  const [submitting, setSubmitting] = useState(false);

  const handleSave = async () => {
    if (!status) return;
    setSubmitting(true);
    try {
      await courseApi.updateStatus(course.id, status);
      showSnackbar('Course status updated successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update course status.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Change Course Status</DialogTitle>
      <DialogContent>
        <TextField select fullWidth label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ mt: 1 }}>
          <MenuItem value={COURSE_STATUS.ACTIVE}>Active</MenuItem>
          <MenuItem value={COURSE_STATUS.INACTIVE}>Inactive</MenuItem>
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

export default CourseStatusDialog;