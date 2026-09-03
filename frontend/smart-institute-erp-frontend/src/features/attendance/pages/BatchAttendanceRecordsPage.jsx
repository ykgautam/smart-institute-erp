import { useState, useEffect, useCallback } from 'react';
import { Typography, Box, Paper, Grid, TextField, MenuItem, IconButton, Tooltip, CircularProgress } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DataTable from '@components/tables/DataTable';
import EditAttendanceDialog from '../components/EditAttendanceDialog';
import { getAttendanceRecordColumns } from '../constants/attendanceColumns';
import { batchApi } from '@services/api/batchApi';
import { attendanceApi } from '@services/api/attendanceApi';
import { showSnackbar } from '@components/feedback/snackbarStore';

// PART 2: pick a batch + date, view the already-marked attendance for
// that combination, and edit individual records.
function BatchAttendanceRecordsPage() {
  const [batches, setBatches] = useState([]);
  const [loadingBatches, setLoadingBatches] = useState(true);
  const [selectedBatchId, setSelectedBatchId] = useState('');
  const [attendanceDate, setAttendanceDate] = useState(() => new Date().toISOString().slice(0, 10));

  const [records, setRecords] = useState([]);
  const [loadingRecords, setLoadingRecords] = useState(false);
  const [error, setError] = useState(null);
  const [editTarget, setEditTarget] = useState(null);

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

  const fetchRecords = useCallback(async () => {
    if (!selectedBatchId || !attendanceDate) return;
    setLoadingRecords(true);
    setError(null);
    try {
      const response = await attendanceApi.getBatchAttendance(selectedBatchId, attendanceDate);
      setRecords(response || []);
    } catch (err) {
      setError(err.message || 'Failed to load attendance records.');
    } finally {
      setLoadingRecords(false);
    }
  }, [selectedBatchId, attendanceDate]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchRecords();
  }, [fetchRecords]);

  const columns = [
    ...getAttendanceRecordColumns(),
    {
      field: 'actions',
      headerName: 'Actions',
      render: (row) => (
        <Tooltip title="Edit">
          <IconButton size="small" onClick={() => setEditTarget(row)}>
            <EditIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      ),
    },
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Attendance Records
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
              label="Date"
              value={attendanceDate}
              onChange={(e) => setAttendanceDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
        </Grid>
      </Paper>

      {!selectedBatchId ? (
        <Typography color="text.secondary">Select a batch and date to view attendance records.</Typography>
      ) : (
        <DataTable
          columns={columns}
          rows={records}
          loading={loadingRecords}
          error={error}
          onRetry={fetchRecords}
          emptyMessage="No attendance marked for this batch and date."
          page={0}
          size={records.length || 10}
          totalElements={records.length}
          onPageChange={() => {}}
          onSizeChange={() => {}}
          getRowId={(row) => row.id}
        />
      )}

      {editTarget && (
        <EditAttendanceDialog
          open={!!editTarget}
          record={editTarget}
          onClose={() => setEditTarget(null)}
          onSuccess={fetchRecords}
        />
      )}
    </Box>
  );
}

export default BatchAttendanceRecordsPage;