import { Box, Typography, Button } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

// Generic error state with an optional retry action. Never displays raw
// backend stack traces (Section 31) — `message` should already be the
// normalized, user-friendly string from apiClient's error interceptor.
function ErrorState({ message = 'Something went wrong. Please try again.', onRetry }) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 6,
        color: 'error.main',
      }}
    >
      <ErrorOutlineIcon sx={{ fontSize: 48, mb: 1 }} />
      <Typography variant="body2" sx={{ mb: onRetry ? 2 : 0 }}>
        {message}
      </Typography>
      {onRetry && (
        <Button variant="outlined" size="small" onClick={onRetry}>
          Retry
        </Button>
      )}
    </Box>
  );
}

export default ErrorState;