import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, Paper, TextField, Button, Typography, Alert } from '@mui/material';
import { loginSchema } from '@schemas/authSchemas';
import { loginUser, fetchCurrentUser } from '@features/auth/authSlice';

function LoginPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [submitError, setSubmitError] = useState(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '' },
  });

  // ProtectedRoute sets location.state.from when it redirects an
  // unauthenticated user here — send them back where they were headed.
  const redirectTo = location.state?.from?.pathname || '/dashboard';

  const onSubmit = async (data) => {
    setSubmitError(null);
    const result = await dispatch(loginUser(data));

    if (loginUser.fulfilled.match(result)) {
      // Fetch the profile before navigating so role-aware UI (built in
      // a later Part) has what it needs on first render of /dashboard.
      await dispatch(fetchCurrentUser());
      navigate(redirectTo, { replace: true });
    } else {
      setSubmitError(result.payload || 'Login failed. Please try again.');
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        bgcolor: 'background.default',
      }}
    >
      <Paper elevation={0} variant="outlined" sx={{ p: 4, width: 380 }}>
        <Typography variant="h2" gutterBottom>
          Smart Institute ERP
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Sign in to continue
        </Typography>

        {submitError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {submitError}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <TextField
            fullWidth
            label="Email"
            type="email"
            margin="normal"
            {...register('email')}
            error={!!errors.email}
            helperText={errors.email?.message}
          />
          <TextField
            fullWidth
            label="Password"
            type="password"
            margin="normal"
            {...register('password')}
            error={!!errors.password}
            helperText={errors.password?.message}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={isSubmitting}
            sx={{ mt: 3 }}
          >
            {isSubmitting ? 'Signing in...' : 'Sign In'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default LoginPage;