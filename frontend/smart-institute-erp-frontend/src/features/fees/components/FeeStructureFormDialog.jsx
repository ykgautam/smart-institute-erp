import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid, CircularProgress, Box } from '@mui/material';
import { createFeeStructureSchema, updateFeeStructureSchema } from '@schemas/feeStructureSchemas';
import { feeStructureApi } from '@services/api/feeStructureApi';
import { courseApi } from '@services/api/courseApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';

function FeeStructureFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';
  const schema = isEdit ? updateFeeStructureSchema : createFeeStructureSchema;

  const [courses, setCourses] = useState([]);
  const [loadingCourses, setLoadingCourses] = useState(true);

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { courseId: '', amount: '', description: '' },
  });

  useEffect(() => {
    if (!open || isEdit) return; // Course dropdown only needed for Create (courseId is immutable on edit)
    async function loadCourses() {
      setLoadingCourses(true);
      try {
        const response = await courseApi.getCourses({ page: 0, size: 100, sortBy: 'courseName', direction: 'ASC' });
        setCourses(response?.content || []);
      } catch {
        showSnackbar('Failed to load courses.', 'error');
      } finally {
        setLoadingCourses(false);
      }
    }
    loadCourses();
  }, [open, isEdit]);

  useEffect(() => {
    if (open) {
      reset(
        isEdit && initialData
          ? { amount: initialData.amount, description: initialData.description || '' }
          : { courseId: '', amount: '', description: '' },
      );
      if (!isEdit) setLoadingCourses(true);
    }
  }, [open, isEdit, initialData, reset]);

  const onSubmit = async (data) => {
    try {
      if (isEdit) {
        await feeStructureApi.update(initialData.id, data);
        showSnackbar('Fee structure updated successfully.', 'success');
      } else {
        await feeStructureApi.create(data);
        showSnackbar('Fee structure created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to save fee structure.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Fee Structure' : 'Add Fee Structure'}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          {!isEdit && (
            <Grid size={12}>
              {loadingCourses ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
                  <CircularProgress size={24} />
                </Box>
              ) : (
                <Controller
                  name="courseId"
                  control={control}
                  render={({ field }) => (
                    <TextField {...field} select fullWidth label="Course" required error={!!errors.courseId} helperText={errors.courseId?.message}>
                      {courses.map((c) => (
                        <MenuItem key={c.id} value={c.id}>
                          {c.courseCode} — {c.courseName}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />
              )}
            </Grid>
          )}
          <Grid size={12}>
            <Controller
              name="amount"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth type="number" label="Amount (₹)" required error={!!errors.amount} helperText={errors.amount?.message} />
              )}
            />
          </Grid>
          <Grid size={12}>
            <Controller
              name="description"
              control={control}
              render={({ field }) => (
                <TextField {...field} fullWidth label="Description" multiline rows={2} error={!!errors.description} helperText={errors.description?.message} />
              )}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSubmitting}>
          Cancel
        </Button>
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting || (!isEdit && loadingCourses)}>
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default FeeStructureFormDialog;