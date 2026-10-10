import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { Box, Paper, TextField, Button, Typography, Grid, MenuItem, Divider, Link } from '@mui/material';
import { onboardingSchema } from '@schemas/onboardingSchemas';
import { onboardingApi } from '@services/api/onboardingApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { INSTITUTE_TYPE, GENDER } from '@constants/enums';

// Public signup flow — creates a brand-new institute AND its first
// owner/admin user in one call (POST /institutes/onboard). This is the
// FIRST step for any new institute using the ERP; every other module
// (Courses, Batches, Students, etc.) is created afterward by the owner
// once logged in.
function OnboardingPage() {
  const navigate = useNavigate();
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(onboardingSchema),
    defaultValues: {
      institute: { name: '', type: '', email: '', mobile: '', address: '', city: '', state: '' },
      owner: { firstName: '', lastName: '', email: '', mobile: '', password: '', gender: '' },
    },
  });

  const onSubmit = async (data) => {
    try {
      await onboardingApi.onboard(data);
      showSnackbar('Institute registered successfully. Please sign in.', 'success');
      navigate('/login');
    } catch (err) {
      showSnackbar(err.message || 'Failed to register institute.', 'error');
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
        py: 4,
      }}
    >
      <Paper elevation={0} variant="outlined" sx={{ p: 4, width: 560 }}>
        <Typography variant="h2" gutterBottom>
          Register Your Institute
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Set up your institute and admin account to get started.
        </Typography>

        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <Typography variant="body1" fontWeight={600} sx={{ mb: 1 }}>
            Institute Details
          </Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid size={{ xs: 12, sm: 8 }}>
              <Controller
                name="institute.name"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="Institute Name" required error={!!errors.institute?.name} helperText={errors.institute?.name?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 4 }}>
              <Controller
                name="institute.type"
                control={control}
                render={({ field }) => (
                  <TextField {...field} select fullWidth label="Type" required error={!!errors.institute?.type} helperText={errors.institute?.type?.message}>
                    <MenuItem value={INSTITUTE_TYPE.COMPUTER_INSTITUTE}>Computer Institute</MenuItem>
                    <MenuItem value={INSTITUTE_TYPE.SCHOOL}>School</MenuItem>
                    <MenuItem value={INSTITUTE_TYPE.COLLEGE}>College</MenuItem>
                    <MenuItem value={INSTITUTE_TYPE.TRAINING_CENTER}>Training Center</MenuItem>
                    <MenuItem value={INSTITUTE_TYPE.COACHING_CENTER}>Coaching Center</MenuItem>
                  </TextField>
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="institute.email"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="Institute Email" error={!!errors.institute?.email} helperText={errors.institute?.email?.message} />}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="institute.mobile"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="Institute Mobile" error={!!errors.institute?.mobile} helperText={errors.institute?.mobile?.message} />}
              />
            </Grid>
            <Grid size={12}>
              <Controller
                name="institute.address"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="Address" error={!!errors.institute?.address} helperText={errors.institute?.address?.message} />}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="institute.city"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="City" error={!!errors.institute?.city} helperText={errors.institute?.city?.message} />}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="institute.state"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="State" error={!!errors.institute?.state} helperText={errors.institute?.state?.message} />}
              />
            </Grid>
          </Grid>

          <Divider sx={{ mb: 3 }} />

          <Typography variant="body1" fontWeight={600} sx={{ mb: 1 }}>
            Your Admin Account
          </Typography>
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="owner.firstName"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="First Name" required error={!!errors.owner?.firstName} helperText={errors.owner?.firstName?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="owner.lastName"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="Last Name" error={!!errors.owner?.lastName} helperText={errors.owner?.lastName?.message} />}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="owner.email"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth label="Email" required error={!!errors.owner?.email} helperText={errors.owner?.email?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="owner.mobile"
                control={control}
                render={({ field }) => <TextField {...field} fullWidth label="Mobile" error={!!errors.owner?.mobile} helperText={errors.owner?.mobile?.message} />}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="owner.password"
                control={control}
                render={({ field }) => (
                  <TextField {...field} fullWidth type="password" label="Password" required error={!!errors.owner?.password} helperText={errors.owner?.password?.message} />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="owner.gender"
                control={control}
                render={({ field }) => (
                  <TextField {...field} select fullWidth label="Gender" error={!!errors.owner?.gender} helperText={errors.owner?.gender?.message}>
                    <MenuItem value="">—</MenuItem>
                    <MenuItem value={GENDER.MALE}>Male</MenuItem>
                    <MenuItem value={GENDER.FEMALE}>Female</MenuItem>
                    <MenuItem value={GENDER.OTHER}>Other</MenuItem>
                  </TextField>
                )}
              />
            </Grid>
          </Grid>

          <Button type="submit" fullWidth variant="contained" size="large" disabled={isSubmitting} sx={{ mt: 3 }}>
            {isSubmitting ? 'Registering...' : 'Register Institute'}
          </Button>

          <Typography variant="body2" align="center" sx={{ mt: 2 }}>
            Already have an account? <Link component={RouterLink} to="/login">Sign in</Link>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}

export default OnboardingPage;