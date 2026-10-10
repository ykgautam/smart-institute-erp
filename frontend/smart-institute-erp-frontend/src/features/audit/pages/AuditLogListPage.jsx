import { useState } from 'react';
import { Typography, Box, Paper, Grid, TextField, MenuItem } from '@mui/material';
import DataTable from '@components/tables/DataTable';
import AuditDetailDialog from '../components/AuditDetailDialog';
import { useAuditLogs } from '../hooks/useAuditLogs';
import { getAuditColumns } from '../constants/auditColumns';
import { AUDIT_ACTION } from '@constants/enums';

// Read-only, sensitive module — kept behind SUPER_ADMIN-only routing
// (enforced in navConfig.js); no create/edit/delete actions exist here
// by design (Section 23: audit logs provide historical record only).
function AuditLogListPage() {
  const { rows, totalElements, loading, error, page, size, setPage, setSize, filters, updateFilter, refetch } =
    useAuditLogs();

  const [detailTarget, setDetailTarget] = useState(null);

  const columns = [
    ...getAuditColumns(),
    {
      field: 'view',
      headerName: '',
      render: (row) => (
        <Typography
          variant="body2"
          color="primary"
          sx={{ cursor: 'pointer' }}
          onClick={() => setDetailTarget(row)}
        >
          View
        </Typography>
      ),
    },
  ];

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 3 }}>
        Audit Logs
      </Typography>

      <Paper variant="outlined" sx={{ p: 2, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              select
              fullWidth
              size="small"
              label="Action"
              value={filters.action}
              onChange={(e) => updateFilter('action', e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              {Object.values(AUDIT_ACTION).map((a) => (
                <MenuItem key={a} value={a}>
                  {a}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              fullWidth
              size="small"
              label="Entity Type"
              placeholder="e.g. Student"
              value={filters.entityType}
              onChange={(e) => updateFilter('entityType', e.target.value)}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="From"
              value={filters.dateFrom}
              onChange={(e) => updateFilter('dateFrom', e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="To"
              value={filters.dateTo}
              onChange={(e) => updateFilter('dateTo', e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Grid>
        </Grid>
      </Paper>

      <DataTable
        columns={columns}
        rows={rows}
        loading={loading}
        error={error}
        onRetry={refetch}
        emptyMessage="No audit log entries found."
        page={page}
        size={size}
        totalElements={totalElements}
        onPageChange={setPage}
        onSizeChange={setSize}
        getRowId={(row) => row.id}
      />

      {detailTarget && (
        <AuditDetailDialog open={!!detailTarget} log={detailTarget} onClose={() => setDetailTarget(null)} />
      )}
    </Box>
  );
}

export default AuditLogListPage;