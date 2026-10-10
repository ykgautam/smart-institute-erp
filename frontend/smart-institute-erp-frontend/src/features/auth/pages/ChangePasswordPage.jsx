import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { Box, Paper, TextField, Button, Typography } from '@mui/material';
import { changePasswordSchema } from '@schemas/authSchemas';
import { authApi } from '@services/api/authApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';

// Matches ChangePasswordRequest exactly (PATCH /auth/change-password) —
// schema already existed from Sprint 1 Part 3, just never had a page
// wired to it until now.
function ChangePasswordPage() {
  const navigate = useNavigate();
  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: { currentPassword: '', newPassword: '', confirmPassword: '' },
  });

  const onSubmit = async (data) => {
    try {
      await authApi.changePassword(data);
      showSnackbar('Password changed successfully.', 'success');
      reset();
      navigate('/dashboard');
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        // Surfaces backend business errors verbatim — e.g. "Current
        // password is incorrect." or "New password must be different
        // from current password." (both thrown explicitly by AuthServiceImpl).
        showSnackbar(err.message || 'Failed to change password.', 'error');
      }
    }
  };

  return (
    <Box sx={{ maxWidth: 420 }}>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Change Password
      </Typography>
      <Paper variant="outlined" sx={{ p: 3 }}>
        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <Controller
            name="currentPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                type="password"
                label="Current Password"
                margin="normal"
                error={!!errors.currentPassword}
                helperText={errors.currentPassword?.message}
              />
            )}
          />
          <Controller
            name="newPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                type="password"
                label="New Password"
                margin="normal"
                error={!!errors.newPassword}
                helperText={errors.newPassword?.message}
              />
            )}
          />
          <Controller
            name="confirmPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                type="password"
                label="Confirm New Password"
                margin="normal"
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
              />
            )}
          />
          <Button type="submit" fullWidth variant="contained" size="large" disabled={isSubmitting} sx={{ mt: 3 }}>
            {isSubmitting ? 'Updating...' : 'Change Password'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default ChangePasswordPage;