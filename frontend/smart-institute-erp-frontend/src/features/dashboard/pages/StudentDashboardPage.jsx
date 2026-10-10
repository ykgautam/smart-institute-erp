import { useState, useEffect, useCallback } from 'react';
import { Typography, Box, Grid, Paper, List, ListItem, ListItemText, Chip, CircularProgress } from '@mui/material';
import KpiCard from '../components/KpiCard';
import EmptyState from '@components/tables/EmptyState';
import ErrorState from '@components/tables/ErrorState';
import { studentDashboardApi } from '@services/api/studentDashboardApi';
import AssignmentTurnedInOutlinedIcon from '@mui/icons-material/AssignmentTurnedInOutlined';
import PendingActionsOutlinedIcon from '@mui/icons-material/PendingActionsOutlined';
import TrendingUpOutlinedIcon from '@mui/icons-material/TrendingUpOutlined';
import EmojiEventsOutlinedIcon from '@mui/icons-material/EmojiEventsOutlined';

// Dashboard for role === STUDENT. Every KPI and percentage here comes
// directly from StudentDashboardResponse — no frontend recalculation,
// same rule as the Admin dashboard (Section 12/22).
function StudentDashboardPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboard = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentDashboardApi.getDashboard();
      setData(response);
    } catch (err) {
      setError(err.message || 'Failed to load your dashboard.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchDashboard();
  }, [fetchDashboard]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Paper variant="outlined">
        <ErrorState message={error} onRetry={fetchDashboard} />
      </Paper>
    );
  }

  const recentTests = data?.recentTests || [];
  const upcomingTests = data?.upcomingTests || [];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 1 }}>
        Welcome, {data?.studentName}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Here's an overview of your test performance.
      </Typography>

      <Grid container spacing={2} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={AssignmentTurnedInOutlinedIcon} label="Tests Attempted" value={data?.testsAttempted ?? 0} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={PendingActionsOutlinedIcon} label="Pending Tests" value={data?.pendingTests ?? 0} color="warning" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard
            icon={TrendingUpOutlinedIcon}
            label="Average Score"
            value={`${Number(data?.averageScore ?? 0).toFixed(1)}%`}
            color="info"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard
            icon={EmojiEventsOutlinedIcon}
            label="Pass Percentage"
            value={`${Number(data?.passPercentage ?? 0).toFixed(1)}%`}
            color="success"
          />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Recent Tests
            </Typography>
            {recentTests.length === 0 ? (
              <EmptyState message="No tests attempted yet." />
            ) : (
              <List dense>
                {recentTests.map((t) => (
                  <ListItem
                    key={t.studentTestId}
                    secondaryAction={
                      <Chip
                        label={t.passed ? 'Passed' : 'Failed'}
                        size="small"
                        color={t.passed ? 'success' : 'error'}
                      />
                    }
                  >
                    <ListItemText
                      primary={t.testTitle}
                      secondary={`${Number(t.percentage).toFixed(1)}% — ${t.submittedAt ? new Date(t.submittedAt).toLocaleDateString('en-IN') : '—'}`}
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Upcoming Tests
            </Typography>
            {upcomingTests.length === 0 ? (
              <EmptyState message="No upcoming tests scheduled." />
            ) : (
              <List dense>
                {upcomingTests.map((t) => (
                  <ListItem key={t.testId}>
                    <ListItemText
                      primary={t.testTitle}
                      secondary={`${t.courseName} · ${t.durationInMinutes} min · Starts ${
                        t.startTime ? new Date(t.startTime).toLocaleString('en-IN') : '—'
                      }`}
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

export default StudentDashboardPage;