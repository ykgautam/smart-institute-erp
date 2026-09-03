import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Grid, CircularProgress, Box } from '@mui/material';
import { batchSchema } from '@schemas/batchSchemas';
import { batchApi } from '@services/api/batchApi';
import { courseApi } from '@services/api/courseApi';
import { userApi } from '@services/api/userApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { ROLE_TYPE, USER_STATUS } from '@constants/enums';

function BatchFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';

  const [courses, setCourses] = useState([]);
  const [faculty, setFaculty] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(batchSchema),
    defaultValues: {
      batchCode: '',
      batchName: '',
      courseId: '',
      facultyId: '',
      startDate: '',
      endDate: '',
      startTime: '',
      endTime: '',
      capacity: '',
    },
  });

  // Load dropdown options (Course, Faculty) once when the dialog opens.
  // No dedicated "getAll" endpoints for either — Course uses the paged
  // list with a large size; Faculty filters the unpaged /users list
  // client-side, mirroring BatchServiceImpl.getFaculty()'s validation
  // (must be ACTIVE and role=FACULTY).
  useEffect(() => {
    if (!open) return;

    async function loadOptions() {
      setLoadingOptions(true);
      try {
        const [courseRes, userRes] = await Promise.all([
          courseApi.getCourses({ page: 0, size: 100, sortBy: 'courseName', direction: 'ASC' }),
          userApi.getAllUsers(),
        ]);
        setCourses(courseRes?.content || []);
        const activeFaculty = (userRes || []).filter(
          (u) => u.role === ROLE_TYPE.FACULTY && u.status === USER_STATUS.ACTIVE,
        );
        setFaculty(activeFaculty);
      } catch {
        showSnackbar('Failed to load courses/faculty options.', 'error');
      } finally {
        setLoadingOptions(false);
      }
    }

    loadOptions();
  }, [open]);

  useEffect(() => {
    if (open) {
      reset(
        isEdit && initialData
          ? {
              batchCode: initialData.batchCode,
              batchName: initialData.batchName,
              courseId: initialData.courseId,
              facultyId: initialData.facultyId || '',
              startDate: initialData.startDate,
              endDate: initialData.endDate,
              // Backend LocalTime likely returns "HH:mm:ss" — trim to
              // "HH:mm" for the native time input.
              startTime: initialData.startTime?.slice(0, 5) || '',
              endTime: initialData.endTime?.slice(0, 5) || '',
              capacity: initialData.capacity,
            }
          : {
              batchCode: '',
              batchName: '',
              courseId: '',
              facultyId: '',
              startDate: '',
              endDate: '',
              startTime: '',
              endTime: '',
              capacity: '',
            },
      );
    }
  }, [open, isEdit, initialData, reset]);

  const onSubmit = async (data) => {
    const payload = {
      ...data,
      facultyId: data.facultyId === '' ? undefined : data.facultyId,
    };

    try {
      if (isEdit) {
        await batchApi.updateBatch(initialData.id, payload);
        showSnackbar('Batch updated successfully.', 'success');
      } else {
        await batchApi.createBatch(payload);
        showSnackbar('Batch created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        // Surfaces business errors verbatim: duplicate batch code/name,
        // date/time conflicts, "Selected user is not a faculty.", etc.
        showSnackbar(err.message || 'Failed to save batch.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit Batch' : 'Add Batch'}</DialogTitle>
      <DialogContent>
        {loadingOptions ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress size={28} />
          </Box>
        ) : (
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="batchCode"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="Batch Code" required error={!!errors.batchCode} helperText={errors.batchCode?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="batchName"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="Batch Name" required error={!!errors.batchName} helperText={errors.batchName?.message} />
                )}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
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
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="facultyId"
                control={control}
                render={({ field }) => (
                  <TextField {...field} select fullWidth label="Faculty (optional)" error={!!errors.facultyId} helperText={errors.facultyId?.message}>
                    <MenuItem value="">—</MenuItem>
                    {faculty.map((f) => (
                      <MenuItem key={f.id} value={f.id}>
                        {f.firstName} {f.lastName}
                      </MenuItem>
                    ))}
                  </TextField>
                )}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="startDate"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    type="date"
                    label="Start Date"
                    required
                    slotProps={{ inputLabel: { shrink: true } }}
                    error={!!errors.startDate}
                    helperText={errors.startDate?.message}
                  />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="endDate"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    type="date"
                    label="End Date"
                    required
                    slotProps={{ inputLabel: { shrink: true } }}
                    error={!!errors.endDate}
                    helperText={errors.endDate?.message}
                  />
                )}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="startTime"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    type="time"
                    label="Start Time"
                    required
                    slotProps={{ inputLabel: { shrink: true } }}
                    error={!!errors.startTime}
                    helperText={errors.startTime?.message}
                  />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="endTime"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    type="time"
                    label="End Time"
                    required
                    slotProps={{ inputLabel: { shrink: true } }}
                    error={!!errors.endTime}
                    helperText={errors.endTime?.message}
                  />
                )}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="capacity"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth type="number" label="Capacity" required error={!!errors.capacity} helperText={errors.capacity?.message} />
                )}
              />
            </Grid>
          </Grid>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSubmitting}>
          Cancel
        </Button>
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting || loadingOptions}>
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create Batch'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default BatchFormDialog;