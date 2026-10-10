import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography, Box, Divider, Chip, Grid } from '@mui/material';
import { getAuditActionColor } from '@constants/enums';

// Renders oldValue/newValue as readable key-value rows rather than raw
// JSON dumps (Section 24: "provide readable formatting instead of
// displaying raw unformatted JSON"). Falls back gracefully if a value
// isn't a plain object (e.g. null, or a primitive).
function ValueBlock({ title, value }) {
  const isPlainObject = value && typeof value === 'object' && !Array.isArray(value);

  return (
    <Box>
      <Typography variant="body2" fontWeight={600} sx={{ mb: 1 }}>
        {title}
      </Typography>
      {!value ? (
        <Typography variant="body2" color="text.secondary">
          —
        </Typography>
      ) : isPlainObject ? (
        <Box sx={{ bgcolor: 'background.default', borderRadius: 1, p: 1.5 }}>
          {Object.entries(value).map(([key, val]) => (
            <Box key={key} sx={{ display: 'flex', justifyContent: 'space-between', py: 0.25 }}>
              <Typography variant="body2" color="text.secondary">
                {key}
              </Typography>
              <Typography variant="body2">{String(val)}</Typography>
            </Box>
          ))}
        </Box>
      ) : (
        <Typography variant="body2">{String(value)}</Typography>
      )}
    </Box>
  );
}

function AuditDetailDialog({ open, log, onClose }) {
  if (!log) return null;

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Audit Log Detail</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid size={6}>
            <Typography variant="body2" color="text.secondary">
              User
            </Typography>
            <Typography variant="body2">
              {log.userName} ({log.userEmail})
            </Typography>
          </Grid>
          <Grid size={6}>
            <Typography variant="body2" color="text.secondary">
              Action
            </Typography>
            <Chip label={log.action} size="small" color={getAuditActionColor(log.action)} />
          </Grid>
          <Grid size={6}>
            <Typography variant="body2" color="text.secondary">
              Entity
            </Typography>
            <Typography variant="body2">
              {log.entityType} #{log.entityId}
            </Typography>
          </Grid>
          <Grid size={6}>
            <Typography variant="body2" color="text.secondary">
              Timestamp
            </Typography>
            <Typography variant="body2">
              {log.createdAt ? new Date(log.createdAt).toLocaleString('en-IN') : '—'}
            </Typography>
          </Grid>
          <Grid size={12}>
            <Typography variant="body2" color="text.secondary">
              Description
            </Typography>
            <Typography variant="body2">{log.description || '—'}</Typography>
          </Grid>
        </Grid>

        <Divider sx={{ mb: 2 }} />

        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid size={6}>
            <ValueBlock title="Old Value" value={log.oldValue} />
          </Grid>
          <Grid size={6}>
            <ValueBlock title="New Value" value={log.newValue} />
          </Grid>
        </Grid>

        <Divider sx={{ mb: 2 }} />

        <Grid container spacing={2}>
          <Grid size={6}>
            <Typography variant="body2" color="text.secondary">
              IP Address
            </Typography>
            <Typography variant="body2">{log.ipAddress || '—'}</Typography>
          </Grid>
          <Grid size={6}>
            <Typography variant="body2" color="text.secondary">
              User Agent
            </Typography>
            <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
              {log.userAgent || '—'}
            </Typography>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
}

export default AuditDetailDialog;