import { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  MenuItem,
  Grid,
} from '@mui/material';
import { createStudentSchema, updateStudentSchema, normalizeFieldErrors } from '@schemas/studentSchemas';
import { studentApi } from '@services/api/studentApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { GENDER } from '@constants/enums';

// Handles BOTH create and edit — driven by `mode`. This avoids building
// two near-identical forms (Section 25: no duplicate UI logic). The only
// real differences are: which schema validates, which API call fires,
// and whether admissionNumber is shown (immutable after creation per
// Section 9 — it's absent from UpdateStudentRequest entirely).
function StudentFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';
  const schema = isEdit ? updateStudentSchema : createStudentSchema;

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      admissionNumber: '',
      rollNumber: '',
      firstName: '',
      lastName: '',
      gender: '',
      dateOfBirth: '',
      mobile: '',
      email: '',
      fatherName: '',
      motherName: '',
      guardianMobile: '',
      address: '',
      city: '',
      state: '',
      pincode: '',
      admissionDate: '',
    },
  });

  // When editing, populate the form with the selected student's data.
  // When opening fresh for create, reset to blank defaults. This effect
  // re-runs whenever the dialog opens or the target row changes.
  useEffect(() => {
    if (open) {
      if (isEdit && initialData) {
        reset({
          rollNumber: initialData.rollNumber || '',
          firstName: initialData.firstName || '',
          lastName: initialData.lastName || '',
          gender: initialData.gender || '',
          dateOfBirth: initialData.dateOfBirth || '',
          mobile: initialData.mobile || '',
          email: initialData.email || '',
          fatherName: initialData.fatherName || '',
          motherName: initialData.motherName || '',
          guardianMobile: initialData.guardianMobile || '',
          address: initialData.address || '',
          city: initialData.city || '',
          state: initialData.state || '',
          pincode: initialData.pincode || '',
          admissionDate: initialData.admissionDate || '',
        });
      } else {
        reset({
          admissionNumber: '',
          rollNumber: '',
          firstName: '',
          lastName: '',
          gender: '',
          dateOfBirth: '',
          mobile: '',
          email: '',
          fatherName: '',
          motherName: '',
          guardianMobile: '',
          address: '',
          city: '',
          state: '',
          pincode: '',
          admissionDate: '',
        });
      }
    }
  }, [open, isEdit, initialData, reset]);

  const onSubmit = async (data) => {
    // Strip empty-string optional fields to undefined so we don't send
    // "" for fields the backend expects to be null/absent.
    const payload = Object.fromEntries(
      Object.entries(data).map(([key, value]) => [key, value === '' ? undefined : value]),
    );

    try {
      if (isEdit) {
        await studentApi.updateStudent(initialData.id, payload);
        showSnackbar('Student updated successfully.', 'success');
      } else {
        await studentApi.createStudent(payload);
        showSnackbar('Student created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      // Map backend field-level validation errors onto the correct
      // inputs (e.g. 409 duplicate admission number) rather than just
      // showing a generic toast — much better UX for form corrections.
      const fieldErrors = normalizeFieldErrors(err.errors);
      const hasFieldErrors = Object.keys(fieldErrors).length > 0;

      if (hasFieldErrors) {
        Object.entries(fieldErrors).forEach(([field, message]) => {
          setError(field, { type: 'server', message });
        });
      } else {
        // No field-specific detail — show the general error message
        // (e.g. "Duplicate admission number/email/mobile" from a 409).
        showSnackbar(err.message || 'Failed to save student.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Student' : 'Add Student'}</DialogTitle>
      <DialogContent>

        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          {!isEdit && (
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="admissionNumber"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Admission Number"
                    error={!!errors.admissionNumber}
                    helperText={errors.admissionNumber?.message}
                  />
                )}
              />
            </Grid>
          )}

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="rollNumber"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Roll Number"
                  error={!!errors.rollNumber}
                  helperText={errors.rollNumber?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="firstName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="First Name"
                  required={!isEdit}
                  error={!!errors.firstName}
                  helperText={errors.firstName?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="lastName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Last Name"
                  error={!!errors.lastName}
                  helperText={errors.lastName?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="gender"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  select
                  fullWidth
                  label="Gender"
                  error={!!errors.gender}
                  helperText={errors.gender?.message}
                >
                  <MenuItem value="">—</MenuItem>
                  <MenuItem value={GENDER.MALE}>Male</MenuItem>
                  <MenuItem value={GENDER.FEMALE}>Female</MenuItem>
                  <MenuItem value={GENDER.OTHER}>Other</MenuItem>
                </TextField>
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="dateOfBirth"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  type="date"
                  label="Date of Birth"
                  slotProps={{ inputLabel: { shrink: true } }}
                  error={!!errors.dateOfBirth}
                  helperText={errors.dateOfBirth?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="mobile"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Mobile"
                  error={!!errors.mobile}
                  helperText={errors.mobile?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Email"
                  error={!!errors.email}
                  helperText={errors.email?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="fatherName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Father's Name"
                  required={!isEdit}
                  error={!!errors.fatherName}
                  helperText={errors.fatherName?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="motherName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Mother's Name"
                  error={!!errors.motherName}
                  helperText={errors.motherName?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="guardianMobile"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Guardian Mobile"
                  error={!!errors.guardianMobile}
                  helperText={errors.guardianMobile?.message}
                />
              )}
            />
          </Grid>

          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="admissionDate"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  type="date"
                  label="Admission Date"
                  slotProps={{ inputLabel: { shrink: true } }}
                  error={!!errors.admissionDate}
                  helperText={errors.admissionDate?.message}
                />
              )}
            />
          </Grid>

          <Grid size={12}>
            <Controller
              name="address"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Address"
                  multiline
                  rows={2}
                  error={!!errors.address}
                  helperText={errors.address?.message}
                />
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
                <TextField
                  {...field}
                  fullWidth
                  label="Pincode"
                  error={!!errors.pincode}
                  helperText={errors.pincode?.message}
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
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Student'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default StudentFormDialog;