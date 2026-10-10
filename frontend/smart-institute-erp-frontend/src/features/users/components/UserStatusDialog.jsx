import { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem } from '@mui/material';
import { userApi } from '@services/api/userApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { USER_STATUS } from '@constants/enums';

function UserStatusDialog({ open, user, onClose, onSuccess }) {
  const [status, setStatus] = useState(user?.status || '');
  const [submitting, setSubmitting] = useState(false);

  const handleSave = async () => {
    if (!status) return;
    setSubmitting(true);
    try {
      await userApi.updateStatus(user.id, status);
      showSnackbar('User status updated successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update user status.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Change User Status</DialogTitle>
      <DialogContent>
        <TextField select fullWidth label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ mt: 1 }}>
          <MenuItem value={USER_STATUS.ACTIVE}>Active</MenuItem>
          <MenuItem value={USER_STATUS.INACTIVE}>Inactive</MenuItem>
          <MenuItem value={USER_STATUS.LOCKED}>Locked</MenuItem>
          <MenuItem value={USER_STATUS.DELETED}>Deleted</MenuItem>
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

export default UserStatusDialog;