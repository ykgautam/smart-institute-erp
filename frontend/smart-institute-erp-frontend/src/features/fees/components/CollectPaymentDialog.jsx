import { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid, Typography } from '@mui/material';
import { collectPaymentSchema } from '@schemas/feePaymentSchemas';
import { feePaymentApi } from '@services/api/feePaymentApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { PAYMENT_MODE } from '@constants/enums';

// studentFee here is the full StudentFeeResponse row — gives us
// studentFeeId (row.id), studentName, and pendingAmount for context/
// pre-filling the amount field with a sensible default.
function CollectPaymentDialog({ open, studentFee, onClose, onSuccess }) {
  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(collectPaymentSchema),
    defaultValues: { amount: '', paymentMode: '', transactionReference: '', remarks: '' },
  });

  useEffect(() => {
    if (open && studentFee) {
      // Pre-fill with the full pending amount as a convenience default —
      // the user can still edit it for a partial payment.
      reset({
        amount: studentFee.pendingAmount || '',
        paymentMode: '',
        transactionReference: '',
        remarks: '',
      });
    }
  }, [open, studentFee, reset]);

  const onSubmit = async (data) => {
    const payload = {
      studentFeeId: studentFee.id,
      amount: data.amount,
      paymentMode: data.paymentMode,
      transactionReference: data.transactionReference || undefined,
      remarks: data.remarks || undefined,
    };

    try {
      await feePaymentApi.collect(payload);
      showSnackbar('Payment collected successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        // Surfaces backend business errors verbatim — e.g. if the
        // backend rejects an amount exceeding pendingAmount.
        showSnackbar(err.message || 'Failed to collect payment.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Collect Payment</DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          <strong>{studentFee?.studentName}</strong> — Pending: ₹
          {Number(studentFee?.pendingAmount || 0).toLocaleString('en-IN')}
        </Typography>

        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="amount"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  type="number"
                  label="Amount (₹)"
                  required
                  error={!!errors.amount}
                  helperText={errors.amount?.message}
                />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="paymentMode"
              control={control}
              render={({ field }) => (
                <TextField
                {...field}
                select
                fullWidth
                label="Payment Mode"
                required
                error={!!errors.paymentMode}
                helperText={errors.paymentMode?.message}
                slotProps={{
                    select: {
                    MenuProps: { PaperProps: { sx: { minWidth: 200 } } },
                    },
                }}
                >
                  <MenuItem value={PAYMENT_MODE.CASH}>Cash</MenuItem>
                  <MenuItem value={PAYMENT_MODE.UPI}>UPI</MenuItem>
                  <MenuItem value={PAYMENT_MODE.CARD}>Card</MenuItem>
                  <MenuItem value={PAYMENT_MODE.BANK_TRANSFER}>Bank Transfer</MenuItem>
                </TextField>
              )}
            />
          </Grid>
          <Grid size={12}>
            <Controller
              name="transactionReference"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Transaction Reference (optional)"
                  error={!!errors.transactionReference}
                  helperText={errors.transactionReference?.message}
                />
              )}
            />
          </Grid>
          <Grid size={12}>
            <Controller
              name="remarks"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Remarks (optional)"
                  multiline
                  rows={2}
                  error={!!errors.remarks}
                  helperText={errors.remarks?.message}
                />
              )}
            />
          </Grid>
        </Grid>

      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSubmitting}>
          Cancel
        </Button>
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting}>
          {isSubmitting ? 'Recording...' : 'Collect Payment'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default CollectPaymentDialog;