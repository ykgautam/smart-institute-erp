import { useState, useEffect, useCallback } from 'react';
import { Typography, Box, Grid, Paper, CircularProgress } from '@mui/material';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import PeopleOutlinedIcon from '@mui/icons-material/PeopleOutlined';
import CheckCircleOutlinedIcon from '@mui/icons-material/CheckCircleOutlined';
import ClassOutlinedIcon from '@mui/icons-material/ClassOutlined';
import GroupsOutlinedIcon from '@mui/icons-material/GroupsOutlined';
import EventAvailableOutlinedIcon from '@mui/icons-material/EventAvailableOutlined';
import PendingActionsOutlinedIcon from '@mui/icons-material/PendingActionsOutlined';
import PaymentsOutlinedIcon from '@mui/icons-material/PaymentsOutlined';
import AssignmentOutlinedIcon from '@mui/icons-material/AssignmentOutlined';
import KpiCard from '../components/KpiCard';
import EmptyState from '@components/tables/EmptyState';
import ErrorState from '@components/tables/ErrorState';
import { adminDashboardApi } from '@services/api/adminDashboardApi';
import { formatMonthLabel, formatCurrency } from '../utils/dashboardUtils';

// Dashboard for SUPER_ADMIN / INSTITUTE_ADMIN (and, as a fallback,
// STAFF/FACULTY/ACCOUNTANT until the backend defines role-specific
// dashboards for them — see DashboardRouter.jsx).
function AdminDashboardPage() {
  const [kpi, setKpi] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboard = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [kpiRes, analyticsRes] = await Promise.all([
        adminDashboardApi.getDashboard(),
        adminDashboardApi.getAnalytics(),
      ]);
      setKpi(kpiRes);
      setAnalytics(analyticsRes);
    } catch (err) {
      setError(err.message || 'Failed to load dashboard data.');
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

  const studentGrowthData = (analytics?.studentGrowth || []).map((d) => ({
    name: formatMonthLabel(d.year, d.month),
    students: d.studentCount,
  }));
  const attendanceTrendData = (analytics?.attendanceTrend || []).map((d) => ({
    name: formatMonthLabel(d.year, d.month),
    attendance: d.attendancePercentage,
  }));
  const feeCollectionData = (analytics?.feeCollectionTrend || []).map((d) => ({
    name: formatMonthLabel(d.year, d.month),
    collection: d.totalCollection,
  }));
  const coursePerformanceData = analytics?.coursePerformance || [];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Dashboard
      </Typography>

      <Grid container spacing={2} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={PeopleOutlinedIcon} label="Total Students" value={kpi?.totalStudents ?? 0} />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={CheckCircleOutlinedIcon} label="Active Students" value={kpi?.activeStudents ?? 0} color="success" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={ClassOutlinedIcon} label="Total Courses" value={kpi?.totalCourses ?? 0} color="info" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={GroupsOutlinedIcon} label="Total Batches" value={kpi?.totalBatches ?? 0} color="info" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard
            icon={EventAvailableOutlinedIcon}
            label="Today's Attendance"
            value={`${Number(kpi?.todayAttendancePercentage ?? 0).toFixed(1)}%`}
            color="success"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard
            icon={PendingActionsOutlinedIcon}
            label="Pending Fee Students"
            value={kpi?.pendingFeeStudents ?? 0}
            color="warning"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard
            icon={PaymentsOutlinedIcon}
            label="Today's Collection"
            value={formatCurrency(kpi?.todayCollection)}
            color="success"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <KpiCard icon={AssignmentOutlinedIcon} label="Upcoming Tests" value={kpi?.upcomingTests ?? 0} color="secondary" />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Student Growth
            </Typography>
            {studentGrowthData.length === 0 ? (
              <EmptyState message="No student growth data yet." />
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <LineChart data={studentGrowthData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Line type="monotone" dataKey="students" stroke="#1565C0" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Attendance Trend
            </Typography>
            {attendanceTrendData.length === 0 ? (
              <EmptyState message="No attendance trend data yet." />
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <LineChart data={attendanceTrendData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis unit="%" domain={[0, 100]} />
                  <Tooltip />
                  <Line type="monotone" dataKey="attendance" stroke="#2E7D32" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Fee Collection Trend
            </Typography>
            {feeCollectionData.length === 0 ? (
              <EmptyState message="No fee collection data yet." />
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={feeCollectionData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip formatter={(value) => formatCurrency(value)} />
                  <Bar dataKey="collection" fill="#ED6C02" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <Typography variant="h3" sx={{ mb: 2 }}>
              Course Performance
            </Typography>
            {coursePerformanceData.length === 0 ? (
              <EmptyState message="No course performance data yet." />
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <BarChart data={coursePerformanceData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="courseName" />
                  <YAxis unit="%" domain={[0, 100]} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="averagePercentage" name="Avg. Score %" fill="#1565C0" />
                  <Bar dataKey="passPercentage" name="Pass %" fill="#2E7D32" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

export default AdminDashboardPage;