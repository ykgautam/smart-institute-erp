import { Box, Typography } from '@mui/material';
import InboxOutlinedIcon from '@mui/icons-material/InboxOutlined';

// Generic "no data" state — reusable across tables AND non-table
// screens (e.g. a dashboard widget with no data yet).
function EmptyState({ message = 'No records found.', icon: Icon = InboxOutlinedIcon }) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 6,
        color: 'text.secondary',
      }}
    >
      <Icon sx={{ fontSize: 48, mb: 1, opacity: 0.5 }} />
      <Typography variant="body2">{message}</Typography>
    </Box>
  );
}

export default EmptyState;