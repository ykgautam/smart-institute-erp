import { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid } from '@mui/material';
import { instituteSchema } from '@schemas/instituteSchemas';
import { instituteApi } from '@services/api/instituteApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { INSTITUTE_TYPE } from '@constants/enums';

function InstituteFormDialog({ open, institute, onClose, onSuccess }) {
  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(instituteSchema),
    defaultValues: {
      name: '',
      email: '',
      mobile: '',
      landline: '',
      address: '',
      city: '',
      state: '',
      pincode: '',
      gstNumber: '',
      website: '',
      type: '',
    },
  });

  useEffect(() => {
    if (open && institute) {
      reset({
        name: institute.name || '',
        email: institute.email || '',
        mobile: institute.mobile || '',
        landline: institute.landline || '',
        address: institute.address || '',
        city: institute.city || '',
        state: institute.state || '',
        pincode: institute.pincode || '',
        gstNumber: institute.gstNumber || '',
        website: institute.website || '',
        type: institute.type || '',
      });
    }
  }, [open, institute, reset]);

  const onSubmit = async (data) => {
    try {
      await instituteApi.update(institute.id, data);
      showSnackbar('Institute details updated successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to update institute.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Edit Institute Details</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="name"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Institute Name" error={!!errors.name} helperText={errors.name?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="type"
              control={control}
              render={({ field }) => (
                <TextField {...field} select fullWidth label="Institute Type" error={!!errors.type} helperText={errors.type?.message}>
                  <MenuItem value={INSTITUTE_TYPE.COMPUTER_INSTITUTE}>Computer Institute</MenuItem>
                  <MenuItem value={INSTITUTE_TYPE.SCHOOL}>School</MenuItem>
                  <MenuItem value={INSTITUTE_TYPE.COLLEGE}>College</MenuItem>
                  <MenuItem value={INSTITUTE_TYPE.TRAINING_CENTER}>Training Center</MenuItem>
                  <MenuItem value={INSTITUTE_TYPE.COACHING_CENTER}>Coaching Center</MenuItem>
                </TextField>
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Email" error={!!errors.email} helperText={errors.email?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="mobile"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Mobile" error={!!errors.mobile} helperText={errors.mobile?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="landline"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Landline" error={!!errors.landline} helperText={errors.landline?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="website"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Website" error={!!errors.website} helperText={errors.website?.message} />
              )}
            />
          </Grid>
          <Grid size={12}>
            <Controller
              name="address"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Address" multiline rows={2} error={!!errors.address} helperText={errors.address?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <Controller
              name="city"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="City" error={!!errors.city} helperText={errors.city?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <Controller
              name="state"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="State" error={!!errors.state} helperText={errors.state?.message} />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <Controller
              name="pincode"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Pincode" error={!!errors.pincode} helperText={errors.pincode?.message} />
              )}
            />
          </Grid>
          <Grid size={12}>
            <Controller
              name="gstNumber"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="GST Number" error={!!errors.gstNumber} helperText={errors.gstNumber?.message} />
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
          {isSubmitting ? 'Saving...' : 'Save Changes'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default InstituteFormDialog;