import { Box, Paper, Grid, TextField, MenuItem, Card, CardContent, Typography } from '@mui/material';
import DataTable from '@components/tables/DataTable';
import { useReportQuery } from '../hooks/useReportQuery';
import { useCourseBatchOptions } from '../hooks/useCourseBatchOptions';
import { formatCurrency } from '@features/dashboard/utils/dashboardUtils';

function formatValue(value, format) {
  if (value === undefined || value === null) return '—';
  if (format === 'percent') return `${Number(value).toFixed(1)}%`;
  if (format === 'currency') return formatCurrency(value);
  return value;
}

// Config-driven report screen reused by ALL admin reports (Section 24:
// filters/pagination/summary/loading/empty/error states consistently
// applied). Each report just supplies its columns/filters/summary
// config — this component is the only place the actual UI mechanics live.
//
// filterFields item shapes:
//   { type: 'course', key, label }
//   { type: 'batch', key, label }
//   { type: 'select', key, label, options: [] }
//   { type: 'dateRange', fromKey, toKey, label }
function GenericReportView({ fetchFn, columns, filterFields = [], summaryFields = [], getRowId, defaultSort }) {
  const { rows, summary, totalElements, loading, error, page, size, setPage, setSize, filters, updateFilter, refetch } =
    useReportQuery(fetchFn, {}, defaultSort);
  const { courses, batches } = useCourseBatchOptions();

  return (
    <Box>
      {filterFields.length > 0 && (
        <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
          <Grid container spacing={2}>
            {filterFields.map((f) => {
              if (f.type === 'course') {
                return (
                  <Grid key={f.key} size={{ xs: 12, sm: 3 }}>
                    <TextField select fullWidth size="small" label={f.label} value={filters[f.key] || ''} onChange={(e) => updateFilter(f.key, e.target.value)}>
                      <MenuItem value="">All</MenuItem>
                      {courses.map((c) => <MenuItem key={c.id} value={c.id}>{c.courseName}</MenuItem>)}
                    </TextField>
                  </Grid>
                );
              }
              if (f.type === 'batch') {
                return (
                  <Grid key={f.key} size={{ xs: 12, sm: 3 }}>
                    <TextField select fullWidth size="small" label={f.label} value={filters[f.key] || ''} onChange={(e) => updateFilter(f.key, e.target.value)}>
                      <MenuItem value="">All</MenuItem>
                      {batches.map((b) => <MenuItem key={b.id} value={b.id}>{b.batchName}</MenuItem>)}
                    </TextField>
                  </Grid>
                );
              }
              if (f.type === 'select') {
                return (
                  <Grid key={f.key} size={{ xs: 12, sm: 3 }}>
                    <TextField select fullWidth size="small" label={f.label} value={filters[f.key] || ''} onChange={(e) => updateFilter(f.key, e.target.value)}>
                      <MenuItem value="">All</MenuItem>
                      {f.options.map((o) => <MenuItem key={o} value={o}>{o}</MenuItem>)}
                    </TextField>
                  </Grid>
                );
              }
              if (f.type === 'dateRange') {
                return (
                  <Grid key={f.fromKey} size={{ xs: 12, sm: 6 }} container spacing={2}>
                    <Grid size={6}>
                      <TextField fullWidth size="small" type="date" label={`${f.label} From`} value={filters[f.fromKey] || ''} onChange={(e) => updateFilter(f.fromKey, e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
                    </Grid>
                    <Grid size={6}>
                      <TextField fullWidth size="small" type="date" label={`${f.label} To`} value={filters[f.toKey] || ''} onChange={(e) => updateFilter(f.toKey, e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
                    </Grid>
                  </Grid>
                );
              }
              return null;
            })}
          </Grid>
        </Paper>
      )}

      {summary && summaryFields.length > 0 && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          {summaryFields.map((s) => (
            <Grid key={s.key} size={{ xs: 6, sm: 4, md: 2 }}>
              <Card variant="outlined">
                <CardContent sx={{ textAlign: 'center' }}>
                  <Typography variant="h3">{formatValue(summary[s.key], s.format)}</Typography>
                  <Typography variant="body2" color="text.secondary">{s.label}</Typography>
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
        onRetry={refetch}
        emptyMessage="No records found."
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={setPage}
        onSizeChange={setSize}
        getRowId={getRowId}
      />
    </Box>
  );
}

export default GenericReportView;