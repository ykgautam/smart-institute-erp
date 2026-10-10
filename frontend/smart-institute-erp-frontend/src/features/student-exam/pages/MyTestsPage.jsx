import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Typography,
  Box,
  Paper,
  List,
  ListItem,
  ListItemText,
  Button,
  CircularProgress,
  Grid,
  TextField,
  MenuItem,
} from '@mui/material';
import EmptyState from '@components/tables/EmptyState';
import ErrorState from '@components/tables/ErrorState';
import DataTable from '@components/tables/DataTable';
import { studentTestApi } from '@services/api/studentTestApi';
import { studentDashboardApi } from '@services/api/studentDashboardApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { useStudentTestHistory } from '../hooks/useStudentTestHistory';
import { getTestHistoryColumns } from '../constants/testHistoryColumns';
import { STUDENT_TEST_STATUS } from '@constants/enums';

// This page has TWO independent sections that each fetch their own
// data, because they come from two entirely different backend concerns:
//
//   1. "Available Tests" — sourced from the Student Dashboard's
//      upcomingTests[] (see the loadAvailableTests effect below). There
//      is no dedicated "tests available to me" endpoint, so we reuse
//      dashboard data as a pragmatic workaround (documented as a known
//      limitation back in Sprint 13).
//
//   2. "Test History" — sourced from the NEW paginated/filterable
//      /student/tests/history endpoint (Sprint 16). This replaces what
//      used to be a simple unpaged list, and now supports status
//      filtering, date-range filtering, search, and real pagination —
//      useful once a student has attempted many tests over time.
function MyTestsPage() {
  const navigate = useNavigate();

  // --- Section 1: Available Tests (unchanged from earlier sprints) ---
  const [upcomingTests, setUpcomingTests] = useState([]);
  const [loadingAvailable, setLoadingAvailable] = useState(true);
  const [availableError, setAvailableError] = useState(null);
  const [starting, setStarting] = useState(null);

  useEffect(() => {
    async function loadAvailableTests() {
      setLoadingAvailable(true);
      setAvailableError(null);
      try {
        const dashboardRes = await studentDashboardApi.getDashboard();
        setUpcomingTests(dashboardRes?.upcomingTests || []);
      } catch (err) {
        setAvailableError(err.message || 'Failed to load available tests.');
      } finally {
        setLoadingAvailable(false);
      }
    }
    loadAvailableTests();
  }, []);

  // "Start Test" calls the backend to create/resume an attempt, then
  // navigates straight into the exam-taking screen using the returned
  // studentTest.id (NOT the testId — these are different IDs: testId
  // identifies the test definition, studentTest.id identifies THIS
  // student's specific attempt at it).
  const handleStart = async (testId) => {
    setStarting(testId);
    try {
      const studentTest = await studentTestApi.startTest(testId);
      navigate(`/exams/take/${studentTest.id}`);
    } catch (err) {
      showSnackbar(err.message || 'Failed to start test.', 'error');
    } finally {
      setStarting(null);
    }
  };

  // --- Section 2: Test History (new, paginated) ---
  const {
    rows,
    totalElements,
    loading,
    error,
    page,
    size,
    setPage,
    setSize,
    status,
    setStatus,
    dateFrom,
    setDateFrom,
    dateTo,
    setDateTo,
    searchInput,
    setSearchInput,
    refetch,
  } = useStudentTestHistory();

  // What happens when the student clicks the action button on a history
  // row depends on whether that attempt is still in progress or done.
  const handleHistoryAction = (row) => {
    if (row.status === 'IN_PROGRESS') {
      navigate(`/exams/take/${row.studentTestId}`);
    } else {
      navigate(`/exams/result/${row.studentTestId}`);
    }
  };

  const historyColumns = getTestHistoryColumns({ onAction: handleHistoryAction });

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        My Tests
      </Typography>

      {/* --- Available Tests section --- */}
      <Typography variant="h3" sx={{ mb: 1 }}>
        Available Tests
      </Typography>
      <Paper variant="outlined" sx={{ mb: 4 }}>
        {loadingAvailable ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress size={28} />
          </Box>
        ) : availableError ? (
          <ErrorState message={availableError} />
        ) : upcomingTests.length === 0 ? (
          <EmptyState message="No tests available right now." />
        ) : (
          <List>
            {upcomingTests.map((t) => (
              <ListItem
                key={t.testId}
                secondaryAction={
                  <Button
                    size="small"
                    variant="contained"
                    disabled={starting === t.testId}
                    onClick={() => handleStart(t.testId)}
                  >
                    {starting === t.testId ? 'Starting...' : 'Start'}
                  </Button>
                }
              >
                <ListItemText primary={t.testTitle} secondary={`${t.courseName} — ${t.durationInMinutes} min`} />
              </ListItem>
            ))}
          </List>
        )}
      </Paper>

      {/* --- Test History section (paginated + filterable) --- */}
      <Typography variant="h3" sx={{ mb: 1 }}>
        Test History
      </Typography>

      {/* Filter bar: status dropdown, date range, search — all wired
          to useStudentTestHistory's state. Changing any of these
          triggers a fresh API call automatically (see the hook). */}
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              select
              fullWidth
              size="small"
              label="Status"
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value={STUDENT_TEST_STATUS.IN_PROGRESS}>In Progress</MenuItem>
              <MenuItem value={STUDENT_TEST_STATUS.SUBMITTED}>Submitted</MenuItem>
              <MenuItem value={STUDENT_TEST_STATUS.AUTO_SUBMITTED}>Auto Submitted</MenuItem>
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="From"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="To"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              fullWidth
              size="small"
              label="Search test name"
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
            />
          </Grid>
        </Grid>
      </Paper>

      {/* DataTable is our shared, reusable table component (built back
          in Sprint 2) — it already knows how to show loading skeletons,
          empty states, error states, and a working pagination bar, so
          we don't rebuild any of that here. */}
      <DataTable
        columns={historyColumns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No test attempts found."
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={setPage}
        onSizeChange={setSize}
        getRowId={(row) => row.studentTestId}
      />
    </Box>
  );
}

export default MyTestsPage;