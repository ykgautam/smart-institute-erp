import { useState, useEffect } from 'react';
import { Snackbar, Alert } from '@mui/material';
import { subscribeSnackbar } from './snackbarStore';

// Renders exactly ONE Snackbar for the whole app, mounted once in
// AppProviders. Any feature calls showSnackbar(...) from snackbarStore.js
// — no need to import/render a Snackbar per page (Section 20: consistent
// feedback pattern, not duplicated per screen).
function SnackbarProvider() {
  const [toast, setToast] = useState(null);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    // Subscribes once on mount; unsubscribes on unmount (cleanup function).
    const unsubscribe = subscribeSnackbar((newToast) => {
      setToast(newToast);
      setOpen(true);
    });
    return unsubscribe;
  }, []);

  const handleClose = (_, reason) => {
    if (reason === 'clickaway') return;
    setOpen(false);
  };

  if (!toast) return null;

  return (
    <Snackbar
      open={open}
      autoHideDuration={4000}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
    >
      <Alert onClose={handleClose} severity={toast.severity} variant="filled" sx={{ width: '100%' }}>
        {toast.message}
      </Alert>
    </Snackbar>
  );
}

export default SnackbarProvider;