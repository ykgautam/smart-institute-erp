import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid, CircularProgress, Box, FormControlLabel, Checkbox } from '@mui/material';
import { createTopicSchema, updateTopicSchema } from '@schemas/topicSchemas';
import { topicApi } from '@services/api/topicApi';
import { courseApi } from '@services/api/courseApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';

function TopicFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';
  const schema = isEdit ? updateTopicSchema : createTopicSchema;

  const [courses, setCourses] = useState([]);
  const [loadingCourses, setLoadingCourses] = useState(true);

  const {
    control, handleSubmit, reset, setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { courseId: '', name: '', description: '', displayOrder: 1, active: true },
  });

  useEffect(() => {
    if (!open) return;
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
  }, [open]);

  useEffect(() => {
    if (open) {
      reset(
        isEdit && initialData
          ? {
              courseId: initialData.courseId,
              name: initialData.name,
              description: initialData.description || '',
              displayOrder: initialData.displayOrder,
              active: initialData.active,
            }
          : { courseId: '', name: '', description: '', displayOrder: 1, active: true },
      );
    }
  }, [open, isEdit, initialData, reset]);

  const onSubmit = async (data) => {
    try {
      if (isEdit) {
        await topicApi.update(initialData.id, data);
        showSnackbar('Topic updated successfully.', 'success');
      } else {
        await topicApi.create(data);
        showSnackbar('Topic created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to save topic.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Topic' : 'Add Topic'}</DialogTitle>
      <DialogContent>
        {loadingCourses ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress size={28} /></Box>
        ) : (
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid size={12}>
              <Controller name="courseId" control={control} render={({ field }) => (
                <TextField {...field} select fullWidth label="Course" required error={!!errors.courseId} helperText={errors.courseId?.message}>
                  {courses.map((c) => <MenuItem key={c.id} value={c.id}>{c.courseCode} — {c.courseName}</MenuItem>)}
                </TextField>
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 8 }}>
              <Controller name="name" control={control} render={({ field }) => (
                <TextField {...field} fullWidth label="Topic Name" required error={!!errors.name} helperText={errors.name?.message} />
              )} />
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <Controller name="displayOrder" control={control} render={({ field }) => (
                <TextField {...field} fullWidth type="number" label="Display Order" required error={!!errors.displayOrder} helperText={errors.displayOrder?.message} />
              )} />
            </Grid>
            <Grid size={12}>
              <Controller name="description" control={control} render={({ field }) => (
                <TextField {...field} fullWidth label="Description" multiline rows={2} error={!!errors.description} helperText={errors.description?.message} />
              )} />
            </Grid>
            {isEdit && (
              <Grid size={12}>
                <Controller name="active" control={control} render={({ field }) => (
                  <FormControlLabel control={<Checkbox {...field} checked={field.value} />} label="Active" />
                )} />
              </Grid>
            )}
          </Grid>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSubmitting}>Cancel</Button>
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting || loadingCourses}>
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Topic'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default TopicFormDialog;