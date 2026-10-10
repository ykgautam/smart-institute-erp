import { useState, useEffect, useCallback } from 'react';
import {
  Typography,
  Box,
  Grid,
  Paper,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Chip,
  CircularProgress,
} from '@mui/material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import TrendingUpOutlinedIcon from '@mui/icons-material/TrendingUpOutlined';
import EmptyState from '@components/tables/EmptyState';
import ErrorState from '@components/tables/ErrorState';
import { studentPerformanceApi } from '@services/api/studentPerformanceApi';
import { formatMonthLabel } from '@features/dashboard/utils/dashboardUtils';

// Deep-dive performance analytics for the logged-in student — distinct
// from StudentDashboardPage (Sprint 8), which only shows top-level KPIs
// and recent/upcoming tests. This page answers "how am I doing over
// time, by course, and by topic" using data the backend already
// computes (Section 12/22: no frontend recalculation of these metrics).
function StudentPerformancePage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPerformance = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentPerformanceApi.getPerformance();
      setData(response);
    } catch (err) {
      setError(err.message || 'Failed to load your performance data.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchPerformance();
  }, [fetchPerformance]);

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
        <ErrorState message={error} onRetry={fetchPerformance} />
      </Paper>
    );
  }

  if (!data) return null;

  const monthlyData = (data.monthlyPerformance || []).map((m) => ({
    name: formatMonthLabel(m.year, m.month),
    average: m.averagePercentage,
  }));

  const kpis = [
    { label: 'Overall Average', value: `${Number(data.overallAverage ?? 0).toFixed(1)}%` },
    { label: 'Tests Attempted', value: data.testsAttempted ?? 0 },
    { label: 'Tests Passed', value: data.testsPassed ?? 0 },
    { label: 'Tests Failed', value: data.testsFailed ?? 0 },
    { label: 'Pass %', value: `${Number(data.passPercentage ?? 0).toFixed(1)}%` },
    { label: 'Highest Score', value: `${Number(data.highestScore ?? 0).toFixed(1)}%` },
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        My Performance
      </Typography>

      {/* KPI row */}
      <Grid container spacing={2} sx={{ mb: 4 }}>
        {kpis.map((k) => (
          <Grid key={k.label} size={{ xs: 6, sm: 4, md: 2 }}>
            <Card variant="outlined">
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h3">{k.value}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {k.label}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3} sx={{ mb: 3 }}>
        {/* Monthly trend chart */}
        <Grid size={{ xs: 12, md: 7 }}>
          <Paper variant="outlined" sx={{ p: 2, height: '100%' }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Monthly Performance Trend
            </Typography>
            {monthlyData.length === 0 ? (
              <EmptyState message="No monthly performance data yet." />
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <LineChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis unit="%" domain={[0, 100]} />
                  <Tooltip />
                  <Line type="monotone" dataKey="average" stroke="#1565C0" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>

        {/* Course-wise performance */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Paper variant="outlined" sx={{ p: 2, height: '100%' }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Course-wise Performance
            </Typography>
            {(!data.coursePerformance || data.coursePerformance.length === 0) ? (
              <EmptyState message="No course performance data yet." />
            ) : (
              <List dense>
                {data.coursePerformance.map((c) => (
                  <ListItem key={c.courseId} divider>
                    <ListItemText
                      primary={c.courseName}
                      secondary={`${c.testsAttempted} attempted — ${c.testsPassed} passed`}
                    />
                    <Chip
                      label={`${Number(c.averagePercentage).toFixed(1)}%`}
                      size="small"
                      color={c.averagePercentage >= 60 ? 'success' : c.averagePercentage >= 40 ? 'warning' : 'error'}
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
      </Grid>

      {/* Strong / Weak topics */}
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
              <TrendingUpOutlinedIcon color="success" fontSize="small" />
              <Typography variant="h3">Strong Topics</Typography>
            </Box>
            {(!data.strongTopics || data.strongTopics.length === 0) ? (
              <EmptyState message="Not enough data to identify strong topics yet." />
            ) : (
              <List dense>
                {data.strongTopics.map((t) => (
                  <ListItem key={t.topicId} divider>
                    <ListItemText primary={t.topicName} secondary={`${t.testsAttempted} tests attempted`} />
                    <Chip label={`${Number(t.averagePercentage).toFixed(1)}%`} size="small" color="success" />
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
              <TrendingUpOutlinedIcon color="error" fontSize="small" sx={{ transform: 'rotate(180deg)' }} />
              <Typography variant="h3">Areas to Improve</Typography>
            </Box>
            {(!data.weakTopics || data.weakTopics.length === 0) ? (
              <EmptyState message="Not enough data to identify weak topics yet." />
            ) : (
              <List dense>
                {data.weakTopics.map((t) => (
                  <ListItem key={t.topicId} divider>
                    <ListItemText primary={t.topicName} secondary={`${t.testsAttempted} tests attempted`} />
                    <Chip label={`${Number(t.averagePercentage).toFixed(1)}%`} size="small" color="error" />
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

export default StudentPerformancePage;