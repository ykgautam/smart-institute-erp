import { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid } from '@mui/material';
import { courseSchema } from '@schemas/courseSchemas';
import { courseApi } from '@services/api/courseApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { DURATION_TYPE } from '@constants/enums';

// Single schema for both modes (see courseSchemas.js) — unlike Student,
// Create and Update DTOs are identical on the backend for Course.
function CourseFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(courseSchema),
    defaultValues: {
      courseCode: '',
      courseName: '',
      description: '',
      duration: '',
      durationType: '',
      fee: '',
    },
  });

  useEffect(() => {
    if (open) {
      reset(
        isEdit && initialData
          ? {
              courseCode: initialData.courseCode,
              courseName: initialData.courseName,
              description: initialData.description || '',
              duration: initialData.duration,
              durationType: initialData.durationType,
              fee: initialData.fee,
            }
          : { courseCode: '', courseName: '', description: '', duration: '', durationType: '', fee: '' },
      );
    }
  }, [open, isEdit, initialData, reset]);

  const onSubmit = async (data) => {
    try {
      if (isEdit) {
        await courseApi.updateCourse(initialData.id, data);
        showSnackbar('Course updated successfully.', 'success');
      } else {
        await courseApi.createCourse(data);
        showSnackbar('Course created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to save course.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Course' : 'Add Course'}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="courseCode"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Course Code"
                  required
                  error={!!errors.courseCode}
                  helperText={errors.courseCode?.message}
                />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="courseName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Course Name"
                  required
                  error={!!errors.courseName}
                  helperText={errors.courseName?.message}
                />
              )}
            />
          </Grid>
          <Grid size={12}>
            <Controller
              name="description"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  label="Description"
                  multiline
                  rows={2}
                  error={!!errors.description}
                  helperText={errors.description?.message}
                />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <Controller
              name="duration"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  type="number"
                  label="Duration"
                  required
                  error={!!errors.duration}
                  helperText={errors.duration?.message}
                />
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 5 }}>
            <Controller
              name="durationType"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  select
                  fullWidth
                  label="Duration Type"
                  required
                  error={!!errors.durationType}
                  helperText={errors.durationType?.message}
                >
                  <MenuItem value={DURATION_TYPE.DAYS}>Days</MenuItem>
                  <MenuItem value={DURATION_TYPE.WEEKS}>Weeks</MenuItem>
                  <MenuItem value={DURATION_TYPE.MONTHS}>Months</MenuItem>
                  <MenuItem value={DURATION_TYPE.YEARS}>Years</MenuItem>
                </TextField>
              )}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <Controller
              name="fee"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  fullWidth
                  type="number"
                  label="Fee (₹)"
                  required
                  error={!!errors.fee}
                  helperText={errors.fee?.message}
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
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Course'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default CourseFormDialog;