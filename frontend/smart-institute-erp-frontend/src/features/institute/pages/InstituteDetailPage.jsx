import { useState, useEffect, useCallback } from 'react';
import { Typography, Box, Paper, Grid, Button, Chip, CircularProgress, Switch, FormControlLabel } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import ErrorState from '@components/tables/ErrorState';
import InstituteFormDialog from '../components/InstituteFormDialog';
import { instituteApi } from '@services/api/instituteApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { useAuth } from '@hooks/useAuth';

// Single-institute detail/edit screen. Now sourced from the authenticated
// user's own instituteId (UserResponse.instituteId, confirmed available
// via /auth/me as of the backend update) — no manual URL param needed
// anymore, and no risk of a user manipulating which institute they view
// (Section 7/9's requirement).
function InstituteDetailPage() {
  const { user, hasRole } = useAuth();
  const canManage = hasRole(['SUPER_ADMIN', 'INSTITUTE_ADMIN']);
  const instituteId = user?.instituteId;

  const [institute, setInstitute] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editOpen, setEditOpen] = useState(false);
  const [togglingStatus, setTogglingStatus] = useState(false);

  const fetchInstitute = useCallback(async () => {
    if (!instituteId) {
      // Can happen briefly on first render before AuthBootstrap's
      // fetchCurrentUser() resolves — not a real error, just "not
      // ready yet". We don't show ErrorState for this specific case.
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await instituteApi.getById(instituteId);
      setInstitute(response);
    } catch (err) {
      setError(err.message || 'Failed to load institute details.');
    } finally {
      setLoading(false);
    }
  }, [instituteId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchInstitute();
  }, [fetchInstitute]);

  const handleToggleStatus = async (e) => {
    const newActive = e.target.checked;
    setTogglingStatus(true);
    try {
      await instituteApi.updateStatus(institute.id, newActive);
      showSnackbar(`Institute ${newActive ? 'activated' : 'deactivated'} successfully.`, 'success');
      fetchInstitute();
    } catch (err) {
      showSnackbar(err.message || 'Failed to update institute status.', 'error');
    } finally {
      setTogglingStatus(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!instituteId) {
    return (
      <Paper variant="outlined">
        <ErrorState message="Your account isn't linked to an institute yet." onRetry={fetchInstitute} />
      </Paper>
    );
  }

  if (error) {
    return (
      <Paper variant="outlined">
        <ErrorState message={error} onRetry={fetchInstitute} />
      </Paper>
    );
  }

  if (!institute) {
    return null;
  }

  const fields = [
    { label: 'Name', value: institute.name },
    { label: 'Type', value: institute.type },
    { label: 'Email', value: institute.email },
    { label: 'Mobile', value: institute.mobile },
    { label: 'Landline', value: institute.landline },
    { label: 'Website', value: institute.website },
    { label: 'Address', value: institute.address },
    { label: 'City', value: institute.city },
    { label: 'State', value: institute.state },
    { label: 'Pincode', value: institute.pincode },
    { label: 'GST Number', value: institute.gstNumber },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h2">Institute Details</Typography>
        {canManage && (
          <Button variant="contained" startIcon={<EditIcon />} onClick={() => setEditOpen(true)}>
            Edit
          </Button>
        )}
      </Box>

      <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <Chip label={institute.status} color={institute.active ? 'success' : 'default'} />
          {canManage && (
            <FormControlLabel
              control={<Switch checked={!!institute.active} onChange={handleToggleStatus} disabled={togglingStatus} />}
              label="Active"
            />
          )}
        </Box>

        <Grid container spacing={2}>
          {fields.map((f) => (
            <Grid key={f.label} size={{ xs: 12, sm: 6 }}>
              <Typography variant="body2" color="text.secondary">
                {f.label}
              </Typography>
              <Typography variant="body1">{f.value || '—'}</Typography>
            </Grid>
          ))}
        </Grid>
      </Paper>

      {editOpen && (
        <InstituteFormDialog
          open={editOpen}
          institute={institute}
          onClose={() => setEditOpen(false)}
          onSuccess={fetchInstitute}
        />
      )}
    </Box>
  );
}

export default InstituteDetailPage;