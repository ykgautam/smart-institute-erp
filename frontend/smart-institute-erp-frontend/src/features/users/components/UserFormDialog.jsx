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
import { createUserSchema, updateUserSchema } from '@schemas/userSchemas';
import { userApi } from '@services/api/userApi';
import { roleApi } from '@services/api/roleApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { normalizeFieldErrors } from '@schemas/studentSchemas';
import { GENDER } from '@constants/enums';

function UserFormDialog({ open, mode, initialData, onClose, onSuccess }) {
  const isEdit = mode === 'edit';
  const schema = isEdit ? updateUserSchema : createUserSchema;

  const [roles, setRoles] = useState([]);
  const [loadingRoles, setLoadingRoles] = useState(true);

  const {
    control,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      firstName: '',
      lastName: '',
      email: '',
      mobile: '',
      password: '',
      gender: '',
      roleId: '',
    },
  });

  useEffect(() => {
    if (!open) return;
    async function loadRoles() {
      setLoadingRoles(true);
      try {
        const response = await roleApi.getAll();
        setRoles(response || []);
      } catch {
        showSnackbar('Failed to load roles.', 'error');
      } finally {
        setLoadingRoles(false);
      }
    }
    loadRoles();
  }, [open]);

  useEffect(() => {
    if (open) {
      if (isEdit && initialData) {
        // initialData.role is a NAME string (e.g. "FACULTY") from
        // UserResponse — we resolve it to a roleId once roles have
        // loaded, since UpdateUserRequest needs the numeric id, not
        // the name. Handled in a follow-up effect below once roles
        // are available.
        reset({
          firstName: initialData.firstName || '',
          lastName: initialData.lastName || '',
          mobile: initialData.mobile || '',
          gender: initialData.gender || '',
          roleId: '',
        });
      } else {
        reset({
          firstName: '',
          lastName: '',
          email: '',
          mobile: '',
          password: '',
          gender: '',
          roleId: '',
        });
      }
    }
  }, [open, isEdit, initialData, reset]);

  // Once roles finish loading, resolve the edited user's role NAME to
  // its numeric roleId and pre-select it in the dropdown — a two-step
  // resolution made necessary because UserResponse and UpdateUserRequest
  // use different representations of "role" (name vs id).
  useEffect(() => {
    if (isEdit && initialData && roles.length > 0) {
      const matched = roles.find((r) => r.name === initialData.role);
      if (matched) {
        reset((prev) => ({ ...prev, roleId: matched.id }));
      }
    }
  }, [isEdit, initialData, roles, reset]);

  const onSubmit = async (data) => {
    try {
      if (isEdit) {
        await userApi.updateUser(initialData.id, data);
        showSnackbar('User updated successfully.', 'success');
      } else {
        await userApi.createUser(data);
        showSnackbar('User created successfully.', 'success');
      }
      onSuccess();
      onClose();
    } catch (err) {
      const fieldErrors = normalizeFieldErrors(err.errors);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => setError(field, { type: 'server', message }));
      } else {
        showSnackbar(err.message || 'Failed to save user.', 'error');
      }
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit User' : 'Add User'}</DialogTitle>
      <DialogContent>
        {loadingRoles ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress size={28} />
          </Box>
        ) : (
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="firstName"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="First Name"
                    required
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
                    required={isEdit}
                    error={!!errors.lastName}
                    helperText={errors.lastName?.message}
                  />
                )}
              />
            </Grid>

            {!isEdit && (
              <>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="email"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        label="Email"
                        required
                        error={!!errors.email}
                        helperText={errors.email?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="password"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        type="password"
                        label="Password"
                        required
                        error={!!errors.password}
                        helperText={errors.password?.message}
                      />
                    )}
                  />
                </Grid>
              </>
            )}

            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="mobile"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    fullWidth
                    label="Mobile"
                    required={isEdit}
                    error={!!errors.mobile}
                    helperText={errors.mobile?.message}
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
                    required={isEdit}
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

            <Grid size={12}>
              <Controller
                name="roleId"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    select
                    fullWidth
                    label="Role"
                    required
                    error={!!errors.roleId}
                    helperText={errors.roleId?.message}
                  >
                    {roles.map((r) => (
                      <MenuItem key={r.id} value={r.id}>
                        {r.name} — {r.description}
                      </MenuItem>
                    ))}
                  </TextField>
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
        <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isSubmitting || loadingRoles}>
          {isSubmitting ? 'Saving...' : isEdit ? 'Save Changes' : 'Create User'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default UserFormDialog;