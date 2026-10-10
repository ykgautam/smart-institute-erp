import { useState, useEffect, useCallback } from 'react';
import { Typography, Box, Paper, Grid, TextField, MenuItem, Card, CardContent } from '@mui/material';
import DataTable from '@components/tables/DataTable';
import { usePagination } from '@hooks/usePagination';
import { testReportApi } from '@services/api/testReportApi';
import { testApi } from '@services/api/testApi';

function TestPerformanceReportPage() {
  const { page, size, setPage, setSize } = usePagination(10);
  const [tests, setTests] = useState([]);
  const [selectedTestId, setSelectedTestId] = useState('');
  const [rows, setRows] = useState([]);
  const [summary, setSummary] = useState(null);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadTests() {
      try {
        const response = await testApi.getAll();
        setTests(response || []);
      } catch {
        // Non-fatal — filter dropdown just stays empty.
      }
    }
    loadTests();
  }, []);

  const fetchReport = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await testReportApi.getReport({
        testId: selectedTestId || undefined,
        page,
        size,
        sortBy: 'submittedAt',
        sortDirection: 'DESC',
      });
      setRows(response?.content || []);
      setSummary(response?.summary || null);
      setTotalElements(response?.totalElements || 0);
    } catch (err) {
      setError(err.message || 'Failed to load test performance report.');
    } finally {
      setLoading(false);
    }
  }, [selectedTestId, page, size]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchReport();
  }, [fetchReport]);

  const columns = [
    { field: 'studentName', headerName: 'Student' },
    { field: 'testName', headerName: 'Test' },
    { field: 'batchName', headerName: 'Batch', render: (row) => row.batchName || '—' },
    { field: 'status', headerName: 'Status' },
    { field: 'percentage', headerName: 'Score %', render: (row) => `${Number(row.percentage || 0).toFixed(1)}%` },
    { field: 'passed', headerName: 'Result', render: (row) => (row.passed ? 'Passed' : 'Failed') },
    {
      field: 'submittedAt',
      headerName: 'Submitted',
      render: (row) => (row.submittedAt ? new Date(row.submittedAt).toLocaleString('en-IN') : '—'),
    },
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>Test Performance Report</Typography>

      <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
        <TextField select fullWidth size="small" label="Filter by Test" value={selectedTestId} onChange={(e) => setSelectedTestId(e.target.value)}>
          <MenuItem value="">All Tests</MenuItem>
          {tests.map((t) => <MenuItem key={t.id} value={t.id}>{t.title}</MenuItem>)}
        </TextField>
      </Paper>

      {summary && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          {[
            { label: 'Total Attempts', value: summary.totalAttempts },
            { label: 'Passed', value: summary.passedAttempts },
            { label: 'Failed', value: summary.failedAttempts },
            { label: 'Avg. %', value: `${Number(summary.averagePercentage || 0).toFixed(1)}%` },
          ].map((k) => (
            <Grid key={k.label} size={{ xs: 6, sm: 3 }}>
              <Card variant="outlined">
                <CardContent sx={{ textAlign: 'center' }}>
                  <Typography variant="h3">{k.value}</Typography>
                  <Typography variant="body2" color="text.secondary">{k.label}</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={fetchReport}
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

export default TestPerformanceReportPage;