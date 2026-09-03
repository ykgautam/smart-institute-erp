import { useState, useEffect, useMemo } from 'react';
import {
  Typography,
  Box,
  Paper,
  Grid,
  TextField,
  MenuItem,
  Checkbox,
  FormControlLabel,
  Button,
  CircularProgress,
  Alert,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Select,
} from '@mui/material';
import { batchApi } from '@services/api/batchApi';
import { studentApi } from '@services/api/studentApi';
import { attendanceApi } from '@services/api/attendanceApi';
import { showSnackbar } from '@components/feedback/snackbarStore';
import { useDebouncedValue } from '@hooks/useDebouncedValue';
import { ATTENDANCE_STATUS } from '@constants/enums';

// PART 1: Mark Attendance.
//
// IMPORTANT LIMITATION (see Sprint 6 assumption flag): there is no
// backend endpoint that returns "students belonging to batch X", and
// StudentResponse doesn't expose a batchId either. So this screen shows
// ALL active students (not filtered by batch) and requires the marker
// to manually check the ones that belong to the selected batch.
// Checkboxes default UNCHECKED deliberately — this adds a small amount
// of friction on purpose, to reduce the risk of accidentally submitting
// attendance for the wrong students.
function MarkAttendancePage() {
  const [batches, setBatches] = useState([]);
  const [loadingBatches, setLoadingBatches] = useState(true);
  const [selectedBatchId, setSelectedBatchId] = useState('');
  const [attendanceDate, setAttendanceDate] = useState(() => new Date().toISOString().slice(0, 10));

  const [students, setStudents] = useState([]);
  const [loadingStudents, setLoadingStudents] = useState(false);
  const [searchInput, setSearchInput] = useState('');
  const debouncedKeyword = useDebouncedValue(searchInput, 300);

  // rowState: { [studentId]: { included: boolean, status: string, remarks: string } }
  const [rowState, setRowState] = useState({});

  // Set to true once we've checked and found existing records for the
  // selected batch/date — blocks re-marking (Section 14 business rule:
  // attendance cannot be duplicated for the same student/date).
  const [alreadyMarked, setAlreadyMarked] = useState(false);
  const [checkingExisting, setCheckingExisting] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Load the batch dropdown once on mount.
  useEffect(() => {
    async function loadBatches() {
      setLoadingBatches(true);
      try {
        const response = await batchApi.getBatches({ page: 0, size: 100, sortBy: 'batchName', direction: 'ASC' });
        setBatches(response?.content || []);
      } catch {
        showSnackbar('Failed to load batches.', 'error');
      } finally {
        setLoadingBatches(false);
      }
    }
    loadBatches();
  }, []);

  // Whenever batch + date both have values, check if attendance is
  // already marked for that combination before showing the student list.
  useEffect(() => {
    if (!selectedBatchId || !attendanceDate) {
      setAlreadyMarked(false);
      return;
    }

    async function checkExisting() {
      setCheckingExisting(true);
      try {
        const existing = await attendanceApi.getBatchAttendance(selectedBatchId, attendanceDate);
        setAlreadyMarked((existing || []).length > 0);
      } catch {
        // If the check itself fails, don't block marking — just proceed
        // and let the backend's own duplicate-prevention surface an
        // error on submit if needed.
        setAlreadyMarked(false);
      } finally {
        setCheckingExisting(false);
      }
    }
    checkExisting();
  }, [selectedBatchId, attendanceDate]);

  // Load the full active-student list once a batch is selected (and not
  // already marked) — see the limitation note at the top of this file.
  useEffect(() => {
    if (!selectedBatchId || alreadyMarked) return;

    async function loadStudents() {
      setLoadingStudents(true);
      try {
        const response = await studentApi.getStudents({ page: 0, size: 100, sortBy: 'firstName', direction: 'ASC' });
        const list = response?.content || [];
        setStudents(list);
        // Reset per-row state for the new batch selection.
        const initialState = {};
        list.forEach((s) => {
          initialState[s.id] = { included: false, status: ATTENDANCE_STATUS.PRESENT, remarks: '' };
        });
        setRowState(initialState);
      } catch {
        showSnackbar('Failed to load students.', 'error');
      } finally {
        setLoadingStudents(false);
      }
    }
    loadStudents();
  }, [selectedBatchId, alreadyMarked]);

  const filteredStudents = useMemo(() => {
    if (!debouncedKeyword) return students;
    const lower = debouncedKeyword.toLowerCase();
    return students.filter((s) => s.fullName?.toLowerCase().includes(lower));
  }, [students, debouncedKeyword]);

  const updateRow = (studentId, patch) => {
    setRowState((prev) => ({ ...prev, [studentId]: { ...prev[studentId], ...patch } }));
  };

  const handleSelectAllVisible = (checked) => {
    setRowState((prev) => {
      const next = { ...prev };
      filteredStudents.forEach((s) => {
        next[s.id] = { ...next[s.id], included: checked };
      });
      return next;
    });
  };

  const handleSubmit = async () => {
    const attendanceList = Object.entries(rowState)
      .filter(([, v]) => v.included)
      .map(([studentId, v]) => ({
        studentId: Number(studentId),
        status: v.status,
        remarks: v.remarks || undefined,
      }));

    if (attendanceList.length === 0) {
      showSnackbar('Select at least one student to mark attendance.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      await attendanceApi.markAttendance({
        batchId: selectedBatchId,
        attendanceDate,
        attendanceList,
      });
      showSnackbar(`Attendance marked for ${attendanceList.length} student(s).`, 'success');
      setAlreadyMarked(true); // Prevent immediate re-submission for the same batch/date.
    } catch (err) {
      // Surfaces backend business errors verbatim — e.g. duplicate
      // attendance for a specific student/date (Section 14).
      showSnackbar(err.message || 'Failed to mark attendance.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const includedCount = Object.values(rowState).filter((v) => v.included).length;

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Mark Attendance
      </Typography>

      <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              select
              fullWidth
              label="Batch"
              value={selectedBatchId}
              onChange={(e) => setSelectedBatchId(e.target.value)}
              disabled={loadingBatches}
            >
              {batches.map((b) => (
                <MenuItem key={b.id} value={b.id}>
                  {b.batchCode} — {b.batchName}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              type="date"
              label="Attendance Date"
              value={attendanceDate}
              onChange={(e) => setAttendanceDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
        </Grid>
      </Paper>

      {checkingExisting && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 3 }}>
          <CircularProgress size={24} />
        </Box>
      )}

      {!checkingExisting && selectedBatchId && alreadyMarked && (
        <Alert severity="info">
          Attendance has already been marked for this batch on {attendanceDate}. Go to{' '}
          <strong>Attendance Records</strong> to view or edit it.
        </Alert>
      )}

      {!checkingExisting && selectedBatchId && !alreadyMarked && (
        <Paper variant="outlined" sx={{ p: 2 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2, flexWrap: 'wrap' }}>
            <TextField
              size="small"
              placeholder="Search students by name..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              sx={{ minWidth: 260 }}
            />
            <FormControlLabel
              control={
                <Checkbox
                  onChange={(e) => handleSelectAllVisible(e.target.checked)}
                  indeterminate={includedCount > 0 && includedCount < students.length}
                />
              }
              label="Select all visible"
            />
            <Box sx={{ flexGrow: 1 }} />
            <Typography variant="body2" color="text.secondary">
              {includedCount} selected
            </Typography>
          </Box>

          {loadingStudents ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress size={28} />
            </Box>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell padding="checkbox"></TableCell>
                  <TableCell>Student</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Remarks</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredStudents.map((s) => {
                  const row = rowState[s.id] || {};
                  return (
                    <TableRow key={s.id} hover>
                      <TableCell padding="checkbox">
                        <Checkbox
                          checked={!!row.included}
                          onChange={(e) => updateRow(s.id, { included: e.target.checked })}
                        />
                      </TableCell>
                      <TableCell>
                        {s.fullName} <Typography variant="caption" color="text.secondary">({s.admissionNumber})</Typography>
                      </TableCell>
                      <TableCell>
                        <Select
                          size="small"
                          value={row.status || ATTENDANCE_STATUS.PRESENT}
                          onChange={(e) => updateRow(s.id, { status: e.target.value })}
                          disabled={!row.included}
                        >
                          <MenuItem value={ATTENDANCE_STATUS.PRESENT}>Present</MenuItem>
                          <MenuItem value={ATTENDANCE_STATUS.ABSENT}>Absent</MenuItem>
                          <MenuItem value={ATTENDANCE_STATUS.LATE}>Late</MenuItem>
                          <MenuItem value={ATTENDANCE_STATUS.LEAVE}>Leave</MenuItem>
                        </Select>
                      </TableCell>
                      <TableCell>
                        <TextField
                          size="small"
                          placeholder="Optional"
                          value={row.remarks || ''}
                          onChange={(e) => updateRow(s.id, { remarks: e.target.value })}
                          disabled={!row.included}
                        />
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          )}

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
            <Button variant="contained" onClick={handleSubmit} disabled={submitting || includedCount === 0}>
              {submitting ? 'Submitting...' : `Submit Attendance (${includedCount})`}
            </Button>
          </Box>
        </Paper>
      )}
    </Box>
  );
}

export default MarkAttendancePage;