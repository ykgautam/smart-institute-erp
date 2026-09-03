import { useState, useEffect } from 'react';
import { Typography, Box, Paper, Grid, TextField, MenuItem, Card, CardContent, CircularProgress } from '@mui/material';
import DataTable from '@components/tables/DataTable';
import { getAttendanceHistoryColumns } from '../constants/attendanceColumns';
import { studentApi } from '@services/api/studentApi';
import { attendanceApi } from '@services/api/attendanceApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// PART 3: pick a student, see their full attendance history plus a
// backend-calculated summary (percentage, totals) — Section 12's rule
// against frontend recalculation applies here too: attendancePercentage
// comes directly from AttendanceSummaryResponse, never computed client-side.
function StudentAttendanceHistoryPage() {
  const [students, setStudents] = useState([]);
  const [loadingStudents, setLoadingStudents] = useState(true);
  const [selectedStudentId, setSelectedStudentId] = useState('');

  const [history, setHistory] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loadingData, setLoadingData] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadStudents() {
      setLoadingStudents(true);
      try {
        const response = await studentApi.getStudents({ page: 0, size: 100, sortBy: 'firstName', direction: 'ASC' });
        setStudents(response?.content || []);
      } catch {
        showSnackbar('Failed to load students.', 'error');
      } finally {
        setLoadingStudents(false);
      }
    }
    loadStudents();
  }, []);

  useEffect(() => {
    if (!selectedStudentId) return;

    async function loadData() {
      setLoadingData(true);
      setError(null);
      try {
        const [historyRes, summaryRes] = await Promise.all([
          attendanceApi.getStudentHistory(selectedStudentId),
          attendanceApi.getStudentSummary(selectedStudentId),
        ]);
        setHistory(historyRes || []);
        setSummary(summaryRes || null);
      } catch (err) {
        setError(err.message || 'Failed to load attendance history.');
      } finally {
        setLoadingData(false);
      }
    }
    loadData();
  }, [selectedStudentId]);

  const columns = getAttendanceHistoryColumns();

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Student Attendance History
      </Typography>

      <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
        <TextField
          select
          fullWidth
          label="Student"
          value={selectedStudentId}
          onChange={(e) => setSelectedStudentId(e.target.value)}
          disabled={loadingStudents}
        >
          {students.map((s) => (
            <MenuItem key={s.id} value={s.id}>
              {s.admissionNumber} — {s.fullName}
            </MenuItem>
          ))}
        </TextField>
      </Paper>

      {loadingData && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress size={28} />
        </Box>
      )}

      {!loadingData && summary && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          {[
            { label: 'Total Classes', value: summary.totalClasses },
            { label: 'Present', value: summary.totalPresent },
            { label: 'Absent', value: summary.totalAbsent },
            { label: 'Late', value: summary.totalLate },
            { label: 'Leave', value: summary.totalLeave },
            { label: 'Attendance %', value: `${Number(summary.attendancePercentage).toFixed(1)}%` },
          ].map((item) => (
            <Grid key={item.label} size={{ xs: 6, sm: 4, md: 2 }}>
              <Card variant="outlined">
                <CardContent sx={{ textAlign: 'center' }}>
                  <Typography variant="h3">{item.value}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {item.label}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {selectedStudentId && !loadingData && (
        <DataTable
          columns={columns}
          rows={history}
          loading={false}
          error={error}
          emptyMessage="No attendance history found."
          page={0}
          size={history.length || 10}
          totalElements={history.length}
          onPageChange={() => {}}
          onSizeChange={() => {}}
          getRowId={(row) => row.id}
        />
      )}
    </Box>
  );
}

export default StudentAttendanceHistoryPage;