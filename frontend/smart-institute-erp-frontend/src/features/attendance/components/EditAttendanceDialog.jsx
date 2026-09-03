import { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';
import { attendanceApi } from '@services/api/attendanceApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { ATTENDANCE_STATUS } from '@constants/enums';

// Edits a single attendance record — matches UpdateAttendanceRequest
// (status required, remarks optional). Used from Part 2's records table.
function EditAttendanceDialog({ open, record, onClose, onSuccess }) {
  const [status, setStatus] = useState(record?.status || '');
  const [remarks, setRemarks] = useState(record?.remarks || '');
  const [submitting, setSubmitting] = useState(false);

  const handleSave = async () => {
    if (!status) return;
    setSubmitting(true);
    try {
      await attendanceApi.updateAttendance(record.id, { status, remarks: remarks || undefined });
      showSnackbar('Attendance record updated.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update attendance record.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Edit Attendance — {record?.studentName}</DialogTitle>
      <DialogContent>
        <TextField
          select
          fullWidth
          label="Status"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          sx={{ mt: 1, mb: 2 }}
        >
          <MenuItem value={ATTENDANCE_STATUS.PRESENT}>Present</MenuItem>
          <MenuItem value={ATTENDANCE_STATUS.ABSENT}>Absent</MenuItem>
          <MenuItem value={ATTENDANCE_STATUS.LATE}>Late</MenuItem>
          <MenuItem value={ATTENDANCE_STATUS.LEAVE}>Leave</MenuItem>
        </TextField>
        <TextField
          fullWidth
          label="Remarks (optional)"
          multiline
          rows={2}
          value={remarks}
          onChange={(e) => setRemarks(e.target.value)}
        />
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

export default EditAttendanceDialog;