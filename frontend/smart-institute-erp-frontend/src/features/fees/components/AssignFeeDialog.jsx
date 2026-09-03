import { useEffect, useState } from 'react';
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
  CircularProgress,
  Box,
} from '@mui/material';
import { assignFeeSchema } from '@schemas/studentFeeSchemas';
import { studentFeeApi } from '@services/api/studentFeeApi';
import { studentApi } from '@services/api/studentApi';
import { feeStructureApi } from '@services/api/feeStructureApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';

// Standalone dialog (not row-edit) since a student may not have an
// existing fee record — needs both a Student picker and a Fee
// Structure picker, fetched fresh each time it opens.
function AssignFeeDialog({ open, onClose, onSuccess }) {
  const [students, setStudents] = useState([]);
  const [feeStructures, setFeeStructures] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(assignFeeSchema),
    defaultValues: { studentId: '', feeStructureId: '', discount: '' },
  });

  useEffect(() => {
    if (!open) return;

    async function loadOptions() {
      setLoadingOptions(true);
      try {
        // Student's dedicated unpaged "getAll" endpoint (/students/getAll)
        // is a better fit here than the paginated list — we need the
        // full active-student set for a dropdown, not one page of it.
        const [studentRes, feeStructureRes] = await Promise.all([
          studentApi.getStudents({ page: 0, size: 100, sortBy: 'firstName', direction: 'ASC' }),
          feeStructureApi.getAll(),
        ]);
        setStudents(studentRes?.content || []);
        setFeeStructures(feeStructureRes || []);
      } catch {
        showSnackbar('Failed to load students/fee structures.', 'error');
      } finally {
        setLoadingOptions(false);
      }
    }

    loadOptions();
    reset({ studentId: '', feeStructureId: '', discount: '' });
  }, [open, reset]);

  const onSubmit = async (data) => {
    const payload = {
      studentId: data.studentId,
      feeStructureId: data.feeStructureId,
      discount: data.discount === '' ? undefined : data.discount,
    };

    try {
      await studentFeeApi.assign(payload);
      showSnackbar('Fee assigned to student successfully.', 'success');
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        // Surfaces backend business errors verbatim (e.g. a duplicate
        // assignment error, if the backend enforces one-fee-per-student).
        showSnackbar(err.message || 'Failed to assign fee.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Assign Fee to Student</DialogTitle>
      <DialogContent>
        {loadingOptions ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress size={28} />
          </Box>
        ) : (
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid size={{ xs: 12}}>
              <Controller
                name="studentId"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    select
                    fullWidth
                    label="Student"
                    required
                    error={!!errors.studentId}
                    helperText={errors.studentId?.message}
                  >
                    {students.map((s) => (
                      <MenuItem key={s.id} value={s.id}>
                        {s.admissionNumber} — {s.fullName}
                      </MenuItem>
                    ))}
                  </TextField>
                )}
              />
            </Grid>
            <Grid size={{ xs: 12}}>
              <Controller
                name="feeStructureId"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    select
                    fullWidth
                    label="Fee Structure"
                    required
                    error={!!errors.feeStructureId}
                    helperText={errors.feeStructureId?.message}
                  >
                    {feeStructures.map((fs) => (
                      <MenuItem key={fs.id} value={fs.id}>
                        {fs.courseName} — ₹{Number(fs.amount).toLocaleString('en-IN')}
                      </MenuItem>
                    ))}
                  </TextField>
                )}
              />
            </Grid>
            <Grid size={{ xs: 12}}>
              <Controller
                name="discount"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    type="number"
                    label="Discount (₹, optional)"
                    error={!!errors.discount}
                    helperText={errors.discount?.message}
                  />
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
          {isSubmitting ? 'Assigning...' : 'Assign'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AssignFeeDialog;