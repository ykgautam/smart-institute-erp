import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Box, Paper, TextField, Button, Typography, Grid, MenuItem, CircularProgress } from '@mui/material';
import { updateUserSchema } from '@schemas/userSchemas';
import { userApi } from '@services/api/userApi';
import { roleApi } from '@services/api/roleApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { useAuth } from '@hooks/useAuth';
import { GENDER } from '@constants/enums';

// Self-service profile edit. Reuses UpdateUserRequest/updateUserSchema
// from the User Management module (Sprint 9) since no separate
// "update my own profile" endpoint exists — see assumption flag.
// Email/role are NOT editable here even though the form component is
// shared conceptually: role change and email change are administrative
// actions that belong in User Management, not self-service.
function MyProfilePage() {
  const { user } = useAuth();
  const [roles, setRoles] = useState([]);
  const [loadingRoles, setLoadingRoles] = useState(true);

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(updateUserSchema),
    defaultValues: { firstName: '', lastName: '', mobile: '', gender: '', roleId: '' },
  });

  useEffect(() => {
    async function loadRoles() {
      try {
        const response = await roleApi.getAll();
        setRoles(response || []);
      } finally {
        setLoadingRoles(false);
      }
    }
    loadRoles();
  }, []);

  // Pre-fill once both the user and roles are available (role name ->
  // id resolution needed, same pattern as UserFormDialog in Sprint 9).
  useEffect(() => {
    if (user && roles.length > 0) {
      const matched = roles.find((r) => r.name === user.role);
      reset({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        mobile: user.mobile || '',
        gender: user.gender || '',
        roleId: matched?.id || '',
      });
    }
  }, [user, roles, reset]);

  const onSubmit = async (data) => {
    try {
      await userApi.updateUser(user.id, data);
      showSnackbar('Profile updated successfully. Changes will reflect on next login/refresh.', 'success');
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to update profile.', 'error');
      }
    }
  };

  if (loadingRoles) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 480 }}>
      <Typography variant="h2" sx={{ mb: 3 }}>
        My Profile
      </Typography>
      <Paper variant="outlined" sx={{ p: 3 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          {user?.email} — role and email cannot be changed here.
        </Typography>
        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="firstName"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="First Name" required error={!!errors.firstName} helperText={errors.firstName?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="lastName"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="Last Name" required error={!!errors.lastName} helperText={errors.lastName?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="mobile"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="Mobile" required error={!!errors.mobile} helperText={errors.mobile?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="gender"
                control={control}
                render={({ field }) => (
                  <TextField {...field} select fullWidth label="Gender" required error={!!errors.gender} helperText={errors.gender?.message}>
                    <MenuItem value={GENDER.MALE}>Male</MenuItem>
                    <MenuItem value={GENDER.FEMALE}>Female</MenuItem>
                    <MenuItem value={GENDER.OTHER}>Other</MenuItem>
                  </TextField>
                )}
              />
            </Grid>
            {/* roleId is submitted (backend requires it in UpdateUserRequest)
                but not shown as editable — locked to the user's current role. */}
          </Grid>
          <Button type="submit" variant="contained" size="large" disabled={isSubmitting} sx={{ mt: 3 }}>
            {isSubmitting ? 'Saving...' : 'Save Changes'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default MyProfilePage;